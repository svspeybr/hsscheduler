package org.opta.coreScheduler.domain;


import java.util.*;

public class Grid {

    //Id
    private final String name;

    //LEFT Integers corresponds with DayOfWeek (dayIndex: 1== Monday, 2==Tuesday, etc.., 8(extra))
    //SECOND Integer with positions in timeslotList
    //e.g. 4-> 3: at Thursday there are 3 slots
    private final Map<Integer, Integer> countSlotsADay;
    private final List<Timeslot> timeslotList;


    //CONSTRUCTOR
    public Grid(String name){
        this.name = name;
        this.countSlotsADay = new HashMap<>(7);
        this.timeslotList = new ArrayList<>();
    }

    //ADJUSTING
    public void addSlot(Timeslot ts){
        int dayIndex = ts.getDay().getValue();

        int index = Collections.binarySearch(this.timeslotList, ts, Timeslot::compareTo);
        if (index < 0){
            index = -index - 1; //binarySearch returns: -1 - indexWhereItShouldBePlaced
        }
        this.timeslotList.add(index, ts);

        if (! this.countSlotsADay.containsKey(dayIndex)){
            this.countSlotsADay.put(dayIndex, 0);
        }
        this.countSlotsADay.put(dayIndex, this.countSlotsADay.get(dayIndex) + 1);
    }

    //GETTERS
    public String getName() {
        return name;
    }

    public int getSlotNumbers(int dayIndex){
        return this.countSlotsADay.get(dayIndex);
    }

    public List<Timeslot> getTimeslotList(){
        return this.timeslotList;
    }

    public Timeslot getTimeslot(int dayIndex, int hourIndex){ //hourIndex = position (- 1) of timeslot in the day
        int count = 0; //
        for (int i = 1; i <= dayIndex; i++){
            if (this.countSlotsADay.containsKey(i)) {
                count += this.countSlotsADay.get(i);
            }
        }
        return this.timeslotList.get(count -1 + hourIndex);
    }

    //Check
    public Boolean hasTimeslotsAtDay(int dayIndex){
        return this.countSlotsADay.containsKey(dayIndex);
    }

    public Boolean hasTimeslotAtTime(int dayIndex, int hourIndex){
        return this.countSlotsADay.containsKey(dayIndex) && hourIndex >= 0 && hourIndex < this.countSlotsADay.get(dayIndex);
    }

}
