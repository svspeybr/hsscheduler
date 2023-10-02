package org.opta.groupScheduler.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public abstract class LessonAssignment {

    protected Integer id;
    private Integer timeslotLine;

    public Integer getId() {
        return id;
    }

    @PlanningVariable(valueRangeProviderRefs = {"timeslotRange"})
    public Integer getTimeslotLine() {
        return timeslotLine;
    }

    public void setTimeslotLine(Integer timeslotLine) {
        this.timeslotLine = timeslotLine;
    }

    public abstract Integer getCourseTaskId();

    @Override
    public String toString(){
        return this.id + " - " + getCourseTaskId();
    }
}
