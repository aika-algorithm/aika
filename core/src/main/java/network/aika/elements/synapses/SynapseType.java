package network.aika.elements.synapses;

import network.aika.elements.Type;
import network.aika.enums.Scope;

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

    public Scope scope();
}
