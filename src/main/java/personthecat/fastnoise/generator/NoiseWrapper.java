package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.NoiseBuilder;
import personthecat.fastnoise.function.NoiseFunction;

public class NoiseWrapper {

    private NoiseFunction noiseFunction = (NoiseFunction.D1) (s, x) -> 0;

    public NoiseFunction noiseFunction() {
        return this.noiseFunction;
    }

    public NoiseWrapper wrapFunction(final NoiseFunction noiseFunction) {
        this.noiseFunction = noiseFunction;
        return this;
    }

    public NoiseWrapper wrapFunction(final NoiseFunction.D1 d1) {
        return this.wrapFunction((NoiseFunction) d1);
    }

    public NoiseWrapper wrapFunction(final NoiseFunction.D2 d2) {
        return this.wrapFunction((NoiseFunction) d2);
    }

    public NoiseWrapper wrapFunction(final NoiseFunction.D3 d3) {
        return this.wrapFunction((NoiseFunction) d3);
    }

    public NoiseBuilder createBuilder() {
        return new NoiseBuilder().provider(cfg -> new WrappedGenerator(this, cfg));
    }

    public FastNoise generatePassthrough() {
        return this.generatePassthrough(1337);
    }

    public FastNoise generatePassthrough(final int seed) {
        return new PassthroughGenerator(this, seed);
    }

    public static class WrappedGenerator extends FastNoise {

        protected final NoiseFunction noiseFunction;

        private WrappedGenerator(final NoiseWrapper wrapper, final NoiseBuilder cfg) {
            super(cfg);
            this.noiseFunction = wrapper.noiseFunction;
        }

        private WrappedGenerator(final NoiseWrapper wrapper, final int seed) {
            this(wrapper, FastNoise.builder().seed(seed));
        }

        @Override
        public float getSingle(int seed, float x) {
            return this.noiseFunction.getNoise(seed, x);
        }

        @Override
        public float getSingle(int seed, float x, float y) {
            return this.noiseFunction.getNoise(seed, x, y);
        }

        @Override
        public float getSingle(int seed, float x, float y, float z) {
            return this.noiseFunction.getNoise(seed, x, y, z);
        }

        public NoiseWrapper toWrapper() {
            return FastNoise.wrapper().wrapFunction(this.noiseFunction);
        }

        @Override
        public NoiseBuilder toBuilder() {
            final NoiseWrapper wrapper = this.toWrapper();
            return super.toBuilder().provider(cfg -> new WrappedGenerator(wrapper, cfg));
        }
    }

    public static class PassthroughGenerator extends WrappedGenerator {

        private PassthroughGenerator(final NoiseWrapper wrapper, final int seed) {
            super(wrapper, seed);
        }

        @Override
        public float getNoise(final float x) {
            return this.noiseFunction.getNoise(this.seed, x);
        }

        @Override
        public float getNoise(float x, float y) {
            return this.noiseFunction.getNoise(this.seed, x, y);
        }

        @Override
        public float getNoise(float x, float y, float z) {
            return this.noiseFunction.getNoise(this.seed, x, y, z);
        }

        @Override
        public NoiseBuilder toBuilder() {
            return super.toWrapper().createBuilder();
        }
    }
}

