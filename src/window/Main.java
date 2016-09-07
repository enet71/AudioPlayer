package window;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import setting.Load;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("form_1.0.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Player");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        primaryStage.show();
        Controller controller = loader.getController();
        controller.dragFile();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
        Load.load();
    }
}
