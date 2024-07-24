package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.MultiType;
import personthecat.fastnoise.data.NoiseBuilder;
import personthecat.fastnoise.function.MultiFunction;

import java.util.stream.Stream;

public abstract class MultiNoise extends FastNoise {

    protected final FastNoise[] references;

    protected MultiNoise(final NoiseBuilder cfg) {
        super(cfg);
        this.references = cfg.buildReferences();
    }

    @Override
    public NoiseBuilder toBuilder() {
        return super.toBuilder().references(
            Stream.of(this.references).map(FastNoise::toBuilder).toArray(NoiseBuilder[]::new));
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

    public static class Min extends MultiNoise {

        public Min(final NoiseBuilder cfg) {
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
        public NoiseBuilder toBuilder() {
            return super.toBuilder().multi(MultiType.MIN);
        }
    }

    public static class Max extends MultiNoise {

        public Max(final NoiseBuilder cfg) {
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
        public NoiseBuilder toBuilder() {
            return super.toBuilder().multi(MultiType.MAX);
        }
    }

    public static class Avg extends MultiNoise {

        public Avg(final NoiseBuilder cfg) {
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
        public NoiseBuilder toBuilder() {
            return super.toBuilder().multi(MultiType.AVG);
        }
    }

    public static class Mul extends MultiNoise {

        public Mul(final NoiseBuilder cfg) {
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
        public NoiseBuilder toBuilder() {
            return super.toBuilder().multi(MultiType.MUL);
        }
    }

    public static class Div extends MultiNoise {

        public Div(final NoiseBuilder cfg) {
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
        public NoiseBuilder toBuilder() {
            return super.toBuilder().multi(MultiType.DIV);
        }
    }

    public static class Sum extends MultiNoise {

        public Sum(final NoiseBuilder cfg) {
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
        public NoiseBuilder toBuilder() {
            return super.toBuilder().multi(MultiType.SUM);
        }
    }

    public static class Function extends MultiNoise {

        private final MultiFunction multiFunction;

        public Function(final NoiseBuilder cfg) {
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
        public NoiseBuilder toBuilder() {
            return super.toBuilder().multiFunction(this.multiFunction);
        }
    }
}
