package org.opta.coreScheduler.app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.opta.coreScheduler.domain.Grid;
import org.opta.coreScheduler.domain.StudentGroup;
import org.opta.coreScheduler.generator.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerTabDraft {

    //TODO: 1.MAKE CONTROLLERS FOR EACH GRID
    //
    private List<Grid> gridList = new ArrayList<>(); //e.g. a grid for 1st grade and a grid for 2nd and 3rd grade combined
    private List<StudentGroup> studentGroupList = new ArrayList<>();
    //CARD PROPERTIES
    private int visualCardsPerRow = 8; //8 cards visual without scrolling in a row
    private int visualCardsPerColumn = 9; //9 cards visual without scrolling in a column
    private double strokeWidth = 1;

    @FXML
    private ScrollPane tableRegion;
    @FXML
    private VBox tablePanelRows;//Container of rows

    //INSTEAD OF USING 'implements Initialisable' with parameter URL and ResourceBundle
    public void initialize(){
        initializeTableElements();//FETCH SLOTS,STUDENTGROUPS, ...
        drawTablePerGroup(this.gridList.get(0)); //DRAW TABLE for first GRID
    }

    private void drawTablePerGroup(Grid grid){ // TODO: COMBINING MULTIPLE GRIDS?

        List<StudentGroup> filterStudentGroupList = this.studentGroupList
                .stream()
                .filter(st-> st.getGrid().equals(grid))
                .toList();
        int studentGroupsSize = filterStudentGroupList.size();

        makeHeader(filterStudentGroupList.stream().map(StudentGroup::getId).collect(Collectors.toList()),
                tableRegion,// EXTRACT WIDTH AND HEIGHT
                tablePanelRows, // ADD THE HEADER TO THE CONTAINER (VBOX): 'TablePanelRows'
                Color.SEAGREEN, //FILL COLOR
                Color.BLACK); //BORDER COLOR

        List<String> dutchDaysOfWeek = Arrays.asList("MA", "DI", "WO", "DO", "VR", "ZA", "ZO");

        for (int dayIndex = 1; dayIndex <= 7 ; dayIndex++){ //Days
            if (grid.hasTimeslotsAtDay(dayIndex)){
                int daySize = grid.getSlotNumbers(dayIndex);
                for (int hourIndex = 1; hourIndex <= daySize; hourIndex++){ //HOURS A DAY
                    HBox row = new HBox();
                    //ROW HEADER
                    StackPane header = makeCard(tableRegion,
                            dutchDaysOfWeek.get(dayIndex-1) + hourIndex,
                            Color.LIGHTBLUE,
                            Color.BLACK);
                    row.getChildren().add(header);
                    for (int j = 0; j < studentGroupsSize; j++){//STUDENTGROUPS
                        StackPane card = makeCard(tableRegion, null, Color.LIGHTBLUE, Color.BLACK);
                        //TODO: ADD ACTIVITIES
                        row.getChildren().add(card);
                    }
                    tablePanelRows.getChildren().add(row);
                }
            }
        }
    }

    private void initializeTableElements() {
        Generator generator = new Generator();
        generator.getTimeslots(this.gridList); //FIRST: TO GENERATE GRIDS FOR STUDENTGROUPS
        generator.getStudentGroups(this.studentGroupList, this.gridList); //SECOND
        Collections.sort(studentGroupList);
    }


    //******************************
    //HELPERS
    //******************************

    //Headers
    private void makeHeader(List<String> columnIds,
                            Region region, // E.g. Scrollable Pane is the region containing the container (VBOX)
                            Pane container, //E.G. VBOX contains rows
                            Color fillColor,
                            Color strokeColor) {//Set list of id's from entities as header for column

        HBox row = new HBox();
        row.getChildren().add(makeCard(region, null, fillColor, strokeColor));//1 empty slot for Left upper corner
        columnIds.stream().forEach(id -> {
            StackPane card = makeCard(region,id , fillColor, strokeColor);
            row.getChildren().add(card);
        });
        container.getChildren().add(row);
    }

    private StackPane makeCard(Region region, String text, Color fillColor, Color strokeColor) {
        double containerWidth = region.getPrefWidth();
        double containerHeight = region.getPrefHeight();
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
            stackPane.getChildren().add(labelId);
        }
        return stackPane;
    }

}
