/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package espol.edu.ec.questions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Luizzz
 */
public class DataLoader {
    public static List<String> loadQuestions(String filename) {
        List<String> questions = new ArrayList<>();
        // Obtener el directorio de trabajo actual
        String workingDir = System.getProperty("user.dir");
        
        // Concatenar el directorio 'data'
        String dataDir = workingDir + "/" + filename;
        try (BufferedReader br = new BufferedReader(new FileReader(dataDir))) {
            String line;
            while ((line = br.readLine()) != null) {
                questions.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questions;
    }
    
    public static List<String[]> loadAnswers(String filename) {
        List<String[]> answers = new ArrayList<>();
        // Obtener el directorio de trabajo actual
        String workingDir = System.getProperty("user.dir");
        
        // Concatenar el directorio 'data'
        String dataDir = workingDir + "/" + filename;
        try (BufferedReader br = new BufferedReader(new FileReader(dataDir))) {
            String line;
            while ((line = br.readLine()) != null) {
                answers.add(line.split(" "));
            }
            
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            
            
        }
        return answers;
    }
}
