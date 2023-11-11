package org.opta.coreScheduler.app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.opta.groupScheduler.fileIO.GroupJacksonSolutionFileIO;
import org.opta.groupScheduler.fileIO.UntisTextReader;
import org.opta.groupScheduler.solver.PartitionSolver;

import java.util.*;


public class ControllerTabPartition {

    private int visualCardsPerRow = 9; //8 cards visual without scrolling in a row
    private int visualCardsPerColumn = 20; //9 cards visual without scrolling in a column
    private double strokeWidth = 1;
    @FXML
    private ScrollPane tableRegionPartition;
    @FXML
    private VBox tablePanelRowsPartition;//Container of rows

    public void initialize() throws Exception {
        // TODO REMOVE GroupJacksonSolution, GroupPerCourse, GroupScheduleSolution
        GroupJacksonSolutionFileIO io = new GroupJacksonSolutionFileIO();
        PartitionSolver partitionSolver = io.createGroupSolverFrom("C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\data\\unsolved\\fifthYear.json");//FETCH SLOTS,STUDENTGROUPS, ...
        /* TODO CHANGE partitionSolver To Calculate first FTLSolution , then GASolution
        partitionSolver.partition(); //solve
         */
        String SOURCE_FTL ="C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\data\\unsolved\\fifthYearFTL_placed.json";
        String SOURCE_GA = "C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\groupScheduler\\data\\unsolved\\fifthYearGA_placed.json";
        Map<Integer, Map<String, List<String>>> mapByRow = partitionSolver.generateMapByRowFromSource(SOURCE_FTL, SOURCE_GA);
        List<String> columnLabels =  partitionSolver.getGroupNames(); //groupNames for columns
        List<String> colorLabels = partitionSolver.getTopicNames(); //Topics for color
        drawTablePerGroup(mapByRow, columnLabels, colorLabels); //DRAW TABLE for first GRID
    }


    private void drawTablePerGroup( Map<Integer, Map<String, List<String>>> mapByRow,
                                    List<String> columnLabels,
                                    List<String> colorLabels){ //

        this.visualCardsPerRow = columnLabels.size()+1;
        Map<String, Color> colorMap = makeColorMap(colorLabels);

        makeHeader(columnLabels,
                tableRegionPartition,// EXTRACT WIDTH AND HEIGHT
                tablePanelRowsPartition, // ADD THE HEADER TO THE CONTAINER (VBOX): 'TablePanelRows'
                Color.SEAGREEN, //FILL COLOR
                Color.BLACK); //BORDER COLOR

        for (Integer timeslotLine: mapByRow.keySet()){ //HOURS A DAY
            HBox row = new HBox();
            //ROW HEADER
            StackPane header = makeCard(tableRegionPartition,
                    1,
                    1,
                    timeslotLine.toString(),
                    Color.LIGHTBLUE,
                    Color.BLACK);
            row.getChildren().add(header);
            for (String studentGroupName: columnLabels){//STUDENTGROUPS
                if (mapByRow.get(timeslotLine).containsKey(studentGroupName)) {
                    VBox overlaps = new VBox();
                    List<String> topicAndLvls =mapByRow.get(timeslotLine).get(studentGroupName);
                    for (String topicAndLvl : topicAndLvls) {
                        String topic = Arrays.stream(topicAndLvl.split(" - ")).toList().get(0);
                        StackPane card = makeCard(tableRegionPartition, 1, (double) 1 / topicAndLvls.size(),topicAndLvl, colorMap.get(topic), Color.BLACK);
                        overlaps.getChildren().add(card);
                    }
                    row.getChildren().add(overlaps);
                } else {
                    StackPane card = makeCard(tableRegionPartition,1, 1, null, Color.LIGHTBLUE, Color.BLACK);
                    row.getChildren().add(card);
                }
            }
            tablePanelRowsPartition.getChildren().add(row);
        }
    }

    private PartitionSolver readFile() throws Exception {
        //READ
        String classSource = "C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\coreScheduler\\src\\data\\KLASSEN2324.TXT";
        String lessonSource = "C:\\Users\\simon\\Desktop\\Scheduling\\hsscheduler\\coreScheduler\\src\\data\\LESSON2223.TXT";

        PartitionSolver partitionSolver = UntisTextReader.readFromPath(5,
                new HashSet<>(List.of("LO", "GE", "GO", "ES", "AC")),
                classSource,
                lessonSource);
        return partitionSolver;
    }

    //TODO PARENT CLASS - makeheader, makeCard is already defined for ControllerTabDraft
    private void makeHeader(List<String> columnIds,
                            Region region, // E.g. Scrollable Pane is the region containing the container (VBOX)
                            Pane container, //E.G. VBOX contains rows
                            Color fillColor,
                            Color strokeColor) {//Set list of id's from entities as header for column

        HBox row = new HBox();
        row.getChildren().add(makeCard(region,1, 1, null, fillColor, strokeColor));//1 empty slot for Left upper corner
        columnIds.stream().forEach(id -> {
            StackPane card = makeCard(region,1, 1, id, fillColor, strokeColor);
            row.getChildren().add(card);
        });
        container.getChildren().add(row);
    }

    private Map<String, Color> makeColorMap(List<String> keyValues){
        Map<String, Color> colorMap = new HashMap<>();
        Integer step = 255 / keyValues.size();
        for (int times = 0; times < keyValues.size();times++){
            Color cl = Color.rgb(255 - times * step, (7* times * step) % 255, (5* times * step) % 255);
            colorMap.put(keyValues.get(times), cl);
        }
        return colorMap;
    }


    private StackPane makeCard(Region region, double scaleWidth, double scaleHeight, String text, Color fillColor, Color strokeColor) {
        double containerWidth = (region.getPrefWidth() + 5) * scaleWidth;
        double containerHeight = (region.getPrefHeight() + 1) * scaleHeight;
        StackPane stackPane = new StackPane();
        Rectangle rectangle = new Rectangle();
        rectangle.setFill(fillColor);
        rectangle.setStroke(strokeColor);
        rectangle.setStrokeWidth(strokeWidth);
        double rectangleWidth = (containerWidth/visualCardsPerRow) - strokeWidth ;
        rectangle.setWidth(rectangleWidth);
        double rectangleHeight = (containerHeight/visualCardsPerColumn) - strokeWidth;
        rectangle.setHeight(rectangleHeight);
        stackPane.getChildren().add(rectangle);
        if (text != null){
            Label labelId = new Label(text);
            labelId.setFont(new Font(labelId.getFont().getSize() * scaleHeight)); //TODO: dependence only on vertical line
            stackPane.getChildren().add(labelId);
        }
        return stackPane;
    }
}
