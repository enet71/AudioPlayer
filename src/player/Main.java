package player;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Player");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        primaryStage.show();
        Controller controller = loader.getController();
        controller.dragFile();
        primaryStage.setResizable(false);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
