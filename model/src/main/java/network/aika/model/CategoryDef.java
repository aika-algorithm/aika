package network.aika.model;

import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.synapses.slots.ConjunctiveSynapseSlot;
import network.aika.elements.typedef.LinkTypeDefinition;
import network.aika.elements.typedef.SynapseSlotTypeDefinition;
import network.aika.elements.typedef.SynapseTypeDefinition;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.Field;

import static network.aika.utils.Utils.TOLERANCE;

public class CategoryDef {

    private TypeModel typeModel;

    private DisjunctiveDef superType;

    private LinkTypeDefinition categoryInputLink;
    private SynapseTypeDefinition categoryInputSynapse;

    private LinkTypeDefinition categoryLink;
    private SynapseTypeDefinition categorySynapse;


    public CategoryDef(TypeModel typeModel, DisjunctiveDef superType) {
        this.typeModel = typeModel;
        this.superType = superType;
    }


    public void init() {
        categoryInputLink = new LinkTypeDefinition(
                "CategoryInputLink",
                Link.class
        );

        categoryInputSynapse = new SynapseTypeDefinition(
                "CategoryInputSynapse",
                Synapse.class
        );

        categoryInputSynapse.initialCategorySynapseWeight = new FieldDefinition<>(
                Field.class,
                categoryInputSynapse,
                "initialCategorySynapseWeight",
                TOLERANCE
        );

        categoryLink = new LinkTypeDefinition(
                "CategoryLink",
                Link.class);

        categorySynapse = new SynapseTypeDefinition(
                "CategorySynapse",
                Synapse.class
        );
    }

    public TypeModel getTypeModel() {
        return typeModel;
    }

    public LinkTypeDefinition getCategoryInputLink() {
        return categoryInputLink;
    }

    public SynapseTypeDefinition getCategoryInputSynapse() {
        return categoryInputSynapse;
    }


    public LinkTypeDefinition getCategoryLink() {
        return categoryLink;
    }

    public SynapseTypeDefinition getCategorySynapse() {
        return categorySynapse;
    }
}
