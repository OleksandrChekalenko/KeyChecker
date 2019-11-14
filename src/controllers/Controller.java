package controllers;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;
import sample.Data;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Controller {
    public void creatingBtn() {
        Button button = new Button("Path");
        button.setLayoutX(250);
        button.setLayoutY(200);
        button.setPrefSize(100, 30);
        button.setOnAction(event -> {
            setKeyLength();
            if (isKeyZero())
                errorLengthMessage();
            else {
                choseButton();
                saveTxt();
                openKeysFile();
            }
        });
        this.root.getChildren().add(button);
    }

    private Group root;
    private VBox failedKeysBox = new VBox();
    private Text failedKeysLabel = new Text();
    private Text enterKeyLength = new Text("   Введіть довжину ключа");

    private TextField keyLengthTF = new TextField();
    private VBox keyLengthBox = new VBox(10);

    private int keyLength = 0;

    public Controller(Group root) {
        this.root = root;
    }

    public void init() {
        keyLengthBox.getChildren().add(enterKeyLength);
        keyLengthBox.getChildren().add(keyLengthTF);
        keyLengthBox.setLayoutX(225);
        keyLengthBox.setLayoutY(100);

        root.getChildren().add(keyLengthBox);
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
        int uncorrectKeys = 0;
        try (FileWriter writer = new FileWriter(Data.path + "\\" + Keys.KEYS_FILE_NAME, false)) {
            for (String key : listOfKeys) {
                if (key.contains("1") || key.contains("O")) {
                    JOptionPane.showMessageDialog(new JFrame(), key + "\n Ключ містить недопустимі символ!",
                            " ", JOptionPane.ERROR_MESSAGE);
                    ++uncorrectKeys;
                }
                if (key.length() != keyLength && !(key.contains("1") || key.contains("O")))
                    ++uncorrectKeys;
                key = checkForbiddenCharacters(key);

                writer.write(key);
                writer.append('\n');
                countKeys++;
                System.out.println(key);
            }
            writer.flush();
            if (uncorrectKeys == 0 && failedKeysBox.isVisible()) failedKeysBox.setVisible(false);
            JOptionPane.showMessageDialog(new JFrame(), "Створено файл: " + Keys.KEYS_FILE_NAME +
                            "\n Шлях до файлу: " + Data.path +
                            "\\" + Keys.KEYS_FILE_NAME +
                            "\n Загальна кількість ключів: " + countKeys +
                            "\n Неправильна кількість ключів: " + uncorrectKeys,
                    " ", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
//        }
    }

    private void openKeysFile() {
        try {
            if (!Data.path.equals("")) {
                Desktop.getDesktop().open(new File(Data.path + "\\" + Keys.KEYS_FILE_NAME));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private String checkForbiddenCharacters(String key) {
        if (key.length() != keyLength)
            key += " !!!!!!! Невірна довжина ключа! " + key.length() + " замість " + keyLength;
        if (key.contains("1"))
            key += " !!!!!!! ключ містить символ \"1\"";
        if (key.contains("O"))
            key += " !!!!!!! ключ містить символ \"O\"";

        createFailedKeys();
        return key;
    }

    private void setKeyLength() {
        if (keyLengthTF.getText().equals("") || keyLengthTF.getText().matches("\\w+\\s+"))
            keyLength = 0;
        else
            keyLength = Integer.parseInt(keyLengthTF.getText());
    }

    private boolean isKeyZero() {
        return keyLength == 0;
    }

    private void errorLengthMessage() {
        JOptionPane.showMessageDialog(new JFrame(), "Ви не ввели довжину ключа", "Dialog", JOptionPane.ERROR_MESSAGE);
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
            Data.path = "";
            /*String message = "Помилка, шлях не отриманий";
            JOptionPane.showMessageDialog(new JFrame(), message, "Dialog", JOptionPane.ERROR_MESSAGE);*/
        }
        if (!Data.path.equals("")) {
            String message = "Шлях до ключів: " + Data.path;
            JOptionPane.showMessageDialog(new JFrame(), message, "Dialog", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void createFailedKeys() {
        failedKeysLabel.setText(Keys.FAILED_KEY_TEXT + "\n" + Data.path);
        if (failedKeysBox.getChildren().size() == 0) {
            failedKeysBox.getChildren().add(failedKeysLabel);
            failedKeysBox.setLayoutX(150);
            failedKeysBox.setLayoutY(50);
            root.getChildren().add(failedKeysBox);
        }
        if (!failedKeysBox.isVisible()) failedKeysBox.setVisible(true);
    }

    public interface Keys {
        String KEYS_FILE_NAME = "keys.txt";
        String JPG_FORMAT = ".jpg";
        int KEY_LENGH = 24;
        int MIN_KEY_LENGH = 17;
        String FAILED_KEY_TEXT = "Деякі з ключів містять заборонені символи \"1\" та \"O\"" + "\nАбо не вырну довжину ключа!";
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
}
