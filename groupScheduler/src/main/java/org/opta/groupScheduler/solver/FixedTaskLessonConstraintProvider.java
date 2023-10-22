package org.opta.groupScheduler.solver;

import org.opta.groupScheduler.domain.ClassGroup;
import org.opta.groupScheduler.domain.FixedTaskLesson;
import org.opta.groupScheduler.domain.GroupPerCourse;
import org.opta.groupScheduler.domain.UnfixedTaskLesson;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.util.*;
import java.util.stream.Stream;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.*;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

public class FixedTaskLessonConstraintProvider implements ConstraintProvider {
        @Override
        public Constraint[] defineConstraints(ConstraintFactory factory) {
            return new Constraint[] {
                    studentGroupConflict(factory),//HARD
                    compactness(factory), //HARD (SOFT IF NUMBERS OF LESSONS DO NOT TAKE UNFIXED INTO ACCOUNT
                    minimalGroupCombos(factory),//MEDIUM
                    groupSpreading(factory)
            };
        }
//HARD
    private Constraint studentGroupConflict(ConstraintFactory factory){
        return factory.forEach(FixedTaskLesson.class)
                .join(FixedTaskLesson.class, Joiners.equal(FixedTaskLesson::getTimeslotLine),
                        Joiners.lessThan(FixedTaskLesson::getId),
                        filtering((le1, le2)-> {
                            Set<ClassGroup> intersection = new HashSet<>(le1.getCourseLevel().getClassGroupList());
                            intersection.retainAll(new HashSet<>(le2.getCourseLevel().getClassGroupList()));
                            return ! intersection.isEmpty();
                        }))
                .penalize(HardMediumSoftScore.ONE_HARD)
                .asConstraint("FTLStudentGroupConflict");
    }
    //TO DO MINIMAL USED LINES/
    private Constraint compactness(ConstraintFactory factory){
        return factory.forEach(FixedTaskLesson.class)
                .filter(lessonAssignment -> lessonAssignment.getCourseLevel().getClassGroupList().stream().mapToInt(ClassGroup::getNumberOfLessons).max().getAsInt() < lessonAssignment.getTimeslotLine())
                .penalize(HardMediumSoftScore.ONE_HARD, lesa->lesa.getCourseLevel().getClassGroupList().size())
                .asConstraint("compactness");
    }
//MEDIUM
    private Constraint minimalGroupCombos(ConstraintFactory factory){
        Set<String> combos = new HashSet<>();
        return factory.forEach(FixedTaskLesson.class)
                .groupBy(FixedTaskLesson::getTimeslotLine, toList())
                .map((timeslot, list)-> list.stream()
                        .map(FixedTaskLesson::getClassGroupList)
                        .flatMap(Collection::stream)
                        .map(ClassGroup::getName)
                        .sorted()
                        .reduce("", String::concat))
                .groupBy(name->name)
                .penalize(HardMediumSoftScore.ONE_MEDIUM)
                .asConstraint("minimalGroupCombos");
    }
//SOFT
    private Constraint groupSpreading(ConstraintFactory factory){
        return factory.forEach(FixedTaskLesson.class)
                .groupBy(FixedTaskLesson::getTimeslotLine, toList())
                .penalize(HardMediumSoftScore.ONE_SOFT, (timeslot, listOfTasks)->
                     (int) Math.pow(listOfTasks.stream()
                            .map(FixedTaskLesson::getClassGroupList)
                            .flatMap(Collection::stream)
                            .mapToInt(ClassGroup::getSize)
                            .sum(),2)
                )
                .asConstraint("groupSpreading");
    }

    /*TODO? ADJUSTING LIMIT in V2 50 (studentgroups)
    private Constraint compactnessV2(ConstraintFactory factory){
        return factory.forEach(ClassGroup.class)
                .join(FixedTaskLesson.class, )
                .groupBy(FixedTaskLesson::getTimeslotLine, toList())
                .penalize(HardSoftScore.ONE_SOFT, (timeslotLine, ftlList)-> 50 - ftlList.stream().mapToInt(ftl -> ftl.getClassGroupList().size()).sum())
                .asConstraint("avoid gaps");
    }*/

}
