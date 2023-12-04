package network.aika.suspension;

public enum SuspensionMode {
    SAVE_ALL(true),
    SAVE_SUSPENDED(true),
    DISCARD(false);

    private boolean save;

    SuspensionMode(boolean save) {
        this.save = save;
    }

    public boolean isSave() {
        return save;
    }
}
