package network.aika.model;

import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.*;

import static network.aika.elements.typedef.FieldTags.INITIAL_CATEGORY_SYNAPSE_WEIGHT;
import static network.aika.fields.InputField.inputField;

public class CategoryDef extends TypeDefinitionBase {

    private ActivationDefinition activation;

    private NeuronDefinition neuron;

    private LinkDefinition link;
    private SynapseDefinition synapse;

    private LinkDefinition inputLink;
    private SynapseDefinition inputSynapse;


    public CategoryDef(TypeModel typeModel, DisjunctiveDef superType) {
        super(typeModel, superType);
    }

    public void initNodes() {
        activation = new ActivationDefinition(
                getTypeModel(),
                "BindingCategoryActivation",
                Activation.class
        )
                .addParent(superType.getActivation());

        neuron = new NeuronDefinition(
                getTypeModel(),
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
                getTypeModel(),
                "CategoryInputLink",
                Link.class
        )
                .addParent(superType.getLink());

        inputSynapse = new SynapseDefinition(
                getTypeModel(),
                "CategoryInputSynapse",
                Synapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(inputLink);

        link = new LinkDefinition(
                getTypeModel(),
                "CategoryLink",
                Link.class)
                .addParent(superType.getLink());

        synapse = new SynapseDefinition(
                getTypeModel(),
                "CategorySynapse",
                Synapse.class
        )
                .addParent(superType.getSynapse())
                .setLink(link);
    }

    @Override
    public TypeModel getTypeModel() {
        return superType.getTypeModel();
    }

    @Override
    public ActivationDefinition getActivation() {
        return activation;
    }

    @Override
    public NeuronDefinition getNeuron() {
        return neuron;
    }

    @Override
    public LinkDefinition getLink() {
        return link;
    }

    @Override
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
