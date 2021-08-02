package personthecat.fastnoise.data;

import lombok.Data;
import lombok.experimental.Accessors;
import personthecat.fastnoise.function.NoiseProvider;
import personthecat.fastnoise.generator.Cellular1EdgeNoise;
import personthecat.fastnoise.generator.Cellular2EdgeNoise;
import personthecat.fastnoise.generator.Cellular3EdgeNoise;
import personthecat.fastnoise.generator.CubicNoise;
import personthecat.fastnoise.generator.CubicFractalNoise;
import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.generator.PerlinNoise;
import personthecat.fastnoise.generator.PerlinFractalNoise;
import personthecat.fastnoise.generator.SimplexNoise;
import personthecat.fastnoise.generator.SimplexFractalNoise;
import personthecat.fastnoise.generator.ValueNoise;
import personthecat.fastnoise.generator.ValueFractalNoise;
import personthecat.fastnoise.generator.WhiteNoise;

@Data
@Accessors(fluent = true)
public class NoiseDescriptor {

    private NoiseProvider provider = d -> this.selectGenerator();
    private NoiseType noise = NoiseType.SIMPLEX_FRACTAL;
    private FractalType fractal = FractalType.FBM;
    private InterpolationType interpolation = InterpolationType.LINEAR;
    private CellularDistanceType distance = CellularDistanceType.EUCLIDEAN;
    private CellularReturnType cellularReturn = CellularReturnType.CELL_VALUE;
    private NoiseDescriptor noiseLookup = null;
    private int seed = 1337;
    private float frequencyX = 0.01F;
    private float frequencyY = 0.01F;
    private float frequencyZ = 0.01F;
    private int octaves = 3;
    private float lacunarityX = 2.0F;
    private float lacunarityY = 2.0F;
    private float lacunarityZ = 2.0F;
    private float gain = 0.5F;
    private float jitterX = 0.45F;
    private float jitterY = 0.45F;
    private float jitterZ = 0.45F;
    private boolean gradientPerturb = false;
    private float gradientPerturbAmplitude = 1.0F / 0.45F;
    private float gradientPerturbFrequency = 0.1F;
    private int offset = 0;
    private boolean invert = false;
    private float scaleAmplitude = 1.0F;
    private float scaleOffset = 0.0F;
    private float minThreshold = 0.0F;
    private float maxThreshold = 1.0F;

    public NoiseDescriptor frequency(final float frequency) {
        this.frequencyX = this.frequencyY = this.frequencyZ = frequency;
        return this;
    }

    public NoiseDescriptor lacunarity(final float lacunarity) {
        this.lacunarityX = this.lacunarityY = this.lacunarityZ = lacunarity;
        return this;
    }

    public NoiseDescriptor jitter(final float jitter) {
        this.jitterX = this.jitterY = this.jitterZ = jitter;
        return this;
    }

    // Backwards compatibility
    public NoiseDescriptor stretch(final float stretch) {
        this.frequencyY /= stretch;
        return this;
    }

    public NoiseDescriptor range(final float min, final float max) {
        this.scaleAmplitude = (max - min) / 2;
        this.scaleOffset = (min + max) / 2;
        return this;
    }

    public NoiseDescriptor threshold(final float min, final float max) {
        // Thread a range of f1~f1 as a single threshold ranging downward.
        this.minThreshold = min != max ? min : -1.0F;
        this.maxThreshold = max;
        return this;
    }

    public NoiseDescriptor gradientPerturbAmplitude(final float amplitude) {
        // In the original library, this value was always reduced.
        this.gradientPerturbAmplitude = amplitude / 0.45F;
        return this;
    }

    private FastNoise selectGenerator() {
        switch (this.noise) {
            case VALUE: return new ValueNoise(this);
            case VALUE_FRACTAL: return new ValueFractalNoise(this);
            case PERLIN: return new PerlinNoise(this);
            case PERLIN_FRACTAL: return new PerlinFractalNoise(this);
            case SIMPLEX: return new SimplexNoise(this);
            case SIMPLEX_FRACTAL: return new SimplexFractalNoise(this);
            case CELLULAR: return this.getCellularGenerator();
            case WHITE_NOISE: return new WhiteNoise(this);
            case CUBIC: return new CubicNoise(this);
            default: return new CubicFractalNoise(this);
        }
    }

    private FastNoise getCellularGenerator() {
        switch (this.cellularReturn) {
            case CELL_VALUE: return new Cellular1EdgeNoise.CellValue(this);
            case NOISE_LOOKUP: return new Cellular1EdgeNoise.NoiseLookup(this);
            case DISTANCE: return new Cellular1EdgeNoise.Distance(this);
            case DISTANCE2: return new Cellular2EdgeNoise.Distance(this);
            case DISTANCE2_ADD: return new Cellular2EdgeNoise.Add(this);
            case DISTANCE2_SUB: return new Cellular2EdgeNoise.Sub(this);
            case DISTANCE2_MUL: return new Cellular2EdgeNoise.Mul(this);
            case DISTANCE2_DIV: return new Cellular2EdgeNoise.Div(this);
            case DISTANCE3_ADD: return new Cellular3EdgeNoise.Add(this);
            case DISTANCE3_SUB: return new Cellular3EdgeNoise.Sub(this);
            case DISTANCE3_MUL: return new Cellular3EdgeNoise.Mul(this);
            default: return new Cellular3EdgeNoise.Div(this);
        }
    }

    public FastNoise generate() {
        return this.provider.apply(this);
    }
}
