package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.function.NoiseProvider;

import static personthecat.fastnoise.util.NoiseUtils.getFractalBounding;

public abstract class FractalNoise extends FastNoise {

    protected final float lacunarityX;
    protected final float lacunarityY;
    protected final float lacunarityZ;
    protected final float gain;
    protected final int octaves;
    protected final float fractalBounding;
    protected final FastNoise reference;

    public FractalNoise(final NoiseDescriptor cfg, final FastNoise reference) {
        super(cfg);
        this.lacunarityX = cfg.lacunarityX();
        this.lacunarityY = cfg.lacunarityY();
        this.lacunarityZ = cfg.lacunarityZ();
        this.gain = cfg.gain();
        this.octaves = cfg.octaves();
        this.fractalBounding = getFractalBounding(this.gain, this.octaves);
        this.reference = reference;
    }

    public FractalNoise(final int seed, final FastNoise reference) {
        super(seed);
        this.lacunarityX = 2.0F;
        this.lacunarityY = 2.0F;
        this.lacunarityZ = 2.0F;
        this.gain = 0.5F;
        this.octaves = 3;
        this.fractalBounding = getFractalBounding(this.gain, this.octaves);
        this.reference = reference;
    }

    public static FastNoise create(final NoiseDescriptor cfg, final NoiseProvider provider) {
        return create(cfg, provider.apply(cfg));
    }

    public static FastNoise create(final NoiseDescriptor cfg, final FastNoise reference) {
        switch (cfg.fractal()) {
            case FBM: return new Fbm(cfg, reference);
            case BILLOW: return new Billow(cfg, reference);
            case RIGID_MULTI: return new Rigid(cfg, reference);
            case PING_PONG: return new PingPong(cfg, reference);
            default: return reference;
        }
    }

    protected abstract float fractal(final float f);

    @Override
    public float getSingle(int seed, float x) {
        float sum = this.fractal(this.reference.getSingle(seed, x));
        float amp = 1;

        for (int i = 1; i < this.octaves; i++) {
            x *= this.lacunarityX;

            amp *= this.gain;
            sum += this.fractal(this.reference.getSingle(++seed, x)) * amp;
        }
        return sum * this.fractalBounding;
    }

    @Override
    public float getSingle(int seed, float x, float y) {
        float sum = this.fractal(this.reference.getSingle(seed, x, y));
        float amp = 1;

        for (int i = 1; i < this.octaves; i++) {
            x *= this.lacunarityX;
            y *= this.lacunarityY;

            amp *= this.gain;
            sum += this.fractal(this.reference.getSingle(++seed, x, y)) * amp;
        }
        return sum * this.fractalBounding;
    }

    @Override
    public float getSingle(int seed, float x, float y, float z) {
        float sum = this.fractal(this.reference.getSingle(++seed, x, y, z));
        float amp = 1;

        for (int i = 1; i < this.octaves; i++) {
            x *= this.lacunarityX;
            y *= this.lacunarityY;
            z *= this.lacunarityZ;

            amp *= this.gain;
            sum += this.fractal(this.reference.getSingle(++seed, x, y, z)) * amp;
        }
        return sum * this.fractalBounding;
    }

    public static class Fbm extends FractalNoise {

        public Fbm(final NoiseDescriptor cfg, final FastNoise reference) {
            super(cfg, reference);
        }

        public Fbm(final NoiseDescriptor cfg, final NoiseProvider provider) {
            this(cfg, provider.apply(cfg));
        }

        public Fbm(final int seed, final FastNoise reference) {
            super(seed, reference);
        }

        @Override
        protected float fractal(float f) {
            return f;
        }
    }

    public static class Billow extends FractalNoise {

        public Billow(final NoiseDescriptor cfg, final FastNoise reference) {
            super(cfg, reference);
        }

        public Billow(final NoiseDescriptor cfg, final NoiseProvider provider) {
            this(cfg, provider.apply(cfg));
        }

        public Billow(final int seed, final FastNoise reference) {
            super(seed, reference);
        }

        @Override
        protected float fractal(float f) {
            return Math.abs(f) * 2 - 1;
        }
    }

    public static class Rigid extends FractalNoise {

        public Rigid(final NoiseDescriptor cfg, final FastNoise reference) {
            super(cfg, reference);
        }

        public Rigid(final NoiseDescriptor cfg, final NoiseProvider provider) {
            this(cfg, provider.apply(cfg));
        }

        public Rigid(final int seed, final FastNoise reference) {
            super(seed, reference);
        }

        @Override
        protected float fractal(float f) {
            return 1 - Math.abs(f);
        }
    }

    public static class PingPong extends FractalNoise {

        private final float strength;

        public PingPong(final NoiseDescriptor cfg, final FastNoise reference) {
            super(cfg, reference);
            this.strength = cfg.pingPongStrength();
        }

        public PingPong(final NoiseDescriptor cfg, final NoiseProvider provider) {
            this(cfg, provider.apply(cfg));
        }

        public PingPong(final int seed, final FastNoise reference) {
            super(seed, reference);
            this.strength = 2.0F;
        }

        @Override
        protected float fractal(float f) {
            f = (f + 1) * this.strength;
            return (pingPong(f) - 0.5F) * 2.0F;
        }

        private static float pingPong(float t) {
            t -= (int) (t * 0.5f) * 2;
            return t < 1 ? t : 2 - t;
        }
    }
}
