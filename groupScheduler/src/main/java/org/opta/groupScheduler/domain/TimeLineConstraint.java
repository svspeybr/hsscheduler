package org.opta.groupScheduler.domain;

//REDUNDANT
public class TimeLineConstraint {

    private final Integer id;
    private Integer limit;

    public TimeLineConstraint(Integer id, Integer limit) {
        this.id = id;
        this.limit = limit;
    }

    public Integer getId() {
        return id;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
