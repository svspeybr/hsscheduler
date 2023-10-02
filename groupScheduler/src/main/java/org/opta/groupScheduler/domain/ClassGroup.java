package org.opta.groupScheduler.domain;

public class ClassGroup {

    private String name;
    private Integer size;

    private Integer numberOfLessons;

    public ClassGroup(){}
    public ClassGroup(String name, Integer size) {
        this.name = name;
        this.size = size;
        this.numberOfLessons =0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getNumberOfLessons() {
        return numberOfLessons;
    }

    public void setNumberOfLessons(Integer numberOfLessons) {
        this.numberOfLessons = numberOfLessons;
    }
}
