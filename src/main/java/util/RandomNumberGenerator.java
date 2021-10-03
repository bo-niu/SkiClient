package util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomNumberGenerator {
    public static int getRandomNumberBetween(int a, int b) {
        return ThreadLocalRandom.current().nextInt(a, b);
    }
}
