package com.ambassadors.javafxsimpleosgui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CardinalOSController implements Initializable {

    @FXML private AnchorPane desktopPane;
    @FXML private AnchorPane startMenu;
    @FXML private VBox terminalWindow;
    @FXML private VBox NotepadWindow;
    @FXML private HBox terminalTitleBar;
    @FXML private TextArea TerminalBox; 
    @FXML private Button NotepadButton;
    @FXML private Button TerminalButton;

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Set default terminal text
        TerminalBox.setText(
            "Welcome to CardinalOS Terminal [Version 0.1.0]\n" +
            "(c) 2025 CardinalOS Dev Team. All rights reserved.\n\n" +
            "C:\\>"
        );

        TerminalBox.positionCaret(TerminalBox.getText().length());

        desktopPane.widthProperty().addListener((obs, oldVal, newVal) -> centerTerminal());
        desktopPane.heightProperty().addListener((obs, oldVal, newVal) -> centerTerminal());
        centerTerminal();


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

    @FXML
    private void handleTerminalClick(MouseEvent event) {
        terminalWindow.setVisible(!terminalWindow.isVisible());

        terminalWindow.toFront();

        event.consume();
    }

    @FXML
    private void handleNotepadClick(MouseEvent event) {
        NotepadWindow.setVisible(!NotepadWindow.isVisible());

        NotepadWindow.toFront();

        event.consume();
    }

    @FXML
    private void handleClose(MouseEvent event) {
        terminalWindow.setVisible(false);
        if (isMaximized) {
            // Restore to previous size and position if maximized
            terminalWindow.setLayoutX(prevX);
            terminalWindow.setLayoutY(prevY);
            terminalWindow.setPrefWidth(prevW);
            terminalWindow.setPrefHeight(prevH);
            isMaximized = false;
        }
      
        centerTerminal();
    }

    @FXML
    private void handleMin(MouseEvent event) {
        terminalWindow.setVisible(!terminalWindow.isVisible());
        terminalWindow.toFront();
        event.consume();
    }

    private boolean isMaximized = false;
    private double prevX, prevY, prevW, prevH;

    @FXML
    private void handleMax(MouseEvent event) {
        if (!isMaximized) {
            // Save current position and size
            prevX = terminalWindow.getLayoutX();
            prevY = terminalWindow.getLayoutY();
            prevW = terminalWindow.getPrefWidth();
            prevH = terminalWindow.getPrefHeight();
            // Maximize within usable desktop area (below menu bar, above dock)
            terminalWindow.setLayoutX(0);
            terminalWindow.setLayoutY(40); // Below the 40px menu bar
            terminalWindow.setPrefWidth(desktopPane.getWidth());
            terminalWindow.setPrefHeight(desktopPane.getHeight() - 40 - 64); // Subtract menu (40) and dock (64)
            terminalWindow.toFront();
            isMaximized = true;
        } else {
            // Restore to previous size and position
            terminalWindow.setLayoutX(prevX);
            terminalWindow.setLayoutY(prevY);
            terminalWindow.setPrefWidth(prevW);
            terminalWindow.setPrefHeight(prevH);
            isMaximized = false;
        }
    }

    @FXML
    private void handleClose2(MouseEvent event) {
        NotepadWindow.setVisible(false);
        if (isMaximized) {
            // Restore to previous size and position if maximized
            NotepadWindow.setLayoutX(prevX);
            NotepadWindow.setLayoutY(prevY);
            NotepadWindow.setPrefWidth(prevW);
            NotepadWindow.setPrefHeight(prevH);
            isMaximized = false;
        }
      
        centerTerminal();
    }

    @FXML
    private void handleMin2(MouseEvent event) {
        NotepadWindow.setVisible(!NotepadWindow.isVisible());
        NotepadWindow.toFront();
        event.consume();
    }

    @FXML
    private void handleMax2(MouseEvent event) {
        if (!isMaximized) {
            // Save current position and size
            prevX = terminalWindow.getLayoutX();
            prevY = terminalWindow.getLayoutY();
            prevW = terminalWindow.getPrefWidth();
            prevH = terminalWindow.getPrefHeight();
            // Maximize within usable desktop area (below menu bar, above dock)
            NotepadWindow.setLayoutX(0);
            NotepadWindow.setLayoutY(40); // Below the 40px menu bar
            NotepadWindow.setPrefWidth(desktopPane.getWidth());
            NotepadWindow.setPrefHeight(desktopPane.getHeight() - 40 - 64); // Subtract menu (40) and dock (64)
            NotepadWindow.toFront();
            isMaximized = true;
        } else {
            // Restore to previous size and position
            NotepadWindow.setLayoutX(prevX);
            NotepadWindow.setLayoutY(prevY);
            NotepadWindow.setPrefWidth(prevW);
            NotepadWindow.setPrefHeight(prevH);
            isMaximized = false;
        }
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

    private void centerTerminal() {
        if (terminalWindow != null && desktopPane != null) {
            double centerX = (desktopPane.getWidth() - terminalWindow.getPrefWidth()) / 2;
            double centerY = (desktopPane.getHeight() - terminalWindow.getPrefHeight()) / 2;
            terminalWindow.setLayoutX(centerX);
            terminalWindow.setLayoutY(centerY);
        }

        if (NotepadWindow != null && desktopPane != null) {
            double centerX = (desktopPane.getWidth() - NotepadWindow.getPrefWidth()) / 2;
            double centerY = (desktopPane.getHeight() - NotepadWindow.getPrefHeight()) / 2;
            NotepadWindow.setLayoutX(centerX);
            NotepadWindow.setLayoutY(centerY);
        }
    }
}
