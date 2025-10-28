package com.ambassadors.javafxsimpleosgui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class CardinalOSController implements Initializable {

    @FXML private AnchorPane desktopPane;
    @FXML private AnchorPane startMenu;
    @FXML private VBox terminalWindow;
    @FXML private VBox NotepadWindow;
    @FXML private HBox terminalTitleBar;
    @FXML private HBox notepadTitleBar;
    @FXML private TextArea TerminalBox;
    @FXML private TextArea NotepadBox;
    @FXML private Button NotepadButton;
    @FXML private Button TerminalButton;
    @FXML private Label clockLabel;
    @FXML private TextField searchField;
    @FXML private VBox searchResults;
    @FXML private ImageView restartImage;

    private double xOffset = 0;
    private double yOffset = 0;

    // Separate maximize states for each window
    private boolean terminalMaximized = false;
    private double terminalPrevX, terminalPrevY, terminalPrevW, terminalPrevH;

    private boolean notepadMaximized = false;
    private double notepadPrevX, notepadPrevY, notepadPrevW, notepadPrevH;

    private String currentDirectory = "C:\\Users\\Cardinal";
    private StringBuilder terminalHistory = new StringBuilder();

    // Searchable apps list
    private final String[][] availableApps = {
            {"Terminal", "📟 Terminal - Command line interface"},
            {"Notepad", "📝 Notepad - Text editor"},
            {"Settings", "⚙️ Settings - System preferences"},
            {"File Explorer", "📁 File Explorer - Browse files"}
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize terminal
        initializeTerminal();

        // Initialize clock
        updateClock();

        // Hide restart image initially
        restartImage.setVisible(false);

        // Center windows on startup
        desktopPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!terminalMaximized) centerWindow(terminalWindow);
            if (!notepadMaximized) centerWindow(NotepadWindow);
        });
        desktopPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (!terminalMaximized) centerWindow(terminalWindow);
            if (!notepadMaximized) centerWindow(NotepadWindow);
        });

        centerWindow(terminalWindow);
        centerWindow(NotepadWindow);

        // Make windows draggable
        makeDraggable(terminalWindow, terminalTitleBar);
        makeDraggable(NotepadWindow, notepadTitleBar);

        // Desktop click closes start menu
        desktopPane.setOnMouseClicked(event -> {
            if (startMenu.isVisible()) {
                startMenu.setVisible(false);
            }
        });

        startMenu.setOnMouseClicked(MouseEvent::consume);

        // Initialize search with all apps
        updateSearchResults("");
    }

    private void initializeTerminal() {
        terminalHistory.append("Welcome to CardinalOS Terminal [Version 0.1.8]\n");
        terminalHistory.append("(c) 2025 CardinalOS Dev Team. All rights reserved.\n\n");
        terminalHistory.append(currentDirectory).append(">");

        TerminalBox.setText(terminalHistory.toString());
        TerminalBox.positionCaret(TerminalBox.getText().length());

        // Handle Enter key for command processing
        TerminalBox.setOnKeyPressed(this::handleTerminalInput);
    }

    private void handleTerminalInput(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            event.consume();

            String fullText = TerminalBox.getText();
            int lastPromptIndex = fullText.lastIndexOf(">");

            if (lastPromptIndex != -1 && lastPromptIndex < fullText.length() - 1) {
                String command = fullText.substring(lastPromptIndex + 1).trim();
                processCommand(command);
            } else {
                appendToTerminal("");
            }
        }
    }

    private void processCommand(String command) {
        if (command.isEmpty()) {
            appendToTerminal("");
            return;
        }

        terminalHistory.append(command).append("\n");

        String[] parts = command.toLowerCase().split("\\s+");
        String cmd = parts[0];

        switch (cmd) {
            case "help":
                terminalHistory.append("Available commands:\n");
                terminalHistory.append("  help     - Show this help message\n");
                terminalHistory.append("  clear    - Clear the terminal\n");
                terminalHistory.append("  date     - Display current date and time\n");
                terminalHistory.append("  echo     - Display a message\n");
                terminalHistory.append("  dir      - List directory contents\n");
                terminalHistory.append("  cd       - Change directory\n");
                terminalHistory.append("  notepad  - Open Notepad\n");
                terminalHistory.append("  exit     - Close terminal\n");
                break;

            case "clear":
                terminalHistory.setLength(0);
                terminalHistory.append("Welcome to CardinalOS Terminal [Version 0.1.8]\n");
                terminalHistory.append("(c) 2025 CardinalOS Dev Team. All rights reserved.\n\n");
                break;

            case "date":
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                terminalHistory.append(LocalDateTime.now().format(formatter)).append("\n");
                break;

            case "echo":
                if (parts.length > 1) {
                    for (int i = 1; i < parts.length; i++) {
                        terminalHistory.append(parts[i]).append(" ");
                    }
                    terminalHistory.append("\n");
                } else {
                    terminalHistory.append("ECHO is on.\n");
                }
                break;

            case "dir":
                terminalHistory.append("Directory of ").append(currentDirectory).append("\n\n");
                terminalHistory.append("10/28/2025  02:30 PM    <DIR>          Documents\n");
                terminalHistory.append("10/28/2025  02:30 PM    <DIR>          Downloads\n");
                terminalHistory.append("10/28/2025  02:30 PM    <DIR>          Pictures\n");
                terminalHistory.append("10/28/2025  02:30 PM    <DIR>          Desktop\n");
                terminalHistory.append("               0 File(s)              0 bytes\n");
                terminalHistory.append("               4 Dir(s)\n");
                break;

            case "cd":
                if (parts.length > 1) {
                    if (parts[1].equals("..")) {
                        int lastSlash = currentDirectory.lastIndexOf("\\");
                        if (lastSlash > 2) {
                            currentDirectory = currentDirectory.substring(0, lastSlash);
                        }
                    } else {
                        currentDirectory = currentDirectory + "\\" + parts[1];
                    }
                }
                break;

            case "notepad":
                NotepadWindow.setVisible(true);
                NotepadWindow.toFront();
                terminalHistory.append("Opening Notepad...\n");
                break;

            case "exit":
                terminalWindow.setVisible(false);
                return;

            default:
                terminalHistory.append("'").append(command).append("' is not recognized as an internal or external command.\n");
                terminalHistory.append("Type 'help' for a list of commands.\n");
                break;
        }

        appendToTerminal("");
    }

    private void appendToTerminal(String text) {
        if (!text.isEmpty()) {
            terminalHistory.append(text).append("\n");
        }
        terminalHistory.append(currentDirectory).append(">");

        TerminalBox.setText(terminalHistory.toString());
        TerminalBox.positionCaret(TerminalBox.getText().length());
    }

    private void updateClock() {
        javafx.animation.Timeline clock = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.ZERO, e -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    if (clockLabel != null) {
                        clockLabel.setText(LocalDateTime.now().format(formatter));
                    }
                }),
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1))
        );
        clock.setCycleCount(javafx.animation.Animation.INDEFINITE);
        clock.play();
    }

    @FXML
    private void handleStartClick(MouseEvent event) {
        startMenu.setVisible(!startMenu.isVisible());
        startMenu.toFront();

        // Clear search and show all apps when opening
        if (startMenu.isVisible()) {
            searchField.clear();
            updateSearchResults("");
        }

        event.consume();
    }

    @FXML
    private void handleSearch(KeyEvent event) {
        String query = searchField.getText().toLowerCase().trim();
        updateSearchResults(query);
    }

    @FXML
    private void handleSleep(MouseEvent event) {
        System.out.println("Sleep mode activated...");
        // You can add visual effect here (fade out screen, etc.)
        // For simulation: dim the desktop
        desktopPane.setOpacity(0);
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(4));
        pause.setOnFinished(e -> desktopPane.setOpacity(1.0));
        pause.play();
        startMenu.setVisible(false);
    }

    @FXML
private void handleRestart(MouseEvent event) {
    System.out.println("Restarting CardinalOS...");
    // Close all windows
    terminalWindow.setVisible(false);
    NotepadWindow.setVisible(false);
    startMenu.setVisible(false);

    // Prepare restart image (centered, visible, but start with opacity 0)
    restartImage.setVisible(true);
    double imgWidth = restartImage.getImage().getWidth();
    double imgHeight = restartImage.getImage().getHeight();
    restartImage.setFitWidth(imgWidth);
    restartImage.setFitHeight(imgHeight);
    restartImage.setLayoutX((desktopPane.getWidth() - imgWidth) / 2);
    restartImage.setLayoutY((desktopPane.getHeight() - imgHeight) / 2);
    restartImage.setOpacity(0.0);  // Start invisible

    // Fade out desktop and fade in image simultaneously (1 second)
    javafx.animation.FadeTransition fadeOutDesktop = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1), desktopPane);
    fadeOutDesktop.setFromValue(1.0);
    fadeOutDesktop.setToValue(0.0);

    javafx.animation.FadeTransition fadeInImage = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1), restartImage);
    fadeInImage.setFromValue(0.0);
    fadeInImage.setToValue(1.0);

    javafx.animation.ParallelTransition parallelFadeOut = new javafx.animation.ParallelTransition(fadeOutDesktop, fadeInImage);
    parallelFadeOut.setOnFinished(e -> {
        // Reset terminal and notepad after fade-out
        initializeTerminal();
        NotepadBox.clear();

        // Fade in desktop and fade out image simultaneously (3 seconds)
        javafx.animation.FadeTransition fadeInDesktop = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(3), desktopPane);
        fadeInDesktop.setFromValue(0.0);
        fadeInDesktop.setToValue(1.0);

        javafx.animation.FadeTransition fadeOutImage = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(3), restartImage);
        fadeOutImage.setFromValue(1.0);
        fadeOutImage.setToValue(0.0);

        javafx.animation.ParallelTransition parallelFadeIn = new javafx.animation.ParallelTransition(fadeInDesktop, fadeOutImage);
        parallelFadeIn.setOnFinished(e2 -> {
            // Hide the restart image after fade-in completes
            restartImage.setVisible(false);
        });
        parallelFadeIn.play();
    });
    parallelFadeOut.play();
}



    @FXML
    private void handleShutdown(MouseEvent event) {
        System.out.println("Shutting down CardinalOS...");
        // Fade out animation
        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(1.5), desktopPane);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> {
            // Close the application
            javafx.application.Platform.exit();
            System.exit(0);
        });
        fade.play();
    }

    private void updateSearchResults(String query) {
        searchResults.getChildren().clear();

        for (String[] app : availableApps) {
            String appName = app[0];
            String appDescription = app[1];

            // Filter based on search query
            if (query.isEmpty() || appName.toLowerCase().contains(query) ||
                    appDescription.toLowerCase().contains(query)) {

                Button appButton = new Button(appDescription);
                appButton.setMaxWidth(Double.MAX_VALUE);
                appButton.getStyleClass().add("start-menu-item");
                appButton.setStyle("-fx-alignment: CENTER_LEFT; -fx-background-color: transparent; " +
                        "-fx-text-fill: white; -fx-padding: 8; -fx-cursor: hand;");

                // Handle click based on app name
                appButton.setOnMouseClicked(e -> {
                    switch (appName) {
                        case "Terminal":
                            handleTerminalClick(e);
                            break;
                        case "Notepad":
                            handleNotepadClick(e);
                            break;
                        case "Settings":
                        case "File Explorer":
                            System.out.println(appName + " clicked (not implemented yet)");
                            break;
                    }
                    startMenu.setVisible(false);
                });

                // Hover effect
                appButton.setOnMouseEntered(e ->
                        appButton.setStyle("-fx-alignment: CENTER_LEFT; -fx-background-color: rgba(255,255,255,0.1); " +
                                "-fx-text-fill: white; -fx-padding: 8; -fx-cursor: hand;"));
                appButton.setOnMouseExited(e ->
                        appButton.setStyle("-fx-alignment: CENTER_LEFT; -fx-background-color: transparent; " +
                                "-fx-text-fill: white; -fx-padding: 8; -fx-cursor: hand;"));

                searchResults.getChildren().add(appButton);
            }
        }

        // Show "No results" if nothing found
        if (searchResults.getChildren().isEmpty() && !query.isEmpty()) {
            Label noResults = new Label("No results found for '" + query + "'");
            noResults.setStyle("-fx-text-fill: gray; -fx-padding: 8;");
            searchResults.getChildren().add(noResults);
        }
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

    // Terminal window controls
    @FXML
    private void handleClose(MouseEvent event) {
        terminalWindow.setVisible(false);
        if (terminalMaximized) {
            restoreTerminal();
        }
    }

    @FXML
    private void handleMin(MouseEvent event) {
        terminalWindow.setVisible(false);
        event.consume();
    }

    @FXML
    private void handleMax(MouseEvent event) {
        if (!terminalMaximized) {
            maximizeTerminal();
        } else {
            restoreTerminal();
        }
    }

    private void maximizeTerminal() {
        terminalPrevX = terminalWindow.getLayoutX();
        terminalPrevY = terminalWindow.getLayoutY();
        terminalPrevW = terminalWindow.getPrefWidth();
        terminalPrevH = terminalWindow.getPrefHeight();

        terminalWindow.setLayoutX(0);
        terminalWindow.setLayoutY(40);
        terminalWindow.setPrefWidth(desktopPane.getWidth());
        terminalWindow.setPrefHeight(desktopPane.getHeight() - 40 - 64);
        terminalWindow.toFront();
        terminalMaximized = true;
    }

    private void restoreTerminal() {
        terminalWindow.setLayoutX(terminalPrevX);
        terminalWindow.setLayoutY(terminalPrevY);
        terminalWindow.setPrefWidth(terminalPrevW);
        terminalWindow.setPrefHeight(terminalPrevH);
        terminalMaximized = false;
    }

    // Notepad window controls
    @FXML
    private void handleClose2(MouseEvent event) {
        NotepadWindow.setVisible(false);
        if (notepadMaximized) {
            restoreNotepad();
        }
    }

    @FXML
    private void handleMin2(MouseEvent event) {
        NotepadWindow.setVisible(false);
        event.consume();
    }

    @FXML
    private void handleMax2(MouseEvent event) {
        if (!notepadMaximized) {
            maximizeNotepad();
        } else {
            restoreNotepad();
        }
    }

    private void maximizeNotepad() {
        notepadPrevX = NotepadWindow.getLayoutX();
        notepadPrevY = NotepadWindow.getLayoutY();
        notepadPrevW = NotepadWindow.getPrefWidth();
        notepadPrevH = NotepadWindow.getPrefHeight();

        NotepadWindow.setLayoutX(0);
        NotepadWindow.setLayoutY(40);
        NotepadWindow.setPrefWidth(desktopPane.getWidth());
        NotepadWindow.setPrefHeight(desktopPane.getHeight() - 40 - 64);
        NotepadWindow.toFront();
        notepadMaximized = true;
    }

    private void restoreNotepad() {
        NotepadWindow.setLayoutX(notepadPrevX);
        NotepadWindow.setLayoutY(notepadPrevY);
        NotepadWindow.setPrefWidth(notepadPrevW);
        NotepadWindow.setPrefHeight(notepadPrevH);
        notepadMaximized = false;
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

    private void centerWindow(Node window) {
        if (window != null && desktopPane != null) {
            double centerX = (desktopPane.getWidth() - ((VBox)window).getPrefWidth()) / 2;
            double centerY = (desktopPane.getHeight() - ((VBox)window).getPrefHeight()) / 2;
            window.setLayoutX(centerX);
            window.setLayoutY(centerY);
        }
    }
}
