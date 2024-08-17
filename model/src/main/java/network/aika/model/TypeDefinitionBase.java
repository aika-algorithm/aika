package network.aika.model;

import network.aika.elements.typedef.*;

public abstract class TypeDefinitionBase implements TypeDefinition {

    protected TypeModel typeModel;
    protected TypeDefinitionBase superType;

    public TypeDefinitionBase(TypeModel typeModel, TypeDefinitionBase superType) {
        this.typeModel = typeModel;
        this.superType = superType;
    }

    public TypeModel getTypeModel() {
        return typeModel;
    }

    public TypeDefinitionBase getSuperType() {
        return superType;
    }

    @Override
    public SynapseSlotDefinition getInputSlot() {
        return superType.getInputSlot();
    }

    @Override
    public SynapseSlotDefinition getOutputSlot() {
        return superType.getOutputSlot();
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
