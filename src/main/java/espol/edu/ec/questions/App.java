package espol.edu.ec.questions;


import java.io.File;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
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

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Configuración inicial de la ventana
        stage.setTitle("Árbol de Decisiones - Juego de Adivinanzas");

        // Prueba de carga de la imagen
        ImageView logo = null;
        try {
            String workingDir = System.getProperty("user.dir");
        
            // Concatenar el directorio 'data'
            String dataDir = workingDir + "/data/logode20preguntas.jpeg";
            System.out.println("DIR: " + dataDir);
            File file = new File(dataDir);
            Image image = new Image(file.toURI().toString());
            if (image.isError()) {
                System.out.println("Error loading image: " + image.getException());
            } else {
                logo = new ImageView(image);
                logo.setFitWidth(250);  // Establecer ancho
                logo.setFitHeight(250);  // Establecer altura
                logo.setEffect(new GaussianBlur(1)); // Añadir un pequeño desenfoque
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

        // Botón
        animalThoughtButton = new Button("Animal Pensado");
        applyButtonStyle(animalThoughtButton);

        animalThoughtButton.setOnAction(e -> {
            startGameButton.setDisable(false);
            animalThoughtButton.setDisable(true);
        });

        // Número de preguntas
        numQuestionsField = new TextField();
        numQuestionsField.setPromptText("Número máximo de preguntas");
        numQuestionsField.setStyle("-fx-background-radius: 10; -fx-padding: 10;");

        // Botón para iniciar el juego
        startGameButton = new Button("Iniciar Juego");
        applyButtonStyle(startGameButton);
        startGameButton.setDisable(true);
        startGameButton.setOnAction(e -> startGame());

        
        VBox layout = new VBox(20, logo, instructionLabel, animalThoughtButton, numQuestionsField, startGameButton);
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

        // Configuración de la escena y el stage
        Scene scene = new Scene(layout, 500, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void applyButtonStyle(Button button) {
        button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10;-fx-font-weight: bold;-fx-font-family: 'Arial';");
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.25)); // Sombra negra con transparencia
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        button.setEffect(shadow);

        // Efectos
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10;");
            button.setScaleX(1.05);  // Agranda el botón un 5% al pasar el mouse
            button.setScaleY(1.05);
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10;");
            button.setScaleX(1);  // Restaura el tamaño original al quitar el mouse
            button.setScaleY(1);
        });
    }

    private void startGame() {
        try {
            int maxQuestions = Integer.parseInt(numQuestionsField.getText());
            decisionTree = new DecisionTree("data/preguntas.txt", "data/respuestas.txt");
            currentNode = decisionTree.getRoot();
            openGameWindow();  // Abre la nueva ventana con el juego
        } catch (NumberFormatException e) {
            showAlert("Error", "Por favor, ingresa un número válido de preguntas");
        }
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
        if (currentNode != null) {
            questionLabel.setText(currentNode.getQuestionOrAnimal());
            yesButton.setDisable(false);
            noButton.setDisable(false);
        } else {
            questionLabel.setText("¡Juego terminado!");
            yesButton.setDisable(true);
            noButton.setDisable(true);
            showPlayAgainButton();
        }
    }

    private void handleYes(Stage gameStage) {
        if (currentNode.getYesBranch() != null) {
            currentNode = currentNode.getYesBranch();
            updateQuestion();
        } else {
            showAlert("Resultado", "No se encontró un animal con estas características");
            endGame(gameStage);
        }
    }

    private void handleNo(Stage gameStage) {
        if (currentNode.getNoBranch() != null) {
            currentNode = currentNode.getNoBranch();
            updateQuestion();
        } else {
            showAlert("Resultado", "No se encontró un animal con estas características");
            endGame(gameStage);
        }
    }

    private void showPlayAgainButton() {
        Button playAgainButton = new Button("Volver a Jugar");
        applyButtonStyle(playAgainButton);

        playAgainButton.setOnAction(e -> {
            primaryStage.show();  // Muestra la ventana principal nuevamente
            Stage gameStage = (Stage) playAgainButton.getScene().getWindow();
            gameStage.close();  // Cierra la ventana de juego
            resetMainWindow();  // Resetea la ventana principal
        });

        VBox parent = (VBox) questionLabel.getParent();
        parent.getChildren().add(playAgainButton);
    }

    private void resetMainWindow() {
        animalThoughtButton.setDisable(false);
        startGameButton.setDisable(true);
        numQuestionsField.clear();
    }

    private void endGame(Stage gameStage) {
        yesButton.setDisable(true);
        noButton.setDisable(true);
        showPlayAgainButton();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}