package personthecat.fastnoise.generator;

import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.function.FractalFunction;

import static personthecat.fastnoise.util.NoiseUtils.getFractalBounding;

public class PerlinFractalNoise extends PerlinNoise {

    private final float lacunarityX;
    private final float lacunarityY;
    private final float lacunarityZ;
    private final float gain;
    private final int octaves;
    private final float fractalBounding;
    private final FractalFunction fractal;

    public PerlinFractalNoise(final NoiseDescriptor cfg, final FractalFunction fractal) {
        super(cfg);
        this.lacunarityX = cfg.lacunarityX();
        this.lacunarityY = cfg.lacunarityY();
        this.lacunarityZ = cfg.lacunarityZ();
        this.gain = cfg.gain();
        this.octaves = cfg.octaves();
        this.fractalBounding = getFractalBounding(this.gain, this.octaves);
        this.fractal = fractal;
    }

    public PerlinFractalNoise(final NoiseDescriptor cfg) {
        this(cfg, cfg.fractal());
    }

    @Override
    public float getNoise(float x, float y) {
        int seed = this.seed;
        float sum = this.fractal.apply(this.singlePerlin(seed, x, y));
        float amp = 1;

        for (int i = 1; i < this.octaves; i++) {
            x *= this.lacunarityX;
            y *= this.lacunarityY;

            amp *= this.gain;
            sum += this.fractal.apply(this.singlePerlin(++seed, x, y)) * amp;
        }
        return sum * this.fractalBounding;
    }

    @Override
    public float getNoise(float x, float y, float z) {
        int seed = this.seed;
        float sum = this.fractal.apply(this.singlePerlin(++seed, x, y, z));
        float amp = 1;

        for (int i = 1; i < this.octaves; i++) {
            x *= this.lacunarityX;
            y *= this.lacunarityY;
            z *= this.lacunarityZ;

            amp *= this.gain;
            sum += this.fractal.apply(this.singlePerlin(++seed, x, y, z)) * amp;
        }
        return sum * this.fractalBounding;
    }
}
