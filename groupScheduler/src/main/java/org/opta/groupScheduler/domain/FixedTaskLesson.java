package org.opta.groupScheduler.domain;

import com.fasterxml.jackson.annotation.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;

import java.util.Set;

@JsonTypeName("fixedTask")
@PlanningEntity
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

    @JsonIgnore
    //TODO courseLevel should have 1 taskID for fixedTaskLesson
    @Override
    public Integer getCourseTaskId(){
        return this.courseLevel.getRelatedTaskIds().get(0);
    }

}
