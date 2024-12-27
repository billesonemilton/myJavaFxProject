import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class RegistrationPage {
    static final List<User> users = new ArrayList<>(); // In-memory user storage
    private final VBox view;

    public RegistrationPage() {
        Label titleLabel = new Label("Register");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button registerButton = new Button("Register");
        Button goToLoginButton = new Button("Go to Login");

        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        goToLoginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        VBox form = new VBox(10, usernameField, passwordField, registerButton, goToLoginButton);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #f4f4f4;");
        form.setMaxWidth(300);

        view = new VBox(15, titleLabel, form);
        view.setPadding(new Insets(30));
        view.setStyle("-fx-alignment: center;");

        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "All fields are required!");
                return;
            }
            if (registerUser(username, password)) {
                showAlert(Alert.AlertType.INFORMATION, "Registration successful!");
                App.showLoginPage();
            } else {
                showAlert(Alert.AlertType.ERROR, "Username already exists!");
            }
        });

        goToLoginButton.setOnAction(e -> App.showLoginPage());
    }

    public VBox getView() {
        return view;
    }

    private boolean registerUser(String username, String password) {
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }
        // Add new user to the list
        users.add(new User(username, password));
        return true;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}
