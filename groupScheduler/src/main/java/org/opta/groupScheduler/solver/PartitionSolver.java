package org.opta.groupScheduler.solver;

import org.opta.groupScheduler.domain.*;
import org.optaplanner.core.api.score.ScoreExplanation;
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

public class PartitionSolver {

    //DATAFETCHING
    private List<ClassGroup> classGroupList;
    private List<CourseLevel> courseLevelList;
    private List<GroupPerCourse> groupPerCourseList;
    private List<LessonAssignment> lessonAssignmentList;

    private List<Integer> timeLineList;

    private GroupScheduleSolution scheduleSolution;

    public PartitionSolver(List<ClassGroup> classGroupList,
                           List<CourseLevel> courseLevelList,
                           List<GroupPerCourse> groupPerCourseList,
                           List<LessonAssignment> lessonAssignmentList,
                           List<Integer> timeLineList) {
        this.classGroupList = classGroupList;
        this.courseLevelList = courseLevelList;
        this.groupPerCourseList = groupPerCourseList;
        this.lessonAssignmentList = lessonAssignmentList;
        this.timeLineList =  timeLineList;
        this.scheduleSolution = new GroupScheduleSolution(this.classGroupList,
                this.courseLevelList,
                this.groupPerCourseList,
                this.lessonAssignmentList,
                this.timeLineList);
    }

    public void partition() { //TODO ??READING PARAMETERS ClassGroupList
        try {
            final Long SINGLETON_TIME_TABLE_ID = 1L;
            //SOLVER
            //String SOURCE = "org.opta.groupScheduler\\solver\\solverConfig.xml"; TODO: FROM RESOURCE INSTEAD OF FROM FILE??
            File file = new File("C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\src\\main\\resources\\org.opta.groupScheduler\\solver\\solverConfig.xml");
            SolverConfig solverConfig = SolverConfig.createFromXmlFile(file);
            SolverFactory solverFactory = SolverFactory.create(solverConfig);
            SolverManager<GroupScheduleSolution, Long> solverManager = SolverManager.create(solverConfig, new SolverManagerConfig());
            SolutionManager<GroupScheduleSolution, HardSoftScore> scoreManager = SolutionManager.create(solverFactory);

            SolverJob<GroupScheduleSolution, Long> solverJob = solverManager.solve(SINGLETON_TIME_TABLE_ID, this.scheduleSolution);
            this.scheduleSolution = solverJob.getFinalBestSolution();
            ScoreExplanation<GroupScheduleSolution, HardSoftScore> scoreExplanation =scoreManager.explain(this.scheduleSolution);

            /*System.out.println("Indictment");
            for (Object o: map.keySet()){
                System.out.println(o);
                System.out.println(map.get(o).getIndictedObject().toString());
                System.out.println(map.get(o).getJustificationList());
            }*/

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



}
