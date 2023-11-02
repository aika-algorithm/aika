package network.aika.utils;

import java.util.function.BinaryOperator;

public class BitUtils {

    public static BinaryOperator<Integer> aggregateOp() {
        return (a, b) -> a;
    }
}
