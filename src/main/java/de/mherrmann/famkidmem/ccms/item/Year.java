package de.mherrmann.famkidmem.ccms.item;

public class Year {

    private int value;
    private boolean enabled;

    private Year() {}

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
