package com.nefariousmachine.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FamilyTreeFileManager {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static void saveToFile(FamilyTree tree, File file) throws IOException {
        FamilyTreeDTO dto = tree.toDTO();
        String json = gson.toJson(dto);
        try(FileWriter writer = new FileWriter(file)) {
            writer.write(json);
        }
    }

    public static FamilyTree loadFromFile(File file) throws IOException {
        try(FileReader reader = new FileReader(file)) {
            FamilyTreeDTO dto = gson.fromJson(reader, FamilyTreeDTO.class);
            return FamilyTree.fromDTO(dto);
        }
    }

    public static void saveWithDialog(FamilyTree tree, Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Family Tree Files", "json"));

        if(fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if(!file.getName().endsWith(".json")) {
                file = new File(file.getAbsolutePath() + ".json");
            }
            try {
                saveToFile(tree, file);
                JOptionPane.showMessageDialog(parent, "Family tree saved successfully!");
            } catch(IOException e) {
                JOptionPane.showMessageDialog(parent,
                        "Error saving file: " + e.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static FamilyTree loadWithDialog(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Family Tree Files", "json"));

        if(fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try {
                return loadFromFile(fileChooser.getSelectedFile());
            } catch(IOException e) {
                JOptionPane.showMessageDialog(parent,
                        "Error loading file: " + e.getMessage(),
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
}
