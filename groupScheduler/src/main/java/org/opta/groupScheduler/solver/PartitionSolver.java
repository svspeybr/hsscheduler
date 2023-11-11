package org.opta.groupScheduler.solver;

import org.opta.groupScheduler.domain.*;
import org.opta.groupScheduler.fileIO.FTLJacksonSolutionFileIO;
import org.opta.groupScheduler.fileIO.GAJacksonSolutionFileIO;
import org.opta.groupScheduler.fileIO.GroupJacksonSolutionFileIO;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PartitionSolver {

    //DATAFETCHING
    private GroupScheduleSolution scheduleSolution;
    private GroupAssignmentSolution groupAssignmentSolution;

    private ClassGroupAssignmentSolution classGroupAssignmentSolution;

    public PartitionSolver(List<ClassGroup> classGroupList,
                           List<CourseLevel> courseLevelList,
                           List<GroupPerCourse> groupPerCourseList,
                           List<LessonAssignment> lessonAssignmentList,
                           List<Integer> timeLineList,
                           List<GroupAssignment> groupAssignmentList,
                           List<ClassGroupAssignment> classGroupAssignmentList) {
        this.scheduleSolution = new GroupScheduleSolution(classGroupList,
                courseLevelList,
                groupPerCourseList,
                lessonAssignmentList,
                timeLineList);
        List<CourseLevel> clListUFTL = new ArrayList<>(); //TODO CLEAN UP!!!!!
        for (CourseLevel cl: this.scheduleSolution.getCourseLevelList()){
            if (cl.getRelatedTaskIds().size() > 1){ //CONSIDER ONLY THE FIXED COMBINATIONS
                clListUFTL.add(cl);
            }
        }
        this.groupAssignmentSolution = new GroupAssignmentSolution(
                classGroupList,
                clListUFTL,
                groupAssignmentList);
        this.classGroupAssignmentSolution = new ClassGroupAssignmentSolution(
                clListUFTL,
                classGroupAssignmentList);
    }

    public PartitionSolver(GroupScheduleSolution groupScheduleSolution){
        this.scheduleSolution = groupScheduleSolution;
    }

    public void partition() { //TODO ??READING PARAMETERS ClassGroupList
        try {
            final Long SINGLETON_TIME_TABLE_ID = 1L;
            //SOLVER
            //String SOURCE = "org.opta.groupScheduler\\solver\\solverConfig.xml"; TODO: FROM RESOURCE INSTEAD OF FROM FILE??
            File file = new File("C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\src\\main\\resources\\org\\opta\\groupScheduler\\solver\\solverConfig.xml");
            SolverConfig solverConfig = SolverConfig.createFromXmlFile(file);
            SolverFactory solverFactory = SolverFactory.create(solverConfig);
            SolverManager<GroupScheduleSolution, Long> solverManager = SolverManager.create(solverConfig, new SolverManagerConfig());
            SolutionManager<GroupScheduleSolution, HardSoftScore> scoreManager = SolutionManager.create(solverFactory);

            SolverJob<GroupScheduleSolution, Long> solverJob = solverManager.solve(SINGLETON_TIME_TABLE_ID, this.scheduleSolution);
            this.scheduleSolution = solverJob.getFinalBestSolution();
            ScoreExplanation<GroupScheduleSolution, HardSoftScore> scoreExplanation =scoreManager.explain(this.scheduleSolution);

            for (ClassGroup cl: this.scheduleSolution.getClassGroupList()){
                System.out.println(cl.getName() + "has numberOfLessons: " + cl.getNumberOfLessons());
            }

            Map<String, ConstraintMatchTotal<HardSoftScore>> map2 = scoreExplanation.getConstraintMatchTotalMap();
            System.out.println("ConstraintMatch");
            for (String o: map2.keySet()){
                System.out.println(map2.get(o).getConstraintName());
                for (ConstraintMatch<HardSoftScore> cm : map2.get(o).getConstraintMatchSet()){
                    System.out.println(cm.getConstraintName());
                    System.out.println(cm.getIndictedObjectList());
                    System.out.println(cm.getScore());

                }
            }

            solverManager.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Map<Integer, Map<String, List<String>>> generateMapByRowFromSource(String sourceFTL, String sourceGA){
        Map<Integer, Map<String, List<String>>> mapByRow = new HashMap<>();
        FTLJacksonSolutionFileIO FTLio = new FTLJacksonSolutionFileIO();
        GAJacksonSolutionFileIO GAio = new GAJacksonSolutionFileIO();
        FixedTaskLessonSolution ftlSolution = FTLio.read(new File(sourceFTL));
        GroupAssignmentSolution gaSolution = GAio.read(new File(sourceGA));
        //TODO CLEAN UP !!!!!!!
        for (FixedTaskLesson ftl :ftlSolution.getFixedTaskLessonList()){
            for (ClassGroup classGroup: ftl.getClassGroupList()) {
                String topicAndCourseId = ftl.getCourseLevel().getTopic() + " - " + ftl.getCourseTaskId();
                String className = classGroup.getName();
                Integer timeslotLine = ftl.getTimeslotLine();
                if (!mapByRow.containsKey(timeslotLine)) {
                    Map<String, List<String>> row = new HashMap<>();
                    List<String> singletonList = new ArrayList<>();
                    singletonList.add(topicAndCourseId);
                    row.put(className, singletonList);
                    mapByRow.put(timeslotLine, row);
                } else if (!mapByRow.get(timeslotLine).containsKey(className)) {
                    List<String> singletonList = new ArrayList<>();
                    singletonList.add(topicAndCourseId);
                    mapByRow.get(timeslotLine).put(className, singletonList);
                } else {
                    mapByRow.get(timeslotLine).get(className).add(topicAndCourseId);
                }
            }
        }
        for (GroupAssignment ga: gaSolution.getGroupAssignmentList()){
            String topicAndCourseId = ga.getCourseLevel().getTopic() + " - " + ga.getCourseTaskId();
            String className = ga.getClassGroup().getName();
            Integer timeslotLine = ga.getTimeslotLine();
            if (!mapByRow.containsKey(timeslotLine)) {
                Map<String, List<String>> row = new HashMap<>();
                List<String> singletonList = new ArrayList<>();
                singletonList.add(topicAndCourseId);
                row.put(className, singletonList);
                mapByRow.put(timeslotLine, row);
            } else if (!mapByRow.get(timeslotLine).containsKey(className)) {
                List<String> singletonList = new ArrayList<>();
                singletonList.add(topicAndCourseId);
                mapByRow.get(timeslotLine).put(className, singletonList);
            } else {
                mapByRow.get(timeslotLine).get(className).add(topicAndCourseId);
            }
        }
        return mapByRow;
    }

    public Map<Integer, Map<String, List<String>>> generateMapByRow(){
        Map<Integer, Map<String, List<String>>> mapByRow = new HashMap<>();
        for (LessonAssignment lessonAssignment: this.scheduleSolution.getLessonAssignmentList()){
            if (lessonAssignment instanceof FixedTaskLesson){
                for (ClassGroup classGroup: ((FixedTaskLesson) lessonAssignment).getCourseLevel().getClassGroupList()) {
                    String topicAndCourseId = ((FixedTaskLesson) lessonAssignment).getCourseLevel().getTopic() + " - " + lessonAssignment.getCourseTaskId();
                    String className = classGroup.getName();
                    Integer timeslotLine = lessonAssignment.getTimeslotLine();
                    if (!mapByRow.containsKey(timeslotLine)) {
                        Map<String, List<String>> row = new HashMap<>();
                        List<String> singletonList = new ArrayList<>();
                        singletonList.add(topicAndCourseId);
                        row.put(className, singletonList);
                        mapByRow.put(timeslotLine, row);
                    } else if (!mapByRow.get(timeslotLine).containsKey(className)) {
                        List<String> singletonList = new ArrayList<>();
                        singletonList.add(topicAndCourseId);
                        mapByRow.get(timeslotLine).put(className, singletonList);
                    } else {
                        mapByRow.get(timeslotLine).get(className).add(topicAndCourseId);
                    }
                }
            } else {
                for (GroupPerCourse groupPerCourse : this.scheduleSolution.getGroupPerCourseList()) {
                    if (Objects.equals(groupPerCourse.getCourseTaskId(), lessonAssignment.getCourseTaskId())) {
                        // topic - lvlID: e.g. LO - 1334
                        String topicAndCourseId = groupPerCourse.getCourseLevel().getTopic() + " - " + groupPerCourse.getCourseTaskId();
                        String className = groupPerCourse.getClassGroup().getName();
                        Integer timeslotLine = lessonAssignment.getTimeslotLine();
                        if (!mapByRow.containsKey(timeslotLine)) {
                            Map<String, List<String>> row = new HashMap<>();
                            List<String> singletonList = new ArrayList<>();
                            singletonList.add(topicAndCourseId);
                            row.put(className, singletonList);
                            mapByRow.put(timeslotLine, row);
                        } else if (!mapByRow.get(timeslotLine).containsKey(className)) {
                            List<String> singletonList = new ArrayList<>();
                            singletonList.add(topicAndCourseId);
                            mapByRow.get(timeslotLine).put(className, singletonList);
                        } else {
                            mapByRow.get(timeslotLine).get(className).add(topicAndCourseId);
                        }
                    }
                }
            }
        }
        return mapByRow;
    }

    public FixedTaskLessonSolution placeTheFixedTaskLessons() throws ExecutionException, InterruptedException {
        List<CourseLevel> clListFTL = new ArrayList<>(); // CourseLvls for FTLSolution;
        List<FixedTaskLesson> ftlList = new ArrayList<>(); // assignments for FTLSolution

        for (CourseLevel cl: this.scheduleSolution.getCourseLevelList()){
            if (cl.getRelatedTaskIds().size() == 1){ //CONSIDER ONLY THE FIXED COMBINATIONS
                clListFTL.add(cl);
            }
        }

        for (LessonAssignment la: this.scheduleSolution.getLessonAssignmentList()){
            if (la instanceof FixedTaskLesson){
                ftlList.add((FixedTaskLesson) la); //SHOULD NOT GIVE ERRORS: FTL's have cl's from clListFTL
            }
        }

        FixedTaskLessonSolution ftlSolution = new FixedTaskLessonSolution(this.scheduleSolution.getClassGroupList(),
                clListFTL,
                ftlList,
                this.scheduleSolution.getTimeLineList());
        //updateNumberOfLessons(ftlSolution.getClassGroupList(), ftlSolution.getFixedTaskLessonList()); //DANGER UPDATE CLASSGROUPS
        for (ClassGroup cl: ftlSolution.getClassGroupList()){
            System.out.println(cl.getName() +" ofLessonSize: "+ cl.getNumberOfLessons());
        }
        final Long SINGLETON_FTL_ID = 2L;
        // TODO: FROM RESOURCE INSTEAD OF FROM FILE??
        File file = new File("C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\src\\main\\resources\\org\\opta\\groupScheduler\\solver\\ftlSolverConfig.xml");
        SolverConfig solverConfig = SolverConfig.createFromXmlFile(file);
        SolverFactory solverFactory = SolverFactory.create(solverConfig);
        SolverManager<FixedTaskLessonSolution, Long> solverManagerFTL = SolverManager.create(solverConfig, new SolverManagerConfig());
        SolutionManager<FixedTaskLessonSolution, HardMediumSoftScore> scoreManagerFTL = SolutionManager.create(solverFactory);

        SolverJob<FixedTaskLessonSolution, Long> solverJob = solverManagerFTL.solve(SINGLETON_FTL_ID, ftlSolution);
        ftlSolution = solverJob.getFinalBestSolution();
        solverManagerFTL.close();

        return ftlSolution;

    }

    public void readGapsFromFTLSolution(String source){
        FTLJacksonSolutionFileIO ioFTL = new FTLJacksonSolutionFileIO();
        FixedTaskLessonSolution ftlSolution = ioFTL.read(new File(source));
        Map<Integer, List<ClassGroup>> filled = new HashMap<>();
        for (FixedTaskLesson ftl: ftlSolution.getFixedTaskLessonList()){
            Integer timeslot = ftl.getTimeslotLine();
            if (! filled.containsKey(timeslot)){
                filled.put(timeslot, new ArrayList<>());
            }
            filled.get(timeslot).addAll(ftl.getClassGroupList());
        }

        Map<Integer, List<ClassGroup>> gaps = new HashMap<>();
        for (Integer timeslot: filled.keySet()){
            List<ClassGroup> complement= new ArrayList<>(ftlSolution.getClassGroupList());
            complement.removeAll(filled.get(timeslot));
            gaps.put(timeslot, complement);
            System.out.println(timeslot + ": " + complement);
        }
    }

    public void generateAndSaveFTLSolution(String source, String nameFile) throws ExecutionException, InterruptedException {
        FixedTaskLessonSolution ftlSolution = placeTheFixedTaskLessons();
        FTLJacksonSolutionFileIO ioFTL = new FTLJacksonSolutionFileIO();
        ioFTL.write(ftlSolution, new File(source, nameFile + "FTL_placed.json"));
    }


    private void initializeGroupAssignmentsFromFTLSolution(FixedTaskLessonSolution ftlSolution){
        Map<String, Set<Integer>> gapMap = generateTimeGapsPerClassGroup(ftlSolution);
        for (GroupAssignment ga: this.groupAssignmentSolution.getGroupAssignmentList()){
            Set<Integer> gaps = new HashSet<>(gapMap.get(ga.getClassGroup().getName()));
            ga.setPossibleTimeslotLines(gaps.stream().toList());
        }
    }

    private Map<String, Set<Integer>> generateTimeGapsPerClassGroup(FixedTaskLessonSolution ftlSolution){
        Map<String, Set<Integer>> filledMap = new HashMap<>();
        Map<String, Set<Integer>> gapMap = new HashMap<>();
        int max = ftlSolution.getFixedTaskLessonList()
                .stream()
                .flatMap(ftl->ftl.getClassGroupList().stream())
                .mapToInt(ClassGroup::getNumberOfLessons)
                .max()
                .getAsInt();
        for (FixedTaskLesson ftl: ftlSolution.getFixedTaskLessonList()){
            Integer timeslot = ftl.getTimeslotLine();
            for (ClassGroup classGroup: ftl.getClassGroupList()) {
                if (!filledMap.containsKey(classGroup.getName())) {
                    filledMap.put(classGroup.getName(), new HashSet<>());
                }
                filledMap.get(classGroup.getName()).add(timeslot);
            }
        }
        //TAKE COMPLEMENTS
        Set<Integer> allTimeslots = IntStream.range(1, max + 1).boxed().collect(Collectors.toSet());
        for (String classGroupName: filledMap.keySet()){
            Set<Integer> complement = new HashSet<>(allTimeslots);
            complement.removeAll(filledMap.get(classGroupName));
            gapMap.put(classGroupName, complement);
        }
        return gapMap;
    }

    private Map<String, Set<Integer>> generatePossibleClassGroups(CourseLevel cl, Map<String, Set<Integer>> gapMap){
        List<String> classGroupNames = new ArrayList<>();
        Map<String, Integer> classSizeMap= new HashMap<>();
        for (ClassGroup classGroup: cl.getClassGroupList()){
            classGroupNames.add(classGroup.getName());
            classSizeMap.put(classGroup.getName(),classGroup.getSize());
        }
        classGroupNames.sort(String::compareTo);
        Map<String, Set<Integer>> possibleClassGroups = recursionGenerateClassGroups(new HashMap<>(),
                classGroupNames,
                cl,
                gapMap,
                classSizeMap);

        return possibleClassGroups;
    }

    private Map<String, Set<Integer>> recursionGenerateClassGroups(Map<String, Set<Integer>> possibleClassGroups,
                                                                   List<String> classGroupList,
                                                                   CourseLevel cl,
                                                                   Map<String, Set<Integer>> gapMap,
                                                                   Map<String, Integer> classSizeMap){
        if (classGroupList.isEmpty()){
            return possibleClassGroups;
        }
        String nextClassGroup = classGroupList.get(0);
        Set<String> oldKeySet = new HashSet<>(possibleClassGroups.keySet());
        for (String combination: oldKeySet){
            String[] classGroups = combination.split("&#");
            int newCombinationSize = Arrays.stream(classGroups)
                    .mapToInt(classSizeMap::get)
                    .reduce(0, Integer::sum) + classSizeMap.get(nextClassGroup);
            if (newCombinationSize <= cl.getUpperBoundClassSize()){
                Set<Integer> newAllowedTimeslots = gapMap.get(nextClassGroup)
                        .stream()
                        .filter(possibleClassGroups.get(combination)::contains)
                    .   collect(Collectors.toSet());
                if(newAllowedTimeslots.size()>= cl.getNumberOfAssignments() ) {
                    possibleClassGroups.put(combination +"&#"+nextClassGroup, newAllowedTimeslots);
                }
            }
        }
        //Add also next as separate combination //TODO reduce smallGroups
        //No check is needed for timeslotSize
        possibleClassGroups.put(nextClassGroup, new HashSet<>(gapMap.get(nextClassGroup)));
        return recursionGenerateClassGroups(possibleClassGroups,
                classGroupList.subList(1, classGroupList.size()),
                cl,
                gapMap,
                classSizeMap);
    }

    //
    private List<String> recGenOfTimeLineCombinations(String start, Integer times, List<Integer> timeLines){
        if (times ==0){
            List<String> singleton = new ArrayList<>(1);
            singleton.add(start);
            return singleton;
        }

        List<String> combinations = new ArrayList<>();
        for (int tlIndex =0; tlIndex < timeLines.size() - times + 1; tlIndex++ ){
            String newtlComb = start +"&#"+ timeLines.get(tlIndex).toString();
            combinations.addAll(recGenOfTimeLineCombinations(newtlComb, times - 1, timeLines.subList(tlIndex+1, timeLines.size())));
        }
        return combinations;
    }
    public List<CourseLevelBlock> generateBlockFromPossibleClassGroups(Integer numberOfTimeLines,
                                                                        IndexGenerator indexGenerator,
                                                                        Map<String, Set<Integer>> possibleClassGroups){
        List<CourseLevelBlock> clBlocks = new ArrayList<>();
        for (String combination: possibleClassGroups.keySet()){
            List<Integer> timeLines = new ArrayList<>(possibleClassGroups
                    .get(combination)
                    .stream()
                    .toList());
            timeLines.sort(Integer::compareTo);
            List<String> possibleTimeLineCombinations = recGenOfTimeLineCombinations(
                    "",
                    numberOfTimeLines,
                    timeLines);
            for (String timeLineComb: possibleTimeLineCombinations){
                List<String> classGroupNames = new ArrayList<>(List.of(combination.split("&#")));
                List<Integer> timeLineList = Stream.of(timeLineComb.split("&#"))
                        .filter(str-> !Objects.equals(str, ""))
                        .map(Integer::valueOf)
                        .toList();
                clBlocks.add(new CourseLevelBlock(indexGenerator.getNewIndex(), timeLineList , classGroupNames));
            }
        }
        return clBlocks;
    }


    public void initializeCGAFromFTLSolution(FixedTaskLessonSolution ftlSolution){
        Map<String, Set<Integer>> gapMapPerClass = generateTimeGapsPerClassGroup(ftlSolution);
        IndexGenerator indexGenerator = new IndexGenerator();
        //GENERATE CL_BLOCKS
        for (ClassGroupAssignment cla: this.classGroupAssignmentSolution.getGroupAssignmentList()){
            CourseLevel cl  = cla.getCourseLevel();
            System.out.println("Start generating groupCombinations");
            Map<String, Set<Integer>> possibleClassGroups = generatePossibleClassGroups(cl, gapMapPerClass);
            System.out.println("Finished generating groupCombinations with size: "+possibleClassGroups.keySet().size());
            System.out.println("Start generating blocks");
            List<CourseLevelBlock> clBlocks = generateBlockFromPossibleClassGroups(cl.getNumberOfAssignments(),
                    indexGenerator,
                    possibleClassGroups);
            /*
            for (CourseLevelBlock clb: clBlocks){
                System.out.println(clb);
            }
             */
            cla.setPossibleCLBS(clBlocks);
            System.out.println(cl.getTopic() +" of size:"+ clBlocks.size());
        }
    }

    public void placeCGAFromFTLSolution(String source) throws ExecutionException, InterruptedException {
        FTLJacksonSolutionFileIO ioFTL = new FTLJacksonSolutionFileIO();
        FixedTaskLessonSolution ftlSolution = ioFTL.read(new File(source));
        initializeCGAFromFTLSolution(ftlSolution);

        Long SINGLETON_CGA_ID =4L;
        File file = new File("C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\src\\main\\resources\\org\\opta\\groupScheduler\\solver\\classGroupAssignmentSolverConfig.xml");
        SolverConfig solverConfig = SolverConfig.createFromXmlFile(file);
        SolverFactory solverFactory = SolverFactory.create(solverConfig);
        SolverManager<ClassGroupAssignmentSolution, Long> solverManagerUFTL = SolverManager.create(solverConfig, new SolverManagerConfig());
        SolutionManager<ClassGroupAssignmentSolution, HardSoftScore> scoreManagerUFTL = SolutionManager.create(solverFactory);

        SolverJob<ClassGroupAssignmentSolution, Long> solverJob = solverManagerUFTL.solve(SINGLETON_CGA_ID, this.classGroupAssignmentSolution);
        ClassGroupAssignmentSolution cgaSolution = solverJob.getFinalBestSolution();
        ScoreExplanation<ClassGroupAssignmentSolution, HardSoftScore> explanation = scoreManagerUFTL.explain(cgaSolution);

        Map<String, ConstraintMatchTotal<HardSoftScore>> map2 = explanation.getConstraintMatchTotalMap();
        System.out.println("ConstraintMatch");
        for (String o: map2.keySet()){
            System.out.println(map2.get(o).getConstraintName());
            for (ConstraintMatch<HardSoftScore> cm : map2.get(o).getConstraintMatchSet()){
                if (! cm.getScore().isFeasible()) {
                    System.out.println(cm.getConstraintName());
                    System.out.println(cm.getIndictedObjectList());
                    System.out.println(cm.getScore());
                }
            }
        }

        solverManagerUFTL.close();
        Map<String, List<String>> classToInfoTimeslot = new HashMap<>();
        for (ClassGroupAssignment cga: cgaSolution.getGroupAssignmentList()) {
            CourseLevelBlock clb = cga.getCLB();
            for (String classGroupName : clb.getClassGroups()) {
                if (!classToInfoTimeslot.containsKey(classGroupName)) {
                    classToInfoTimeslot.put(classGroupName, new ArrayList<>());
                }
                for (Integer timeslotLine: clb.getTimeslotLines()) {
                    classToInfoTimeslot.get(classGroupName).add(cga.getCourseLevel().getTopic() + "- task:" + cga.getCourseLevel().getId() + "- on:" + timeslotLine);
                }
            }
        }
        for (String clg: classToInfoTimeslot.keySet()) {
            System.out.println(clg+ " - "+classToInfoTimeslot.get(clg));
        }



    }

    public void generateAndSaveGASolution(String sourceFTL, String destination, String nameFile) throws ExecutionException, InterruptedException {
        GroupAssignmentSolution gaSolution = placeGroupsFromFTLSolution(sourceFTL);
        GAJacksonSolutionFileIO ioGA = new GAJacksonSolutionFileIO();
        ioGA.write(gaSolution, new File(destination, nameFile + "GA_placed.json"));
    }

    public GroupAssignmentSolution placeGroupsFromFTLSolution(String source) throws ExecutionException, InterruptedException {
        FTLJacksonSolutionFileIO ioFTL = new FTLJacksonSolutionFileIO();
        FixedTaskLessonSolution ftlSolution = ioFTL.read(new File(source));
        initializeGroupAssignmentsFromFTLSolution(ftlSolution);
        for (GroupAssignment ga: this.groupAssignmentSolution.getGroupAssignmentList()){
            System.out.println(ga.getPossibleTimeslotLines());
            System.out.println(ga.getClassGroup().getName() + " - " +ga.getCourseLevel() + " - "+ga.getClassGroup());
        }

        Long SINGLETON_GA_ID =3L;
        File file = new File("C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\src\\main\\resources\\org\\opta\\groupScheduler\\solver\\groupAssignmentSolverConfig.xml");
        SolverConfig solverConfig = SolverConfig.createFromXmlFile(file);
        SolverFactory solverFactory = SolverFactory.create(solverConfig);
        SolverManager<GroupAssignmentSolution, Long> solverManagerFTL = SolverManager.create(solverConfig, new SolverManagerConfig());
        SolutionManager<GroupAssignmentSolution, HardSoftScore> scoreManagerFTL = SolutionManager.create(solverFactory);

        SolverJob<GroupAssignmentSolution, Long> solverJob = solverManagerFTL.solve(SINGLETON_GA_ID, this.groupAssignmentSolution);
        GroupAssignmentSolution gaSolution = solverJob.getFinalBestSolution();
        ScoreExplanation<GroupAssignmentSolution, HardSoftScore> explanation = scoreManagerFTL.explain(gaSolution);

        Map<String, ConstraintMatchTotal<HardSoftScore>> map2 = explanation.getConstraintMatchTotalMap();
        System.out.println("ConstraintMatch");
        for (String o: map2.keySet()){
            System.out.println(map2.get(o).getConstraintName());
            for (ConstraintMatch<HardSoftScore> cm : map2.get(o).getConstraintMatchSet()){
                if (! cm.getScore().isFeasible()){
                    System.out.println(cm.getConstraintName());
                    System.out.println(cm.getIndictedObjectList());
                    System.out.println(cm.getScore());
                }
            }
        }

        solverManagerFTL.close();
        Map<ClassGroup, List<String>> classToTimeslot = new HashMap<>();
        for (GroupAssignment groupAssignment: gaSolution.getGroupAssignmentList()){
            if (!classToTimeslot.containsKey(groupAssignment.getClassGroup())){
                classToTimeslot.put(groupAssignment.getClassGroup(), new ArrayList<>());
            }
            classToTimeslot.get(groupAssignment.getClassGroup()).add(groupAssignment.getCourseLevel().getTopic() +"- task:"+groupAssignment.getCourseTaskId() + "- on:"+ groupAssignment.getTimeslotLine());
        }
        for (ClassGroup cl: classToTimeslot.keySet()) {
            System.out.println(cl.getName()+ " - "+classToTimeslot.get(cl));
        }
        return gaSolution;

    }

    public GroupScheduleSolution getScheduleSolution() {
        return scheduleSolution;
    }

    public List<String> getGroupNames(){
        Set<String> groupNames = new HashSet<>();
        for (GroupPerCourse gp: this.scheduleSolution.getGroupPerCourseList()){
            groupNames.add(gp.getClassGroup().getName());
        }
        return new ArrayList<>(groupNames);
    }

    public List<String> getTopicNames(){
        Set<String> topicNames = new HashSet<>();
        for (CourseLevel cl: this.scheduleSolution.getCourseLevelList()){
            topicNames.add(cl.getTopic());
        }
        return new ArrayList<>(topicNames);
    }

    private void updateNumberOfLessons(List<ClassGroup> classGroupList, List<FixedTaskLesson> ftLessonList){
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
