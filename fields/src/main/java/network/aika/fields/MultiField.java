package network.aika.fields;


import network.aika.fields.link.FieldLink;
import network.aika.fields.link.Inputs;
import network.aika.utils.FieldWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MultiField<O extends FieldObject, I extends Inputs<F>, F extends FieldLink> implements FieldInput<I, F>, FieldWritable {

    private Field[] field;

    @Override
    public I getInputs() {
        return null;
    }

    @Override
    public FieldObject getObject() {
        return null;
    }

    @Override
    public void receiveUpdate(F fl, double u) {

    }

    @Override
    public void write(DataOutput out) throws IOException {

    }

    @Override
    public void readFields(DataInput in) throws Exception {

    }
}
