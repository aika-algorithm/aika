package network.aika.elements.typedef.model;

import network.aika.elements.links.CategoryInputLink;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.CategoryInputSynapse;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.typedef.LinkTypeDefinition;
import network.aika.elements.typedef.SynapseTypeDefinition;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.Field;
import network.aika.fields.SumField;

import static network.aika.utils.Utils.TOLERANCE;

public class CategoryDef {

    private TypeModel typeModel;


    private LinkTypeDefinition categoryInputLink;
    private SynapseTypeDefinition categoryInputSynapse;

    public CategoryDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }


    public void init() {
        categoryInputLink = new LinkTypeDefinition(
                "InputObjectLink",
                Link.class);

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
}
