package network.aika.fields;

import network.aika.debugger.FieldObserver;

import java.util.ArrayList;
import java.util.List;

public class SumField extends Field {

    private List<FieldLink> inputs;

    public SumField(FieldObject reference, String label, Double tolerance) {
        super(reference, label, tolerance);
    }

    @Override
    protected void initIO() {
        super.initIO();

        inputs = new ArrayList<>();
    }

    @Override
    public int getNextArg() {
        return inputs.size();
    }

    @Override
    public void addInput(FieldLink l) {
        inputs.add(l);
    }

    @Override
    public void removeInput(FieldLink l) {
        inputs.remove(l);
    }

    @Override
    public List<FieldLink> getInputs() {
        return inputs;
    }
}
