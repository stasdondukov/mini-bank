package bank;

import java.util.List;

public class CollectionUtils {

    public static <T> void copy(List<? extends T> source, List<? super T> target) {
        for (T element : source) {
            target.add(element);
        }
    }
}
