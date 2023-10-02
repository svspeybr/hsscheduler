package org.opta.coreScheduler.domain;

import java.util.Comparator;

public class StudentGroup implements Comparable<StudentGroup>{

    private final String id;

    private int size;
    private int year;

    private Grid grid;

    public StudentGroup(String id, int size, int year, Grid grid) {
        this.id = id;
        this.size = size;
        this.year = year;
        this.grid = grid;
    }

    //GETTERS
    public String getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public int getYear() {return year;}

    public Grid getGrid() {return grid;}

    //SETTERS
    public void setSize(int size) {
        this.size = size;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int compareTo(StudentGroup stg){
        return Comparator.comparing(StudentGroup::getYear)
                .thenComparing(StudentGroup::getId)
                .compare(this, stg);
    }
}
