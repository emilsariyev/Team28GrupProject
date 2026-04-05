import java.util.HashMap;
import java.util.Map;

public class InventoryManager {

    private HashMap<Ingredient, Integer> inventory;
    private double cash;

    private static final int RESTOCK_AMOUNT = 10;
    private static final double RESTOCK_COST = 5.0;

    public InventoryManager() {
        inventory = new HashMap<>();
        cash = 100.0;
        for (Ingredient i : Ingredient.values()) {
            inventory.put(i, 10);
        }
    }

    public boolean hasIngredients(Map<Ingredient, Integer> required) {
        for (Map.Entry<Ingredient, Integer> entry : required.entrySet()) {
            if (inventory.getOrDefault(entry.getKey(), 0) < entry.getValue())
                return false;
        }
        return true;
    }

    public void consumeIngredients(Map<Ingredient, Integer> required, double reward) {
        for (Map.Entry<Ingredient, Integer> entry : required.entrySet()) {
            inventory.merge(entry.getKey(), -entry.getValue(), Integer::sum);
        }
        cash += reward;
    }

    public boolean restock(Ingredient ingredient) {
        if (cash < RESTOCK_COST) return false;
        inventory.merge(ingredient, RESTOCK_AMOUNT, Integer::sum);
        cash -= RESTOCK_COST;
        return true;
    }

    public int getStock(Ingredient ingredient) {
        return inventory.getOrDefault(ingredient, 0);
    }

    public double getCash() { return cash; }

    public HashMap<Ingredient, Integer> getInventoryCopy() {
        return new HashMap<>(inventory);
    }

    public void setInventory(HashMap<Ingredient, Integer> inv) { this.inventory = inv; }
    public void setCash(double c) { this.cash = c; }
}