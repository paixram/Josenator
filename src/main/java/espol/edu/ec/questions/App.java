package espol.edu.ec.questions;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import util.DecisionTree;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        var scene = new Scene(new StackPane(label), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Imprimir el arbol
        // Inicializar el árbol de decisiones
        DecisionTree decisionTree = new DecisionTree("data/preguntas.txt", "data/respuestas.txt");

        // Imprimir el árbol de decisiones
        decisionTree.printTree();
        launch();
    }

}