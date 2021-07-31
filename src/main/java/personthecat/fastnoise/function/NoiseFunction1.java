package personthecat.fastnoise.function;

@FunctionalInterface
public interface NoiseFunction1 {
    float getNoise(float x);

    default boolean greaterThan(float x, float t) {
        return this.getNoise(x) > t;
    }

    default boolean lessThan(float x, float t) {
        return this.getNoise(x) < t;
    }

    default boolean in(float x, float min, float max) {
        final float noise = this.getNoise(x);
        return noise > min && noise < max;
    }
}
