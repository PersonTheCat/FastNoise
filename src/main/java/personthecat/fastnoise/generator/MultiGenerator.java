package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.NoiseDescriptor;

public abstract class MultiGenerator extends FastNoise {

    protected final FastNoise[] references;

    protected MultiGenerator(final NoiseDescriptor cfg) {
        super(cfg);
        this.references = compile(cfg.noiseLookup());
    }

    private static FastNoise[] compile(final NoiseDescriptor[] references) {
        final FastNoise[] compiled = new FastNoise[references.length];
        for (int i = 0; i < references.length; i++) {
            compiled[i] = references[i].generate();
        }
        return compiled;
    }

    public static class Min extends MultiGenerator {

        public Min(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getSingle(final int seed, final float x) {
            float min = 1;
            for (final FastNoise reference : this.references) {
                min = Math.min(min, reference.getSingle(seed, x));
            }
            return min;
        }

        @Override
        public float getSingle(final int seed, final float x, final float y) {
            float min = 1;
            for (final FastNoise reference : this.references) {
                min = Math.min(min, reference.getSingle(seed, x, y));
            }
            return min;
        }

        @Override
        public float getSingle(final int seed, final float x, final float y, final float z) {
            float min = 1;
            for (final FastNoise reference : this.references) {
                min = Math.min(min, reference.getSingle(seed, x, y, z));
            }
            return min;
        }
    }

    public static class Max extends MultiGenerator {

        public Max(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getSingle(final int seed, final float x) {
            float max = -1;
            for (final FastNoise reference : this.references) {
                max = Math.max(max, reference.getSingle(seed, x));
            }
            return max;
        }

        @Override
        public float getSingle(final int seed, final float x, final float y) {
            float max = -1;
            for (final FastNoise reference : this.references) {
                max = Math.max(max, reference.getSingle(seed, x, y));
            }
            return max;
        }

        @Override
        public float getSingle(final int seed, final float x, final float y, final float z) {
            float max = -1;
            for (final FastNoise reference : this.references) {
                max = Math.max(max, reference.getSingle(seed, x, y, z));
            }
            return max;
        }
    }

    public static class Avg extends MultiGenerator {

        public Avg(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getSingle(final int seed, final float x) {
            float max = 0;
            for (final FastNoise reference : this.references) {
                max = Math.max(max, reference.getSingle(seed, x));
            }
            return max / this.references.length;
        }

        @Override
        public float getSingle(final int seed, final float x, final float y) {
            float max = 0;
            for (final FastNoise reference : this.references) {
                max = Math.max(max, reference.getSingle(seed, x, y));
            }
            return max / this.references.length;
        }

        @Override
        public float getSingle(final int seed, final float x, final float y, final float z) {
            float max = 0;
            for (final FastNoise reference : this.references) {
                max = Math.max(max, reference.getSingle(seed, x, y, z));
            }
            return max / this.references.length;
        }
    }

    public static class Mul extends MultiGenerator {

        public Mul(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getSingle(final int seed, final float x) {
            float out = this.references[0].getSingle(seed, x);
            for (int i = 1; i < this.references.length; i++) {
                out *= this.references[i].getSingle(seed, x);
            }
            return Math.max(-1, Math.min(1, out));
        }

        @Override
        public float getSingle(final int seed, final float x, final float y) {
            float out = this.references[0].getSingle(seed, x);
            for (int i = 1; i < this.references.length; i++) {
                out *= this.references[i].getSingle(seed, x, y);
            }
            return Math.max(-1, Math.min(1, out));
        }

        @Override
        public float getSingle(final int seed, final float x, final float y, final float z) {
            float out = this.references[0].getSingle(seed, x);
            for (int i = 1; i < this.references.length; i++) {
                out *= this.references[i].getSingle(seed, x, 0);
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
    }

    public static class Div extends MultiGenerator {

        public Div(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getSingle(final int seed, final float x) {
            float out = this.references[0].getSingle(seed, x);
            for (int i = 1; i < this.references.length; i++) {
                float value = this.references[i].getSingle(seed, x);
                if (value != 0) {
                    out /= value;
                }
            }
            return Math.max(-1, Math.min(1, out));
        }

        @Override
        public float getSingle(final int seed, final float x, final float y) {
            float out = this.references[0].getSingle(seed, x);
            for (int i = 1; i < this.references.length; i++) {
                float value = this.references[i].getSingle(seed, x, y);
                if (value != 0) {
                    out /= value;
                }
            }
            return Math.max(-1, Math.min(1, out));
        }

        @Override
        public float getSingle(final int seed, final float x, final float y, final float z) {
            float out = this.references[0].getSingle(seed, x);
            for (int i = 1; i < this.references.length; i++) {
                float value = this.references[i].getSingle(seed, x, 0);
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
    }

    public static class Sum extends MultiGenerator {

        public Sum(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getSingle(final int seed, final float x) {
            float sum = 0;
            for (final FastNoise reference : this.references) {
                sum += reference.getSingle(seed, x);
            }
            return Math.max(-1, Math.min(1, sum));
        }

        @Override
        public float getSingle(final int seed, final float x, final float y) {
            float sum = 0;
            for (final FastNoise reference : this.references) {
                sum += reference.getSingle(seed, x, y);
            }
            return Math.max(-1, Math.min(1, sum));
        }

        @Override
        public float getSingle(final int seed, final float x, final float y, final float z) {
            float sum = 0;
            for (final FastNoise reference : this.references) {
                sum += reference.getSingle(seed, x, y, z);
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
    }
}
