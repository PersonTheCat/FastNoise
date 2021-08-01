package personthecat.fastnoise.function;

@FunctionalInterface
public interface NoiseFunction3 {
    float getNoise(int seed, float x, float y, float z);
}
