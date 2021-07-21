// %3040931838:de.hattrickorganizer.logik.matchengine%
package core.prediction.engine;

import java.util.Map;

public class TeamRatings {
    //~ Instance fields ----------------------------------------------------------------------------

    private double leftAttack;
    private double leftDef;
    private double middleAttack;
    private double middleDef;
    private double midfield;
    private double rightAttack;
    private double rightDef;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TeamRatings object.
     */
    public TeamRatings() {
    }

    public TeamRatings(double midfield, double leftDef, double middleDef, double rightDef,
                       double leftAttack, double middleAttack, double rightAttack) {
        this.midfield = midfield;
        this.leftDef = leftDef;
        this.middleDef = middleDef;
        this.rightDef = rightDef;
        this.leftAttack = leftAttack;
        this.middleAttack = middleAttack;
        this.rightAttack = rightAttack;
    }

    public final void setLeftAttack(double d) {
        leftAttack = d;
    }

    public final double getLeftAttack() {
        return leftAttack;
    }

    public final void setLeftDef(double d) {
        leftDef = d;
    }

    public final double getLeftDef() {
        return leftDef;
    }

    public final void setMiddleAttack(double d) {
        middleAttack = d;
    }

    public final double getMiddleAttack() {
        return middleAttack;
    }

    public final void setMiddleDef(double d) {
        middleDef = d;
    }

    public final double getMiddleDef() {
        return middleDef;
    }

    public final void setMidfield(double d) {
        midfield = d;
    }

    public final double getMidfield() {
        return midfield;
    }

    public final void setRightAttack(double d) {
        rightAttack = d;
    }

    public final double getRightAttack() {
        return rightAttack;
    }

    public final void setRightDef(double d) {
        rightDef = d;
    }

    public final double getRightDef() {
        return rightDef;
    }

    @Override
	public final String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("TeamRatings[");
        buffer.append("leftDef = " + leftDef);
        buffer.append(", middleDef = " + middleDef);
        buffer.append(", rightDef = " + rightDef);
        buffer.append(", leftAttack = " + leftAttack);
        buffer.append(", middleAttack = " + middleAttack);
        buffer.append(", rightAttack = " + rightAttack);
        buffer.append(", midfield = " + midfield);
        buffer.append("]");
        return buffer.toString();
    }
}
