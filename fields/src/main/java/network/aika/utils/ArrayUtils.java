package network.aika.utils;

public class ArrayUtils {

    public static boolean isAllNull(Object[] array) {
        for (Object element : array) {
            if (element != null) {
                return false; // As soon as a non-null element is found, return false
            }
        }
        return true; // If no non-null element is found, return true
    }
}
