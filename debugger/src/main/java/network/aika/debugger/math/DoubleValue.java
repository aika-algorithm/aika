package network.aika.debugger.math;

import static network.aika.utils.Utils.TOLERANCE;

public class DoubleValue implements Value<DoubleValue> {

    double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    @Override
    public DoubleValue add(DoubleValue x) {
        return new DoubleValue(value + x.value);
    }

    @Override
    public DoubleValue sub(DoubleValue x) {
        return new DoubleValue(value - x.value);
    }

    @Override
    public DoubleValue scale(double s) {
        return new DoubleValue(s * value);
    }

    @Override
    public DoubleValue mul(DoubleValue x) {
        return new DoubleValue(value * x.value);
    }

    @Override
    public boolean belowTolerance() {
        return Math.abs(value) < TOLERANCE;
    }

    @Override
    public DoubleValue getUninitialized() {
        return new DoubleValue(0.0);
    }

    @Override
    public DoubleValue negate() {
        return new DoubleValue(-value);
    }


    public double getValue() {
        return value;
    }
}
