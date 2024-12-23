package network.aika.fields.manyobjects;


import network.aika.type.Type;
import network.aika.type.TypeRegistry;

import java.util.List;


public class TestTypeOne extends Type<TestTypeOne, TestObjectOne> {

    public TestTypeOne(TypeRegistry registry, String name) {
        super(registry, name);
    }

    public TestObjectOne instantiate() {
        return instantiate(
                List.of(TestTypeOne.class),
                List.of(this)
        );
    }
}
