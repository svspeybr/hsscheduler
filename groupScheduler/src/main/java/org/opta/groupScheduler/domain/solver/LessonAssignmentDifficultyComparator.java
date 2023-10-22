package org.opta.groupScheduler.domain.solver;

import org.opta.groupScheduler.domain.FixedTaskLesson;
import org.opta.groupScheduler.domain.LessonAssignment;
import org.opta.groupScheduler.domain.UnfixedTaskLesson;

import java.util.Comparator;

public class LessonAssignmentDifficultyComparator implements Comparator<LessonAssignment> {

    private static final Comparator<FixedTaskLesson> COMPARATOR = Comparator.comparingInt(fixedTaskLesson -> fixedTaskLesson.getCourseLevel().getClassGroupList().size());

    @Override
    public int compare(LessonAssignment le1, LessonAssignment le2){
        if (le1 instanceof UnfixedTaskLesson){
            return -1;
        } else if (le2 instanceof UnfixedTaskLesson){
            return 1;
        } else {
            return COMPARATOR.compare((FixedTaskLesson) le1, (FixedTaskLesson) le2);
        }
    }

}
