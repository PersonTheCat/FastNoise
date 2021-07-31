package personthecat.fastnoise.function;

@FunctionalInterface
public interface NoiseFunction3 {
    float getNoise(float x, float y, float z);

    default boolean greaterThan(float x, float y, float z, float t) {
        return this.getNoise(x, y, z) > t;
    }

    default boolean lessThan(float x, float y, float z, float t) {
        return this.getNoise(x, y, z) < t;
    }

    default boolean in(float x, float y, float z, float min, float max) {
        float noise = this.getNoise(x, y, z);
        return noise > min && noise < max;
    }
}
