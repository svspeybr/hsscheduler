package org.opta.groupScheduler.domain;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.ArrayList;
import java.util.List;

@PlanningSolution
public class FixedTaskLessonSolution {

    private List<ClassGroup> classGroupList;
    private List<CourseLevel> courseLevelList;
    private List<FixedTaskLesson> fixedTaskLessonList;
    private List<Integer> timeLineList;
    private List<List<FixedTaskLesson>> possibleFullRows;
    private HardMediumSoftScore score;

    public FixedTaskLessonSolution(){
    }
    public FixedTaskLessonSolution(List<ClassGroup> classGroupList,
                                   List<CourseLevel> courseLevelList,
                                   List<FixedTaskLesson> fixedTaskLessonList,
                                   List<Integer> timeLineList){
        this.classGroupList = classGroupList;
        this.courseLevelList = courseLevelList;
        this.fixedTaskLessonList = fixedTaskLessonList;
        this.timeLineList = timeLineList;
        this.possibleFullRows = new ArrayList<>(); //TODO: REMOVE
    }
    @ProblemFactCollectionProperty
    public List<List<FixedTaskLesson>> getPossibleFullRows() {return possibleFullRows;}

    public void setPossibleFullRows(List<List<FixedTaskLesson>> possibleFullRows) {
        this.possibleFullRows = possibleFullRows;
    }

    @ProblemFactCollectionProperty
    public List<ClassGroup> getClassGroupList() {
        return classGroupList;
    }

    @ProblemFactCollectionProperty
    public List<CourseLevel> getCourseLevelList() {
        return courseLevelList;
    }

    @PlanningEntityCollectionProperty
    public List<FixedTaskLesson> getFixedTaskLessonList() {
        return fixedTaskLessonList;
    }
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id= "timeslotRange")
    public List<Integer> getTimeLineList() {
        return timeLineList;
    }

    @PlanningScore
    public HardMediumSoftScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftScore score) {
        this.score = score;
    }
}
