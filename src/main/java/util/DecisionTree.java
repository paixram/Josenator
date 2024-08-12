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
        boolean isLeaf;
        List<String> possibleAnimals;

        public DecisionTreeNode(String questionOrAnimal) {
            this.questionOrAnimal = questionOrAnimal;
            this.yesBranch = null;
            this.noBranch = null;
            this.isLeaf = false;
            this.possibleAnimals = new ArrayList<>();
        }

        public boolean isLeaf() {
            return isLeaf;
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

        public List<String> getPossibleAnimals() {
            return possibleAnimals;
        }
    }

    private DecisionTreeNode root;
    private List<String> questions;
    private List<String[]> answers;
    private int maxQuestions;

    public DecisionTree(String questionsFile, String answersFile, int maxQuestions) {
        this.maxQuestions = maxQuestions;
        questions = DataLoader.loadQuestions(questionsFile);
        answers = DataLoader.loadAnswers(answersFile);
        root = buildTree(0, IntStream.range(0, answers.size()).boxed().collect(Collectors.toList()));
    }

    public DecisionTreeNode buildTree(int questionIndex, List<Integer> currentIndices) {
        if (currentIndices.isEmpty()) {
            return null;
        }

        if (questionIndex >= questions.size() || questionIndex >= maxQuestions) {
            if (currentIndices.size() == 1) {
                // Si queda solo un animal en las respuestas posibles, se ha adivinado
                String animal = answers.get(currentIndices.get(0))[0];
                DecisionTreeNode leafNode = new DecisionTreeNode(animal);
                leafNode.isLeaf = true;
                return leafNode;
            } else {
                // Si hay varios animales posibles, la computadora no puede decidir
                DecisionTreeNode uncertainNode = new DecisionTreeNode("No estoy seguro, pero podría ser uno de estos animales: ");
                uncertainNode.possibleAnimals = currentIndices.stream()
                        .map(index -> answers.get(index)[0])
                        .collect(Collectors.toList());
                return uncertainNode;
            }
        }

        DecisionTreeNode currentNode = new DecisionTreeNode(questions.get(questionIndex));

        List<Integer> yesBranchIndices = new ArrayList<>();
        List<Integer> noBranchIndices = new ArrayList<>();

        for (Integer index : currentIndices) {
            if (answers.get(index)[questionIndex + 1].equals("si")) {
                yesBranchIndices.add(index);
            } else {
                noBranchIndices.add(index);
            }
        }

        if (!yesBranchIndices.isEmpty()) {
            currentNode.yesBranch = buildTree(questionIndex + 1, yesBranchIndices);
        }

        if (!noBranchIndices.isEmpty()) {
            currentNode.noBranch = buildTree(questionIndex + 1, noBranchIndices);
        }

        return currentNode;
    }

    public DecisionTreeNode getRoot() {
        return root;
    }
}