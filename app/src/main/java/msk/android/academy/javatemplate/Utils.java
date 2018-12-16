package msk.android.academy.javatemplate;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    public static Long getRandom() {
        return ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
    }
}
