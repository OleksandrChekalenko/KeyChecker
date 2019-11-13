package sample;

import controllers.Controller;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Group root = new Group();
    private Controller mainSceneController;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();

        mainSceneController = new Controller(root);
    }

    @Override
    public void start(Stage primaryStage) {
        mainSceneController.init();
        mainSceneController.creatingBtn();

        Scene scene = new Scene(root, 600, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}