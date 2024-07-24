package personthecat.fastnoise.function;

public interface NoiseFunction {
    float getNoise(int seed, float x);
    float getNoise(int seed, float x, float y);
    float getNoise(int seed, float x, float y, float z);

    @FunctionalInterface
    interface D1 extends NoiseFunction {
        @Override
        default float getNoise(int seed, float x, float y) {
            return this.getNoise(seed, x);
        }

        @Override
        default float getNoise(int seed, float x, float y, float z) {
            return this.getNoise(seed, x);
        }
    }

    @FunctionalInterface
    interface D2 extends NoiseFunction {
        @Override
        default float getNoise(int seed, float x) {
            return this.getNoise(seed, x, 1337);
        }

        @Override
        default float getNoise(int seed, float x, float y, float z) {
            return this.getNoise(seed, x, y);
        }
    }

    interface D3 extends NoiseFunction {
        float getNoise(int seed, float x, float y, float z);

        @Override
        default float getNoise(int seed, float x) {
            return this.getNoise(seed, x, 1337, 1337);
        }

        @Override
        default float getNoise(int seed, float x, float y) {
            return this.getNoise(seed, x, y, 1337);
        }
    }
}
