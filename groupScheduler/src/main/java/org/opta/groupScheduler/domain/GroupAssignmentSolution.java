package org.opta.groupScheduler.domain;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@PlanningSolution
public class GroupAssignmentSolution {
    private List<ClassGroup> classGroupList;
    private List<CourseLevel> courseLevelList;
    private List<GroupAssignment> groupAssignmentList;
    private HardSoftScore score;

    public GroupAssignmentSolution(){}
    public GroupAssignmentSolution(List<ClassGroup> classGroupList, List<CourseLevel> courseLevelList, List<GroupAssignment> groupAssignmentList) {
        this.classGroupList = classGroupList;
        this.courseLevelList = courseLevelList;
        this.groupAssignmentList = groupAssignmentList;
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
    public List<GroupAssignment> getGroupAssignmentList() {
        return groupAssignmentList;
    }

    @PlanningScore
    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}
