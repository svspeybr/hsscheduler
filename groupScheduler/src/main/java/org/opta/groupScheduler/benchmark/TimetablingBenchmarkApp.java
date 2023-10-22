package org.opta.groupScheduler.benchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opta.groupScheduler.domain.*;
import org.opta.groupScheduler.domain.solver.FullRowChangeMove;
import org.opta.groupScheduler.fileIO.FTLJacksonSolutionFileIO;
import org.opta.groupScheduler.fileIO.GroupJacksonSolutionFileIO;
import org.opta.groupScheduler.fileIO.UntisTextReader;
import org.opta.groupScheduler.solver.PartitionSolver;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.persistence.jackson.api.OptaPlannerJacksonModule;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public abstract class TimetablingBenchmarkApp {
    public static void main(String[] args) throws Exception {
        String SOURCE = "org\\opta\\groupScheduler\\benchmark\\groupSchedulerBenchmarkConfig.xml";
        String SOURCE_FLT = "org\\opta\\groupScheduler\\benchmark\\fixTaskSchedulerBenchmarkConfig.xml";
        String SOURCE_TO_INIT = "C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\data\\unsolved";
        String SOURCE_CLASSES = "C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\data\\untis\\KLASSEN2324.TXT";
        String SOURCE_LESSONS = "C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\data\\untis\\LESSON2223.TXT";

        String test = "test";
        String fileSixthYear = "sixthYear";
        String fileFifthYear = "fifthYear";
        String fileFourthYear = "fourthYear";
        String fileThirdYear = "thirdYear";

        /*
        //Partition - counter
        GroupJacksonSolutionFileIO io = new GroupJacksonSolutionFileIO();
        GroupScheduleSolution groupSchedule = io.read(new File("C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\data\\unsolved\\fifthYear.json"));


        List<FullRowChangeMove> moveList = new ArrayList<FullRowChangeMove>();

        //SETUP - partitioner
        List<List<ClassGroup>> combinations = new ArrayList<>();
        List<CourseLevel> clList = groupSchedule.getCourseLevelList();
        for (int i =0; i< clList.size(); i++){
            if (clList.get(i).getRelatedTaskIds().size() == 1){ //CONSIDER ONLY THE FIXED COMBINATIONS
                combinations.add(clList.get(i).getClassGroupList());
            }
        }
        ClassGroupPartitioner partitioner = new ClassGroupPartitioner(combinations, null);
        //SETUP - rowsOfAssignments
        List<List<FixedTaskLesson>> rowsOfAssignments = partitioner.convertKeyRowsToAssignmentRows(groupSchedule.getLessonAssignmentList(), 20000);

        //Create moves
        for (List<FixedTaskLesson> rowOfAssignment: rowsOfAssignments){
            for (Integer timeline: groupSchedule.getTimeLineList()){
                Map<FixedTaskLesson, Integer> rowToTimeline = new HashMap<>();
                for (FixedTaskLesson fixedTaskLesson : rowOfAssignment){
                    rowToTimeline.put(fixedTaskLesson, timeline);
                }
                moveList.add(new FullRowChangeMove(rowToTimeline));
            }
        }

        System.out.println(moveList.size());
        */

        /*
        List<CourseLevel> clList = dataset5.getCourseLevelList();
        List<LessonAssignment> laList = dataset5.getLessonAssignmentList();
        Map<Integer, Integer> clIdToPosition = new HashMap<>();
        List<List<ClassGroup>> combinations = new ArrayList<>();
        List<Integer> frequencyList = new ArrayList<>();

        for (int i =0; i< clList.size(); i++){
            if (clList.get(i).getRelatedTaskIds().size() == 1){ //CONSIDER ONLY THE FIXED COMBINATIONS
                combinations.add(clList.get(i).getClassGroupList());
                frequencyList.add(0);
                clIdToPosition.put(clList.get(i).getId(), frequencyList.size()-1);
            }
        }

        for (LessonAssignment la: laList){
            if (clIdToPosition.containsKey(la.getCourseTaskId())){
                Integer pos = clIdToPosition.get(la.getCourseTaskId());
                frequencyList.set(pos, frequencyList.get(pos) + 1);
            }
        }
        ClassGroupPartitioner partitioner = new ClassGroupPartitioner(combinations, frequencyList);
        partitioner.placeCombinationInRows();
        */
/*
        File file5Group = new File(SOURCE_TO_INIT + "\\"+fileFifthYear + ".json");
        file5Group.delete();
        File file5FTL = new File(SOURCE_TO_INIT + "\\"+fileFifthYear + "FTL.json");
        file5FTL.delete();

        GroupJacksonSolutionFileIO ioGroup = new GroupJacksonSolutionFileIO();
        FTLJacksonSolutionFileIO ioFTL = new FTLJacksonSolutionFileIO(); //reader/writer for FTLSolution
        GroupScheduleSolution sol5Group = UntisTextReader.readFromPath(5,
                new HashSet<>(List.of("LO", "GE", "GO", "ES", "AC")),
                SOURCE_CLASSES,
                SOURCE_LESSONS).getScheduleSolution();

        List<CourseLevel> clList = sol5Group.getCourseLevelList();
        List<CourseLevel> clListFTL = new ArrayList<>(); // CourseLvls for FTLSolution
        List<LessonAssignment> laList = sol5Group.getLessonAssignmentList();
        List<FixedTaskLesson> ftlList = new ArrayList<>(); // assignments for FTLSolution
        Map<Integer, Integer> clIdToPosition = new HashMap<>();
        List<List<ClassGroup>> combinations = new ArrayList<>();
        List<Integer> frequencyList = new ArrayList<>();

        for (int i =0; i< clList.size(); i++){
            if (clList.get(i).getRelatedTaskIds().size() == 1){ //CONSIDER ONLY THE FIXED COMBINATIONS
                clListFTL.add(clList.get(i));
                combinations.add(clList.get(i).getClassGroupList());
                frequencyList.add(0);
                clIdToPosition.put(clList.get(i).getId(), frequencyList.size()-1);
            }
        }

        for (LessonAssignment la: laList){
            if (clIdToPosition.containsKey(la.getCourseTaskId())){
                ftlList.add((FixedTaskLesson) la); //SHOULD NOT GIVE ERROR: FTL have cl from clListFTL == clIdToPosition.keySet()
                Integer pos = clIdToPosition.get(la.getCourseTaskId());
                frequencyList.set(pos, frequencyList.get(pos) + 1);
            }
        }
        ClassGroupPartitioner partitioner = new ClassGroupPartitioner(combinations, null);
        FixedTaskLessonSolution sol5FTL = new FixedTaskLessonSolution(sol5Group.getClassGroupList(),
                clListFTL,
                ftlList,
                sol5Group.getTimeLineList());
        sol5FTL.setPossibleFullRows(partitioner.convertKeyRowsToAssignmentRows(laList, 1000));

        ioGroup.write(sol5Group, new File(SOURCE_TO_INIT, fileFifthYear+".json"));
        //updateNumberOfLessons(sol5FTL.getClassGroupList(), sol5FTL.getFixedTaskLessonList());
        ioFTL.write(sol5FTL, new File(SOURCE_TO_INIT, fileFifthYear + "FTL.json"));
*/

        /*
        GroupScheduleSolution sol5v2 = io.read(new File("C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\data\\unsolved\\fifthYear.json"));

        List<List<FixedTaskLesson>> fullRows = sol5v2.getPossibleFullRows();
        List<LessonAssignment> lessonAssignments = sol5v2.getLessonAssignmentList().stream().filter(la->la instanceof FixedTaskLesson).toList();
        for (LessonAssignment lessonAssignment: lessonAssignments){
            System.out.println(lessonAssignment);
        }
        System.out.println(lessonAssignments);
        for (List<FixedTaskLesson> row:fullRows){
            System.out.println();
            System.out.println("new row: ");
            for (FixedTaskLesson task:row){
                System.out.println(task);
                if (lessonAssignments.contains((LessonAssignment) task)){
                    System.out.println("yes");
                }
            }
            System.out.println();
        }*/
        /*
        //FETCH DATA
        File file6 = new File(SOURCE_TO_INIT + "\\"+fileSixthYear +".json");
        File file5 = new File(SOURCE_TO_INIT + "\\"+fileFifthYear +".json");
        File file4 = new File(SOURCE_TO_INIT + "\\"+fileFourthYear +".json");
        File file3 = new File(SOURCE_TO_INIT + "\\"+fileThirdYear +".json");
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

        /*
        PlannerBenchmarkFactory plannerBenchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(SOURCE_FLT);
        PlannerBenchmark plannerBenchmark = plannerBenchmarkFactory.buildPlannerBenchmark();
        plannerBenchmark.benchmarkAndShowReportInBrowser();
        */
        /*
        GroupJacksonSolutionFileIO io = new GroupJacksonSolutionFileIO();
        PartitionSolver partitionSolver = io.createGroupSolverFrom("C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\data\\unsolved\\fifthYear.json");
        partitionSolver.placeTheFixedTaskLessons();
        */


    }
    private static void updateNumberOfLessons(List<ClassGroup> classGroupList, List<FixedTaskLesson> ftLessonList){
        for (ClassGroup clg: classGroupList){
            clg.setNumberOfLessons(0);
            for (FixedTaskLesson ftl: ftLessonList){
                if (ftl.getClassGroupList().contains(clg)){
                    clg.setNumberOfLessons(clg.getNumberOfLessons()+1);
                }
            }
        }
    }
}

