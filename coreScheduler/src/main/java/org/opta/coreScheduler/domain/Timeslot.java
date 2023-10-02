package org.opta.coreScheduler.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Comparator;

/*TODO: AFTERNOON, FORENOON
   --> IDEA? SET AFTERNOON/ FORENOON IN a 'DEFAULTSETTING OBJECT' AND
   --> CONSTRUCT A NON persistable variable BOOLEAN 'IS_AFTERNOON?'
   --> BEFORE OPTIMIZATION/ update the timeslots
 */

public class Timeslot implements Comparable<Timeslot> {

    private final int id;

    private LocalTime start;
    private LocalTime end;
    private DayOfWeek day;

    public Timeslot(int id, LocalTime start, LocalTime end, DayOfWeek day) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.day = day;
    }

    //GETTERS


    public int getId() {return id;}

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public DayOfWeek getDay() {
        return day;
    }

    //SETTERS


    public void setStart(LocalTime start) {
        this.start = start;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    //COMPARE
    @Override
    public int compareTo(Timeslot ts){
        return Comparator.comparing(Timeslot::getDay)
                .thenComparing(Timeslot::getDay)
                .thenComparing(Timeslot::getEnd)
                .compare(this, ts);
    }
}
