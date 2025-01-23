package network.aika;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

public class HelloWorld {

    // Method to return a greeting message
    @CEntryPoint(name = "greet")
    public static CCharPointer greet(IsolateThread thread, CCharPointer cFilter) {
        String input = CTypeConversion.toJavaString(cFilter);
        String result = "Hello, " + new String(input) + "!";
        return CTypeConversion.toCString(result).get();
    }
}
