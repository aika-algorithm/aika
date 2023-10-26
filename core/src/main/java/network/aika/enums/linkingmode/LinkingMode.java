package network.aika.enums.linkingmode;

import network.aika.elements.activations.Activation;

/**
 *
 * @author Lukas Molzberger
 */
public interface LinkingMode {

    LinkingMode REGULAR = new Regular();
    LinkingMode FEEDBACK = new Feedback();

    boolean check(Activation oAct);
}
