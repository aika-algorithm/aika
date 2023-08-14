package network.aika.queue.link;


import network.aika.elements.links.RelationInputLink;
import network.aika.queue.ElementStep;
import network.aika.queue.Phase;

public class InstantiateCorrespondingSPS extends ElementStep<RelationInputLink> {

    private RelationInputLink newInstance;

    public static void add(RelationInputLink template, RelationInputLink newInstance) {
        add(new InstantiateCorrespondingSPS(template, newInstance));
    }

    private InstantiateCorrespondingSPS(RelationInputLink template, RelationInputLink newInstance) {
        super(template);

        this.newInstance = newInstance;
    }

    @Override
    public Phase getPhase() {
        return Phase.POST_INSTANTIATION;
    }

    @Override
    public void process() {
        RelationInputLink l = getElement();

        l.instantiateCorrespondingSPS(newInstance);
    }
}
