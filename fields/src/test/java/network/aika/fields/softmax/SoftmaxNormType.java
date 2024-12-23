package network.aika.fields.softmax;

import network.aika.type.Type;
import network.aika.type.TypeRegistry;

import java.util.List;


public class SoftmaxNormType extends Type<SoftmaxNormType, SoftmaxNormObj> {

    public SoftmaxNormType(TypeRegistry registry, String name) {
        super(registry, name);
    }

    public SoftmaxNormObj instantiate() {
        return instantiate(
                List.of(SoftmaxNormType.class),
                List.of(this)
        );
    }
}
