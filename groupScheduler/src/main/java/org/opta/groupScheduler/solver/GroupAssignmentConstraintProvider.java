package org.opta.groupScheduler.solver;

import org.opta.groupScheduler.domain.*;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.toList;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.toSet;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

public class GroupAssignmentConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                onSameTimeslotConflict(factory),
                differentTaskConflict(factory),
                groupSizeConflict(factory),
                sameTopicOnSameTimeslotConflict(factory),
                sameTaskConflict(factory),
                //TODO: ADDING A CONSTRAINT FOR 33 HOUR
                //groupSpreadingConflict(factory)
            };
        }

    private Constraint onSameTimeslotConflict(ConstraintFactory factory){
        return factory.forEach(GroupAssignment.class)
                .join(GroupAssignment.class,
                        Joiners.equal(GroupAssignment::getTimeslotLine),
                        Joiners.equal(GroupAssignment::getClassGroup),
                        Joiners.lessThan(GroupAssignment::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("onSameTimeslotConflict");
    }

    private Constraint sameTopicOnSameTimeslotConflict(ConstraintFactory factory){
        return factory.forEach(GroupAssignment.class)
                .join(GroupAssignment.class,
                        Joiners.equal(GroupAssignment::getTimeslotLine),
                        Joiners.equal(gr->gr.getCourseLevel().getTeacher()),
                        Joiners.lessThan(GroupAssignment::getCourseTaskId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("sameTopicOnSameTimeslotConflict");
    }

    private Constraint sameTaskConflict(ConstraintFactory factory){
            return factory.forEach(GroupAssignment.class)
                    .groupBy(GroupAssignment::getCourseLevel, GroupAssignment::getCourseTaskId, toSet(GroupAssignment::getTimeslotLine))
                    .penalize(HardSoftScore.ONE_HARD, (cl, id, set)-> Math.abs(set.size()- cl.getNumberOfAssignments())
                    )
                    .asConstraint("sameTaskConflict");
        }

    //ALL GROUPASSIGNMENTS OF A COURSE MATH/RELIGION/SPORT NEED TO HAVE THE SAME COURSELEVEL (!) BUT DIFFERENT IDS
    private Constraint differentTaskConflict(ConstraintFactory factory){
        return factory.forEach(GroupAssignment.class)
                .join(GroupAssignment.class,
                        Joiners.equal(GroupAssignment::getClassGroup),
                        Joiners.equal(GroupAssignment::getCourseLevel),
                        Joiners.lessThan(GroupAssignment::getId))
                .filter((ga1, ga2)-> !Objects.equals(ga1.getCourseTaskId(), ga2.getCourseTaskId()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("differentTaskConflict");
    }

    private Constraint groupSizeConflict(ConstraintFactory factory){
        return factory.forEach(GroupAssignment.class)
                .groupBy(GroupAssignment::getCourseTaskId, toList())
                .penalize(HardSoftScore.ONE_HARD, (id, gaList)->{
                    Set<ClassGroup> cgSet = gaList.stream().map(GroupAssignment::getClassGroup).collect(Collectors.toSet());
                    int totalSize = cgSet.stream().mapToInt(ClassGroup::getSize).sum();
                    int difference = totalSize - gaList.get(0).getCourseLevel().getUpperBoundClassSize();
                    return Math.max(difference, 0);
                })
                .asConstraint("groupSizeConflict");
    }

    private Constraint groupSpreadingConflict(ConstraintFactory factory){
        return factory.forEach(GroupAssignment.class)
                .groupBy(GroupAssignment::getCourseTaskId, toSet(GroupAssignment::getClassGroup))
                .penalize(HardSoftScore.ONE_SOFT, (id, cgs)->{
                    int size = cgs.stream().mapToInt(ClassGroup::getSize).reduce(0, Integer::sum);
                    return size * size;
                })
                .asConstraint("groupSpreadingConflict");
    }


}
