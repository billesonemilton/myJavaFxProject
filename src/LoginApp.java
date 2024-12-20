import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Title Label
        Label titleLabel = new Label("Login to File Sharing App");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // Username Field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");
        usernameField.setPrefHeight(40);

        // Password Field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setPrefHeight(40);

        // Login Button
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-font-size: 16px; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        // Message Label
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        // Layouts
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f4f4f4;");

        root.getChildren().addAll(
            titleLabel,
            usernameField,
            passwordField,
            loginButton,
            messageLabel
        );

        // Scene and Stage
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Login Button Action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please fill in both fields.");
            } else {
                if (authenticateUser(username, password)) {
                    messageLabel.setText("Login Successful!");
                    messageLabel.setStyle("-fx-text-fill: green;");

                    // Proceed to the main application
                    primaryStage.close();
                    try {
                        HelloWorldApp fileSharingApp = new HelloWorldApp();
                        fileSharingApp.start(new Stage()); // Launch HelloWorldApp
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    messageLabel.setText("Invalid username or password.");
                }
            }
        });
    }

    // Dummy authentication method
    private boolean authenticateUser(String username, String password) {
        // Replace with actual authentication logic (e.g., server-side validation)
        return "admin".equals(username) && "password".equals(password);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
