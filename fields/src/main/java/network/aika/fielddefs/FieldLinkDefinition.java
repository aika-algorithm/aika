package network.aika.fielddefs;



public class FieldLinkDefinition {

    boolean propagateUpdates;

    public FieldLinkDefinition(FieldOutputDefinition in, int arg, FieldInputDefinition out) {

    }

    public static FieldLinkDefinition link(FieldOutputDefinition in, int arg, FieldInputDefinition out) {
        FieldLinkDefinition fl = new FieldLinkDefinition(in, arg, out);
        out.addInput(fl);
        in.addOutput(fl);
        return fl;
    }

    public static FieldLinkDefinition link(FieldOutputDefinition in, FieldInputDefinition out) {
        return link(in, out.size(), out);
    }


    public static void linkAll(FieldOutputDefinition in, FieldInputDefinition... out) {
        assert in != null;

        for(FieldInputDefinition o : out) {
            if(o != null) {
                link(in, 0, o);
            }
        }
    }

    public void setPropagateUpdates(boolean propagateUpdates) {
        this.propagateUpdates = propagateUpdates;
    }
}
