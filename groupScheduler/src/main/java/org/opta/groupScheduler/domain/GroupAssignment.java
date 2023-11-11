package org.opta.groupScheduler.domain;

import com.fasterxml.jackson.annotation.*;
import org.opta.groupScheduler.domain.solver.GroupAssignmentDifficultyComparator;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.List;
@PlanningEntity(difficultyComparatorClass = GroupAssignmentDifficultyComparator.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "GAid")
public class GroupAssignment {

    private Integer id;
    private ClassGroup classGroup;
    private CourseLevel courseLevel;
    @JsonProperty("allowedTimeslots")
    private List<Integer> allowedTimeslots;
    @JsonProperty("timeslot")
    private Integer timeslotLine;
    @JsonProperty("courseTaskId")
    private Integer courseTaskId;

    public GroupAssignment(){}
    @JsonCreator
    public GroupAssignment(@JsonProperty("GAid") Integer id,
                           @JsonProperty("CAid") ClassGroup classGroup,
                           @JsonProperty("Clid") CourseLevel courseLevel) {
        this.id = id;
        this.classGroup = classGroup;
        this.courseLevel = courseLevel;
    }

    @JsonGetter("GAid")
    public Integer getId() {
        return id;
    }

    @JsonSetter("GAid")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonGetter("courseTaskId")
    @PlanningVariable(valueRangeProviderRefs = {"courseIds"})
    public Integer getCourseTaskId() {
        return courseTaskId;
    }
    @JsonIgnore
    @ValueRangeProvider(id="courseIds")
    public List<Integer> getPossibleTaskIds() {
        return this.courseLevel.getRelatedTaskIds();
    }
    @JsonSetter("courseTaskId")
    public void setCourseTaskId(Integer courseTaskId) {
        this.courseTaskId = courseTaskId;
    }
    @JsonGetter("timeslot")
    @PlanningVariable(valueRangeProviderRefs = {"timeslotRange"})
    public Integer getTimeslotLine() {
        return timeslotLine;
    }

    @JsonGetter("allowedTimeslots")
    @ValueRangeProvider(id="timeslotRange")
    public List<Integer> getPossibleTimeslotLines() {
        return this.allowedTimeslots;
    }

    @JsonSetter("allowedTimeslots")
    public void setPossibleTimeslotLines(List<Integer> allowedTimeslots){
        this.allowedTimeslots = allowedTimeslots; }

    @JsonSetter("timeslot")
    public void setTimeslotLine(Integer timeslotLine) {
        this.timeslotLine = timeslotLine;
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
