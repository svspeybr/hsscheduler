package org.opta.groupScheduler.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "CGid")
public class ClassGroup {

    @JsonProperty("CGid")
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
    @Override
    public String toString(){
        return "cg - " + this.name;
    }
}
