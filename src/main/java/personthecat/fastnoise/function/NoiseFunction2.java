package personthecat.fastnoise.function;

@FunctionalInterface
public interface NoiseFunction2 {
    float getNoise(float x, float y);

    default boolean greaterThan(float x, float y, float t) {
        return this.getNoise(x, y) > t;
    }

    default boolean lessThan(float x, float y, float t) {
        return this.getNoise(x, y) < t;
    }

    default boolean in(float x, float y, float min, float max) {
        float noise = this.getNoise(x, y);
        return noise > min && noise < max;
    }
}
