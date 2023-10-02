package org.opta.coreScheduler.domain;

import java.util.HashSet;
import java.util.Set;

public class Course {

    //key
    private int code;

    private String name;
    private Set<Teacher> teachers;
    private Set<StudentGroup> studentGroups;

    public Course(int code, String name, Set<Teacher> teachers, Set<StudentGroup> studentGroups) {
        this.code = code;
        this.name = name;
        this.teachers = new HashSet<Teacher>(teachers);
        this.studentGroups = new HashSet<StudentGroup>(studentGroups);
    }

    //GETTERS
    public String getName() {
        return name;
    }

    public Set<Teacher> getTeachers() {
        return teachers;
    }

    public Set<StudentGroup> getStudentGroups() {
        return studentGroups;
    }

    /* TODO - fetch timeslots
        public List<Timeslot> getTimeslots(){
        Set<Timeslot> timeslotSet= new HashSet<>();
        Set<Grid> gridSet = new HashSet<>();
        studentGroups.forEach(studentGroup -> {gridSet.add(studentGroup.getGrid());});
        for (Grid grid:gridSet){
            timeslotSet.addAll(grid.getTimeslotList());
        }
    }*/

    //INSERTING - REMOVING
    public void addTeacher(Teacher teacher){
        this.teachers.add(teacher);
    }

    public void addStudentGroup(StudentGroup st){
        this.studentGroups.add(st);
    }

    public void removeTeacher(Teacher teacher){
        this.teachers.remove(teacher); //TODO: ERROR IF not contained?
    }

}
