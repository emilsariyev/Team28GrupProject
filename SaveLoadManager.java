import java.io.*;
import java.util.*;

public class SaveLoadManager {

    private static final String SAVE_FILE = "savegame.txt";

    public static void save(InventoryManager inv, Queue<MenuItem> queue) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            writer.println("CASH:" + inv.getCash());
            for (Map.Entry<Ingredient, Integer> entry : inv.getInventoryCopy().entrySet()) {
                writer.println("INGREDIENT:" + entry.getKey().name() + ":" + entry.getValue());
            }
            for (MenuItem item : queue) {
                if (item instanceof HotFood) {
                    writer.println("QUEUE:HOTFOOD:" + ((HotFood) item).getFoodType());
                } else if (item instanceof Beverage) {
                    writer.println("QUEUE:BEVERAGE:JUICE");
                } else if (item instanceof Coffee) {
                    writer.println("QUEUE:COFFEE:COFFEE");
                }
            }
        } catch (IOException e) {
            System.out.println("[SaveLoad] ERROR saving: " + e.getMessage());
        }
    }

    public static boolean load(InventoryManager inv, Queue<MenuItem> queue) {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return false;

        HashMap<Ingredient, Integer> loadedInv = new HashMap<>();
        queue.clear();
        double cash = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                switch (parts[0]) {
                    case "CASH":
                        cash = Double.parseDouble(parts[1]);
                        break;
                    case "INGREDIENT":
                        loadedInv.put(Ingredient.valueOf(parts[1]), Integer.parseInt(parts[2]));
                        break;
                    case "QUEUE":
                        if ("HOTFOOD".equals(parts[1]))   queue.add(new HotFood(parts[2]));
                        else if ("BEVERAGE".equals(parts[1])) queue.add(new Beverage());
                        else if ("COFFEE".equals(parts[1]))   queue.add(new Coffee());
                        break;
                }
            }
            inv.setInventory(loadedInv);
            inv.setCash(cash);
            return true;
        } catch (IOException e) {
            System.out.println("[SaveLoad] ERROR loading: " + e.getMessage());
            return false;
        }
    }
}