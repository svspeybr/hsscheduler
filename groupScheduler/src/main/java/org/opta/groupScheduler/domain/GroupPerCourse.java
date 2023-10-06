package org.opta.groupScheduler.domain;

import com.fasterxml.jackson.annotation.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.List;

@PlanningEntity
public class GroupPerCourse {

    private Integer id;
    private ClassGroup classGroup;

    private Integer courseTaskId;
    private CourseLevel courseLevel;

    public GroupPerCourse(){

    }
    @JsonCreator
    public GroupPerCourse(@JsonProperty("GPCid") Integer id, @JsonProperty("CGid") ClassGroup classGroup, @JsonProperty("Clid") CourseLevel courseLevel) {
        this.id = id;
        this.classGroup = classGroup;
        this.courseLevel = courseLevel;
    }

    @JsonGetter("GPCid")
    public Integer getId() {
        return id;
    }

    @JsonSetter("GPCid")
    public void setId(Integer id) {
        this.id = id;
    }

    @PlanningVariable(valueRangeProviderRefs = {"courseIds"})
    public Integer getCourseTaskId() {
        return courseTaskId;
    }
    @JsonIgnore
    @ValueRangeProvider(id="courseIds")
    public List<Integer> getPossibleTaskIds() {
        return this.courseLevel.getRelatedTaskIds();
    }


    public void setCourseTaskId(Integer courseTaskId) {
        this.courseTaskId = courseTaskId;
    }

    @JsonGetter("CGid")
    public ClassGroup getClassGroup() {
        return classGroup;
    }
    @JsonSetter("CGid")
    public void setClassGroup(ClassGroup classGroup) {
        this.classGroup = classGroup;
    }

    @JsonGetter("CLid")
    public CourseLevel getCourseLevel() {
        return courseLevel;
    }
    @JsonSetter("CLid")
    public void setCourseLevel(CourseLevel courseLevel) {
        this.courseLevel = courseLevel;
    }

    @JsonIgnore
    public Integer getSize(){return this.classGroup.getSize();}

    @Override
    public String toString(){
        return this.classGroup.getName() + " - old " + this.courseLevel.toString();
    }
}
