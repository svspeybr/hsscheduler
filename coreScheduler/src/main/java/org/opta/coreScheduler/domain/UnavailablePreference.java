package org.opta.coreScheduler.domain;

public class UnavailablePreference {

    int id;

    Teacher teacher;
    Timeslot timeslot;

    public UnavailablePreference(int id, Teacher teacher, Timeslot timeslot) {
        this.id = id;
        this.teacher = teacher;
        this.timeslot = timeslot;
    }

    //GETTERS
    public Teacher getTeacher() {
        return teacher;
    }

    public Timeslot getTimeslot() {
        return timeslot;
    }

}
