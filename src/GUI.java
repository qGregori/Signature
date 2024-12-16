import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

import java.io.*;
import java.util.List;

public class GUI {

    private TextField signaturePathField;
    private TextField signatureMaskField;
    private TextField hexSignatureMaskField;
    private TextArea resultArea;
    private SignatureDatabase signatureDatabase;
    private TextArea fileHexSignatureArea; // Для отображения сигнатуры выбранного файла

    private final String fixedSearchPath = "C:/Users/YourUsername/Documents/FilesToSearch";

    // Создание интерфейса
    public VBox createInterface(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        // Панель для ввода пути базы данных сигнатур
        HBox signaturePathBox = new HBox(10);
        Button selectSignatureButton = new Button("Выбрать папку для сигнатур");
        signaturePathField = new TextField();
        signaturePathField.setEditable(false);

        selectSignatureButton.setOnAction(event -> onSelectSignatureFolder(primaryStage));

        signaturePathBox.getChildren().addAll(selectSignatureButton, signaturePathField);

        // Панель для ввода маски сигнатуры
        HBox signatureMaskBox = new HBox(10);
        signatureMaskBox.getChildren().addAll(new Label("Маска сигнатуры:"), signatureMaskField = new TextField());

        // Панель для ввода HEX маски
        HBox hexSignatureMaskBox = new HBox(10);
        hexSignatureMaskBox.getChildren().addAll(new Label("Маска HEX:"), hexSignatureMaskField = new TextField());

        // Кнопка для поиска
        Button searchButton = new Button("Поиск");
        searchButton.setOnAction(event -> onSearchButtonClick());

        // Поле для отображения результатов поиска
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(200);

        // Кнопка для выбора файла и вывода его сигнатуры в HEX
        Button selectFileButton = new Button("Выбрать файл для анализа");
        selectFileButton.setOnAction(event -> onSelectFileButtonClick(primaryStage));

        // Поле для отображения HEX сигнатуры выбранного файла
        fileHexSignatureArea = new TextArea();
        fileHexSignatureArea.setEditable(false);
        fileHexSignatureArea.setPrefHeight(200);

        // Кнопка для добавления новой сигнатуры
        Button addSignatureButton = new Button("Добавить новую сигнатуру");
        addSignatureButton.setOnAction(event -> onAddSignatureButtonClick(primaryStage));

        // Добавляем все элементы в VBox
        root.getChildren().addAll(
                signaturePathBox,
                signatureMaskBox,
                hexSignatureMaskBox,
                searchButton,
                resultArea,
                selectFileButton,
                fileHexSignatureArea,
                addSignatureButton
        );
        return root;
    }

    // Действие при нажатии на кнопку "Выбрать папку для сигнатур"
    private void onSelectSignatureFolder(Stage primaryStage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку с сигнатурами");

        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            signaturePathField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    // Действие при нажатии на кнопку "Поиск"
    private void onSearchButtonClick() {
        String signaturePath = signaturePathField.getText();
        String signatureMask = signatureMaskField.getText();
        String hexSignatureMask = hexSignatureMaskField.getText();

        signatureDatabase = new SignatureDatabase(signaturePath);

        if (!hexSignatureMask.isEmpty()) {
            // Убираем пробелы в HEX маске перед поиском
            String cleanedHexMask = hexSignatureMask.replaceAll("\\s", "");

            var matchedFiles = FileSearch.searchFilesWithHexSignature(fixedSearchPath, cleanedHexMask, signatureDatabase.getSignatureFiles());
            displayResults(matchedFiles);
        } else if (!signatureMask.isEmpty()) {
            var matchedFiles = FileSearch.searchFilesWithMask(fixedSearchPath, signatureMask, signatureDatabase.getSignatureFiles());
            displayResults(matchedFiles);
        } else {
            resultArea.appendText("Пожалуйста, введите маску сигнатуры или HEX.\n");
        }
    }

    // Отображаем результаты поиска
    private void displayResults(List<File> matchedFiles) {
        resultArea.clear();
        if (matchedFiles.isEmpty()) {
            resultArea.appendText("Файлы не найдены.");
        } else {
            for (File file : matchedFiles) {
                resultArea.appendText("Найден файл: " + file.getAbsolutePath() + "\n");
                resultArea.appendText(FileMetadata.getMetadata(file) + "\n\n");
            }
        }
    }

    // Действие при нажатии на кнопку "Выбрать файл для анализа"
    private void onSelectFileButtonClick(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Все файлы", "*.*"));
        fileChooser.setTitle("Выберите файл для анализа");

        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            // Отображаем его HEX сигнатуру
            String fileHexSignature = getFileHexSignature(selectedFile);
            fileHexSignatureArea.setText(fileHexSignature);
        }
    }

    // Чтение файла и преобразование его содержимого в HEX строку
    private String getFileHexSignature(File file) {
        StringBuilder hexSignature = new StringBuilder();
        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[16]; // Читаем по 16 байт за раз
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    hexSignature.append(String.format("%02X ", buffer[i]));
                }
                hexSignature.append("\n"); // Добавляем новую строку после каждого блока
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hexSignature.toString();
    }

    // Действие при нажатии на кнопку "Добавить новую сигнатуру"
    private void onAddSignatureButtonClick(Stage primaryStage) {
        // Открытие диалогового окна для выбора директории
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите директорию для сохранения сигнатуры");

        File selectedDirectory = directoryChooser.showDialog(primaryStage);

        if (selectedDirectory != null) {
            // Получаем сигнатуру выбранного файла
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Все файлы", "*.*"));
            fileChooser.setTitle("Выберите файл для добавления сигнатуры");

            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                // Получаем сигнатуру выбранного файла
                String hexSignature = getFileHexSignature(selectedFile);

                // Создаем новый файл для сохранения сигнатуры в выбранной директории
                File signatureFile = new File(selectedDirectory, selectedFile.getName() + "_signature.txt");

                // Добавляем сигнатуру в выбранный файл
                addSignatureToDatabase(signatureFile, hexSignature);
            }
        }
    }

    // Добавление сигнатуры в файл выбранной директории
    private void addSignatureToDatabase(File file, String hexSignature) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(hexSignature);
            writer.newLine();
            resultArea.appendText("Сигнатура добавлена в файл: " + file.getAbsolutePath() + "\n");
        } catch (IOException e) {
            resultArea.appendText("Ошибка при добавлении сигнатуры.\n");
            e.printStackTrace();
        }
    }
}
