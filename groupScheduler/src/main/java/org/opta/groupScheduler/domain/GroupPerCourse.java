package org.opta.groupScheduler.domain;

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
    public GroupPerCourse(Integer id, ClassGroup classGroup, CourseLevel courseLevel) {
        this.id = id;
        this.classGroup = classGroup;
        this.courseLevel = courseLevel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @PlanningVariable(valueRangeProviderRefs = {"courseIds"})
    public Integer getCourseTaskId() {
        return courseTaskId;
    }
    @ValueRangeProvider(id="courseIds")
    public List<Integer> getPossibleTaskIds() {
        return this.courseLevel.getRelatedTaskIds();
    }


    public void setCourseTaskId(Integer courseTaskId) {
        this.courseTaskId = courseTaskId;
    }

    public ClassGroup getClassGroup() {
        return classGroup;
    }

    public void setClassGroup(ClassGroup classGroup) {
        this.classGroup = classGroup;
    }

    public CourseLevel getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(CourseLevel courseLevel) {
        this.courseLevel = courseLevel;
    }

    public Integer getSize(){return this.classGroup.getSize();}

    @Override
    public String toString(){
        return this.classGroup.getName() + " - old " + this.courseLevel.toString();
    }
}
