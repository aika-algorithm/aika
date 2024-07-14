package network.aika.fielddefs;


public interface FieldInputDefinition<O extends ObjectDefinition<O>, I extends FieldInputsDefinition<O>> {

    I getInputs();

    O getObject();

}
