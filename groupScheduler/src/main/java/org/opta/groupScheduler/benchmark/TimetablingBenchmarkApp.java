package org.opta.groupScheduler.benchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opta.groupScheduler.domain.*;
import org.opta.groupScheduler.fileIO.GroupJacksonSolutionFileIO;
import org.opta.groupScheduler.fileIO.UntisTextReader;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.persistence.jackson.api.OptaPlannerJacksonModule;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class TimetablingBenchmarkApp {
    public static void main(String[] args) throws Exception {
        String SOURCE = "org\\opta\\groupScheduler\\benchmark\\groupSchedulerBenchmarkConfig.xml";
        String SOURCE_TO_INIT = "C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\data\\unsolved";
        String SOURCE_CLASSES = "C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\data\\untis\\KLASSEN2324.TXT";
        String SOURCE_LESSONS = "C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\data\\untis\\LESSON2223.TXT";

        String test = "test.json";
        String fileSixthYear = "sixthYear.json";
        String fileFifthYear = "fifthYear.json";
        String fileFourthYear = "fourthYear.json";
        String fileThirdYear = "thirdYear.json";

        //FETCH DATA
        /*
        File file6 = new File(SOURCE_TO_INIT + "\\"+fileSixthYear);
        File file5 = new File(SOURCE_TO_INIT + "\\"+fileFifthYear);
        File file4 = new File(SOURCE_TO_INIT + "\\"+fileFourthYear);
        File file3 = new File(SOURCE_TO_INIT + "\\"+fileThirdYear);
        file6.delete();
        file5.delete();
        file4.delete();
        file3.delete();

        GroupJacksonSolutionFileIO io = new GroupJacksonSolutionFileIO();
        io.write(UntisTextReader.readFromPath(6,
                new HashSet<>(List.of("LO", "GE", "GO", "ES", "AC")),
               SOURCE_CLASSES,
                SOURCE_LESSONS).getScheduleSolution(), new File(SOURCE_TO_INIT, fileSixthYear) );
        io.write(UntisTextReader.readFromPath(5,
                new HashSet<>(List.of("LO", "GE", "GO", "ES", "AC")),
                SOURCE_CLASSES,
                SOURCE_LESSONS).getScheduleSolution(), new File(SOURCE_TO_INIT, fileFifthYear) );
        io.write(UntisTextReader.readFromPath(4,
                new HashSet<>(List.of("LO", "GE", "GO", "ES", "AC")),
                SOURCE_CLASSES,
                SOURCE_LESSONS).getScheduleSolution(), new File(SOURCE_TO_INIT, fileFourthYear) );
        io.write(UntisTextReader.readFromPath(3,
                new HashSet<>(List.of("LO", "GE", "GO", "ES", "AC")),
                SOURCE_CLASSES,
                SOURCE_LESSONS).getScheduleSolution(), new File(SOURCE_TO_INIT, fileThirdYear) );
        */

        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(SOURCE);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        plannerBenchmark.benchmarkAndShowReportInBrowser();

    }
}
