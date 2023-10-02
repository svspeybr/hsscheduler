package org.opta.coreScheduler.app;

import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import org.opta.coreScheduler.domain.Grid;
import org.opta.coreScheduler.domain.Schedule;
import org.opta.coreScheduler.domain.StudentGroup;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.opta.coreScheduler.generator.Generator;
import javafx.fxml.FXMLLoader;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.SolutionManager;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;

import java.io.IOException;

public class Controller{

    //TODO SCOREMANAGER
    private static final String SOURCE= "org/opta/coreScheduler/solver/solverConfig.xml";

    SolverConfig solverConfig = SolverConfig.createFromXmlResource(SOURCE);
    SolverManager<Schedule, String> solverManager = SolverManager.create(solverConfig, new SolverManagerConfig());
    SolutionManager<Schedule, HardSoftScore> scoreManager;

    @FXML
    Tab tabDraft;
    @FXML
    Tab tabPartition;

    public void initialize() {
        FXMLLoader loader = new FXMLLoader();
        try{
            AnchorPane anch1 = loader.load(getClass().getResource("tabPartition.fxml"));
            tabPartition.setContent(anch1);
        } catch(IOException iex) {
            System.out.println("File not found");
        }
        loader = new FXMLLoader();
        try {
            AnchorPane anch2 = loader.load(getClass().getResource("tabDraft.fxml"));
            tabDraft.setContent(anch2);
        }
        catch(IOException iex)
        {
            System.out.println("File not found");
        }

    }




}