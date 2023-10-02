package org.opta.groupScheduler.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

import java.util.Set;

@PlanningEntity
public class FixedTaskLesson extends LessonAssignment{

    private CourseLevel courseLevel;
    public FixedTaskLesson(){}

    public FixedTaskLesson(Integer id, CourseLevel courseLevel) {
        this.id = id;
        this.courseLevel = courseLevel;
    }

    public CourseLevel getCourseLevel() {
        return courseLevel;
    }

    //TODO courseLevel should have 1 taskID for fixedTaskLesson
    @Override
    public Integer getCourseTaskId(){
        return this.courseLevel.getRelatedTaskIds().get(0);
    }

}
