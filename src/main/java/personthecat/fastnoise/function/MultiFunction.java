package personthecat.fastnoise.function;

import personthecat.fastnoise.FastNoise;

public interface MultiFunction {
    MultiFunction.D3 NO_OP = (x, y, z, generators) -> 0;

    float getNoise(float x, float y, FastNoise[] generators);
    float getNoise(float x, float y, float z, FastNoise[] generators);

    @FunctionalInterface // (x, y, generators) -> n
    interface D2 extends MultiFunction {
        @Override
        default float getNoise(float x, float y, float z, FastNoise[] generators) {
            return this.getNoise(x, y, generators);
        }
    }

    @FunctionalInterface // (x, y, z, generators) -> n
    interface D3 extends MultiFunction {
        @Override
        default float getNoise(float x, float y, FastNoise[] generators) {
            return this.getNoise(x, y, 1337, generators);
        }
    }

    @FunctionalInterface // (o) -> n
    interface Combiner extends MultiFunction {
        float getNoise(float[] output);

        @Override
        default float getNoise(float x, float y, FastNoise[] generators) {
            float[] output = new float[generators.length];
            for (int i = 0; i < generators.length; i++) {
                output[i] = generators[i].getNoiseScaled(x, y);
            }
            return this.getNoise(output);
        }

        @Override
        default float getNoise(float x, float y, float z, FastNoise[] generators) {
            float[] output = new float[generators.length];
            for (int i = 0; i < generators.length; i++) {
                output[i] = generators[i].getNoiseScaled(x, y, z);
            }
            return this.getNoise(output);
        }
    }
}
