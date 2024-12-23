package network.aika.fields.manyobjects;

import network.aika.type.Type;
import network.aika.type.TypeRegistry;

import java.util.List;


public class TestTypeMany extends Type<TestTypeMany, TestObjectMany> {

    public TestTypeMany(TypeRegistry registry, String name) {
        super(registry, name);
    }

    public TestObjectMany instantiate() {
        return instantiate(
                List.of(TestTypeMany.class),
                List.of(this)
        );
    }
}
