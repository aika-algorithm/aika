package network.aika.fields.oneobject;


import network.aika.type.Type;
import network.aika.type.TypeRegistry;

import java.util.List;


public class TestType extends Type<TestType, TestObject> {

    public TestType(TypeRegistry registry, String name) {
        super(registry, name);
    }

    public TestObject instantiate() {
        return instantiate(
                List.of(TestType.class),
                List.of(this)
        );
    }
}
