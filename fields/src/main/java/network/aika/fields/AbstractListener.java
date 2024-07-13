package network.aika.fields;

import network.aika.fields.link.FixedFieldLink;
import network.aika.fields.link.FixedInputs;


public abstract class AbstractListener<O extends FieldObject> extends Field<O, FixedInputs, FixedFieldLink> {

    public AbstractListener() {
        super(new FixedInputs(1));
    }
}
