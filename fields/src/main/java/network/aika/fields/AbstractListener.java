package network.aika.fields;

import network.aika.fields.link.FieldLink;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractListener<O extends FieldObject> extends Field<O, FieldLink> {

    private FieldLink input;


    @Override
    public int size() {
        return 1;
    }

    @Override
    public void addInput(FieldLink l) {
        input = l;
    }

    @Override
    public void removeInput(FieldLink l) {
        input = null;
    }

    @Override
    public List<FieldLink> getInputs() {
        return Arrays.asList(input);
    }
}
