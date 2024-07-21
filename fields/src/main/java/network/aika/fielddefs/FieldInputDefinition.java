package network.aika.fielddefs;


import network.aika.fielddefs.inputs.FieldInputsDefinition;
import network.aika.fielddefs.link.FieldLinkDefinition;

public interface FieldInputDefinition<O extends ObjectDefinition<O>, F extends FieldLinkDefinition<F>> {

    FieldInputsDefinition<O, F> getInputs();

    O getObject();

}
