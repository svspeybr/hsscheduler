package org.opta.groupScheduler.domain.solver;

import org.opta.groupScheduler.domain.FixedTaskLesson;
import org.opta.groupScheduler.domain.GroupAssignment;
import org.opta.groupScheduler.domain.LessonAssignment;
import org.opta.groupScheduler.domain.UnfixedTaskLesson;

import java.util.Comparator;

public class GroupAssignmentDifficultyComparator implements Comparator<GroupAssignment> {

    private static final Comparator<GroupAssignment> COMPARATOR = Comparator.comparingInt(groupAssignment -> groupAssignment.getCourseLevel().getNumberOfAssignments());

    @Override
    public int compare(GroupAssignment ga1, GroupAssignment ga2){
            return COMPARATOR.compare(ga1, ga2);

    }

}
