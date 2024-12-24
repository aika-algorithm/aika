package network.aika.fields.softmax;


import network.aika.type.Type;
import network.aika.type.TypeRegistry;

import java.util.List;


public class SoftmaxOutputType extends Type<SoftmaxOutputType, SoftmaxOutputObj> {

    public SoftmaxOutputType(TypeRegistry registry, String name) {
        super(registry, name);
    }

    public SoftmaxOutputObj instantiate(int bsId) {
        return instantiate(
                List.of(SoftmaxOutputType.class, Integer.class),
                List.of(this, bsId)
        );
    }
}
