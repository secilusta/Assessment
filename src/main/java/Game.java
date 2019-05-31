import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Game.java - Main class of application
 */
public class Game extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/MainScreen.fxml"));
        Scene scene = new Scene(root, 400, 450);
        scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
        primaryStage.setTitle("CS-TECH ASSESSMENT - SECIL USTA");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
