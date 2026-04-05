import java.util.Map;

public abstract class MenuItem {
    private String name;
    private double reward;

    public MenuItem(String name, double reward) {
        this.name = name;
        this.reward = reward;
    }

    public abstract Map<Ingredient, Integer> getRequiredIngredients();
    public abstract String getRequiredApplianceType();

    public String getName()   { return name; }
    public double getReward() { return reward; }

    @Override
    public String toString() { return name; }
}