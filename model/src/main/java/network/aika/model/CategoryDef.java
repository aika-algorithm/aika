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

    private SynapseSlotTypeDefinition categoryInputAnnealingSynapseOutputSlot;

    private LinkTypeDefinition categoryInputLink;
    private SynapseTypeDefinition categoryInputSynapse;

    private LinkTypeDefinition categoryLink;
    private SynapseTypeDefinition categorySynapse;


    public CategoryDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }


    public void init() {
        categoryInputAnnealingSynapseOutputSlot = new SynapseSlotTypeDefinition(
                "CategoryInputAnnealingSynapseOutputSlot",
                ConjunctiveSynapseSlot.class
        )
                .addParent(typeModel.conjunctiveDef.getAnnealingSynapseOutputSlot());

        categoryInputLink = new LinkTypeDefinition(
                "CategoryInputLink",
                Link.class
        );

        categoryInputSynapse = new SynapseTypeDefinition(
                "CategoryInputSynapse",
                Synapse.class
        );

        categoryInputSynapse.initialCategorySynapseWeight = new FieldDefinition<Synapse, Field>(
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

    public SynapseSlotTypeDefinition getCategoryInputAnnealingSynapseOutputSlot() {
        return categoryInputAnnealingSynapseOutputSlot;
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
