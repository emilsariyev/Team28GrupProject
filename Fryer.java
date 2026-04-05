public class Fryer implements IAppliance {
    @Override
    public boolean canProcess(MenuItem item) {
        return "FRYER".equals(item.getRequiredApplianceType());
    }
    @Override
    public void processTask(MenuItem item) {
        System.out.println("[Fryer] Frying: " + item.getName());
    }
    @Override
    public String getType() { return "FRYER"; }
}