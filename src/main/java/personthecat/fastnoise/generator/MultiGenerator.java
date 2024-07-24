package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.MultiType;
import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.function.MultiFunction;

import java.util.stream.Stream;

public abstract class MultiGenerator extends FastNoise {

    protected final FastNoise[] references;

    protected MultiGenerator(final NoiseDescriptor cfg) {
        super(cfg);
        this.references = Stream.of(cfg.noiseLookup()).map(NoiseDescriptor::generate).toArray(FastNoise[]::new);
    }

    @Override
    public NoiseDescriptor toDescriptor() {
        return super.toDescriptor().noiseLookup(
            Stream.of(this.references).map(FastNoise::toDescriptor).toArray(NoiseDescriptor[]::new));
    }

    @Override
    public float getSingle(final int seed, final float x) {
        return 0F;
    }

    @Override
    public float getSingle(final int seed, final float x, final float y) {
        return 0F;
    }

    @Override
    public float getSingle(final int seed, final float x, final float y, final float z) {
        return 0F;
    }

    public static class Min extends MultiGenerator {

        public Min(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getNoise(final float x) {
            float min = 1;
            for (final FastNoise reference : this.references) {
                min = Math.min(min, reference.getNoise(x));
            }
            return min;
        }

        @Override
        public float getNoise(final float x, final float y) {
            float min = 1;
            for (final FastNoise reference : this.references) {
                min = Math.min(min, reference.getNoise(x, y));
            }
            return min;
        }

        @Override
        public float getNoise(final float x, final float y, final float z) {
            float min = 1;
            for (final FastNoise reference : this.references) {
                min = Math.min(min, reference.getNoise(x, y, z));
            }
            return min;
        }

        @Override
        public NoiseDescriptor toDescriptor() {
            return super.toDescriptor().multi(MultiType.MIN);
        }
    }

    public static class Max extends MultiGenerator {

        public Max(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getNoise(final float x) {
            float max = -1;
            for (final FastNoise reference : this.references) {
                max = Math.max(max, reference.getNoise(x));
            }
            return max;
        }

        @Override
        public float getNoise(final float x, final float y) {
            float max = -1;
            for (final FastNoise reference : this.references) {
                max = Math.max(max, reference.getNoise(x, y));
            }
            return max;
        }

        @Override
        public float getNoise(final float x, final float y, final float z) {
            float max = -1;
            for (final FastNoise reference : this.references) {
                max = Math.max(max, reference.getNoise(x, y, z));
            }
            return max;
        }

        @Override
        public NoiseDescriptor toDescriptor() {
            return super.toDescriptor().multi(MultiType.MAX);
        }
    }

    public static class Avg extends MultiGenerator {

        public Avg(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getNoise(final float x) {
            float max = 0;
            for (final FastNoise reference : this.references) {
                max = Math.max(max, reference.getNoise(x));
            }
            return max / this.references.length;
        }

        @Override
        public float getNoise(final float x, final float y) {
            float max = 0;
            for (final FastNoise reference : this.references) {
                max = Math.max(max, reference.getNoise(x, y));
            }
            return max / this.references.length;
        }

        @Override
        public float getNoise(final float x, final float y, final float z) {
            float max = 0;
            for (final FastNoise reference : this.references) {
                max = Math.max(max, reference.getNoise(x, y, z));
            }
            return max / this.references.length;
        }

        @Override
        public NoiseDescriptor toDescriptor() {
            return super.toDescriptor().multi(MultiType.AVG);
        }
    }

    public static class Mul extends MultiGenerator {

        public Mul(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getNoise(final float x) {
            float out = this.references[0].getNoise(x);
            for (int i = 1; i < this.references.length; i++) {
                out *= this.references[i].getNoise(x);
            }
            return Math.max(-1, Math.min(1, out));
        }

        @Override
        public float getNoise(final float x, final float y) {
            float out = this.references[0].getNoise(x);
            for (int i = 1; i < this.references.length; i++) {
                out *= this.references[i].getNoise(x, y);
            }
            return Math.max(-1, Math.min(1, out));
        }

        @Override
        public float getNoise(final float x, final float y, final float z) {
            float out = this.references[0].getNoise(x);
            for (int i = 1; i < this.references.length; i++) {
                out *= this.references[i].getNoise(x, 0);
            }
            return Math.max(-1, Math.min(1, out));
        }

        @Override
        public float getNoiseScaled(final float x) {
            float out = this.references[0].getNoiseScaled(x);
            for (int i = 1; i < this.references.length; i++) {
                out *= this.references[i].getNoiseScaled(x);
            }
            return out;
        }

        @Override
        public float getNoiseScaled(final float x, final float y) {
            float out = this.references[0].getNoiseScaled(x, y);
            for (int i = 1; i < this.references.length; i++) {
                out *= this.references[i].getNoiseScaled(x, y);
            }
            return out;
        }

        @Override
        public float getNoiseScaled(final float x, final float y, final float z) {
            float out = this.references[0].getNoiseScaled(x, y, z);
            for (int i = 1; i < this.references.length; i++) {
                out *= this.references[i].getNoiseScaled(x, y, z);
            }
            return out;
        }

        @Override
        public NoiseDescriptor toDescriptor() {
            return super.toDescriptor().multi(MultiType.MUL);
        }
    }

    public static class Div extends MultiGenerator {

        public Div(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getNoise(final float x) {
            float out = this.references[0].getNoise(x);
            for (int i = 1; i < this.references.length; i++) {
                float value = this.references[i].getNoise(x);
                if (value != 0) {
                    out /= value;
                }
            }
            return Math.max(-1, Math.min(1, out));
        }

        @Override
        public float getNoise(final float x, final float y) {
            float out = this.references[0].getNoise(x);
            for (int i = 1; i < this.references.length; i++) {
                float value = this.references[i].getNoise(x, y);
                if (value != 0) {
                    out /= value;
                }
            }
            return Math.max(-1, Math.min(1, out));
        }

        @Override
        public float getNoise(final float x, final float y, final float z) {
            float out = this.references[0].getNoise(x);
            for (int i = 1; i < this.references.length; i++) {
                float value = this.references[i].getNoise(x, 0);
                if (value != 0) {
                    out /= value;
                }
            }
            return Math.max(-1, Math.min(1, out));
        }

        @Override
        public float getNoiseScaled(final float x) {
            float out = this.references[0].getNoiseScaled(x);
            for (int i = 1; i < this.references.length; i++) {
                float value = this.references[i].getNoiseScaled(x);
                if (value != 0) {
                    out /= value;
                }
            }
            return out;
        }

        @Override
        public float getNoiseScaled(final float x, final float y) {
            float out = this.references[0].getNoiseScaled(x, y);
            for (int i = 1; i < this.references.length; i++) {
                float value = this.references[i].getNoiseScaled(x, y);
                if (value != 0) {
                    out /= value;
                }
            }
            return out;
        }

        @Override
        public float getNoiseScaled(final float x, final float y, final float z) {
            float out = this.references[0].getNoiseScaled(x, y, z);
            for (int i = 1; i < this.references.length; i++) {
                float value = this.references[i].getNoiseScaled(x, y, z);
                if (value != 0) {
                    out /= value;
                }
            }
            return out;
        }

        @Override
        public NoiseDescriptor toDescriptor() {
            return super.toDescriptor().multi(MultiType.DIV);
        }
    }

    public static class Sum extends MultiGenerator {

        public Sum(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getNoise(final float x) {
            float sum = 0;
            for (final FastNoise reference : this.references) {
                sum += reference.getNoise(x);
            }
            return Math.max(-1, Math.min(1, sum));
        }

        @Override
        public float getNoise(final float x, final float y) {
            float sum = 0;
            for (final FastNoise reference : this.references) {
                sum += reference.getNoise(x, y);
            }
            return Math.max(-1, Math.min(1, sum));
        }

        @Override
        public float getNoise(final float x, final float y, final float z) {
            float sum = 0;
            for (final FastNoise reference : this.references) {
                sum += reference.getNoise(x, y, z);
            }
            return Math.max(-1, Math.min(1, sum));
        }

        @Override
        public float getNoiseScaled(final float x) {
            float sum = 0;
            for (final FastNoise reference : this.references) {
                sum += reference.getNoiseScaled(x);
            }
            return sum;
        }

        @Override
        public float getNoiseScaled(final float x, final float y) {
            float sum = 0;
            for (final FastNoise reference : this.references) {
                sum += reference.getNoiseScaled(x, y);
            }
            return sum;
        }

        @Override
        public float getNoiseScaled(final float x, final float y, final float z) {
            float sum = 0;
            for (final FastNoise reference : this.references) {
                sum += reference.getNoiseScaled(x, y, z);
            }
            return sum;
        }

        @Override
        public NoiseDescriptor toDescriptor() {
            return super.toDescriptor().multi(MultiType.SUM);
        }
    }

    public static class Function extends MultiGenerator {

        private final MultiFunction multiFunction;

        public Function(final NoiseDescriptor cfg) {
            super(cfg);
            this.multiFunction = cfg.multiFunction();
        }

        @Override
        public float getNoise(final float x) {
            return this.multiFunction.getNoise(x, 1337, this.references);
        }

        @Override
        public float getNoise(final float x, final float y) {
            return this.multiFunction.getNoise(x, y, this.references);
        }

        @Override
        public float getNoise(final float x, final float y, final float z) {
            return this.multiFunction.getNoise(x, y, z, this.references);
        }

        @Override
        public NoiseDescriptor toDescriptor() {
            return super.toDescriptor().multiFunction(this.multiFunction);
        }
    }
}
