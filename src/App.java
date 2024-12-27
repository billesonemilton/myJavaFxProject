import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        showRegistrationPage();
    }

    public static void showRegistrationPage() {
        RegistrationPage registrationPage = new RegistrationPage();
        Scene scene = new Scene(registrationPage.getView(), 600, 400);
        primaryStage.setTitle("Registration Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showLoginPage() {
        LoginPage loginPage = new LoginPage();
        Scene scene = new Scene(loginPage.getView(), 600, 400);
        primaryStage.setTitle("Login Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showFileTransferApp() {
        HelloWorldApp fileTransferApp = new HelloWorldApp();
        fileTransferApp.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
