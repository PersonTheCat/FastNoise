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

    public static FastNoise create(final NoiseDescriptor cfg, final NoiseProvider provider) {
        return create(cfg, provider.apply(cfg));
    }

    public static FastNoise create(final NoiseDescriptor cfg, final FastNoise reference) {
        switch (cfg.fractal()) {
            case FBM: return new FbmFractalNoise(cfg, reference);
            case BILLOW: return new BillowFractalNoise(cfg, reference);
            default: return new RigidFractalNoise(cfg, reference);
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

}
