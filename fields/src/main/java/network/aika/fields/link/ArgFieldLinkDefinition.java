package network.aika.fields.link;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.defs.FixedArgumentsFieldDefinition;
import network.aika.fields.direction.Direction;
import network.aika.type.Type;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationOne;
import network.aika.fields.field.Field;
import network.aika.type.Obj;


public class ArgFieldLinkDefinition<
        T extends Type<T, O>,
        O extends Obj<T, O>,
        RT extends Type<RT, RO>,
        RO extends Obj<RT, RO>
        > extends FieldLinkDefinition<T, O, RT, RO> {

    private final int argument;

    public ArgFieldLinkDefinition(
            FieldDefinition<T, O> originFD,
            FieldDefinition<RT, RO> relatedFD,
            Relation<T, O, RT, RO> relation,
            Direction direction,
            int argument
    ) {
        super(originFD, relatedFD, relation, direction);

        this.argument = argument;
    }

    public int getArgument() {
        return argument;
    }

    public Field getInputField(O obj) {
        var rt = (RelationOne<T, O, RT, RO>) getRelation();
        RO inputObj = rt.followOne(obj);

        return inputObj.getField(getRelatedFD());
    }

    public double getInputValue(O obj) {
        Field f = getInputField(obj);
        return f != null ?
                f.getValue() :
                0.0;
    }

    public double getUpdatedInputValue(O obj) {
        Field f = getInputField(obj);

        return f != null ?
                f.getUpdatedValue() :
                0.0;
    }
}
