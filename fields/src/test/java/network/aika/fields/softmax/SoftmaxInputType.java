package network.aika.fields.softmax;


import network.aika.type.Type;
import network.aika.type.TypeRegistry;

import java.util.List;


public class SoftmaxInputType extends Type<SoftmaxInputType, SoftmaxInputObj> {

    public SoftmaxInputType(TypeRegistry registry, String name) {
        super(registry, name);
    }

    public SoftmaxInputObj instantiate() {
        return instantiate(
                List.of(SoftmaxInputType.class),
                List.of(this)
        );
    }
}
