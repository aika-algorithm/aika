package network.aika.fields;


import network.aika.fields.link.FieldLink;
import network.aika.fields.link.FieldInputs;
import network.aika.utils.FieldWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class MultiField<O extends Obj, I extends FieldInputs<F>, F extends FieldLink> implements FieldInput<I, F>, FieldWritable {

    protected I inputs;

    private O object;

    protected FieldOutput[] outputFields;

    public MultiField(I inputs, FieldOutput[] outputFields) {
        this.inputs = inputs;
        this.outputFields = outputFields;
    }

    @Override
    public I getInputs() {
        return inputs;
    }

    public FieldOutput[] getOutputFields() {
        return outputFields;
    }

    @Override
    public Obj getObject() {
        return object;
    }

    public void setObject(O object) {
        this.object = object;
    }


    @Override
    public void write(DataOutput out) throws IOException {

    }

    @Override
    public void readFields(DataInput in) throws Exception {

    }
}
