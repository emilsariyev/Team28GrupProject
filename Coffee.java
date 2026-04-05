import java.util.HashMap;
import java.util.Map;

public class Coffee extends MenuItem {

    public Coffee() {
        super("Coffee", 5.0);
    }

    @Override
    public Map<Ingredient, Integer> getRequiredIngredients() {
        Map<Ingredient, Integer> req = new HashMap<>();
        req.put(Ingredient.COFFEE_BEANS, 2);
        req.put(Ingredient.MILK, 1);
        req.put(Ingredient.SUGAR, 1);
        req.put(Ingredient.CUPS, 1);
        return req;
    }

    @Override
    public String getRequiredApplianceType() {
        return "COFFEE_MACHINE";
    }
}