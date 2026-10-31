package bank;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class CollectionFunctions {

    private CollectionFunctions() {
    }

    public static <T> List<T> filter(List<T> source, Predicate<? super T> condition) {
        List<T> result = new ArrayList<>();
        for (T value : source) {
            if (condition.test(value)) {
                result.add(value);
            }
        }
        return result;
    }
}
