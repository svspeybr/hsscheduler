package org.opta.groupScheduler.solver;

import org.opta.groupScheduler.domain.ClassGroup;
import org.opta.groupScheduler.domain.FixedTaskLesson;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.util.*;
import java.util.stream.Collectors;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.*;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

public class FixedTaskLessonConstraintProvider implements ConstraintProvider {
        @Override
        public Constraint[] defineConstraints(ConstraintFactory factory) {
            return new Constraint[] {
                    studentGroupConflict(factory),//HARD
                    teacherConflict(factory), //HARD
                    compactness(factory), //HARD (SOFT IF NUMBERS OF LESSONS DO NOT TAKE UNFIXED INTO ACCOUNT
                    //minimalGroupCombos(factory),//MEDIUM
                    //minimalTimeslotCombos(factory),//MEDIUM
                    //minimalCombosPerGroup(factory),
                    maximalOverlap(factory),
                    groupSpreading(factory)//SOFT
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
    private Constraint teacherConflict(ConstraintFactory factory){
        return factory.forEach(FixedTaskLesson.class)
                .join(FixedTaskLesson.class,
                        Joiners.equal(FixedTaskLesson::getTimeslotLine),
                        Joiners.equal(ftl->ftl.getCourseLevel().getTeacher()),
                        Joiners.lessThan(FixedTaskLesson::getId))
                .penalize(HardMediumSoftScore.ONE_HARD)
                .asConstraint("FTLTeacherConflict");
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
        return factory.forEach(FixedTaskLesson.class)
                .groupBy(FixedTaskLesson::getTimeslotLine, toList())
                .map((timeslot, list)-> list.stream()
                        .map(FixedTaskLesson::getClassGroupList)
                        .flatMap(Collection::stream)
                        .map(ClassGroup::getName)
                        .sorted()
                        .reduce("", String::concat))
                .groupBy(name->name)
                .penalize(HardMediumSoftScore.ONE_SOFT)
                .asConstraint("minimalGroupCombos");
    }

    private Constraint minimalCombosPerGroup(ConstraintFactory factory){
        return factory.forEach(FixedTaskLesson.class)
                .groupBy(FixedTaskLesson::getTimeslotLine, toList())
                .map((timeslot, list)-> list.stream()
                        .map(FixedTaskLesson::getClassGroupList)
                        .flatMap(Collection::stream)
                        .map(ClassGroup::getName)
                        .sorted()
                        .reduce("", String::concat))
                .groupBy(name->name)
                .join(ClassGroup.class, Joiners.filtering((combo, cl)->combo.contains(cl.getName())))
                .groupBy((combo, cl)->cl, toList((combo, cl)->combo))
                .penalize(HardMediumSoftScore.ONE_MEDIUM, (cl, combos)->combos.size()*combos.size())
                .asConstraint("minimalGroupCombosPerGroup");
    }
    private Constraint maximalOverlap(ConstraintFactory factory){
        return factory.forEach(FixedTaskLesson.class)
                .groupBy(FixedTaskLesson::getTimeslotLine, toList())
                .join(FixedTaskLesson.class, filtering((ts,list, ftl)->ts < ftl.getTimeslotLine()))
                .groupBy((ts,list1, ftl2)-> ts, (ts,list1, ftl2)-> list1, (ts,list1, ftl2)->ftl2.getTimeslotLine(), toList((ts,list1, ftl2)-> ftl2))
                .filter((ts1, list1, ts2, list2)->{
                    Set<String> combo1 = list1.stream()
                            .map(FixedTaskLesson::getClassGroupList)
                            .flatMap(Collection::stream)
                            .map(ClassGroup::getName)
                            .collect(Collectors.toSet());
                    Set<String> combo2 = list2.stream()
                            .map(FixedTaskLesson::getClassGroupList)
                            .flatMap(Collection::stream)
                            .map(ClassGroup::getName)
                            .collect(Collectors.toSet());
                    Set<String> compl1 = new HashSet<>(combo1);
                    compl1.removeAll(combo2);
                    if (compl1.isEmpty()){
                        return false;
                    }
                    combo2.removeAll(combo1);
                    if (combo2.isEmpty()){
                        return false;
                    }
                    return true;})
                .penalize(HardMediumSoftScore.ONE_MEDIUM)
                .asConstraint("maximalOverlap");
    }

/*
    private Constraint maximalOverlap(ConstraintFactory factory){
        return factory.forEach(FixedTaskLesson.class)
                .join(FixedTaskLesson.class, Joiners.lessThan(FixedTaskLesson::getTimeslotLine))
                .groupBy((ft1,ft2)->ft1.getTimeslotLine(), (ft1,ft2)->ft2, toList((ft1,ft2)->ft1))
                .groupBy((ts, ft2, list1)-> ts, (ts, ft2, list1)-> list1, (ts,ft2, list1)->ft2.getTimeslotLine(), toList((ts,ft2, list)->ft2))
                .filter((ts1, list1, ts2, list2)->{
                    Set<String> combo1 = list1.stream()
                            .map(FixedTaskLesson::getClassGroupList)
                            .flatMap(Collection::stream)
                            .map(ClassGroup::getName)
                            .collect(Collectors.toSet());
                    Set<String> combo2 = list2.stream()
                            .map(FixedTaskLesson::getClassGroupList)
                            .flatMap(Collection::stream)
                            .map(ClassGroup::getName)
                            .collect(Collectors.toSet());
                    Set<String> compl1 = new HashSet<>(combo1);
                    compl1.removeAll(combo2);
                    if (compl1.isEmpty()){
                        return false;
                    }
                    combo2.removeAll(combo1);
                    if (combo2.isEmpty()){
                        return false;
                    }
                    return true;})
                .penalize(HardMediumSoftScore.ONE_MEDIUM)
                .asConstraint("maximaloverlap");
    }*/

    private Constraint minimalTimeslotCombos(ConstraintFactory factory){
        return factory.forEach(FixedTaskLesson.class)
                .groupBy(FixedTaskLesson::getTimeslotLine, fixedTaskLesson -> fixedTaskLesson)
                .flattenLast(FixedTaskLesson::getClassGroupList)
                .groupBy((ts, cl)->cl, toList((ts,cl)->ts))
                .map((cl, timeslots)-> timeslots.stream()
                        .map(Object::toString)
                        .sorted()
                        .reduce("", (str1,str2)-> str1 +"-" + str2))
                .groupBy(name->name)
                .penalize(HardMediumSoftScore.ONE_MEDIUM)
                .asConstraint("minimalTimeslotCombos");
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

}
