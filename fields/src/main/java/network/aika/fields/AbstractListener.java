package network.aika.fields;

import network.aika.fields.link.FieldLink;
import network.aika.fields.link.FixedInputs;
import network.aika.fields.link.Inputs;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractListener<O extends FieldObject> extends Field<O, FixedInputs, FieldLink> {

    public AbstractListener() {
        super(new FixedInputs(1));
    }
}
