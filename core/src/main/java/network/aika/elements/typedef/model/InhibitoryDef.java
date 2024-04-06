package network.aika.elements.typedef.model;

import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.DisjunctiveActivation;
import network.aika.elements.links.CategoryLink;
import network.aika.elements.links.ConjunctiveCategoryInputLink;
import network.aika.elements.links.DisjunctiveLink;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.DisjunctiveNeuron;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.ActivationTypeDefinition;
import network.aika.elements.typedef.LinkTypeDefinition;
import network.aika.elements.typedef.NeuronTypeDefinition;
import network.aika.elements.typedef.SynapseTypeDefinition;

import static network.aika.ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.elements.NeuronType.*;
import static network.aika.elements.activations.StateType.PRE_FEEDBACK;
import static network.aika.elements.activations.bsslots.BSSlotDefinition.SINGLE_INPUT;
import static network.aika.enums.Transition.INPUT_INPUT;
import static network.aika.enums.Transition.SAME_INPUT;
import static network.aika.enums.Trigger.FIRED_PRE_FEEDBACK;
import static network.aika.enums.Trigger.NOT_FIRED;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;

public class InhibitoryDef {

    private TypeModel typeModel;

    private ActivationTypeDefinition inhibitoryActivation;
    private NeuronTypeDefinition inhibitoryNeuron;
    private ActivationTypeDefinition inhibitoryCategoryActivation;
    private NeuronTypeDefinition inhibitoryCategoryNeuron;

    private LinkTypeDefinition inhibitoryLink;
    private SynapseTypeDefinition inhibitorySynapse;
    private LinkTypeDefinition primaryInhibitoryLink;
    private SynapseTypeDefinition primaryInhibitorySynapse;
    private LinkTypeDefinition inhibitoryCategoryInputLink;
    private SynapseTypeDefinition inhibitoryCategoryInputSynapse;
    private LinkTypeDefinition inhibitoryCategoryLink;
    private SynapseTypeDefinition inhibitoryCategorySynapse;

    public InhibitoryDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public void init() {

        inhibitoryActivation = new ActivationTypeDefinition(
                "InhibitoryActivation",
                DisjunctiveActivation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState());

        inhibitoryNeuron = new NeuronTypeDefinition(
                "InhibitoryNeuron",
                DisjunctiveNeuron.class
        )
                .setNeuronType(INHIBITORY)
                .setActivationType(inhibitoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(100,100,255);");


        inhibitoryCategoryActivation = new ActivationTypeDefinition(
                "InhibitoryCategoryActivation",
                CategoryActivation.class
        )
                .addStateType(typeModel.states.getPreFeedbackState());

        inhibitoryCategoryNeuron = new NeuronTypeDefinition(
                "InhibitoryCategoryNeuron",
                CategoryNeuron.class
        )
                .setNeuronType(CATEGORY)
                .setActivationType(inhibitoryCategoryActivation)
                .setActivationFunction(LIMITED_RECTIFIED_LINEAR_UNIT)
                .setBindingSignalSlots(SINGLE_INPUT)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(100,0,200);");


        inhibitoryLink = new LinkTypeDefinition(
                "InhibitoryLink",
                DisjunctiveLink.class
        )
                .setInputDef(typeModel.bindingDef.getBindingActivation())
                .setOutputDef(inhibitoryActivation);

        inhibitorySynapse = new SynapseTypeDefinition(
                "InhibitorySynapse",
                DisjunctiveSynapse.class
        )
                .setLinkType(inhibitoryLink)
                .setInputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(BINDING)
                .setOutputNeuronType(INHIBITORY)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(INPUT)
                .setDebugStyle("fill-color: rgb(100,100,255);");


        primaryInhibitoryLink = new LinkTypeDefinition(
                "PrimaryInhibitoryLink",
                DisjunctiveLink.class);

        primaryInhibitorySynapse = new SynapseTypeDefinition(
                "PrimaryInhibitorySynapse",
                DisjunctiveSynapse.class
        )
                .setLinkType(primaryInhibitoryLink)
                .setInputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(PATTERN)
                .setOutputNeuronType(INHIBITORY)
                .setTransition(SAME_INPUT)
                .setRequired(SAME_INPUT)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setDebugStyle("fill-color: rgb(70,70,210);");


        inhibitoryCategoryInputLink = new LinkTypeDefinition(
                "InhibitoryCategoryInputLink",
                ConjunctiveCategoryInputLink.class);

        inhibitoryCategoryInputSynapse = new SynapseTypeDefinition(
                "InhibitoryCategoryInputSynapse",
                Synapse.class
        )
                .setLinkType(inhibitoryCategoryInputLink)
                .setInputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(CATEGORY)
                .setOutputNeuronType(BINDING)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setOutputState(PRE_FEEDBACK)
                .setTrigger(FIRED_PRE_FEEDBACK)
                .setStoredAt(OUTPUT)
                .setTrainingAllowed(false)
                .setDebugStyle("fill-color: rgb(110,200,220); ");


        inhibitoryCategoryLink = new LinkTypeDefinition(
                "InhibitoryCategoryLink",
                CategoryLink.class);

        inhibitoryCategorySynapse = new SynapseTypeDefinition(
                "InhibitoryCategorySynapse",
                CategorySynapse.class
        )
                .setLinkType(inhibitoryCategoryLink)
                .setInputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseInputSlot())
                .setOutputSlotType(typeModel.disjunctiveDef.getDisjunctiveSynapseOutputSlot())
                .setInputNeuronType(INHIBITORY)
                .setOutputNeuronType(CATEGORY)
                .setTransition(INPUT_INPUT)
                .setRequired(INPUT_INPUT)
                .setTrigger(NOT_FIRED)
                .setStoredAt(INPUT)
                .setDebugStyle("fill-color: rgb(110,0,220);");
    }


    public ActivationTypeDefinition getInhibitoryActivation() {
        return inhibitoryActivation;
    }

    public NeuronTypeDefinition getInhibitoryNeuron() {
        return inhibitoryNeuron;
    }

    public ActivationTypeDefinition getInhibitoryCategoryActivation() {
        return inhibitoryCategoryActivation;
    }

    public NeuronTypeDefinition getInhibitoryCategoryNeuron() {
        return inhibitoryCategoryNeuron;
    }

    public LinkTypeDefinition getInhibitoryLink() {
        return inhibitoryLink;
    }

    public SynapseTypeDefinition getInhibitorySynapse() {
        return inhibitorySynapse;
    }

    public LinkTypeDefinition getPrimaryInhibitoryLink() {
        return primaryInhibitoryLink;
    }

    public SynapseTypeDefinition getPrimaryInhibitorySynapse() {
        return primaryInhibitorySynapse;
    }

    public LinkTypeDefinition getInhibitoryCategoryInputLink() {
        return inhibitoryCategoryInputLink;
    }

    public SynapseTypeDefinition getInhibitoryCategoryInputSynapse() {
        return inhibitoryCategoryInputSynapse;
    }

    public LinkTypeDefinition getInhibitoryCategoryLink() {
        return inhibitoryCategoryLink;
    }

    public SynapseTypeDefinition getInhibitoryCategorySynapse() {
        return inhibitoryCategorySynapse;
    }

}
