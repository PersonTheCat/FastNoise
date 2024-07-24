package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.FractalType;
import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.function.FractalFunction;

import static personthecat.fastnoise.util.NoiseUtils.getFractalBounding;

public class FractalNoise extends FastNoise {

    protected final FractalType fractalType;
    protected final FractalFunction fractalFunction;
    protected final float lacunarityX;
    protected final float lacunarityY;
    protected final float lacunarityZ;
    protected final float gain;
    protected final int octaves;
    protected final float fractalBounding;
    protected final FastNoise reference;
    protected final float pingPongStrength;

    public FractalNoise(final NoiseDescriptor cfg, final FastNoise reference) {
        super(cfg);
        this.fractalType = cfg.fractal();
        this.fractalFunction = cfg.fractalFunction();
        this.lacunarityX = cfg.lacunarityX();
        this.lacunarityY = cfg.lacunarityY();
        this.lacunarityZ = cfg.lacunarityZ();
        this.gain = cfg.gain();
        this.octaves = cfg.octaves();
        this.fractalBounding = getFractalBounding(this.gain, this.octaves);
        this.reference = reference;
        this.pingPongStrength = cfg.pingPongStrength();
    }

    public FractalNoise(final int seed, final FastNoise reference) {
        this(FastNoise.createDescriptor().seed(seed), reference);
    }

    @Override // this will get broken by NoiseType.FRACTAL due to loss of noiseLookup
    public NoiseDescriptor toDescriptor() {
        final NoiseDescriptor reference = this.reference.toDescriptor();
        return super.toDescriptor()
            .noise(reference.noise())
            .fractal(this.fractalType)
            .fractalFunction(this.fractalFunction)
            .lacunarityX(this.lacunarityX)
            .lacunarityY(this.lacunarityY)
            .lacunarityZ(this.lacunarityZ)
            .gain(this.gain)
            .octaves(this.octaves)
            .pingPongStrength(this.pingPongStrength);
    }

    @Override
    public float getSingle(int seed, float x) {
        float amp = 1;
        float sum = 0;
        switch (this.fractalType) {
            case FBM:
                for (int i = 0; i < this.octaves; i++) {
                    sum += this.reference.getSingle(seed++, x) * amp;
                    x *= this.lacunarityX;
                    amp *= this.gain;
                }
                break;
            case BILLOW:
                for (int i = 0; i < this.octaves; i++) {
                    sum += (Math.abs(this.reference.getSingle(seed++, x)) * 2 - 1) * amp;
                    x *= this.lacunarityX;
                    amp *= this.gain;
                }
                break;
            case RIGID_MULTI:
                for (int i = 0; i < this.octaves; i++) {
                    sum += (1 - Math.abs(this.reference.getSingle(seed++, x))) * amp;
                    x *= this.lacunarityX;
                    amp *= this.gain;
                }
                break;
            case PING_PONG:
                for (int i = 0; i < this.octaves; i++) {
                    sum += ((pingPong((this.reference.getSingle(seed++, x) + 1) * this.pingPongStrength) - 0.5F) * 2.0F) * amp;
                    x *= this.lacunarityX;
                    amp *= this.gain;
                }
                break;
            default:
                for (int i = 0; i < this.octaves; i++) {
                    sum += this.fractalFunction.fractal(this.reference.getSingle(seed++, x)) * amp;
                    x *= this.lacunarityX;
                    amp *= this.gain;
                }
        }
        return sum * this.fractalBounding;
    }

    @Override
    public float getSingle(int seed, float x, float y) {
        float amp = 1;
        float sum = 0;
        switch (this.fractalType) {
            case FBM:
                for (int i = 0; i < this.octaves; i++) {
                    sum += this.reference.getSingle(seed++, x, y) * amp;
                    x *= this.lacunarityX;
                    y *= this.lacunarityY;
                    amp *= this.gain;
                }
                break;
            case BILLOW:
                for (int i = 0; i < this.octaves; i++) {
                    sum += (Math.abs(this.reference.getSingle(seed++, x, y)) * 2 - 1) * amp;
                    x *= this.lacunarityX;
                    y *= this.lacunarityY;
                    amp *= this.gain;
                }
                break;
            case RIGID_MULTI:
                for (int i = 0; i < this.octaves; i++) {
                    sum += (1 - Math.abs(this.reference.getSingle(seed++, x, y))) * amp;
                    x *= this.lacunarityX;
                    y *= this.lacunarityY;
                    amp *= this.gain;
                }
                break;
            case PING_PONG:
                for (int i = 0; i < this.octaves; i++) {
                    sum += ((pingPong((this.reference.getSingle(seed++, x, y) + 1) * this.pingPongStrength) - 0.5F) * 2.0F) * amp;
                    x *= this.lacunarityX;
                    y *= this.lacunarityY;
                    amp *= this.gain;
                }
                break;
            default:
                for (int i = 0; i < this.octaves; i++) {
                    sum += this.fractalFunction.fractal(this.reference.getSingle(seed++, x, y)) * amp;
                    x *= this.lacunarityX;
                    y *= this.lacunarityY;
                    amp *= this.gain;
                }
        }
        return sum * this.fractalBounding;
    }

    @Override
    public float getSingle(int seed, float x, float y, float z) {
        float amp = 1;
        float sum = 0;
        switch (this.fractalType) {
            case FBM:
                for (int i = 0; i < this.octaves; i++) {
                    sum += this.reference.getSingle(seed++, x, y, z) * amp;
                    x *= this.lacunarityX;
                    y *= this.lacunarityY;
                    z *= this.lacunarityZ;
                    amp *= this.gain;
                }
                break;
            case BILLOW:
                for (int i = 0; i < this.octaves; i++) {
                    sum += (Math.abs(this.reference.getSingle(seed++, x, y, z)) * 2 - 1) * amp;
                    x *= this.lacunarityX;
                    y *= this.lacunarityY;
                    z *= this.lacunarityZ;
                    amp *= this.gain;
                }
                break;
            case RIGID_MULTI:
                for (int i = 0; i < this.octaves; i++) {
                    sum += (1 - Math.abs(this.reference.getSingle(seed++, x, y, z))) * amp;
                    x *= this.lacunarityX;
                    y *= this.lacunarityY;
                    z *= this.lacunarityZ;
                    amp *= this.gain;
                }
                break;
            case PING_PONG:
                for (int i = 0; i < this.octaves; i++) {
                    sum += ((pingPong((this.reference.getSingle(seed++, x, y, z) + 1) * this.pingPongStrength) - 0.5F) * 2.0F) * amp;
                    x *= this.lacunarityX;
                    y *= this.lacunarityY;
                    z *= this.lacunarityZ;
                    amp *= this.gain;
                }
                break;
            default:
                for (int i = 0; i < this.octaves; i++) {
                    sum += this.fractalFunction.fractal(this.reference.getSingle(seed++, x, y, z)) * amp;
                    x *= this.lacunarityX;
                    y *= this.lacunarityY;
                    z *= this.lacunarityZ;
                    amp *= this.gain;
                }
        }
        return sum * this.fractalBounding;
    }

    protected static float pingPong(float t) {
        t -= (int) (t * 0.5f) * 2;
        return t < 1 ? t : 2 - t;
    }
}
