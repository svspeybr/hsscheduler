package org.opta.groupScheduler.domain;

import com.fasterxml.jackson.annotation.*;
import org.opta.groupScheduler.domain.solver.LessonAssignmentDifficultyComparator;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.List;
import java.util.Set;

@JsonTypeName("fixedTask")
@PlanningEntity(difficultyComparatorClass = LessonAssignmentDifficultyComparator.class)
public class FixedTaskLesson extends LessonAssignment{

    private CourseLevel courseLevel;
    public FixedTaskLesson(){}

    @JsonCreator
    public FixedTaskLesson(@JsonProperty("LAid") Integer id, @JsonProperty("CLid") CourseLevel courseLevel) {
        this.id = id;
        this.courseLevel = courseLevel;
    }

    @JsonGetter("CLid")
    public CourseLevel getCourseLevel() {
        return courseLevel;
    }

    @Override
    @PlanningVariable(valueRangeProviderRefs = {"timeslotRange"})
    public Integer getTimeslotLine() {
        return this.timeslotLine;
    }

    @JsonIgnore
    //TODO courseLevel should have 1 taskID for fixedTaskLesson
    @Override
    public Integer getCourseTaskId(){
        return this.courseLevel.getRelatedTaskIds().get(0);
    }

    @JsonIgnore
    public List<ClassGroup> getClassGroupList(){
        return this.courseLevel.getClassGroupList();}

}
