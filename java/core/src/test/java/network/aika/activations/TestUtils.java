package network.aika.activations;

public class TestUtils {

    public static Link getInputLink(Activation oAct, int synId) {
        return oAct.getInputLinks()
                .filter(l -> l.getSynapse().getSynapseId() == synId)
                .findAny()
                .orElse(null);
    }
}
