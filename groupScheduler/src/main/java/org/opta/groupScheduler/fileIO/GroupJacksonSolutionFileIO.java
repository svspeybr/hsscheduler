package org.opta.groupScheduler.fileIO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opta.groupScheduler.domain.GroupScheduleSolution;
import org.optaplanner.persistence.jackson.impl.domain.solution.JacksonSolutionFileIO;

public class GroupJacksonSolutionFileIO extends JacksonSolutionFileIO<GroupScheduleSolution> {
    public GroupJacksonSolutionFileIO(){
        super(GroupScheduleSolution.class);
    }

}
