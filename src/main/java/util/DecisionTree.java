/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import espol.edu.ec.questions.DataLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author Luizzz
 */
public class DecisionTree {
    public class DecisionTreeNode {
        String questionOrAnimal;
        DecisionTreeNode yesBranch;
        DecisionTreeNode noBranch;

        public DecisionTreeNode(String questionOrAnimal) {
            this.questionOrAnimal = questionOrAnimal;
            this.yesBranch = null;
            this.noBranch = null;
        }

        public boolean isLeaf() {
            return yesBranch == null && noBranch == null;
        }
        public String getQuestionOrAnimal() {
            return questionOrAnimal;
        }

        public DecisionTreeNode getYesBranch() {
            return yesBranch;
        }

        public DecisionTreeNode getNoBranch() {
            return noBranch;
        }
    }
    
    private DecisionTreeNode root;
    private List<String> questions;
    private List<String[]> answers;
    
    public DecisionTree(String questionsFile, String answersFile) {
        questions = DataLoader.loadQuestions(questionsFile);
        answers = DataLoader.loadAnswers(answersFile);
        root = buildTree(0, IntStream.range(0, answers.size()).boxed().collect(Collectors.toList()));
    }
    
    public DecisionTreeNode buildTree(int questionIndex, List<Integer> currentIndices) {
        if (currentIndices.isEmpty()) {
            return null; // No hay respuestas que procesar.
        }
        
        if (questionIndex >= questions.size()) {
            // Asumimos que todas las respuestas en `currentIndices` son del mismo animal si no quedan preguntas.
            String animal = answers.get(currentIndices.get(0))[0]; // Tomar el nombre del animal del primer índice.
            return new DecisionTreeNode(animal); // Crear nodo hoja con el nombre del animal.
        }

        DecisionTreeNode currentNode = new DecisionTreeNode(questions.get(questionIndex));

        // Filtrar respuestas para la rama 'Sí' y 'No'
        List<Integer> yesBranchIndices = new ArrayList<>();
        List<Integer> noBranchIndices = new ArrayList<>();
        
        System.out.println("Question size: " + questions.size());
        for (Integer index : currentIndices) {
            if (answers.get(index)[questionIndex + 1].equals("si")) {
                yesBranchIndices.add(index);
            } else {
                noBranchIndices.add(index);
            }
        }

        // Recursivamente construir ramas 'sí' y 'no'
        if (!yesBranchIndices.isEmpty()) {
            currentNode.yesBranch = buildTree(questionIndex + 1, yesBranchIndices);
        } else if (yesBranchIndices.isEmpty() && questionIndex + 1 == questions.size()) {
            currentNode.yesBranch = new DecisionTreeNode("No hay animal que coincida.");
        }

        if (!noBranchIndices.isEmpty()) {
            currentNode.noBranch = buildTree(questionIndex + 1, noBranchIndices);
        } else if (noBranchIndices.isEmpty() && questionIndex + 1 == questions.size()) {
            currentNode.noBranch = new DecisionTreeNode("No hay animal que coincida.");
        }

        return currentNode;
    }
    public DecisionTreeNode getRoot() {
        return root;
    }
    
    public void printTree() {
        printTree(root, "", "Root");
    }

    // Método recursivo que imprime cada nodo con la indentación correspondiente
    private void printTree(DecisionTreeNode node, String prefix, String childLabel) {
        if (node == null) {
            System.out.println(prefix + childLabel + ": null");
            return;
        }

        // Imprimir la pregunta o el nombre del animal con un prefijo que muestra la relación
        System.out.println(prefix + childLabel + ": " + node.questionOrAnimal);

        // Recursivamente imprimir las ramas 'sí' y 'no' con mayor indentación
        if (node.yesBranch != null || node.noBranch != null) {
            printTree(node.yesBranch, prefix + "    ", "Yes");
            printTree(node.noBranch, prefix + "    ", "No");
        }
    }
}