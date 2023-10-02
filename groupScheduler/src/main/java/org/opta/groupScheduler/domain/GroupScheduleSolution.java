package org.opta.groupScheduler.domain;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@PlanningSolution
public class GroupScheduleSolution {

    private List<ClassGroup> classGroupList;
    private List<CourseLevel> courseLevelList;
    private List<GroupPerCourse> groupPerCourseList;

    private List<LessonAssignment> lessonAssignmentList;
    private List<Integer> timeLineList;

    private HardSoftScore score;

    public GroupScheduleSolution(){
    }
    public GroupScheduleSolution(List<ClassGroup> classGroupList,
                                 List<CourseLevel> courseLevelList,
                                 List<GroupPerCourse> groupPerCourseList,
                                 List<LessonAssignment> lessonAssignmentList,
                                 List<Integer> timeLineList){
        this.classGroupList = classGroupList;
        this.courseLevelList = courseLevelList;
        this.groupPerCourseList = groupPerCourseList;
        this.lessonAssignmentList = lessonAssignmentList;
        this.timeLineList = timeLineList;
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
    public List<GroupPerCourse> getGroupPerCourseList() {
        return groupPerCourseList;
    }

    @PlanningEntityCollectionProperty
    public List<LessonAssignment> getLessonAssignmentList() {
        return lessonAssignmentList;
    }
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id= "timeslotRange")
    public List<Integer> getTimeLineList() {
        return timeLineList;
    }

    @PlanningScore
    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}
