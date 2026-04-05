import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class DashboardGUI {

    private final SimulationEngine engine;
    private final InventoryManager inventoryManager;

    private VBox queueBox;
    private TextArea logArea;
    private Label cashLabel;
    private VBox ingredientBox;
    private ComboBox<String> ingredientDropdown;

    // Color palette
    private static final String BG_APP    = "#0f0f13";
    private static final String BG_CARD   = "#17171f";
    private static final String BG_ITEM   = "#1e1e2a";
    private static final String BG_LOG    = "#0c0c12";
    private static final String BORDER    = "#2a2a3a";
    private static final String ACCENT    = "#6c47ff";
    private static final String TEXT_MAIN = "#e0e0f0";
    private static final String TEXT_MUTED= "#8888aa";
    private static final String COL_OK    = "#3af0a0";
    private static final String COL_ERR   = "#ff5555";
    private static final String COL_INFO  = "#6c47ff";
    private static final String COL_GOLD  = "#f0c83a";

    public DashboardGUI(Stage stage, SimulationEngine engine) {
        this.engine = engine;
        this.inventoryManager = engine.getInventoryManager();

        engine.setLogCallback(msg ->
            javafx.application.Platform.runLater(() -> appendLog(msg)));
        engine.setRefreshCallback(() ->
            javafx.application.Platform.runLater(this::refreshDisplay));

        stage.setTitle("The Silicon Spatula");
        stage.setScene(buildScene());
        stage.setWidth(1050);
        stage.setHeight(680);
        stage.show();

        refreshDisplay();
    }

    private Scene buildScene() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        grid.setStyle("-fx-background-color: " + BG_APP + ";");

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        grid.getColumnConstraints().addAll(col, col);

        RowConstraints row = new RowConstraints();
        row.setPercentHeight(50);
        grid.getRowConstraints().addAll(row, row);

        grid.add(buildQueuePanel(),    0, 0);
        grid.add(buildResourcePanel(), 1, 0);
        grid.add(buildRestockPanel(),  0, 1);
        grid.add(buildLogPanel(),      1, 1);

        return new Scene(grid);
    }

    // ── Panel 1: Orders Queue ──────────────────────────────────────────────
    private VBox buildQueuePanel() {
        VBox card = makeCard();

        Label title = cardTitle("Orders Queue");

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        queueBox = new VBox(6);
        queueBox.setStyle("-fx-background-color: transparent;");
        scroll.setContent(queueBox);

        Button cookBtn = accentButton("Cook Next Order", ACCENT);
        cookBtn.setMaxWidth(Double.MAX_VALUE);
        cookBtn.setOnAction(e -> engine.cookNextOrder());

        card.getChildren().addAll(title, scroll, cookBtn);
        return card;
    }

    // ── Panel 2: Inventory ─────────────────────────────────────────────────
    private VBox buildResourcePanel() {
        VBox card = makeCard();

        Label title = cardTitle("Inventory");

        cashLabel = new Label("124.00 AZN");
        cashLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        cashLabel.setTextFill(Color.WHITE);

        Label cashSub = new Label("Current Balance");
        cashSub.setFont(Font.font("Segoe UI", 12));
        cashSub.setTextFill(Color.web(TEXT_MUTED));

        ingredientBox = new VBox(2);
        ingredientBox.setStyle("-fx-background-color: transparent;");

        ScrollPane scroll = new ScrollPane(ingredientBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        card.getChildren().addAll(title, cashLabel, cashSub, scroll);
        return card;
    }

    // ── Panel 3: Restock ───────────────────────────────────────────────────
    private VBox buildRestockPanel() {
        VBox card = makeCard();

        Label title = cardTitle("Restock");

        ingredientDropdown = new ComboBox<>();
        for (Ingredient ing : Ingredient.values()) {
            ingredientDropdown.getItems().add(ing.name());
        }
        ingredientDropdown.getSelectionModel().selectFirst();
        ingredientDropdown.setMaxWidth(Double.MAX_VALUE);
        ingredientDropdown.setStyle(
            "-fx-background-color: " + BG_ITEM + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-text-fill: " + TEXT_MUTED + ";"
        );

        Button buyBtn = accentButton("Buy Ingredient — 5.00 AZN", COL_OK);
        buyBtn.setMaxWidth(Double.MAX_VALUE);
        buyBtn.setOnAction(e -> {
            Ingredient ing = Ingredient.valueOf(ingredientDropdown.getValue());
            boolean ok = inventoryManager.restock(ing);
            appendLog(ok
                ? "Restocked 10x " + ing.name() + " for 5.00 AZN"
                : "ERROR: Not enough balance to restock!");
            refreshDisplay();
        });

        HBox saveRow = new HBox(8);
        Button saveBtn = ghostButton("Save Game");
        Button loadBtn = ghostButton("Load Game");
        HBox.setHgrow(saveBtn, Priority.ALWAYS);
        HBox.setHgrow(loadBtn, Priority.ALWAYS);
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        loadBtn.setMaxWidth(Double.MAX_VALUE);

        saveBtn.setOnAction(e -> {
            SaveLoadManager.save(inventoryManager, engine.getOrderQueue());
            appendLog("OK: Game saved successfully.");
        });
        loadBtn.setOnAction(e -> {
            boolean ok = SaveLoadManager.load(inventoryManager, engine.getOrderQueue());
            appendLog(ok ? "OK: Game loaded successfully." : "ERROR: No save file found.");
            refreshDisplay();
        });

        saveRow.getChildren().addAll(saveBtn, loadBtn);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(title, ingredientDropdown, buyBtn, spacer, saveRow);
        return card;
    }

    // ── Panel 4: System Log ────────────────────────────────────────────────
    private VBox buildLogPanel() {
        VBox card = makeCard();

        Label title = cardTitle("System Log");

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setFont(Font.font("Consolas", 12));
        logArea.setStyle(
            "-fx-control-inner-background: " + BG_LOG + ";" +
            "-fx-text-fill: " + COL_OK + ";" +
            "-fx-background-color: " + BG_LOG + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );
        VBox.setVgrow(logArea, Priority.ALWAYS);

        card.getChildren().addAll(title, logArea);
        return card;
    }

    // ── Refresh ────────────────────────────────────────────────────────────
    private void refreshDisplay() {
        // Cash
        cashLabel.setText(String.format("%.2f AZN", inventoryManager.getCash()));

        // Ingredients with progress bars
        ingredientBox.getChildren().clear();
        for (Ingredient ing : Ingredient.values()) {
            int stock = inventoryManager.getStock(ing);
            int max   = 10;
            double pct = Math.min((double) stock / max, 1.0);
            boolean low = stock <= 2;

            // Row
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 4, 2, 4));

            Label nameLabel = new Label(ing.name());
            nameLabel.setFont(Font.font("Segoe UI", 12));
            nameLabel.setTextFill(Color.web(TEXT_MUTED));
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            Label valLabel = new Label(String.valueOf(stock));
            valLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            valLabel.setTextFill(Color.web(low ? COL_ERR : TEXT_MAIN));

            row.getChildren().addAll(nameLabel, valLabel);

            // Progress bar
            StackPane barBg = new StackPane();
            barBg.setMinHeight(3);
            barBg.setMaxHeight(3);
            barBg.setStyle("-fx-background-color: " + BG_ITEM + "; -fx-background-radius: 2;");

            HBox barFill = new HBox();
            barFill.setMinHeight(3);
            barFill.setMaxHeight(3);
            barFill.setMaxWidth(pct * 460);
            barFill.setStyle("-fx-background-color: " + (low ? COL_ERR : ACCENT) + "; -fx-background-radius: 2;");
            barFill.setAlignment(Pos.CENTER_LEFT);
            StackPane.setAlignment(barFill, Pos.CENTER_LEFT);
            barBg.getChildren().add(barFill);

            ingredientBox.getChildren().addAll(row, barBg);
        }

        // Queue
        queueBox.getChildren().clear();
        int i = 1;
        for (MenuItem item : engine.getOrderQueue()) {
            HBox qRow = new HBox(10);
            qRow.setAlignment(Pos.CENTER_LEFT);
            qRow.setPadding(new Insets(10, 14, 10, 14));
            qRow.setStyle(
                "-fx-background-color: " + BG_ITEM + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;"
            );

            Label num = new Label("#" + i++);
            num.setFont(Font.font("Segoe UI", 11));
            num.setTextFill(Color.web(TEXT_MUTED));

            Label nameLabel = new Label(item.getName());
            nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            nameLabel.setTextFill(Color.web(TEXT_MAIN));
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            String badgeText  = item.getRequiredApplianceType().replace("_", " ");
            String badgeColor = getBadgeColor(item.getRequiredApplianceType());
            Label badge = new Label(badgeText);
            badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
            badge.setTextFill(Color.web(badgeColor));
            badge.setPadding(new Insets(3, 8, 3, 8));
            badge.setStyle(
                "-fx-background-color: " + badgeColor + "22;" +
                "-fx-border-color: " + badgeColor + "66;" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;"
            );

            qRow.getChildren().addAll(num, nameLabel, badge);
            queueBox.getChildren().add(qRow);
        }
    }

    private String getBadgeColor(String type) {
        switch (type) {
            case "GRILL":          return "#f0843a";
            case "FRYER":          return "#f0c83a";
            case "DRINK_DISPENSER":return "#3af0a0";
            case "COFFEE_MACHINE": return "#c8a96e"; 
            default:               return TEXT_MUTED;
        }
    }

    private void appendLog(String message) {
        String prefix = "› ";
        logArea.appendText(prefix + message + "\n");
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    // ── UI Helpers ─────────────────────────────────────────────────────────
    private VBox makeCard() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(14));
        box.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        );
        return box;
    }

    private Label cardTitle(String text) {
        Label lbl = new Label(text.toUpperCase());
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        lbl.setStyle("-fx-letter-spacing: 2;");
        return lbl;
    }

    private Button accentButton(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setTextFill(Color.web(BG_APP));
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 16 10 16;"
        );
        return btn;
    }

    private Button ghostButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        btn.setTextFill(Color.web(TEXT_MUTED));
        btn.setStyle(
            "-fx-background-color: " + BG_ITEM + ";" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 8 12 8 12;"
        );
        return btn;
    }
}