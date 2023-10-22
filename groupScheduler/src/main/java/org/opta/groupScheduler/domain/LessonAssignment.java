package org.opta.groupScheduler.domain;

import com.fasterxml.jackson.annotation.*;
import org.opta.groupScheduler.domain.solver.LessonAssignmentDifficultyComparator;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.lang.annotation.Inherited;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "LAid")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type") //How does this work???
@JsonSubTypes({
        @JsonSubTypes.Type(value = FixedTaskLesson.class, name = "fixedTask"),
        @JsonSubTypes.Type(value = UnfixedTaskLesson.class, name = "unfixedTask"),
})
@PlanningEntity(difficultyComparatorClass = LessonAssignmentDifficultyComparator.class)
public abstract class LessonAssignment {

    @JsonProperty("LAid")
    protected Integer id;
    @JsonProperty("timeslot")
    protected Integer timeslotLine;

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
