package personthecat.fastnoise.function;

@FunctionalInterface
public interface NoiseFunction2 {
    float getNoise(int seed, float x, float y);
}
