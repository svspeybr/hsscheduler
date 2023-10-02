package org.opta.coreScheduler.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.List;

@PlanningEntity
public class Lesson{

    private final int id;

    private Course course;
    private Timeslot timeslot;
    private Room room;
    private Boolean pinned;

    public Lesson(int id, Course course, Timeslot timeslot, Room room) {
        this.id = id;
        this.course = course;
        this.timeslot = timeslot;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @PlanningVariable(valueRangeProviderRefs = {"timeslotRange"})
    public Timeslot getTimeslot() {
        return timeslot;
    }

    @ValueRangeProvider(id = "timeslotRange")
    public List<Timeslot> getPossibleTimeslots(){
        return null;
    } //TODO

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    @PlanningVariable
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @PlanningPin
    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }




}