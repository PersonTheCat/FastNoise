package personthecat.fastnoise.generator;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.function.NoiseFunction1;
import personthecat.fastnoise.function.NoiseFunction2;
import personthecat.fastnoise.function.NoiseFunction3;

@Getter
@Setter
@Accessors(fluent = true)
public class DummyNoiseWrapper {

    private NoiseFunction1 wrapNoise1 = (s, x) -> 0;
    private NoiseFunction2 wrapNoise2 = (s, x, y) -> 0;
    private NoiseFunction3 wrapNoise3 = (s, x, y, z) -> 0;

    public NoiseDescriptor createDescriptor() {
        return new NoiseDescriptor().provider(cfg -> new WrappedGenerator(this, cfg));
    }

    public FastNoise generatePassthrough() {
        return this.generatePassthrough(1337);
    }

    public FastNoise generatePassthrough(final int seed) {
        return new PassthroughGenerator(this, seed);
    }

    public static class WrappedGenerator extends FastNoise {

        protected final NoiseFunction1 wrapNoise1;
        protected final NoiseFunction2 wrapNoise2;
        protected final NoiseFunction3 wrapNoise3;

        private WrappedGenerator(final DummyNoiseWrapper wrapper, final NoiseDescriptor cfg) {
            super(cfg);
            this.wrapNoise1 = wrapper.wrapNoise1;
            this.wrapNoise2 = wrapper.wrapNoise2;
            this.wrapNoise3 = wrapper.wrapNoise3;
        }

        private WrappedGenerator(final DummyNoiseWrapper wrapper, final int seed) {
            this(wrapper, FastNoise.createDescriptor().seed(seed));
        }

        @Override
        public float getSingle(int seed, float x) {
            return this.wrapNoise1.getNoise(seed, x);
        }

        @Override
        public float getSingle(int seed, float x, float y) {
            return this.wrapNoise2.getNoise(seed, x, y);
        }

        @Override
        public float getSingle(int seed, float x, float y, float z) {
            return this.wrapNoise3.getNoise(seed, x, y, z);
        }

        public DummyNoiseWrapper toWrapper() {
            return FastNoise.createWrapper()
                .wrapNoise1(this.wrapNoise1)
                .wrapNoise2(this.wrapNoise2)
                .wrapNoise3(this.wrapNoise3);
        }

        @Override
        public NoiseDescriptor toDescriptor() {
            final DummyNoiseWrapper wrapper = this.toWrapper();
            return super.toDescriptor().provider(cfg -> new WrappedGenerator(wrapper, cfg));
        }
    }

    public static class PassthroughGenerator extends WrappedGenerator {

        private PassthroughGenerator(final DummyNoiseWrapper wrapper, final int seed) {
            super(wrapper, seed);
        }

        @Override
        public float getNoise(final float x) {
            return this.wrapNoise1.getNoise(this.seed, x);
        }

        @Override
        public float getNoise(float x, float y) {
            return this.wrapNoise2.getNoise(this.seed, x, y);
        }

        @Override
        public float getNoise(float x, float y, float z) {
            return this.wrapNoise3.getNoise(this.seed, x, y, z);
        }

        @Override
        public NoiseDescriptor toDescriptor() {
            return super.toWrapper().createDescriptor();
        }
    }
}

