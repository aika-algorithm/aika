package network.aika.model;

import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.*;

import static network.aika.elements.typedef.FieldTags.INITIAL_CATEGORY_SYNAPSE_WEIGHT;
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


    public void initNodes() {
        activation = new ActivationDefinition(
                "BindingCategoryActivation",
                Activation.class
        )
                .addParent(superType.getActivation());

        neuron = new NeuronDefinition(
                "BindingCategoryNeuron",
                Neuron.class
        )
                .addParent(superType.getNeuron())
                .setActivation(activation);

        inputField(
                neuron,
                INITIAL_CATEGORY_SYNAPSE_WEIGHT
        );
    }


    public void initRelations() {
        inputLink = new LinkDefinition(
                "CategoryInputLink",
                Link.class
        )
                .addParent(superType.getLink());

        inputSynapse = new SynapseDefinition(
                "CategoryInputSynapse",
                Synapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(inputLink);

        link = new LinkDefinition(
                "CategoryLink",
                Link.class)
                .addParent(superType.link);

        synapse = new SynapseDefinition(
                "CategorySynapse",
                Synapse.class
        )
                .addParent(superType.synapse)
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
