package org.opta.coreScheduler.generator;

import org.opta.coreScheduler.domain.Grid;
import org.opta.coreScheduler.domain.StudentGroup;
import org.opta.coreScheduler.domain.Timeslot;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Generator {

    public Generator(){}
    public void getTimeslots(List<Grid> gridList) {
        List<DayOfWeek> weekList = new ArrayList<>();
        weekList.add(DayOfWeek.MONDAY);
        weekList.add(DayOfWeek.TUESDAY);
        weekList.add(DayOfWeek.WEDNESDAY);
        weekList.add(DayOfWeek.THURSDAY);
        weekList.add(DayOfWeek.FRIDAY);

        List<Integer> indexSingleton= new ArrayList<>(1); // for generating timeslots
        indexSingleton.add(0); //reset Index

        // 2nd & 3rd GRADE:
        Grid thirdGradeGrid = new Grid("grade23");
        //1ste GRADE;
        Grid firstGradeGrid = new Grid("grade1");

        for (int i = 0; i < 5; i++){

            thirdGradeGrid.addSlot(new Timeslot(newIndex(indexSingleton),
                    LocalTime.of(8, 55),
                    LocalTime.of(9, 45),
                    weekList.get(i)));
            firstGradeGrid.addSlot(new Timeslot(newIndex(indexSingleton),
                    LocalTime.of(8, 35),
                    LocalTime.of(9, 25),
                    weekList.get(i)));

            Timeslot hour2nd = new Timeslot(newIndex(indexSingleton),
                    LocalTime.of(9, 45),
                    LocalTime.of(10, 35),
                    weekList.get(i));
            thirdGradeGrid.addSlot(hour2nd);
            firstGradeGrid.addSlot(hour2nd);

            Timeslot hour3rd = new Timeslot(newIndex(indexSingleton),
                    LocalTime.of(10, 35),
                    LocalTime.of(11, 25),
                    weekList.get(i));
            firstGradeGrid.addSlot(hour3rd);

            if (i != 2) {//NOT wednesday
                //3RD HOUR
                thirdGradeGrid.addSlot(new Timeslot(newIndex(indexSingleton),
                        LocalTime.of(10, 35),
                        LocalTime.of(12, 25), //INCLUSIVE PAUSE
                        weekList.get(i)));
                //4TH HOUR
                thirdGradeGrid.addSlot(new Timeslot(newIndex(indexSingleton),
                        LocalTime.of(12, 25),
                        LocalTime.of(13, 15),
                        weekList.get(i)));
                firstGradeGrid.addSlot(new Timeslot(newIndex(indexSingleton),
                        LocalTime.of(11, 25),
                        LocalTime.of(13, 15), //INCLUSIVE PAUSE
                        weekList.get(i)));

                //5TH HOUR
                Timeslot hour5th = new Timeslot(newIndex(indexSingleton),
                        LocalTime.of(13, 15),
                        LocalTime.of(14, 05),
                        weekList.get(i));
                thirdGradeGrid.addSlot(hour5th);
                firstGradeGrid.addSlot(hour5th);

                //6TH HOUR

                Timeslot hour6th = new Timeslot(newIndex(indexSingleton),
                        LocalTime.of(14, 05),
                        LocalTime.of(14, 55),
                        weekList.get(i));
                thirdGradeGrid.addSlot(hour6th);
                firstGradeGrid.addSlot(hour6th);

                //7th

                thirdGradeGrid.addSlot(new Timeslot(newIndex(indexSingleton),
                        LocalTime.of(15, 10),
                        LocalTime.of(16, 00),
                        weekList.get(i)));
                firstGradeGrid.addSlot(new Timeslot(newIndex(indexSingleton),
                        LocalTime.of(14, 55),
                        LocalTime.of(15, 45),
                        weekList.get(i)));
                //8th
                thirdGradeGrid.addSlot(new Timeslot(newIndex(indexSingleton),
                        LocalTime.of(16, 00),
                        LocalTime.of(16, 50),
                        weekList.get(i)));
                firstGradeGrid.addSlot(new Timeslot(newIndex(indexSingleton),
                        LocalTime.of(15, 45),
                        LocalTime.of(16, 35),
                        weekList.get(i)));
            } else { // Wednesday
                //3RD HOUR - 3RD GRADE
                thirdGradeGrid.addSlot(hour3rd);
                //4TH
                thirdGradeGrid.addSlot(new Timeslot(newIndex(indexSingleton),
                        LocalTime.of(11, 40),
                        LocalTime.of(12, 30),
                        weekList.get(i)));
                firstGradeGrid.addSlot(new Timeslot(newIndex(indexSingleton),
                        LocalTime.of(11, 25),
                        LocalTime.of(12, 15),
                        weekList.get(i)));
                //5TH
                thirdGradeGrid.addSlot(new Timeslot(newIndex(indexSingleton),
                        LocalTime.of(12, 30),
                        LocalTime.of(13, 20),
                        weekList.get(i)));
                firstGradeGrid.addSlot(new Timeslot(newIndex(indexSingleton),
                        LocalTime.of(12, 15),
                        LocalTime.of(13, 05),
                        weekList.get(i)));
            }
        }
        gridList.add(thirdGradeGrid);
        gridList.add(firstGradeGrid);
    }

    public void getStudentGroups(List<StudentGroup> studentGroupList, List<Grid> gridList) {
        Grid thirdGradeGrid = gridList.get(0);
        Grid firstGradeGrid = gridList.get(1);
        studentGroupList.add(new StudentGroup("5WEWIA", 17, 5, thirdGradeGrid));
        studentGroupList.add(new StudentGroup("5WEWIB", 3, 5, thirdGradeGrid));
        studentGroupList.add(new StudentGroup("5LAWIA", 2, 5, thirdGradeGrid));
        studentGroupList.add(new StudentGroup("5LAWIB", 2, 5, thirdGradeGrid));
        studentGroupList.add(new StudentGroup("5GRWI", 1, 5, thirdGradeGrid));
        studentGroupList.add(new StudentGroup("5ECWI", 2, 5, thirdGradeGrid));
        studentGroupList.add(new StudentGroup("6MTWE",8 , 6, thirdGradeGrid));
        studentGroupList.add(new StudentGroup("4HUM", 17, 4, thirdGradeGrid));
    }

    //******************************
    //HELPERS
    //******************************

    //FOR GENERATING TIMESLOTS
    private int newIndex(List<Integer> singleton) {
        singleton.set(0,singleton.get(0)+1);
        return singleton.get(0)+1;
    }

}
