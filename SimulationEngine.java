import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.util.*;
import java.util.function.Consumer;

public class SimulationEngine {

    private final Queue<MenuItem> orderQueue;
    private final List<IAppliance> appliances;
    private final InventoryManager inventoryManager;
    private final Random random;
    private Timeline timeline;

    private Consumer<String> logCallback;
    private Runnable refreshCallback;

    public SimulationEngine(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
        this.orderQueue = new LinkedList<>();
        this.appliances = new ArrayList<>();
        this.random = new Random();

        appliances.add(new Grill());
        appliances.add(new Fryer());
        appliances.add(new DrinkDispenser());
        appliances.add(new CoffeeMachine());
    }

    public void setLogCallback(Consumer<String> logCallback) {
        this.logCallback = logCallback;
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    public void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> generateNewOrder()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stopTimer() {
        if (timeline != null) timeline.stop();
    }

    private void generateNewOrder() {
        MenuItem item = createRandomItem();
        orderQueue.add(item);
        log("New Order: " + item.getName());
        if (refreshCallback != null) refreshCallback.run();
    }

    private MenuItem createRandomItem() {
        switch (random.nextInt(4)) {
            case 0:  return new HotFood("BURGER");
            case 1:  return new HotFood("FRIES");
            case 2:  return new Beverage();
            default: return new Coffee();
        }
    }

    public void cookNextOrder() {
        MenuItem item = orderQueue.poll();
        if (item == null) {
            log("No orders. Please wait a few seconds for a new order.");
            return;
        }

        if (!inventoryManager.hasIngredients(item.getRequiredIngredients())) {
            log("ERROR: Cannot cook " + item.getName() + " - Insufficient ingredients!");
            if (refreshCallback != null) refreshCallback.run();
            return;
        }

        IAppliance selected = null;
        for (IAppliance appliance : appliances) {
            if (appliance.canProcess(item)) {
                selected = appliance;
                break;
            }
        }

        if (selected == null) {
            log("ERROR: No appliance available for " + item.getName());
            return;
        }

        selected.processTask(item);
        inventoryManager.consumeIngredients(item.getRequiredIngredients(), item.getReward());
        log("Cooked: " + item.getName() + " | Earned " + item.getReward() + " AZN");
        if (refreshCallback != null) refreshCallback.run();
    }

    private void log(String message) {
        if (logCallback != null) logCallback.accept(message);
    }

    public Queue<MenuItem> getOrderQueue() { return orderQueue; }
    public InventoryManager getInventoryManager() { return inventoryManager; }
}