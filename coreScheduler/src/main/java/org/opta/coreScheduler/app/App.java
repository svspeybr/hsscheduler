package org.opta.coreScheduler.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.opta.groupScheduler.reader.UntisTextReader;
import org.opta.groupScheduler.solver.PartitionSolver;


import java.util.HashSet;
import java.util.List;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        //TODO:  DO NOT USE Classgroup, CourseLevel, LessonAssignment in this module, Create convertor

        //GENERATE TODO: 2. READ PREFERENCES, TASK, ETC, FROM FILE
        /* for getResource to work: location name of App (rooted at 'java')
        needs to agree with location (rooted at 'resources') for gui.fxml */
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("gui.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TimeTable Optimization");
        stage.setScene(scene);
        stage.show();
    }


}