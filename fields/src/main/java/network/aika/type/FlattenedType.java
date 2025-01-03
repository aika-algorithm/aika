package network.aika.type;

import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.link.FieldLinkDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

public class FlattenedType<T extends Type<T, O>, O extends Obj<T, O>> {

    private final T type;

    private final short[] fields;
    private final FieldDefinition<T, O>[] fieldsReverse;

    private FieldLinkDefinition<?, ?, T, O>[][] inputs; // From-Type, FD-List
    private FieldLinkDefinition<T, O, ?, ?>[][] outputs; // To-Type, FD-List


    public FlattenedType(T type) {
        this.type = type;

        fields = new short[type.getTypeRegistry().getNumberOfFields()];
        Arrays.fill(fields, (short) -1);

        short numberOfFields = 0;
        ArrayList<FieldDefinition<T, O>> fieldsRev = new ArrayList<>();
        SortedSet<Type<T, O>> sortedTypes = type.collectTypes();
        for (Type<T, O> t : sortedTypes) {
            for (FieldDefinition<T, O> fd : t.getFieldDefinitions()) {
                fields[fd.getFieldId()] = numberOfFields++;
                fieldsRev.add(fd);
            }
        }

        fieldsReverse = fieldsRev.toArray(new FieldDefinition[0]);

        for (Type<?, ?> relType: type.getTypeRegistry().getTypes()) {
            List<? extends FieldLinkDefinition> results = new ArrayList<>();
            for(FieldDefinition<T, O> fd: fieldsRev) {
                flattenOutputs(results, relType, fd);
            }
            outputs[relType.getId()] = results.toArray(new FieldLinkDefinition[0]);
        }
    }

    @SuppressWarnings("unchecked")
    private void flattenOutputs(List<? extends FieldLinkDefinition> results, Type<?, ?> relType, FieldDefinition<T, O> fd) {
        List<? extends FieldLinkDefinition> results = fd.getOutputs()
                .filter(fl -> fl.getOutput().getObjectType() == relType)
                .filter(fl ->
                        fl.getInputToOutputRelationType().testRelation(type, relType)
                )
                .toList();

        return results.toArray(new FieldLinkDefinition[0]);
    }

    public short getFieldIndex(FieldDefinition<T, O> fd) {
        return fields[fd.getFieldId()];
    }

    public short getNumberOfFields() {
        return (short) fieldsReverse.length;
    }

    public T getType() {
        return type;
    }

    public FieldDefinition<T, O> getFieldDefinitionIdByIndex(short idx) {
        return fieldsReverse[idx];
    }
}
