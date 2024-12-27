import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginPage {
    private final VBox view;

    public LoginPage() {
        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button goToRegisterButton = new Button("Go to Register");

        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        goToRegisterButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        VBox form = new VBox(10, usernameField, passwordField, loginButton, goToRegisterButton);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #f4f4f4;");
        form.setMaxWidth(300);

        view = new VBox(15, titleLabel, form);
        view.setPadding(new Insets(30));
        view.setStyle("-fx-alignment: center;");

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "All fields are required!");
                return;
            }
            if (authenticateUser(username, password)) {
                App.showFileTransferApp();
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid credentials!");
            }
        });

        goToRegisterButton.setOnAction(e -> App.showRegistrationPage());
    }

    public VBox getView() {
        return view;
    }

    private boolean authenticateUser(String username, String password) {
        // Check if the username and password match any user in the list
        for (User user : RegistrationPage.users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}
