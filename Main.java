import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        InventoryManager inventoryManager = new InventoryManager();
        SimulationEngine engine = new SimulationEngine(inventoryManager);
        new DashboardGUI(primaryStage, engine);
        engine.startTimer();
    }

    public static void main(String[] args) {
        launch(args);
        System.out.println("test");
    }
}