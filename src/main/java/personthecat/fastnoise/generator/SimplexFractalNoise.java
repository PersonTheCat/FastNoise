package personthecat.fastnoise.generator;

import personthecat.fastnoise.data.FractalType;
import personthecat.fastnoise.data.NoiseDescriptor;

import static personthecat.fastnoise.util.NoiseUtils.getFractalBounding;

public class SimplexFractalNoise extends SimplexNoise {

    private final float lacunarityX;
    private final float lacunarityY;
    private final float lacunarityZ;
    private final float gain;
    private final int octaves;
    private final float fractalBounding;
    private final FractalType fractal;

    public SimplexFractalNoise(final NoiseDescriptor cfg, final FractalType fractal) {
        super(cfg);
        this.lacunarityX = cfg.lacunarityX();
        this.lacunarityY = cfg.lacunarityY();
        this.lacunarityZ = cfg.lacunarityZ();
        this.gain = cfg.gain();
        this.octaves = cfg.octaves();
        this.fractalBounding = getFractalBounding(this.gain, this.octaves);
        this.fractal = fractal;
    }

    public SimplexFractalNoise(final NoiseDescriptor cfg) {
        this(cfg, cfg.fractal());
    }

    @Override
    public float getSingle(int seed, float x, float y) {
        float sum = this.fractal.apply(super.getSingle(seed, x, y));
        float amp = 1;

        for (int i = 1; i < this.octaves; i++) {
            x *= this.lacunarityX;
            y *= this.lacunarityY;

            amp *= this.gain;
            sum += this.fractal.apply(super.getSingle(++seed, x, y)) * amp;
        }
        return sum * this.fractalBounding;
    }

    @Override
    public float getSingle(int seed, float x, float y, float z) {
        float sum = this.fractal.apply(super.getSingle(++seed, x, y, z));
        float amp = 1;

        for (int i = 1; i < this.octaves; i++) {
            x *= this.lacunarityX;
            y *= this.lacunarityY;
            z *= this.lacunarityZ;

            amp *= this.gain;
            sum += this.fractal.apply(super.getSingle(++seed, x, y, z)) * amp;
        }
        return sum * this.fractalBounding;
    }
}
