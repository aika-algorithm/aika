package network.aika.fields.link;

import network.aika.fields.defs.FieldDefinition;
import network.aika.type.Type;
import network.aika.type.relations.RelationType;
import network.aika.type.Obj;


public class FieldLinkDefinition<
        IT extends Type<IT, IO>,
        IO extends Obj<IT, IO>,
        OT extends Type<OT, OO>,
        OO extends Obj<OT, OO>
        > {

    private final FieldDefinition<IT, IO> input;
    private final FieldDefinition<OT, OO> output;
    private final RelationType<IT, IO, OT, OO> inputToOutputRelationType;


    public FieldLinkDefinition(
            FieldDefinition<IT, IO> input,
            FieldDefinition<OT, OO> output,
            RelationType<IT, IO, OT, OO> inputToOutputRelationType
    ) {
        this.input = input;
        this.output = output;
        this.inputToOutputRelationType = inputToOutputRelationType;
    }

    public FieldDefinition<IT, IO> getInput() {
        return input;
    }

    public RelationType<IT, IO, OT, OO> getInputToOutputRelationType() {
        return inputToOutputRelationType;
    }

    public FieldDefinition<OT, OO> getOutput() {
        return output;
    }

    public RelationType<OT, OO, IT, IO> getOutputToInputRelationType() {
        return inputToOutputRelationType.getReverse();
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
        return input + " -- (" + inputToOutputRelationType + ") -> " + output;
    }
}
