package network.aika.fields;


import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.synapses.Synapse;

public class SynapseSlot extends MaxField {
    public SynapseSlot(Synapse ref, String label) {
        super(ref, label);
    }

    public ConjunctiveLink getSelectedLink() {
        return getLink(getSelectedInput());
    }

    public static ConjunctiveLink getLink(FieldLink fl) {
        return (ConjunctiveLink) fl.getInput().getReference();
    }

    @Override
    protected boolean isCandidate(FieldLink fl) {
        return getLink(fl).getInput() != null;
    }
}
