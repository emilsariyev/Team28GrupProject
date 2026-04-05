import java.util.HashMap;
import java.util.Map;

public class Beverage extends MenuItem {

    public Beverage() {
    super("🥤 Juice", 3.0);
}

    @Override
    public Map<Ingredient, Integer> getRequiredIngredients() {
        Map<Ingredient, Integer> req = new HashMap<>();
        req.put(Ingredient.SYRUP, 1);
        req.put(Ingredient.CUPS, 1);
        return req;
    }

    @Override
    public String getRequiredApplianceType() {
        return "DRINK_DISPENSER";
    }
}