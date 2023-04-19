package ch.epfl.tchu;

/**
 * @author Sebastian Maier (327504)
 * @author Theo Schifferli (326468)
 */
public final class Preconditions {

    private Preconditions() {


    }

    /**
     * Checks if the argument is correctly given
     * @param shouldBeTrue
     * @throws IllegalArgumentException if param is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }

}
