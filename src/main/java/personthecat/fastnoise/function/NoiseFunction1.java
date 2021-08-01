package personthecat.fastnoise.function;

@FunctionalInterface
public interface NoiseFunction1 {
    float getNoise(int seed, float x);
}
