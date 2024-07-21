package network.aika.fielddefs.inputs;

import network.aika.enums.Direction;
import network.aika.fielddefs.FieldOutputDefinition;
import network.aika.fielddefs.ObjectDefinition;
import network.aika.fielddefs.ObjectPath;
import network.aika.fielddefs.ObjectRelationDefinition;
import network.aika.fielddefs.link.VariableFieldLinkDefinition;

import java.util.List;
import java.util.function.BiFunction;


public class VariableFieldInputsDefinition<O extends ObjectDefinition<O>> extends FieldInputsDefinition<O, VariableFieldLinkDefinition> {

    public VariableFieldInputsDefinition<O> in(BiFunction<O, ObjectPath, FieldOutputDefinition> pathProvider, boolean propagateUpdates) {
        ObjectPath objectPath = new ObjectPath(Direction.INPUT);
        objectPath.add(new ObjectRelationDefinition(object, o -> List.of(o)));
        FieldOutputDefinition in = pathProvider.apply(object, objectPath);

        VariableFieldLinkDefinition fl = new VariableFieldLinkDefinition(objectPath, in, this, propagateUpdates);
        addInput(fl);
        in.addOutput(fl);

        return this;
    }

    public VariableFieldInputsDefinition<O> in(BiFunction<O, ObjectPath, FieldOutputDefinition> pathProvider) {
        return in(pathProvider, true);
    }
}
