package network.aika.debugger.math;


public interface Value<V extends Value> {

    V add(V x);

    V sub(V x);

    V scale(double s);

    V mul(V x);

    boolean belowTolerance();

    V getUninitialized();

    V negate();
}
