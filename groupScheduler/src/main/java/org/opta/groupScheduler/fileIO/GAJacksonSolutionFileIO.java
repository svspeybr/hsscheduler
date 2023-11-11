package org.opta.groupScheduler.fileIO;

import org.opta.groupScheduler.domain.FixedTaskLessonSolution;
import org.opta.groupScheduler.domain.GroupAssignmentSolution;
import org.optaplanner.persistence.jackson.impl.domain.solution.JacksonSolutionFileIO;

public class GAJacksonSolutionFileIO extends JacksonSolutionFileIO<GroupAssignmentSolution> {
    public GAJacksonSolutionFileIO(){
        super(GroupAssignmentSolution.class);
    }

}
