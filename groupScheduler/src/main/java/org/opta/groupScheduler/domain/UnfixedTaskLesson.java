package org.opta.groupScheduler.domain;

import com.fasterxml.jackson.annotation.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@JsonTypeName("unfixedTask")
public class UnfixedTaskLesson extends LessonAssignment{

    private Integer courseTaskId;

    public UnfixedTaskLesson(){}

    @JsonCreator
    public UnfixedTaskLesson(@JsonProperty("LAid") Integer id, @JsonProperty("CTid") Integer courseTaskId) {
        this.id = id;
        this.courseTaskId = courseTaskId;
    }

    @JsonGetter("CTid")
    @Override
    public Integer getCourseTaskId() {
        return this.courseTaskId;
    }

    @JsonSetter("CTid")
    public void setCourseTaskId(Integer courseTaskId) {
        this.courseTaskId = courseTaskId;
    }

}
