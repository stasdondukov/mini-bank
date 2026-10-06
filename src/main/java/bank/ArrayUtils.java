package bank;

public class ArrayUtils {

    public static <T> T first(T[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Array is empty or null");
        }
        return values[0];
    }

    public static <T> T last(T[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Array is empty or null");
        }
        return values[values.length - 1];
    }

    public static <T> boolean contains(T[] values, T target) {
        if (values == null) {
            return false;
        }
        for (T element : values) {
            if (target == null) {
                if (element == null) {
                    return true;
                }
            } else if (target.equals(element)) {
                return true;
            }
        }
        return false;
    }
}
