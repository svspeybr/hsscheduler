package org.opta.groupScheduler.domain;

import com.fasterxml.jackson.annotation.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type") //How does this work???
@JsonSubTypes({
        @JsonSubTypes.Type(value = FixedTaskLesson.class, name = "fixedTask"),
        @JsonSubTypes.Type(value = UnfixedTaskLesson.class, name = "unfixedTask"),
})
@PlanningEntity
public abstract class LessonAssignment {

    @JsonProperty("LAid")
    protected Integer id;
    @JsonProperty("timeslot")
    private Integer timeslotLine;

    @JsonGetter("LAid")
    public Integer getId() {
        return id;
    }
    @JsonSetter("LAid")
    public void setId(Integer id) {this.id = id;}

    @JsonGetter("timeslot")
    @PlanningVariable(valueRangeProviderRefs = {"timeslotRange"})
    public Integer getTimeslotLine() {
        return timeslotLine;
    }

    @JsonSetter("timeslot")
    public void setTimeslotLine(Integer timeslotLine) {
        this.timeslotLine = timeslotLine;
    }

    public abstract Integer getCourseTaskId();

    @Override
    public String toString(){
        return "la -" + this.id + " - " + getCourseTaskId();
    }
}
