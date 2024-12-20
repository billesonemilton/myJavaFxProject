import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class HelloWorldApp extends Application {
    private File selectedFile; // Holds the selected file reference
    private static final String SERVER_ADDRESS = "localhost"; // Server address
    private static final int SERVER_PORT = 5000; // Server port

    @Override
    public void start(Stage primaryStage) {
        // Title Label
        Label titleLabel = new Label("File Sharing App");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // TextField for File Path (Editable)
        TextField filePathField = new TextField();
        filePathField.setPromptText("Enter file path here or use 'Choose File'...");
        filePathField.setPrefWidth(400);
        filePathField.setPrefHeight(40);

        // Buttons for Actions
        Button uploadButton = new Button("Upload File");
        Button downloadButton = new Button("Download File");
        Button selectFileButton = new Button("Choose File");
        Button deleteButton = new Button("Delete File");

        // Style Buttons
        String buttonStyle = "-fx-font-size: 16px; -fx-background-color: #4CAF50; -fx-text-fill: white;";
        selectFileButton.setStyle(buttonStyle);
        uploadButton.setStyle(buttonStyle);
        deleteButton.setStyle("-fx-font-size: 16px; -fx-background-color: #FF6347; -fx-text-fill: white;");
        downloadButton.setStyle("-fx-font-size: 16px; -fx-background-color: #2196F3; -fx-text-fill: white;");

        // ListView for Available Files
        ListView<String> fileListView = new ListView<>();
        fileListView.setPrefHeight(200);

        // Layouts
        VBox root = new VBox(15); // Main container
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f4f4f4;");

        HBox fileInputBox = new HBox(10);
        fileInputBox.getChildren().addAll(filePathField, selectFileButton, uploadButton);

        HBox actionButtonsBox = new HBox(10);
        actionButtonsBox.getChildren().addAll(downloadButton, deleteButton);

        // Add all components to root
        root.getChildren().addAll(
            titleLabel,
            fileInputBox,
            new Label("Available Files for Download:"),
            fileListView,
            actionButtonsBox
        );

        // Scene and Stage
        Scene scene = new Scene(root, 700, 500);
        primaryStage.setTitle("File Sharing App");
        primaryStage.setScene(scene);
        primaryStage.show();

        // FileChooser Action for Selecting Files
        selectFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose File to Upload");
            selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                filePathField.setText(selectedFile.getAbsolutePath());
            } else {
                filePathField.setText("No file selected");
            }
        });

        // Upload Button Action
        uploadButton.setOnAction(e -> {
            String filePath = filePathField.getText();
            if (filePath != null && !filePath.trim().isEmpty()) {
                // Add file to the ListView
                fileListView.getItems().add(filePath.substring(filePath.lastIndexOf("/") + 1));

                // Upload file to the server
                uploadFile(selectedFile);

                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "File Uploaded Successfully:\n" + filePath, ButtonType.OK);
                alert.showAndWait();
                filePathField.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "Please choose a valid file or enter a file path!", ButtonType.OK);
                alert.showAndWait();
            }
        });

        // Download Button Action
        downloadButton.setOnAction(e -> {
            String selectedFileToDownload = fileListView.getSelectionModel().getSelectedItem();
            if (selectedFileToDownload != null) {
                downloadFile(selectedFileToDownload);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "Please select a file to download!", ButtonType.OK);
                alert.showAndWait();
            }
        });

        // Delete Button Action
        deleteButton.setOnAction(e -> {
            String selectedFileToDelete = fileListView.getSelectionModel().getSelectedItem();
            if (selectedFileToDelete != null) {
                fileListView.getItems().remove(selectedFileToDelete); // Remove file from ListView
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "File Deleted Successfully: " + selectedFileToDelete, ButtonType.OK);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "Please select a file to delete!", ButtonType.OK);
                alert.showAndWait();
            }
        });
    }

    private void uploadFile(File file) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             FileInputStream fileInput = new FileInputStream(file)) {

            // Send UPLOAD command and file metadata
            output.writeUTF("UPLOAD");
            output.writeUTF(file.getName());
            output.writeLong(file.length());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInput.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            System.out.println("File uploaded successfully.");
        } catch (IOException e) {
            System.out.println("Error uploading file: " + e.getMessage());
        }
    }

    private void downloadFile(String fileName) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream())) {

            // Send DOWNLOAD command and file name
            output.writeUTF("DOWNLOAD");
            output.writeUTF(fileName);

            String response = input.readUTF();
            if (response.equals("DOWNLOAD_READY")) {
                long fileSize = input.readLong();
                File destinationFile = new File("downloaded_" + fileName);

                try (FileOutputStream fileOutput = new FileOutputStream(destinationFile)) {
                    byte[] buffer = new byte[4096];
                    long bytesRead = 0;
                    while (bytesRead < fileSize) {
                        int read = input.read(buffer);
                        fileOutput.write(buffer, 0, read);
                        bytesRead += read;
                    }
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "File Downloaded Successfully: " + destinationFile.getAbsolutePath(), ButtonType.OK);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Error: File not found on server.", ButtonType.OK);
                alert.showAndWait();
            }
        } catch (IOException e) {
            System.out.println("Error downloading file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
