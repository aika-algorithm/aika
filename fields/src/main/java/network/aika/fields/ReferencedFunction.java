package network.aika.fields;

public interface ReferencedFunction<R extends FieldObject> {

    double f(R ref, double x);
}
