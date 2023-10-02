package org.opta.groupScheduler.domain;

import java.util.ArrayList;
import java.util.List;

public class CourseLevel {

    private Integer id;

    private List<Integer> relatedTaskIds;

    private Integer upperBoundClassSize;

    private String topic;

    private List<ClassGroup> classGroupList;

    public CourseLevel(){}
    public CourseLevel(Integer id, String topic, List<Integer> relatedTaskIds, Integer upperBoundClassSize) {
        this.id = id;
        this.topic = topic;
        this.relatedTaskIds = new ArrayList<>(relatedTaskIds);
        this.upperBoundClassSize = upperBoundClassSize;
    }

    public List<ClassGroup> getClassGroupList() {
        return classGroupList;
    }

    public void setClassGroupList(List<ClassGroup> classGroupList) {
        this.classGroupList = new ArrayList<>(classGroupList);
    }

    public List<Integer> getRelatedTaskIds() {
        return relatedTaskIds;
    }

    public Integer getUpperBoundClassSize(){return this.upperBoundClassSize;}

    public Integer getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString(){
        return "cl - " + this.id +" - " + this.topic;
    }
}
