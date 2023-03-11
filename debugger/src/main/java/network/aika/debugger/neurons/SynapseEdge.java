package network.aika.debugger.neurons;

import network.aika.debugger.Edge;
import network.aika.elements.synapses.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class SynapseEdge<S extends Synapse> extends Edge<S> {



    protected static Map<Class<? extends Synapse>, Class<? extends SynapseEdge>> typeMap = new HashMap<>();

    static {
        typeMap.put(InputPatternSynapse.class, SynapseEdge.class);
        typeMap.put(BindingCategoryInputSynapse.class, SynapseEdge.class);
        typeMap.put(PositiveFeedbackSynapse.class, SynapseEdge.class);
        typeMap.put(NegativeFeedbackSynapse.class, SynapseEdge.class);
        typeMap.put(SamePatternSynapse.class, SynapseEdge.class);
        typeMap.put(RelationInputSynapse.class, SynapseEdge.class);
        typeMap.put(ReversePatternSynapse.class, SynapseEdge.class);
        typeMap.put(PatternSynapse.class, SynapseEdge.class);
        typeMap.put(PatternCategoryInputSynapse.class, SynapseEdge.class);
        typeMap.put(InhibitorySynapse.class, SynapseEdge.class);
        typeMap.put(InhibitoryCategoryInputSynapse.class, SynapseEdge.class);
        typeMap.put(BindingCategorySynapse.class, SynapseEdge.class);
        typeMap.put(PatternCategorySynapse.class, SynapseEdge.class);
        typeMap.put(InhibitoryCategorySynapse.class, SynapseEdge.class);
    }

    public static SynapseEdge create(Synapse s) {
        Class<? extends SynapseEdge> clazz = typeMap.get(s.getClass());

        try {
            return clazz.getDeclaredConstructor(Synapse.class)
                    .newInstance(s);

        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public SynapseEdge(S syn) {
        super(syn);
    }

    /*
    public void calculateForce(Vector3 delta, Point3 pos, Direction dir, ActivationParticle other) {
        double targetDistance = getInitialYDistance();

        Point3 opos = other.getPosition();
        double dy = 0.0;

        if(dir == INPUT) {
            dy = (opos.y + targetDistance) - pos.y;
            dy = Math.max(0.0, dy);
        } else if(dir == OUTPUT) {
            dy = opos.y - (pos.y + targetDistance);
            dy = Math.min(0.0, dy);
        }

        delta.set(0.0, dy, 0.0);
    }
*/
}