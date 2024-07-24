package personthecat.fastnoise.data;

import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.experimental.Accessors;
import personthecat.fastnoise.function.CellularDistance;
import personthecat.fastnoise.function.CellularReturn;
import personthecat.fastnoise.function.FractalFunction;
import personthecat.fastnoise.function.MultiFunction;
import personthecat.fastnoise.function.NoiseProvider;
import personthecat.fastnoise.function.NoiseScalar;
import personthecat.fastnoise.generator.*;
import personthecat.fastnoise.FastNoise;

import java.util.Collection;

@Data
@Accessors(fluent = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class NoiseDescriptor implements Cloneable {

    private NoiseProvider provider = d -> this.createGenerator();
    private NoiseType noise = NoiseType.SIMPLEX;
    private FractalType fractal = FractalType.NONE;
    private DomainWarpType warp = DomainWarpType.NONE;
    private CellularDistanceType distance = CellularDistanceType.EUCLIDEAN;
    private CellularReturnType cellularReturn = CellularReturnType.CELL_VALUE;
    private CellularDistance distanceFunction = CellularDistance.NO_OP;
    private CellularReturn returnFunction = CellularReturn.NO_OP;
    private MultiFunction multiFunction = MultiFunction.NO_OP;
    private FractalFunction fractalFunction = FractalFunction.NO_OP;
    private NoiseDescriptor[] noiseLookup = {};
    private MultiType multi = MultiType.SUM;
    private NoiseScalar scalar = null;
    private int seed = 1337;
    private float frequencyX = 0.01F;
    private float frequencyY = 0.01F;
    private float frequencyZ = 0.01F;
    private int octaves = 3;
    private float lacunarityX = 2.0F;
    private float lacunarityY = 2.0F;
    private float lacunarityZ = 2.0F;
    private float gain = 0.5F;
    private float pingPongStrength = 2.0F;
    private float jitterX = 1.0F;
    private float jitterY = 1.0F;
    private float jitterZ = 1.0F;
    private float warpAmplitudeX = 5.0F;
    private float warpAmplitudeY = 5.0F;
    private float warpAmplitudeZ = 5.0F;
    private float warpFrequencyX = 0.075F;
    private float warpFrequencyY = 0.075F;
    private float warpFrequencyZ = 0.075F;
    private float offsetX = 0;
    private float offsetY = 0;
    private float offsetZ = 0;
    private boolean invert = false;
    @Exclude private float scaleAmplitude = 1.0F;
    @Exclude private float scaleOffset = 0.0F;
    @Exclude private float minThreshold = 0.0F;
    @Exclude private float maxThreshold = 1.0F;

    public CellularReturn returnFunction() {
        return this.returnFunction;
    }

    public NoiseDescriptor returnFunction(final CellularReturn returnFunction) {
        this.returnFunction = returnFunction;
        return this.cellularReturn(CellularReturnType.FUNCTION);
    }

    public NoiseDescriptor returnFunction(final CellularReturn.D2 d2) {
        return this.returnFunction((CellularReturn) d2);
    }

    public NoiseDescriptor returnFunction(final CellularReturn.D3 d3) {
        return this.returnFunction((CellularReturn) d3);
    }

    public CellularDistance distanceFunction() {
        return this.distanceFunction;
    }

    public NoiseDescriptor distanceFunction(final CellularDistance distanceFunction) {
        this.distanceFunction = distanceFunction;
        return this.distance(CellularDistanceType.FUNCTION);
    }

    public NoiseDescriptor distanceFunction(final CellularDistance.D2 d2) {
        return this.distanceFunction((CellularDistance) d2);
    }

    public NoiseDescriptor distanceFunction(final CellularDistance.D3 d3) {
        return this.distanceFunction((CellularDistance) d3);
    }

    public MultiFunction multiFunction() {
        return this.multiFunction;
    }

    public NoiseDescriptor multiFunction(final MultiFunction multiFunction) {
        this.multiFunction = multiFunction;
        return this.multi(MultiType.FUNCTION);
    }

    public NoiseDescriptor multiFunction(final MultiFunction.D2 d2) {
        return this.multiFunction((MultiFunction) d2);
    }

    public NoiseDescriptor multiFunction(final MultiFunction.D3 d3) {
        return this.multiFunction((MultiFunction) d3);
    }

    public NoiseDescriptor multiFunction(final MultiFunction.Combiner combiner) {
        return this.multiFunction((MultiFunction) combiner);
    }

    public FractalFunction fractalFunction() {
        return this.fractalFunction;
    }

    public NoiseDescriptor fractalFunction(final FractalFunction fractalFunction) {
        this.fractalFunction = fractalFunction;
        return this.fractal(FractalType.FUNCTION);
    }

    public NoiseDescriptor[] noiseLookup() {
        return this.noiseLookup;
    }

    public NoiseDescriptor noiseLookup(final NoiseDescriptor lookup) {
        this.noiseLookup = new NoiseDescriptor[] { lookup };
        return this;
    }

    public NoiseDescriptor noiseLookup(final NoiseDescriptor... references) {
        this.noiseLookup = references;
        return this;
    }

    public NoiseDescriptor noiseLookup(final Collection<NoiseDescriptor> references) {
        this.noiseLookup = references.toArray(new NoiseDescriptor[0]);
        return this;
    }

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

    public NoiseDescriptor warpAmplitude(final float amplitude) {
        this.warpAmplitudeX = this.warpAmplitudeY = this.warpAmplitudeZ = amplitude;
        return this;
    }

    public NoiseDescriptor warpFrequency(final float frequency) {
        this.warpFrequencyX = this.warpFrequencyY = this.warpFrequencyZ = frequency;
        return this;
    }

    public NoiseDescriptor offset(final float offset) {
        this.offsetY = offset;
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

    private FastNoise createGenerator() {
        FastNoise generator = this.getBasicGenerator();
        if (this.fractal != FractalType.NONE) {
            generator = new FractalNoise(this, generator);
        }
        generator = DomainWarpedNoise.create(this, generator);
        return ScaledNoise.create(this, generator);
    }

    private FastNoise getBasicGenerator() {
        switch (this.noise) {
            case VALUE: return new ValueNoise(this);
            case PERLIN: return new PerlinNoise(this);
            case SIMPLEX: return new SimplexNoise(this);
            case SIMPLEX2: return new OpenSimplex2Noise(this);
            case SIMPLEX2S: return new OpenSimplex2SNoise(this);
            case CELLULAR: return new CellularNoise(this);
            case WHITE_NOISE: return new WhiteNoise(this);
            case CUBIC: return new CubicNoise(this);
            default : return this.getMultiGenerator();
        }
    }

    private FastNoise getMultiGenerator() {
        if (this.noiseLookup.length == 0) {
            this.noiseLookup = new NoiseDescriptor[] { new NoiseDescriptor() };
        }
        switch (this.multi) {
            case MIN: return new MultiGenerator.Min(this);
            case MAX: return new MultiGenerator.Max(this);
            case AVG: return new MultiGenerator.Avg(this);
            case MUL: return new MultiGenerator.Mul(this);
            case DIV: return new MultiGenerator.Div(this);
            case SUM: return new MultiGenerator.Sum(this);
            default: return new MultiGenerator.Function(this);
        }
    }

    public FastNoise generate() {
        return this.provider.apply(this);
    }

    @Override
    public NoiseDescriptor clone() {
        try {
            final NoiseDescriptor clone = (NoiseDescriptor) super.clone();
            for (int i = 0; i < clone.noiseLookup.length; i++) {
                clone.noiseLookup[i] = clone.noiseLookup[i].clone();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
