package network.aika.fields;

import network.aika.fields.link.FieldLink;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractListener<O extends FieldObject> implements FieldInput {

    private O ref;

    private String label;

    private FieldLink input;

    public AbstractListener(O ref, String label) {
        this.ref = ref;
        this.label = label;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void connectInputs(boolean initialize) {

    }

    @Override
    public void disconnectAndUnlinkInputs(boolean deinitialize) {

    }

    @Override
    public FieldObject getReference() {
        return ref;
    }

    @Override
    public String getLabel() {
        return label;
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
