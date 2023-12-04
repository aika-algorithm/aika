package network.aika.queue.steps;

import network.aika.Document;
import network.aika.fields.Field;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;
import network.aika.queue.Step;

public class FeedbackTrigger extends ElementStep<Document> {


    boolean isInstantiation;

    boolean state;

    public static void add(Document doc, boolean inst, boolean state) {
        Step.add(new FeedbackTrigger(doc, inst, state));
    }

    public static void add(Document doc, boolean inst) {
        Step.add(new FeedbackTrigger(doc, inst, true));
        Step.add(new FeedbackTrigger(doc, inst, false));
    }

    public FeedbackTrigger(Document doc, boolean isInstantiation, boolean state) {
        super(doc);
        this.isInstantiation = isInstantiation;
        this.state = state;
    }

    @Override
    public void process() {
        Document doc = getElement();
        Field ft = isInstantiation ?
                doc.getInstantiationFeedbackTrigger() :
                doc.getFeedbackTrigger();

        ft.receiveUpdate(
                null,
                state ?
                        1.0 :
                        -1.0
        );
    }

    @Override
    public Phase getPhase() {
        return state ?
                Phase.PRE_FEEDBACK_TRIGGER :
                Phase.POST_FEEDBACK_TRIGGER;
    }

    @Override
    public String toString() {
        return super.toString() + " Inst:" + isInstantiation + " State:" + state;
    }
}
