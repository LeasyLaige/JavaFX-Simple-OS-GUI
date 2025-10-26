package com.ambassadors.javafxsimpleosgui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CardinalOSController implements Initializable {

    @FXML private AnchorPane desktopPane;
    @FXML private VBox startMenu;
    @FXML private VBox terminalWindow;
    @FXML private HBox terminalTitleBar;

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        makeDraggable(terminalWindow, terminalTitleBar);

        desktopPane.setOnMouseClicked(event -> {
            if (startMenu.isVisible()) {
                startMenu.setVisible(false);
            }
        });

        startMenu.setOnMouseClicked(MouseEvent::consume);
    }

    @FXML
    private void handleStartClick(MouseEvent event) {
        startMenu.setVisible(!startMenu.isVisible());

        startMenu.toFront();

        event.consume();
    }

    private void makeDraggable(Node node, Node titleBar) {
        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX() - node.getLayoutX();
            yOffset = event.getSceneY() - node.getLayoutY();
            titleBar.setCursor(Cursor.CLOSED_HAND);
            node.toFront();
        });

        titleBar.setOnMouseDragged(event -> {
            node.setLayoutX(event.getSceneX() - xOffset);
            node.setLayoutY(event.getSceneY() - yOffset);
        });

        titleBar.setOnMouseReleased(event -> {
            titleBar.setCursor(Cursor.HAND);
        });

        titleBar.setOnMouseEntered(event -> {
            if (!event.isPrimaryButtonDown()) {
                titleBar.setCursor(Cursor.HAND);
            }
        });

        titleBar.setOnMouseExited(event -> {
            if (!event.isPrimaryButtonDown()) {
                titleBar.setCursor(Cursor.DEFAULT);
            }
        });
    }
}
