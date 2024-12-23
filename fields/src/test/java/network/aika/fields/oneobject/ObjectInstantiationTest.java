package network.aika.fields.oneobject;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ObjectInstantiationTest extends AbstractTestWithObjects {

    @BeforeEach
    public void init() {
        super.init();
    }

    @Test
    public void testObjectInstantiation() {
        TestObject oa = typeA.instantiate();

        Assertions.assertNotNull(oa);
    }
}
