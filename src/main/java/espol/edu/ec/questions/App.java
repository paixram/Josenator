package espol.edu.ec.questions;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
        totalQuestionsAsked = 0;
        this.maxQuestions = maxQuestions;
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
        } else if (currentNode != null && currentNode.isLeaf()) {
            showAlert("Resultado", "El animal que pensaste es: " + currentNode.getQuestionOrAnimal());
            endGame();
        } else {
            List<String> possibleAnimals = getPossibleAnimals(currentNode);
            if (possibleAnimals.size() == 1) {
                // Mostrar un mensaje personalizado cuando solo quede un animal
                showPossibleAnimals(possibleAnimals, true);
            } else if (possibleAnimals.isEmpty()) {
                showAddAnimalPrompt();
            } else {
                showPossibleAnimals(possibleAnimals, false);
            }
            endGame();
        }
    }
    
    private void showFinalAnimalMessage(String animalName) {
        Stage messageStage = new Stage();
        messageStage.setTitle("¡Adiviné tu animal!");

        Label messageLabel = new Label("El animal que estás pensando es: " + animalName);
        messageLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 18px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, messageLabel);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, null,
                        new Stop(0, Color.web("#4CAF50")),
                        new Stop(1, Color.web("#45a049"))
                ),
                new CornerRadii(10), Insets.EMPTY
        )));
        layout.setStyle("-fx-border-color: #ffffff; -fx-border-width: 2px; -fx-border-radius: 10;");

        Scene scene = new Scene(layout, 400, 200);
        messageStage.setScene(scene);
        messageStage.showAndWait();
    }
    
    /*private void updateQuestion() {
        if (currentNode != null && !currentNode.isLeaf() && totalQuestionsAsked < maxQuestions) {
            questionLabel.setText(currentNode.getQuestionOrAnimal());
            questionHistory.add(currentNode.getQuestionOrAnimal());
            yesButton.setDisable(false);
            noButton.setDisable(false);
        } else if (currentNode != null && currentNode.isLeaf()) {
            showAlert("Resultado", "El animal que pensaste es: " + currentNode.getQuestionOrAnimal());
            endGame();
        } else {
            List<String> possibleAnimals = getPossibleAnimals(currentNode);
            if (possibleAnimals.isEmpty()) {
                // Si no hay posibles coincidencias, mostrar directamente el formulario para agregar el animal
                System.out.println("Bellelleeee");
                showAddAnimalPrompt(); 
            } else {
                // Mostrar los animales posibles y preguntar si desea agregar uno nuevo si no está en la lista
                showPossibleAnimals(possibleAnimals);
            }
            endGame();
        }
    }*/

    private List<String> getPossibleAnimals(DecisionTree.DecisionTreeNode node) {
        List<String> animals = new ArrayList<>();
        if (node.isLeaf()) {
            // Solo agrega el animal si no es la frase "No hay animal que coincida"
            if (!node.getQuestionOrAnimal().equalsIgnoreCase("No hay animal que coincida.")) {
                animals.add(node.getQuestionOrAnimal());
            }
        } else {
            // Recursivamente buscar en las ramas "Sí" y "No"
            if (node.getYesBranch() != null) {
                animals.addAll(getPossibleAnimals(node.getYesBranch()));
            }
            if (node.getNoBranch() != null) {
                animals.addAll(getPossibleAnimals(node.getNoBranch()));
            }
        }
        return animals;
    }

    private void showPossibleAnimals(List<String> possibleAnimals, boolean isSingleAnimal) {
        Stage animalsStage = new Stage();
        animalsStage.setTitle("Posibles Animales");

        // Mensaje personalizado según si hay un solo animal o más
        String message = isSingleAnimal 
            ? "El animal que estás pensando es: " 
            : "No estoy seguro, pero podría ser uno de estos animales:";

        Label resultLabel = new Label(message);
        resultLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-font-weight: bold;");
        resultLabel.setAlignment(Pos.CENTER);

        VBox animalsBox = new VBox(10);
        animalsBox.setAlignment(Pos.CENTER);
        for (String animal : possibleAnimals) {
            Label animalLabel = new Label(animal);
            animalLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
            animalsBox.getChildren().add(animalLabel);
        }

        ScrollPane scrollPane = new ScrollPane(animalsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(200);

        Label addAnimalPrompt = new Label("El animal que pensaste no es uno de estos. ¿Deseas agregarlo?");
        addAnimalPrompt.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px;");
        addAnimalPrompt.setAlignment(Pos.CENTER);

        Button yesButton = new Button("Sí");
        Button noButton = new Button("No");
        applyButtonStyle(yesButton);
        applyButtonStyle(noButton);

        yesButton.setOnAction(e -> {
            animalsStage.close();
            askForNewAnimal();
        });

        noButton.setOnAction(e -> animalsStage.close());

        HBox buttonsBox = new HBox(20, yesButton, noButton);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, resultLabel, scrollPane, addAnimalPrompt, buttonsBox);
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

        Scene scene = new Scene(layout, 600, 400);
        animalsStage.setScene(scene);
        animalsStage.showAndWait();
    }
    
    private void askToAddAnimal() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Agregar nuevo animal");
        alert.setHeaderText("No se encontró un animal con estas características.");
        alert.setContentText("¿Te gustaría agregar un nuevo animal?");

        ButtonType yesButtonType = new ButtonType("Sí");
        ButtonType noButtonType = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButtonType, noButtonType);

        // Mostrar el cuadro de diálogo y obtener el botón seleccionado
        alert.showAndWait().ifPresent(type -> {
            if (type == yesButtonType) {
                askForNewAnimal();
            } else {
                endGame();
            }
        });

        // Aplicar estilos después de que los botones se han creado
        Button yesButton = (Button) alert.getDialogPane().lookupButton(yesButtonType);
        Button noButton = (Button) alert.getDialogPane().lookupButton(noButtonType);

        applyButtonStyle(yesButton);
        applyButtonStyle(noButton);
    }

    private void showAddAnimalPrompt() {
        Stage addAnimalStage = new Stage();
        addAnimalStage.setTitle("Agregar Nuevo Animal");

        Label instructionLabel = new Label("Responde a las preguntas para agregar un nuevo animal:");
        instructionLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px;");

        VBox questionsBox = new VBox(15);
        questionsBox.setPadding(new Insets(10));
        questionsBox.setAlignment(Pos.TOP_CENTER);

        List<TextField> answerFields = new ArrayList<>();
        List<String> questions = decisionTree.getQuestions();

        for (String question : questions) {
            Label questionLabel = new Label(question);
            questionLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
            questionLabel.setWrapText(true);

            TextField answerField = new TextField();
            answerField.setPromptText("Ingrese 'sí' o 'no'");
            answerField.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
            answerFields.add(answerField);

            VBox questionBox = new VBox(5, questionLabel, answerField);
            questionBox.setAlignment(Pos.CENTER_LEFT);
            questionsBox.getChildren().add(questionBox);
        }

        ScrollPane scrollPane = new ScrollPane(questionsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(300);

        Label animalNameLabel = new Label("Nombre del nuevo animal:");
        TextField animalNameField = new TextField();
        animalNameField.setPromptText("Ingrese el nombre del animal");
        animalNameField.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");

        Button saveButton = new Button("Guardar Animal");
        applyButtonStyle(saveButton);

        saveButton.setOnAction(e -> {
            String animalName = animalNameField.getText().trim();
            if (animalName.isEmpty()) {
                showAlert("Error", "Por favor, ingrese el nombre del animal.");
                return;
            }

            List<String> newAnimalAnswers = new ArrayList<>();
            for (TextField answerField : answerFields) {
                String answer = answerField.getText().trim().toLowerCase();
                if (answer.equals("sí") || answer.equals("si")) {
                    newAnimalAnswers.add("si");
                } else if (answer.equals("no")) {
                    newAnimalAnswers.add("no");
                } else {
                    showAlert("Error", "Por favor, ingrese 'sí' o 'no' en todas las preguntas.");
                    return;
                }
            }

            saveNewAnimal(animalName, newAnimalAnswers);
            addAnimalStage.close();
        });

        VBox layout = new VBox(20, instructionLabel, scrollPane, animalNameLabel, animalNameField, saveButton);
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

        Scene scene = new Scene(layout, 600, 500);
        addAnimalStage.setScene(scene);
        addAnimalStage.showAndWait();
    }
    


    private void askForNewAnimal() {
        Stage newAnimalStage = new Stage();
        newAnimalStage.setTitle("Agregar Nuevo Animal");

        VBox questionsBox = new VBox(15);
        questionsBox.setPadding(new Insets(10));
        questionsBox.setAlignment(Pos.TOP_CENTER);

        List<TextField> answerFields = new ArrayList<>();
        List<String> questions = decisionTree.getQuestions();

        // Crear campos para las respuestas de las preguntas
        for (String question : questions) {
            Label questionLabel = new Label(question);
            questionLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
            questionLabel.setWrapText(true);

            TextField answerField = new TextField();
            answerField.setPromptText("Ingrese 'sí' o 'no'");
            answerField.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
            answerFields.add(answerField);

            VBox questionBox = new VBox(5, questionLabel, answerField);
            questionBox.setAlignment(Pos.CENTER_LEFT);
            questionsBox.getChildren().add(questionBox);
        }

        ScrollPane scrollPane = new ScrollPane(questionsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(300);

        Button saveButton = new Button("Siguiente");

        // Aplica el estilo del botón "Iniciar Juego"
        applyButtonStyle(saveButton);

        saveButton.setOnAction(e -> {
            List<String> newAnimalAnswers = new ArrayList<>();
            for (TextField answerField : answerFields) {
                String answer = answerField.getText().trim().toLowerCase();
                if (answer.equals("sí") || answer.equals("si")) {
                    newAnimalAnswers.add("si");
                } else if (answer.equals("no")) {
                    newAnimalAnswers.add("no");
                } else {
                    showAlert("Error", "Por favor, ingrese 'sí' o 'no' en todas las preguntas.");
                    return;
                }
            }
            askForAnimalName(newAnimalAnswers);
            newAnimalStage.close();
        });

        VBox layout = new VBox(20, scrollPane, saveButton);
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

        Scene scene = new Scene(layout, 600, 400);
        newAnimalStage.setScene(scene);
        newAnimalStage.showAndWait();
    }
    
    private void askForAnimalName(List<String> newAnimalAnswers) {
        Stage nameStage = new Stage();
        nameStage.setTitle("Nombre del Nuevo Animal");

        Label instructionLabel = new Label("Ingrese el nombre del nuevo animal:");
        instructionLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px;");
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre del animal");
        nameField.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");

        Button saveButton = new Button("Guardar");
        applyButtonStyle(saveButton);

        saveButton.setOnAction(e -> {
            String animalName = nameField.getText().trim();
            if (animalName.isEmpty()) {
                showAlert("Error", "Por favor, ingrese el nombre del animal.");
                return;
            }
            saveNewAnimal(animalName, newAnimalAnswers);
            nameStage.close();
        });

        VBox layout = new VBox(20, instructionLabel, nameField, saveButton);
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

        Scene scene = new Scene(layout, 400, 200);
        nameStage.setScene(scene);
        nameStage.showAndWait();
    }

    private void saveNewAnimal(String animalName, List<String> newAnimalAnswers) {
        try (FileWriter writer = new FileWriter("data/respuestas.txt", true)) {
            String newAnimalData = animalName + " " + String.join(" ", newAnimalAnswers);
            writer.write("\n" + newAnimalData);
            showAlert("Éxito", "El nuevo animal ha sido guardado exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Hubo un problema al guardar el nuevo animal.");
        }
    }

    private void handleYes(Stage gameStage) {
        if (totalQuestionsAsked < maxQuestions) {
            if (currentNode.getYesBranch() != null) {
                currentNode = currentNode.getYesBranch();
                totalQuestionsAsked++;
                updateQuestion();
            } else {
                showAlert("Resultado", "No se encontró un animal con estas características.");
                askToAddAnimal();
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
                askToAddAnimal();
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
