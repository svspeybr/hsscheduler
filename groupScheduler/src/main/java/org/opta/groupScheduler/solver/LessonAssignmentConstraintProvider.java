package org.opta.groupScheduler.solver;

import org.opta.groupScheduler.domain.ClassGroup;
import org.opta.groupScheduler.domain.FixedTaskLesson;
import org.opta.groupScheduler.domain.GroupPerCourse;
import org.opta.groupScheduler.domain.UnfixedTaskLesson;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.*;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

public class LessonAssignmentConstraintProvider implements ConstraintProvider {
        @Override
        public Constraint[] defineConstraints(ConstraintFactory factory) {
            return new Constraint[] {
                    groupSizeConflict(factory),
                    twoUnfixedStudentGroupConflict(factory),
                    twoFixedStudentGroupConflict(factory),
                    fixedUnfixedStudentGroupConflict(factory),
                    unfixedAvoidTimeLines(factory),
                    fixedAvoidTimeLines(factory)
            };
        }

        //TODO - faster? How to insert upperBound with minimal computational effort??
        private Constraint groupSizeConflict(ConstraintFactory factory){
            return factory.forEach(GroupPerCourse.class)
                    .groupBy(GroupPerCourse::getCourseTaskId, toList())
                    .filter((integer, gps) -> {
                        int totalSize = gps.stream().mapToInt(GroupPerCourse::getSize).sum();
                        return totalSize > gps.get(0).getCourseLevel().getUpperBoundClassSize() || totalSize == 0;
                    })
                    .penalize(HardSoftScore.ONE_HARD, ((integer, gps) -> {
                        int totalSize = gps.stream().mapToInt(GroupPerCourse::getSize).sum();
                        return Integer.max(totalSize - gps.get(0).getCourseLevel().getUpperBoundClassSize(),0)
                                +  Integer.max(Integer.min(1- totalSize, 1),0);
                    }))
                    .asConstraint("groupSizeConflict");
        }

        //TODO: PATHETIC...
        private Constraint twoUnfixedStudentGroupConflict(ConstraintFactory factory){
            return factory.forEach(GroupPerCourse.class)
                    .join(UnfixedTaskLesson.class, Joiners.equal(GroupPerCourse::getCourseTaskId, UnfixedTaskLesson::getCourseTaskId))
                    .join(UnfixedTaskLesson.class, filtering((groupPerCourse, lessonAssignment1, lessonAssignment2)
                            -> Objects.equals(lessonAssignment1.getTimeslotLine(), lessonAssignment2.getTimeslotLine())
                            && lessonAssignment1.getId() < lessonAssignment2.getId()
                    ))
                    .join(GroupPerCourse.class, filtering((groupPerCourse1, lessonAssignment1, lessonAssignment2, groupPerCourse2)
                            -> Objects.equals(groupPerCourse1.getClassGroup(), groupPerCourse2.getClassGroup())
                            && Objects.equals(lessonAssignment2.getCourseTaskId(), groupPerCourse2.getCourseTaskId())
                    ))
                    .penalize(HardSoftScore.ONE_HARD)
                    .asConstraint("twoUnfixedStudentGroupConflict");

        }

    private Constraint twoFixedStudentGroupConflict(ConstraintFactory factory){
        return factory.forEach(FixedTaskLesson.class)
                .join(FixedTaskLesson.class, Joiners.equal(FixedTaskLesson::getTimeslotLine),
                        Joiners.lessThan(FixedTaskLesson::getId),
                        filtering((le1, le2)-> {
                            Set<ClassGroup> intersection = new HashSet<>(le1.getCourseLevel().getClassGroupList());
                            intersection.retainAll(new HashSet<>(le2.getCourseLevel().getClassGroupList()));
                            return ! intersection.isEmpty();
                        }))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("twoFixStudentGroupConflict");

    }
    private Constraint fixedUnfixedStudentGroupConflict(ConstraintFactory factory){
        return factory.forEach(GroupPerCourse.class)
                .join(UnfixedTaskLesson.class, Joiners.equal(GroupPerCourse::getCourseTaskId, UnfixedTaskLesson::getCourseTaskId))
                .join(FixedTaskLesson.class, filtering((gr, le1, le2)-> Objects.equals(le1.getTimeslotLine(), le2.getTimeslotLine())
                && le2.getCourseLevel().getClassGroupList().contains(gr.getClassGroup())))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("fixedUnfixedStudentGroupConflict");

    }

    private Constraint unfixedAvoidTimeLines(ConstraintFactory factory){
            return factory.forEach(UnfixedTaskLesson.class)
                    .filter(lessonAssignment -> lessonAssignment.getTimeslotLine() > 32)
                    .join(GroupPerCourse.class, Joiners.equal(UnfixedTaskLesson::getCourseTaskId, GroupPerCourse::getCourseTaskId))
                    .groupBy((la, grCourse)-> grCourse.getCourseTaskId(), (la, grCourse)-> la.getTimeslotLine(), toList((la, grCourse)-> grCourse.getClassGroup().getNumberOfLessons() - la.getTimeslotLine()))
                    .filter((taskid, timeslot,penaltyList )->penaltyList.stream().max(Integer::compareTo).get() <0)
                    .penalize(HardSoftScore.ONE_HARD, (taskid, timeslot,penaltyList )->penaltyList.size())
                    .asConstraint("uf - no 8th/5th hour");
    }

    private Constraint fixedAvoidTimeLines(ConstraintFactory factory){
        return factory.forEach(FixedTaskLesson.class)
                .filter(lessonAssignment -> lessonAssignment.getCourseLevel().getClassGroupList().stream().mapToInt(ClassGroup::getNumberOfLessons).max().getAsInt() < lessonAssignment.getTimeslotLine())
                .penalize(HardSoftScore.ONE_HARD, lesa->lesa.getCourseLevel().getClassGroupList().size())
                .asConstraint("fix - no 8th/5th hour");
    }

}
