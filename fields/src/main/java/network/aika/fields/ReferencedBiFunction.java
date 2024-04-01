
package network.aika.fields;

/**
 * @author Lukas Molzberger
 */
public interface ReferencedBiFunction<R extends FieldObject> {

    double f(R ref, double a, double b);
}
