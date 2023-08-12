package network.aika.fields;

import network.aika.debugger.FieldObserver;

import java.util.ArrayList;
import java.util.List;

public class SumField extends Field {

    private List<FieldLink> inputs;

    private List<FieldObserver> observers = new ArrayList<>();


    public SumField(FieldObject reference, String label, Double tolerance) {
        super(reference, label, tolerance);
    }

    public void addObserver(FieldObserver observer) {
        if(observers.contains(observer))
            return;

        observers.add(observer);
    }

    public void removeObserver(FieldObserver observer) {
        observers.remove(observer);
    }

    protected void updateObservers() {
        observers.forEach(o ->
                o.receiveUpdate(value)
        );
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
