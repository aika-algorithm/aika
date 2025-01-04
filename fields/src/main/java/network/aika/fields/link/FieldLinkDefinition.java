package network.aika.fields.link;

import network.aika.fields.defs.FieldDefinition;
import network.aika.type.Type;
import network.aika.type.relations.Relation;
import network.aika.type.Obj;


public class FieldLinkDefinition<
        IT extends Type<IT, IO>,
        IO extends Obj<IT, IO>,
        OT extends Type<OT, OO>,
        OO extends Obj<OT, OO>
        > {

    private final FieldDefinition<IT, IO> input;
    private final FieldDefinition<OT, OO> output;
    private final Relation<IT, IO, OT, OO> inputToOutputRelation;


    public FieldLinkDefinition(
            FieldDefinition<IT, IO> input,
            FieldDefinition<OT, OO> output,
            Relation<IT, IO, OT, OO> inputToOutputRelation
    ) {
        this.input = input;
        this.output = output;
        this.inputToOutputRelation = inputToOutputRelation;
    }

    public FieldDefinition<IT, IO> getInput() {
        return input;
    }

    public Relation<IT, IO, OT, OO> getInputToOutputRelationType() {
        return inputToOutputRelation;
    }

    public FieldDefinition<OT, OO> getOutput() {
        return output;
    }

    public Relation<OT, OO, IT, IO> getOutputToInputRelationType() {
        return inputToOutputRelation.getReverse();
    }

    public void fetchFrom(IO sourceObj, OO obj) {
        var rt = getOutputToInputRelationType();
        if (sourceObj != null) {
            if (rt.testRelation(obj, sourceObj))
                fetchFromObject(sourceObj, obj);
        } else {
            rt.followAll(obj)
                    .forEach(fo ->
                        fetchFromObject(fo, obj)
                    );
        }
    }

    private void fetchFromObject(IO sourceObj, OO obj) {
        double inputFieldValue = sourceObj.getFieldValue(input);
        output.receiveUpdate(obj, this, inputFieldValue);
    }

    @Override
    public String toString() {
        return input + " -- (" + inputToOutputRelation + ") -> " + output;
    }
}
