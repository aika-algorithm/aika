package network.aika.debugger.math;



import static network.aika.utils.Utils.TOLERANCE;

public class Vector implements Value<Vector> {

    public static Vector ZERO_VEC = new Vector(0, 0, 0);

    public double[] value;

    public Vector(int dim) {
        this.value = new double[dim];
    }

    public Vector(double... value) {
        this.value = value;
    }

    public double sum() {
        double s = 0.0;
        for (int i = 0; i < value.length; i++)
            s += value[i];

        return s;
    }

    @Override
    public Vector add(Vector x) {
        double[] result = new double[value.length];

        for(int i = 0; i < value.length; i++)
            result[i] = value[i] + x.value[i];

        return new Vector(result);
    }

    @Override
    public Vector sub(Vector x) {
        double[] result = new double[value.length];

        for(int i = 0; i < value.length; i++)
            result[i] = value[i] - x.value[i];

        return new Vector(result);
    }

    @Override
    public Vector scale(double s) {
        double[] result = new double[value.length];

        for(int i = 0; i < value.length; i++)
            result[i] = s * value[i];

        return new Vector(result);
    }

    @Override
    public Vector mul(Vector x) {
        double[] result = new double[value.length];

        for(int i = 0; i < value.length; i++)
            result[i] = value[i] * x.value[i];

        return new Vector(result);
    }

    @Override
    public boolean belowTolerance() {
        for(int i = 0; i < value.length; i++)
            if(Math.abs(value[i]) >= TOLERANCE)
                return false;

        return true;
    }

    @Override
    public Vector getUninitialized() {
        return new Vector(new double[value.length]);
    }

    @Override
    public Vector negate() {
        return new Vector(negate(value));
    }

    public static double[] negate(double[] x) {
        double[] result = new double[x.length];

        for(int i = 0; i < x.length; i++)
            for(int j = 0; j < x.length; j++)
                result[i] = -x[i];

        return result;
    }


    public double length() {
        return length(value);
    }

    public static double length(double[] a) {
        double y = 0.0;
        for (double x : a)
            y += Math.pow(x, 2);

        return Math.sqrt(y);
    }

    public Vector extendTo4d() {
        return new Vector(extendTo4dA(value));
    }

    public static double[] extendTo4dA(double[] x) {
        return new double[]{x[0], x[1], x[2], 1.0};
    }

    public Vector reduceTo3d() {
        return new Vector(new double[] {value[0], value[1], value[2]});
    }

    public Vector copy() {
        return new Vector(copy(value));
    }

    public static double[] copy(double[] x) {
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++)
            y[i] = x[i];

        return y;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for(int i = 0; i < value.length; i++) {
            sb.append(value[i]);
            if(i != value.length - 1)
                sb.append(", ");
        }
        sb.append(']');
        return sb.toString();
    }

    public double[] getValue() {
        return value;
    }
}
