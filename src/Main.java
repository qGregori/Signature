import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Инициализация и запуск интерфейса
        GUI gui = new GUI();
        // Передаем Stage в метод createInterface
        Scene scene = new Scene(gui.createInterface(primaryStage), 600, 400);

        primaryStage.setTitle("Поиск файлов по сигнатурам");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
