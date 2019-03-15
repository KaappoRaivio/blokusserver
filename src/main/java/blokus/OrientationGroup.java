package blokus;

public enum OrientationGroup {
    SYMMETRIC_90_NO_FLIP (true, false, false, false, false, false, false, false),
    SYMMETRIC_180_NO_FLIP (true, false, true, false, false, false, false, false),
    SYMMETRIC_180_FLIP (true, true, true, true, false, false, false, false),
    SYMMETRIC_360_NO_FLIP (true, false, true, false, true, false, true, false),
    SYMMETRIC_360_FLIP (true, true, true, true, true, true, true, true);

    private boolean upFalse;
    private boolean upTrue;
    private boolean leftFalse;
    private boolean leftTrue;
    private boolean downFalse;
    private boolean downTrue;
    private boolean rightFalse;
    private boolean rightTrue;

    OrientationGroup(boolean upFalse, boolean upTrue, boolean leftFalse, boolean leftTrue, boolean downFalse, boolean downTrue, boolean rightFalse, boolean rightTrue) {
        this.upFalse = upFalse;
        this.upTrue = upTrue;
        this.leftFalse = leftFalse;
        this.leftTrue = leftTrue;
        this.downFalse = downFalse;
        this.downTrue = downTrue;
        this.rightFalse = rightFalse;
        this.rightTrue = rightTrue;
    }

    public boolean isRelevant (Orientation orientation, boolean flip) {
        if (flip) {
            switch (orientation) {
                case UP:
                    return upTrue;
                case DOWN:
                    return downTrue;
                case LEFT:
                    return leftTrue;
                case RIGHT:
                    return rightTrue;
                default:
                    throw new RuntimeException("Invalid stuff (shouldn't happen)");
            }
        } else {
            switch (orientation) {
                case UP:
                    return upFalse;
                case DOWN:
                    return downFalse;
                case LEFT:
                    return leftFalse;
                case RIGHT:
                    return rightFalse;
                default:
                    throw new RuntimeException("Invalid stuff (shouldn't happen)");
            }
        }
    }
}
