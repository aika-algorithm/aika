package network.aika.model;

import network.aika.elements.typedef.ActivationDefinition;
import network.aika.elements.typedef.LinkDefinition;
import network.aika.elements.typedef.NeuronDefinition;
import network.aika.elements.typedef.SynapseDefinition;

public abstract class TypeDefinitionBase implements TypeDefinition {

    protected TypeModel typeModel;

    public TypeDefinitionBase(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public TypeModel getTypeModel() {
        return typeModel;
    }

    @Override
    public ActivationDefinition getCategoryActivation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NeuronDefinition getCategoryNeuron() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LinkDefinition getCategoryInputLink() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SynapseDefinition getCategoryInputSynapse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LinkDefinition getCategoryLink() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SynapseDefinition getCategorySynapse() {
        throw new UnsupportedOperationException();
    }
}
