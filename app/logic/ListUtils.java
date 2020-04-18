package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class ListUtils {

    public static <E> List<E> filter(List<E> eList, Function<? super E, Boolean> fn) {
        List<E> filteredList = new ArrayList<>();

        for (E e : eList) {
            if (fn.apply(e)) {
                filteredList.add(e);
            }
        }
        return filteredList;
    }
}
