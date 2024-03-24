package network.aika.fielddefs;

import network.aika.fields.Field;

import java.util.ArrayList;
import java.util.List;

public class FieldDefinition implements FieldInputDefinition, FieldOutputDefinition {

    protected int fieldId;

    protected Class<? extends Field> clazz;

    protected FieldObjectDefinition ref;

    protected String fieldName;

    protected Double tolerance;

    protected List<FieldLinkDefinition> inputs = new ArrayList<>();
    protected List<FieldLinkDefinition> outputs = new ArrayList<>();



    public FieldDefinition(Class<? extends Field> clazz, FieldObjectDefinition ref, String name) {
        this.clazz = clazz;
        this.ref = ref;
        this.fieldName = name;
    }

    public FieldDefinition(Class<? extends Field> clazz, FieldObjectDefinition ref, String name, double tolerance) {
        this(clazz, ref, name);

        this.tolerance = tolerance;
    }

    public void setFieldId(int id) {
        fieldId = id;
    }

    @Override
    public void addInput(FieldLinkDefinition fl) {
        inputs.add(fl);
    }

    @Override
    public int size() {
        return inputs.size();
    }

    @Override
    public void addOutput(FieldLinkDefinition fl) {
        outputs.add(fl);
    }
}
