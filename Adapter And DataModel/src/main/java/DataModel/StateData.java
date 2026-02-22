package DataModel;

public class StateData {
    private String stateName;
    private String isoCode;

    public StateData(String stateName, String isoCode) {
        this.stateName = stateName;
        this.isoCode = isoCode;
    }

    public String getStateName() {
        return stateName;
    }

    public String getIsoCode() {
        return isoCode;
    }
}
