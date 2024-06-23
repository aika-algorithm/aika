package network.aika.model;

import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.LinkDefinition;
import network.aika.elements.typedef.SynapseDefinition;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.Field;

import static network.aika.utils.Utils.TOLERANCE;

public class CategoryDef {

    private TypeModel typeModel;

    private DisjunctiveDef superType;

    private LinkDefinition categoryInputLink;
    private SynapseDefinition categoryInputSynapse;

    private LinkDefinition categoryLink;
    private SynapseDefinition categorySynapse;


    public CategoryDef(TypeModel typeModel, DisjunctiveDef superType) {
        this.typeModel = typeModel;
        this.superType = superType;
    }


    public void init() {
        categoryInputLink = new LinkDefinition(
                "CategoryInputLink",
                Link.class
        );

        categoryInputSynapse = new SynapseDefinition(
                "CategoryInputSynapse",
                Synapse.class
        );

        categoryInputSynapse.initialCategorySynapseWeight = new FieldDefinition<>(
                Field.class,
                categoryInputSynapse,
                "initialCategorySynapseWeight",
                TOLERANCE
        );

        categoryLink = new LinkDefinition(
                "CategoryLink",
                Link.class);

        categorySynapse = new SynapseDefinition(
                "CategorySynapse",
                Synapse.class
        );
    }

    public TypeModel getTypeModel() {
        return typeModel;
    }

    public LinkDefinition getCategoryInputLink() {
        return categoryInputLink;
    }

    public SynapseDefinition getCategoryInputSynapse() {
        return categoryInputSynapse;
    }


    public LinkDefinition getCategoryLink() {
        return categoryLink;
    }

    public SynapseDefinition getCategorySynapse() {
        return categorySynapse;
    }
}
