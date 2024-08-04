package network.aika.fields;

import network.aika.fields.link.FixedFieldLink;
import network.aika.fields.link.FixedFieldInputs;


public abstract class AbstractListener<O extends Obj> extends Field<O, FixedFieldInputs, FixedFieldLink> {

    public AbstractListener() {
        super(new FixedFieldInputs(1));
    }
}
