public class CoffeeMachine implements IAppliance {
    @Override
    public boolean canProcess(MenuItem item) {
        return "COFFEE_MACHINE".equals(item.getRequiredApplianceType());
    }
    @Override
    public void processTask(MenuItem item) {
        System.out.println("[CoffeeMachine] Brewing: " + item.getName());
    }
    @Override
    public String getType() { return "COFFEE_MACHINE"; }
}