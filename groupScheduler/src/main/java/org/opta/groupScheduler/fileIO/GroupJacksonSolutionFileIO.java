package org.opta.groupScheduler.fileIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opta.groupScheduler.domain.GroupScheduleSolution;
import org.opta.groupScheduler.solver.PartitionSolver;
import org.optaplanner.persistence.jackson.impl.domain.solution.JacksonSolutionFileIO;

import java.io.File;

public class GroupJacksonSolutionFileIO extends JacksonSolutionFileIO<GroupScheduleSolution> {
    public GroupJacksonSolutionFileIO(){
        super(GroupScheduleSolution.class);
    }

    public PartitionSolver createGroupSolverFrom(String source){
        GroupScheduleSolution sol = this.read(new File(source));
        return new PartitionSolver(sol);
    }

}
