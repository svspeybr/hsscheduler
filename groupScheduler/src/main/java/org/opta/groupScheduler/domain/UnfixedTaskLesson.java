package org.opta.groupScheduler.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

public class UnfixedTaskLesson extends LessonAssignment{

    private Integer courseTaskId;

    public UnfixedTaskLesson(){}
    public UnfixedTaskLesson(Integer id, Integer courseTaskId) {
        this.id = id;
        this.courseTaskId = courseTaskId;
    }

    @Override
    public Integer getCourseTaskId() {
        return this.courseTaskId;
    }

    public void setCourseTaskId(Integer courseTaskId) {
        this.courseTaskId = courseTaskId;
    }

}
