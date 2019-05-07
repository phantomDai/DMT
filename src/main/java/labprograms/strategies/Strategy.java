package labprograms.strategies;

public interface Strategy {
    public Object getTestCase(String objectName, String testframe);

    public void executeTestCase(String objectName);

}
