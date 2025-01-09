package network.aika.fields.oneobject;

import network.aika.type.TypeRegistry;
import network.aika.type.TypeRegistryImpl;
import network.aika.type.relations.RelationOne;


public abstract class AbstractTestWithObjects {

    protected TypeRegistry registry;

    protected TestType typeA;
    protected TestType typeB;

    public void init() {
        registry = new TypeRegistryImpl();

        typeA = new TestType(registry, "A")
                .setClazz(TestObject.class);

        typeB = new TestType(registry, "B")
                .setClazz(TestObject.class);
    }
}
