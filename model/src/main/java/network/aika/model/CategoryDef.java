package network.aika.model;

import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.ActivationDefinition;
import network.aika.elements.typedef.LinkDefinition;
import network.aika.elements.typedef.NeuronDefinition;
import network.aika.elements.typedef.SynapseDefinition;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.Field;

import static network.aika.elements.activations.StateType.NON_FEEDBACK;
import static network.aika.fields.InputField.inputField;

public class CategoryDef {

    private TypeModel typeModel;

    private DisjunctiveDef superType;

    private ActivationDefinition activation;

    private NeuronDefinition neuron;

    private LinkDefinition link;
    private SynapseDefinition synapse;

    private LinkDefinition inputLink;
    private SynapseDefinition inputSynapse;


    public CategoryDef(TypeModel typeModel, DisjunctiveDef superType) {
        this.typeModel = typeModel;
        this.superType = superType;
    }


    public void init() {
        activation = new ActivationDefinition(
                "BindingCategoryActivation",
                Activation.class
        )
                .addStateType(activation.getState(NON_FEEDBACK));

        neuron = new NeuronDefinition(
                "BindingCategoryNeuron",
                Neuron.class
        )
                .setActivation(activation);

        inputField(
                neuron,
                "initialCategorySynapseWeight"
        );

        inputLink = new LinkDefinition(
                "CategoryInputLink",
                Link.class
        );

        inputSynapse = new SynapseDefinition(
                "CategoryInputSynapse",
                Synapse.class
        )
                .setLink(inputLink);

        link = new LinkDefinition(
                "CategoryLink",
                Link.class);

        synapse = new SynapseDefinition(
                "CategorySynapse",
                Synapse.class
        )
                .setLink(link);
    }

    public TypeModel getTypeModel() {
        return typeModel;
    }

    public ActivationDefinition getActivation() {
        return activation;
    }

    public NeuronDefinition getNeuron() {
        return neuron;
    }

    public LinkDefinition getLink() {
        return link;
    }

    public SynapseDefinition getSynapse() {
        return synapse;
    }

    public LinkDefinition getInputLink() {
        return inputLink;
    }

    public SynapseDefinition getInputSynapse() {
        return inputSynapse;
    }
}
