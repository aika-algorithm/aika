package network.aika.elements.synapses;

import network.aika.elements.Type;
import network.aika.enums.LinkingMode;
import network.aika.enums.Transition;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SynapseType {

    public int synapseTypeId() default -1;

    public Type inputType();

    public Type outputType();

    public Transition[] transition();

    public Transition[] required();

    public Transition[] forbidden();

    public LinkingMode linkingMode() default LinkingMode.REGULAR;
}
