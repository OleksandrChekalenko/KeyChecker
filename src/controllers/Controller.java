package controllers;

import javafx.scene.Group;
import javafx.scene.control.Button;
import org.jetbrains.annotations.NotNull;
import sample.Data;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Controller {

    private Group root;

    public Controller(Group root) {
        this.root = root;
    }

    public void creatingBtn() {
        Button button = new Button("Path");
        button.setLayoutX(250);
        button.setLayoutY(200);
        button.setPrefSize(100, 30);
        button.setOnAction(event -> {
            choseButton();
            saveTxt();
        });
        this.root.getChildren().add(button);
    }

    private void choseButton() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("C:\\"));
        chooser.setDialogTitle("Вибір папки з ключами");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.OPEN_DIALOG) {
            System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
            Data.path = chooser.getSelectedFile().getAbsolutePath();
        } else {
            String message = "Помилка, шлях не отриманий";
            JOptionPane.showMessageDialog(new JFrame(), message, "Dialog", JOptionPane.ERROR_MESSAGE);
        }
        if (!Data.path.equals("")) {
            String message = "Шлях до ключів: " + Data.path;
            JOptionPane.showMessageDialog(new JFrame(), message, "Dialog", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String message = "Вкажіть папку";
            JOptionPane.showMessageDialog(new JFrame(), message, "Dialog",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveTxt() {
        File folder = new File(Data.path);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            return;
        }

//        if (checkKeys(listOfFiles)){
        ArrayList<String> listOfKeys = getListOfKeys(folder);
        int countKeys = 0;
        try (FileWriter writer = new FileWriter(Data.path + "\\" + Keys.KEYS_FILE_NAME, false)) {
            for (String file : listOfKeys) {
                writer.write(file);
                writer.append('\n');
                countKeys++;
                System.out.println(file);
            }
            writer.flush();
            JOptionPane.showMessageDialog(new JFrame(), "Створено файл: " + Keys.KEYS_FILE_NAME +
                            "\n Шлях до файлу: " + Data.path +
                            "\\" + Keys.KEYS_FILE_NAME +
                            "\n Кількість ключів: " + countKeys,
                    " ", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
//        }
    }

    private ArrayList<String> getListOfKeys(File folder) {
        if (folder == null) return null;
        ArrayList<String> keysList = new ArrayList<>();

        File[] files = folder.listFiles();

        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            if (isJPGFormat(files[i]))
                keysList.add(files[i].getName().substring(0, files[i].getName().length() - 4));
        }
        return keysList;
    }

    private boolean isJPGFormat(File file) {
        String fileFormat;
        String fileName;
        fileName = file.getName();
        fileFormat = fileName.substring(fileName.length() - 4);
        if (!fileFormat.equals(Controller.Keys.JPG_FORMAT)) {
            if (file.isFile() && fileName.length() + 4 >= Controller.Keys.MIN_KEY_LENGH)
                JOptionPane.showMessageDialog(new JFrame(), "Невірний формат ключа: " + file.getName() + "\n" + fileFormat, "Невірний формат ключа!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean checkKeys(@NotNull File[] listOfFiles) {
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (isJPGFormat(file) /*&& checkLength(file)*/) {
                    System.out.println(file.getName());
                } else return false;
            }
        }
        return true;
    }

    private boolean checkLength(@NotNull File file) {
        if (Controller.Keys.KEY_LENGH != file.getName().substring(0, file.getName().length() - 4).length()) {
            JOptionPane.showMessageDialog(new JFrame(), "Невірна довжина ключа: " + file.getName(), "Невірна довжина ключа!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public interface Keys {
        String KEYS_FILE_NAME = "keys.txt";
        String JPG_FORMAT = ".jpg";
        int KEY_LENGH = 24;
        int MIN_KEY_LENGH = 17;
    }
}
