package network.aika.elements.synapses;

import network.aika.elements.Type;
import network.aika.enums.Transition;
import network.aika.enums.linkingmode.LinkingMode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.util.Set;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SynapseType {

    public int synapseTypeId() default -1;

    public Type inputType();

    public Type outputType();

    public Transition[] transition();

}
