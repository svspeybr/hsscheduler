package org.opta.groupScheduler.fileIO;

import org.opta.groupScheduler.domain.*;
import org.opta.groupScheduler.solver.PartitionSolver;

import java.io.File;
import java.util.*;


public class UntisTextReader {

    public static PartitionSolver readFromPath(int fromYear,
                                               Set<String> coursesToChange,
                                               String classesPath,
                                               String lessonsPath) throws Exception {
        List<ClassGroup> classGroupList = new ArrayList<>();
        List<CourseLevel> courseLevelList = new ArrayList<>();
        List<GroupPerCourse> groupPerCourseList = new ArrayList<>();
        List<LessonAssignment> lessonAssignmentList = new ArrayList<>();
        List<GroupAssignment> groupAssignmentList = new ArrayList<>();
        List<ClassGroupAssignment> classGroupAssignmentList = new ArrayList<>();
        //DATAFETCHING
        classGroupList.addAll(UntisTextReader.readClasses(classesPath, fromYear));
        UntisTextReader.readCourseLevels(lessonsPath,
                classGroupList,
                coursesToChange,
                courseLevelList,
                groupPerCourseList,
                lessonAssignmentList,
                groupAssignmentList,
                classGroupAssignmentList);
        //TODO - timelineReading

        List<Integer> timeLineList = new ArrayList<>();
        for (int i=1;i < 38; i++){
            timeLineList.add(i);
        }

        return new PartitionSolver(classGroupList,
                 courseLevelList,
                groupPerCourseList,
                lessonAssignmentList,
                timeLineList,
                groupAssignmentList,
                classGroupAssignmentList);
    }

    public static List<ClassGroup> readClasses(String classSource, Integer year) throws Exception {

        File file = new File(classSource);
        Scanner sc = new Scanner(file);
        List<ClassGroup> classes = new ArrayList<>();

        // Holds true till there is nothing to read
        while (sc.hasNextLine()){
            String line = sc.nextLine();
            List<String> lineList = Arrays.stream(line.split(",")).toList();
            String className = lineList.get(0); //LINES NOT EMPTY + FIRST accent is "+ second is number
            if (Character.isDigit(className.charAt(1)) && Integer.parseInt(String.valueOf(className.charAt(1))) == year){
                className = className.substring(1, className.length()-1);
                Integer classSize = Integer.parseInt(lineList.get(16));
                ClassGroup classgroup = new ClassGroup(className, classSize);
                classes.add(classgroup);
            }
        }
        return classes;
    }

    public static void readCourseLevels(String lessonSource,
                                        List<ClassGroup> classes,
                                        Set<String> coursesToChange,
                                        List<CourseLevel> courseLevels,
                                        List<GroupPerCourse> groupPerCourseList,
                                        List<LessonAssignment> lessonAssignmentList,
                                        List<GroupAssignment> groupAssignmentList,
                                        List<ClassGroupAssignment> classGroupAssignmentList) throws Exception {

        File file = new File(lessonSource);
        Scanner sc = new Scanner(file);
        Map<String, Set<Integer>> topicToIds =  new HashMap<>(); //NAMECOURSE e.g. GE-> to IDS OF TASK e.g. 1321, 1234
        Map<String, Set<ClassGroup>> topicToClassGroups = new HashMap<>();
        Map<String, ClassGroup> nameToClasses = new HashMap<>();
        for (ClassGroup cl: classes){
            nameToClasses.put(cl.getName(), cl);
        }

        List<Integer> indexSingleton= new ArrayList<>(1); // for generating timeslots
        indexSingleton.add(0); //reset Index
        List<Integer> indexSingletonGA= new ArrayList<>(1); // for generating timeslots
        indexSingletonGA.add(0); //reset Index
        List<Integer> indexSingletonCGA= new ArrayList<>(1); // for generating timeslots
        indexSingletonCGA.add(0); //reset Index

        Map<Integer, Set<ClassGroup>> idToClasses = new HashMap<>();
        Map<Integer, Integer> idToNumbOfAssignments = new HashMap<>();
        Map<Integer, String> idToTeacher = new HashMap<>();
        Map<Integer, CourseLevel> idToCourseLevel= new HashMap<>();
        Set<Integer> topicToChangeIds = new HashSet<>();

        while (sc.hasNextLine()){
            String line = sc.nextLine(); //READ LINE
            List<String> lineList = Arrays.stream(line.split(",")).toList(); //WORDS SEPARATED BY ","
            String className = lineList.get(4); // CLASSNAME AT position 5
            if (! className.isEmpty() &&
                    nameToClasses.containsKey(className.substring(1, className.length() - 1)) &&
                    !lineList.get(6).substring(1, lineList.get(6).length() - 1).equals("NEX") //IGNORE NederlandsExtra
            ){
                String topicName = lineList.get(6).substring(1, lineList.get(6).length()-1); // TOPIC_NAME AT POSITION 7
                String teacher = lineList.get(5).substring(1, lineList.get(5).length()-1);
                Integer id = Integer.parseInt(lineList.get(0));
                if (coursesToChange.contains(topicName)){
                    topicToChangeIds.add(id);
                }
                if (! idToTeacher.containsKey(id)){
                    idToTeacher.put(id, teacher);
                }
                if (! topicToIds.containsKey(topicName)){
                    topicToIds.put(topicName, new HashSet<>());
                    topicToClassGroups.put(topicName, new HashSet<>());
                }
                if (! idToClasses.containsKey(id)){
                    idToClasses.put(id, new HashSet<>());
                }
                if (! idToNumbOfAssignments.containsKey(id)){
                    idToNumbOfAssignments.put(id, Integer.parseInt(String.valueOf(lineList.get(10).charAt(0)))); //number of assignments e.g. 6.0000 WARNING ONE DIGIT IS TAKEN
                }

                topicToIds.get(topicName).add(id);
                ClassGroup clGroup = nameToClasses.get(className.substring(1, className.length() - 1));
                clGroup.setNumberOfLessons(clGroup.getNumberOfLessons()+idToNumbOfAssignments.get(id));
                idToClasses.get(id).add(clGroup);
                topicToClassGroups.get(topicName).add(clGroup);
            }
        }
        for (String topic: topicToIds.keySet()){
            if (! coursesToChange.contains(topic)){
                for (Integer id: topicToIds.get(topic)){
                    Integer upperBound = Integer.max(idToClasses.get(id).stream().map(ClassGroup::getSize).reduce(0, Integer::sum), 25);
                    CourseLevel cl = new CourseLevel(id, topic, idToTeacher.get(id), List.of(id), upperBound, idToNumbOfAssignments.get(id));
                    cl.setClassGroupList(new ArrayList<>(idToClasses.get(id)));
                    courseLevels.add(cl);
                    idToCourseLevel.put(id, cl);
                }
            } else { //TODO CHECK IF SAME MULTIPLICITY - now multiplicity from idList.get(0)
                List<Integer> idList = topicToIds.get(topic).stream().toList();
                Integer upperBound = idList.stream().map(id->idToClasses.get(id).stream().map(ClassGroup::getSize).reduce(0, Integer::sum)).max(Integer::compareTo).get();
                List<ClassGroup> selectedClassGroups = topicToClassGroups.get(topic).stream().toList();
                CourseLevel cl = new CourseLevel();
                for (Integer id: idList) {
                    cl = new CourseLevel(id, topic, idToTeacher.get(id), idList, Integer.max(upperBound, 25), idToNumbOfAssignments.get(id));
                    cl.setClassGroupList(selectedClassGroups);
                    courseLevels.add(cl);
                    classGroupAssignmentList.add(new ClassGroupAssignment(newIndex(indexSingletonCGA), cl));
                }
                for (ClassGroup classGroup: selectedClassGroups){
                    GroupPerCourse groupPerCourse = new GroupPerCourse(newIndex(indexSingleton), classGroup, cl);
                    groupPerCourseList.add(groupPerCourse);
                    for (int times = 0; times < idToNumbOfAssignments.get(idList.get(0)); times++){
                        GroupAssignment groupAssignment = new GroupAssignment(newIndex(indexSingletonGA), classGroup, cl);
                        groupAssignmentList.add(groupAssignment);
                    }
                }
            }
        }

        for (Integer id: idToNumbOfAssignments.keySet()){
            for (int times = 0; times < idToNumbOfAssignments.get(id); times++) {
                if (topicToChangeIds.contains(id)) {
                    lessonAssignmentList.add(new UnfixedTaskLesson(newIndex(indexSingleton), id));
                } else {
                    lessonAssignmentList.add(new FixedTaskLesson(newIndex(indexSingleton), idToCourseLevel.get(id)));
                }
            }
        }
    }

    //FOR GENERATING TIMESLOTS
    private static int newIndex(List<Integer> singleton) {
        singleton.set(0,singleton.get(0)+1);
        return singleton.get(0)+1;
    }

}
