public class Grill implements IAppliance {
    @Override
    public boolean canProcess(MenuItem item) {
        return "GRILL".equals(item.getRequiredApplianceType());
    }
    @Override
    public void processTask(MenuItem item) {
        System.out.println("[Grill] Cooking: " + item.getName());
    }
    @Override
    public String getType() { return "GRILL"; }
}