package network.aika.fields.link;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.defs.FixedArgumentsFieldDefinition;
import network.aika.type.Type;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationOne;
import network.aika.fields.field.Field;
import network.aika.type.Obj;


public class ArgFieldLinkDefinition<
        IT extends Type<IT, IO>,
        IO extends Obj<IT, IO>,
        OT extends Type<OT, OO>,
        OO extends Obj<OT, OO>
        > extends FieldLinkDefinition<IT, IO, OT, OO> {

    private final int argument;

    public ArgFieldLinkDefinition(
            FieldDefinition<IT, IO> input,
            FixedArgumentsFieldDefinition<OT, OO> output,
            Relation<IT, IO, OT, OO> relation,
            int argument
    ) {
        super(input, output, relation);

        this.argument = argument;
    }

    public int getArgument() {
        return argument;
    }

    public Field getInputField(OO obj) {
        var rt = (RelationOne<OT, OO, IT, IO>) getOutputToInputRelationType();
        IO inputObj = rt.followOne(obj);

        return inputObj.getField(getInput());
    }

    public double getInputValue(OO obj) {
        Field f = getInputField(obj);
        return f != null ?
                f.getValue() :
                0.0;
    }

    public double getUpdatedInputValue(OO obj) {
        Field f = getInputField(obj);

        return f != null ?
                f.getUpdatedValue() :
                0.0;
    }
}
