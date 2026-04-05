public class DrinkDispenser implements IAppliance {
    @Override
    public boolean canProcess(MenuItem item) {
        return "DRINK_DISPENSER".equals(item.getRequiredApplianceType());
    }
    @Override
    public void processTask(MenuItem item) {
        System.out.println("[DrinkDispenser] Dispensing: " + item.getName());
    }
    @Override
    public String getType() { return "DRINK_DISPENSER"; }
}