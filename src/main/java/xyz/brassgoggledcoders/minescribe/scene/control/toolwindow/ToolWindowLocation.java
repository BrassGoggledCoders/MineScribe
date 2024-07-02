package xyz.brassgoggledcoders.minescribe.scene.control.toolwindow;

public enum ToolWindowLocation {
    LEFT_TOP(true, true),
    LEFT_BOTTOM(true, false, true),
    BOTTOM_LEFT(true, false),
    RIGHT_TOP(false, true),
    RIGHT_BOTTOM(false, false, true),
    BOTTOM_RIGHT(false, false);

    public static final String KEY = "toolWindowLocation";

    private final boolean left;
    private final boolean top;
    private final boolean grow;

    ToolWindowLocation(boolean left, boolean top) {
        this(left, top, false);
    }

    ToolWindowLocation(boolean left, boolean top, boolean grow) {
        this.left = left;
        this.top = top;
        this.grow = grow;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isTop() {
        return top;
    }

    public boolean isGrow() {
        return grow;
    }
}
