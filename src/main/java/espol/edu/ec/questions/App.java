package espol.edu.ec.questions;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import util.DecisionTree;


/**
 * JavaFX App
 */

public class App extends Application {

    private DecisionTree decisionTree;
    private DecisionTree.DecisionTreeNode currentNode;
    private Label questionLabel;
    private TextField numQuestionsField;
    private Button yesButton;
    private Button noButton;
    private Button startGameButton;
    private Button animalThoughtButton;
    private Stage primaryStage;
    
    private List<String> questionHistory = new ArrayList<>();
    private TextArea historyArea;
    
    private int gamesPlayed = 0;
    private int correctGuesses = 0;
    private int totalQuestionsAsked = 0;
    private int maxQuestions;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        stage.setTitle("Árbol de Decisiones - Juego de Adivinanzas");

        ImageView logo = null;
        try {
            String workingDir = System.getProperty("user.dir");
            String dataDir = workingDir + "/data/logode20preguntas.jpeg";
            File file = new File(dataDir);
            Image image = new Image(file.toURI().toString());
            if (image.isError()) {
                System.out.println("Error loading image: " + image.getException());
            } else {
                logo = new ImageView(image);
                logo.setFitWidth(260);  
                logo.setFitHeight(260);  
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (logo == null) {
            logo = new ImageView();
        }

        Label instructionLabel = new Label("Piensa en un animal");
        instructionLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-text-fill: #444;-fx-font-weight: bold;");
        instructionLabel.setAlignment(Pos.CENTER);

        animalThoughtButton = new Button("Animal Pensado");
        applyButtonStyle(animalThoughtButton);

        animalThoughtButton.setOnAction(e -> {
            startGameButton.setDisable(false);
            animalThoughtButton.setDisable(true);
        });

        numQuestionsField = new TextField();
        numQuestionsField.setPromptText("Número máximo de preguntas");
        numQuestionsField.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        startGameButton = new Button("Iniciar Juego");
        applyButtonStyle(startGameButton);
        startGameButton.setDisable(true);
        startGameButton.setOnAction(e -> {
            try {
                int maxQuestions = Integer.parseInt(numQuestionsField.getText().trim());
                if (maxQuestions <= 0) {
                    showAlert("Error", "Ingresa un número válido mayor que cero.");
                } else {
                    startGame(maxQuestions);
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Por favor, ingresa un número válido de preguntas.");
            }
        });

        historyArea = new TextArea();
        historyArea.setEditable(false);
        historyArea.setPrefHeight(100);
        historyArea.setWrapText(true);

        VBox layout = new VBox(20, logo, instructionLabel, animalThoughtButton, numQuestionsField, startGameButton, historyArea);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, null,
                        new Stop(0, Color.web("#f0f4f7")),
                        new Stop(1, Color.web("#d9e2ec"))
                ),
                CornerRadii.EMPTY, Insets.EMPTY
        )));
        layout.setStyle("-fx-font-family: 'Arial';");

        Scene scene = new Scene(layout, 500, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void applyButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10;-fx-font-weight: bold;-fx-font-family: 'Arial';");
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.25)); 
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        button.setEffect(shadow);

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10;");
            button.setScaleX(1.05);  
            button.setScaleY(1.05);
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10;");
            button.setScaleX(1);  
            button.setScaleY(1);
        });
    }

    private void startGame(int maxQuestions) {
        decisionTree = new DecisionTree("data/preguntas.txt", "data/respuestas.txt");
        currentNode = decisionTree.getRoot();
        questionHistory.clear(); 
        gamesPlayed++; 
        totalQuestionsAsked = 0;  // Reiniciar el conteo de preguntas para este juego.
        this.maxQuestions = maxQuestions; // Almacenar el máximo de preguntas permitidas.
        openGameWindow();
    }

    private void openGameWindow() {
        Stage gameStage = new Stage();
        gameStage.setTitle("Juego de Adivinanzas");

        questionLabel = new Label("Pregunta inicial");
        questionLabel.setWrapText(true);
        questionLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-font-weight: bold;");
        questionLabel.setAlignment(Pos.CENTER);

        yesButton = new Button("Sí");
        noButton = new Button("No");

        applyButtonStyle(yesButton);
        applyButtonStyle(noButton);

        yesButton.setOnAction(e -> handleYes(gameStage));
        noButton.setOnAction(e -> handleNo(gameStage));

        HBox buttonBox = new HBox(20, yesButton, noButton);
        buttonBox.setAlignment(Pos.CENTER);  

        VBox gameLayout = new VBox(20, questionLabel, buttonBox);
        gameLayout.setPadding(new Insets(20));
        gameLayout.setAlignment(Pos.CENTER);
        gameLayout.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, null,
                        new Stop(0, Color.web("#f0f4f7")),
                        new Stop(1, Color.web("#d9e2ec"))
                ),
                CornerRadii.EMPTY, Insets.EMPTY
        )));
        gameLayout.setStyle("-fx-font-family: 'Arial';");

        Scene gameScene = new Scene(gameLayout, 400, 300);
        gameStage.setScene(gameScene);
        gameStage.show();

        updateQuestion(); 
    }

    private void updateQuestion() {
        if (currentNode != null && !currentNode.isLeaf() && totalQuestionsAsked < maxQuestions) {
            questionLabel.setText(currentNode.getQuestionOrAnimal());
            questionHistory.add(currentNode.getQuestionOrAnimal());
            yesButton.setDisable(false);
            noButton.setDisable(false);
        } else {
            List<String> possibleAnimals = getPossibleAnimals(currentNode);
            if (possibleAnimals.isEmpty()) {
                showAlert("Resultado", "No se encontró un animal con estas características.");
            } else {
                String message = "No estoy seguro, pero el animal pensado podría ser uno de los siguientes: " + String.join(", ", possibleAnimals);
                showAlert("Resultado", message);
            }
            endGame();
        }
    }

   private List<String> getPossibleAnimals(DecisionTree.DecisionTreeNode node) {
        List<String> animals = new ArrayList<>();
        if (node.isLeaf()) {
            // Añadir sólo si el nodo hoja no contiene la frase "No hay animal que coincida"
            if (!node.getQuestionOrAnimal().equals("No hay animal que coincida.")) {
                animals.add(node.getQuestionOrAnimal());
            }
        } else {
            if (node.getYesBranch() != null) {
                animals.addAll(getPossibleAnimals(node.getYesBranch()));
            }
            if (node.getNoBranch() != null) {
                animals.addAll(getPossibleAnimals(node.getNoBranch()));
            }
        }
        return animals;
    }

    private void handleYes(Stage gameStage) {
        if (totalQuestionsAsked < maxQuestions) {
            if (currentNode.getYesBranch() != null) {
                currentNode = currentNode.getYesBranch();
                totalQuestionsAsked++;
                updateQuestion();
            } else {
                showAlert("Resultado", "No se encontró un animal con estas características.");
                endGame();
            }
        } else {
            showAlert("Resultado", "Se alcanzó el límite de preguntas.");
            endGame();
        }
    }

    private void handleNo(Stage gameStage) {
        if (totalQuestionsAsked < maxQuestions) {
            if (currentNode.getNoBranch() != null) {
                currentNode = currentNode.getNoBranch();
                totalQuestionsAsked++;
                updateQuestion();
            } else {
                showAlert("Resultado", "No se encontró un animal con estas características.");
                endGame();
            }
        } else {
            showAlert("Resultado", "Se alcanzó el límite de preguntas.");
            endGame();
        }
    }

    private void endGame() {
        yesButton.setDisable(true);
        noButton.setDisable(true);
        showPlayAgainButton();
        showQuestionHistory();
        showStatistics(); 
    }

    private void showPlayAgainButton() {
        Button playAgainButton = new Button("Volver a Jugar");
        applyButtonStyle(playAgainButton);

        playAgainButton.setOnAction(e -> {
            primaryStage.show();  
            Stage gameStage = (Stage) playAgainButton.getScene().getWindow();
            gameStage.close();  
            resetMainWindow();  
        });

        VBox parent = (VBox) questionLabel.getParent();
        parent.getChildren().add(playAgainButton);
    }

    private void resetMainWindow() {
        animalThoughtButton.setDisable(false);
        startGameButton.setDisable(true);
        numQuestionsField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showQuestionHistory() {
        String history = String.join("\n", questionHistory);
        historyArea.setText(history);
    }

    private void showStatistics() {
        String statisticsMessage = String.format(
            "Juegos jugados: %d\nAdivinanzas correctas: %d\nPreguntas totales realizadas: %d",
            gamesPlayed, correctGuesses, totalQuestionsAsked
        );
        showAlert("Estadísticas del Juego", statisticsMessage);
    }

    public static void main(String[] args) {
        launch();
    }
}