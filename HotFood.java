import java.util.HashMap;
import java.util.Map;

public class HotFood extends MenuItem {
    private String foodType;

    public HotFood(String foodType) {
    super(foodType.equals("BURGER") ? "🍔 Burger" : "🍟 Fries",
          foodType.equals("BURGER") ? 8.0 : 4.0);
    this.foodType = foodType;
}

    @Override
    public Map<Ingredient, Integer> getRequiredIngredients() {
        Map<Ingredient, Integer> req = new HashMap<>();
        if (foodType.equals("BURGER")) {
            req.put(Ingredient.BUNS, 1);
            req.put(Ingredient.PATTIES, 1);
            req.put(Ingredient.CHEESE, 1);
            req.put(Ingredient.VEGETABLES, 1);  
        } else {
            req.put(Ingredient.FRIES, 2);
            req.put(Ingredient.OIL, 1);
        }
        return req;
    }

    @Override
    public String getRequiredApplianceType() {
        return foodType.equals("BURGER") ? "GRILL" : "FRYER";
    }

    public String getFoodType() { return foodType; }
}