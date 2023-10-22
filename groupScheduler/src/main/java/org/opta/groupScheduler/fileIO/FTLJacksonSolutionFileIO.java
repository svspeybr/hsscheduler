package org.opta.groupScheduler.fileIO;

import org.opta.groupScheduler.domain.FixedTaskLesson;
import org.opta.groupScheduler.domain.FixedTaskLessonSolution;
import org.opta.groupScheduler.domain.GroupScheduleSolution;
import org.opta.groupScheduler.solver.PartitionSolver;
import org.optaplanner.persistence.jackson.impl.domain.solution.JacksonSolutionFileIO;

import java.io.File;

public class FTLJacksonSolutionFileIO extends JacksonSolutionFileIO<FixedTaskLessonSolution> {
    public FTLJacksonSolutionFileIO(){
        super(FixedTaskLessonSolution.class);
    }

}
