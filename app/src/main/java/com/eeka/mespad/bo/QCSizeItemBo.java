package com.eeka.mespad.bo;

public class QCSizeItemBo {

    /**
     * UNIT : cm
     * MEASURED_ATTRIBUTE : KYW
     * SIZE : 69
     */

    private String UNIT;
    private String MEASURED_ATTRIBUTE;
    private String MEASURED_DESC;
    private String SIZE;

    public String getMEASURED_DESC() {
        return MEASURED_DESC;
    }

    public void setMEASURED_DESC(String MEASURED_DESC) {
        this.MEASURED_DESC = MEASURED_DESC;
    }

    public String getUNIT() {
        return UNIT;
    }

    public void setUNIT(String UNIT) {
        this.UNIT = UNIT;
    }

    public String getMEASURED_ATTRIBUTE() {
        return MEASURED_ATTRIBUTE;
    }

    public void setMEASURED_ATTRIBUTE(String MEASURED_ATTRIBUTE) {
        this.MEASURED_ATTRIBUTE = MEASURED_ATTRIBUTE;
    }

    public String getSIZE() {
        return SIZE;
    }

    public void setSIZE(String SIZE) {
        this.SIZE = SIZE;
    }

}
