package personthecat.fastnoise.data;

import lombok.Data;
import lombok.EqualsAndHashCode.Exclude;
import lombok.experimental.Accessors;
import personthecat.fastnoise.function.DistanceFunction;
import personthecat.fastnoise.function.ReturnFunction;
import personthecat.fastnoise.function.FractalFunction;
import personthecat.fastnoise.function.MultiFunction;
import personthecat.fastnoise.function.NoiseProvider;
import personthecat.fastnoise.function.ScaleFunction;
import personthecat.fastnoise.generator.*;
import personthecat.fastnoise.FastNoise;

import java.util.Collection;
import java.util.stream.Stream;

@Data
@Accessors(fluent = true)
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class NoiseBuilder implements Cloneable {

    private NoiseProvider provider = d -> this.createGenerator();
    private NoiseType type = NoiseType.SIMPLEX;
    private FractalType fractal = FractalType.NONE;
    private WarpType warp = WarpType.NONE;
    private DistanceType distance = DistanceType.EUCLIDEAN;
    private ReturnType cellularReturn = ReturnType.CELL_VALUE;
    private DistanceFunction distanceFunction = DistanceFunction.NO_OP;
    private ReturnFunction returnFunction = ReturnFunction.NO_OP;
    private MultiFunction multiFunction = MultiFunction.NO_OP;
    private FractalFunction fractalFunction = FractalFunction.NO_OP;
    private ScaleFunction scaleFunction = null;
    private NoiseBuilder noiseLookup = null;
    private NoiseBuilder[] references = {};
    private MultiType multi = MultiType.SUM;
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

    public NoiseType type() {
        return this.type;
    }

    public NoiseBuilder type(final NoiseType type) {
        this.type = type;
        if (type == NoiseType.FRACTAL && this.fractal == FractalType.NONE) {
            this.fractal = FractalType.FBM;
        } else if (type == NoiseType.WARPED && this.warp == WarpType.NONE) {
            this.warp = WarpType.BASIC_GRID;
        }
        return this;
    }

    public ReturnFunction returnFunction() {
        return this.returnFunction;
    }

    public NoiseBuilder returnFunction(final ReturnFunction returnFunction) {
        this.returnFunction = returnFunction;
        return this.cellularReturn(ReturnType.FUNCTION);
    }

    public NoiseBuilder returnFunction(final ReturnFunction.D2 d2) {
        return this.returnFunction((ReturnFunction) d2);
    }

    public NoiseBuilder returnFunction(final ReturnFunction.D3 d3) {
        return this.returnFunction((ReturnFunction) d3);
    }

    public DistanceFunction distanceFunction() {
        return this.distanceFunction;
    }

    public NoiseBuilder distanceFunction(final DistanceFunction distanceFunction) {
        this.distanceFunction = distanceFunction;
        return this.distance(DistanceType.FUNCTION);
    }

    public NoiseBuilder distanceFunction(final DistanceFunction.D2 d2) {
        return this.distanceFunction((DistanceFunction) d2);
    }

    public NoiseBuilder distanceFunction(final DistanceFunction.D3 d3) {
        return this.distanceFunction((DistanceFunction) d3);
    }

    public MultiFunction multiFunction() {
        return this.multiFunction;
    }

    public NoiseBuilder multiFunction(final MultiFunction multiFunction) {
        this.multiFunction = multiFunction;
        return this.multi(MultiType.FUNCTION);
    }

    public NoiseBuilder multiFunction(final MultiFunction.D2 d2) {
        return this.multiFunction((MultiFunction) d2);
    }

    public NoiseBuilder multiFunction(final MultiFunction.D3 d3) {
        return this.multiFunction((MultiFunction) d3);
    }

    public NoiseBuilder multiFunction(final MultiFunction.Combiner combiner) {
        return this.multiFunction((MultiFunction) combiner);
    }

    public FractalFunction fractalFunction() {
        return this.fractalFunction;
    }

    public NoiseBuilder fractalFunction(final FractalFunction fractalFunction) {
        this.fractalFunction = fractalFunction;
        return this.fractal(FractalType.FUNCTION);
    }

    public NoiseBuilder reference() {
        return this.references.length == 0 ? null : this.references[0];
    }

    public NoiseBuilder reference(final NoiseBuilder reference) {
        this.references = new NoiseBuilder[] { reference };
        return this;
    }

    public NoiseBuilder[] references() {
        return this.references;
    }

    public NoiseBuilder references(final NoiseBuilder... references) {
        this.references = references;
        return this;
    }

    public NoiseBuilder references(final Collection<NoiseBuilder> reference) {
        this.references = reference.toArray(new NoiseBuilder[0]);
        return this;
    }

    public NoiseBuilder frequency(final float frequency) {
        this.frequencyX = this.frequencyY = this.frequencyZ = frequency;
        return this;
    }

    public NoiseBuilder lacunarity(final float lacunarity) {
        this.lacunarityX = this.lacunarityY = this.lacunarityZ = lacunarity;
        return this;
    }

    public NoiseBuilder jitter(final float jitter) {
        this.jitterX = this.jitterY = this.jitterZ = jitter;
        return this;
    }

    public NoiseBuilder warpAmplitude(final float amplitude) {
        this.warpAmplitudeX = this.warpAmplitudeY = this.warpAmplitudeZ = amplitude;
        return this;
    }

    public NoiseBuilder warpFrequency(final float frequency) {
        this.warpFrequencyX = this.warpFrequencyY = this.warpFrequencyZ = frequency;
        return this;
    }

    public NoiseBuilder offset(final float offset) {
        this.offsetY = offset;
        return this;
    }

    // Backwards compatibility
    public NoiseBuilder stretch(final float stretch) {
        this.frequencyY /= stretch;
        return this;
    }

    public NoiseBuilder range(final float min, final float max) {
        this.scaleAmplitude = (max - min) / 2;
        this.scaleOffset = (min + max) / 2;
        return this;
    }

    public NoiseBuilder threshold(final float min, final float max) {
        // Thread a range of f1~f1 as a single threshold ranging downward.
        this.minThreshold = min != max ? min : -1.0F;
        this.maxThreshold = max;
        return this;
    }

    private FastNoise createGenerator() {
        FastNoise generator = this.getBasicGenerator();
        if (this.fractal != FractalType.NONE && this.type != NoiseType.FRACTAL) {
            generator = new FractalNoise(this, generator);
        }
        if (this.warp != WarpType.NONE && this.type != NoiseType.WARPED) {
            generator = this.applyWarp(generator);
        }
        if (this.scaleFunction != null) {
            generator = new ScaledNoise(generator, this.scaleFunction);
        }
        return generator;
    }

    private FastNoise getBasicGenerator() {
        switch (this.type) {
            case VALUE: return new ValueNoise(this);
            case PERLIN: return new PerlinNoise(this);
            case SIMPLEX2: return new OpenSimplex2Noise(this);
            case SIMPLEX2S: return new OpenSimplex2SNoise(this);
            case CELLULAR: return new CellularNoise(this);
            case WHITE: return new WhiteNoise(this);
            case CUBIC: return new CubicNoise(this);
            case FRACTAL: return new FractalNoise(this, this.buildReference());
            case WARPED: return this.applyWarp(this.buildReference());
            case MULTI: return this.getMultiGenerator();
            default: return new SimplexNoise(this);
        }
    }

    private FastNoise getMultiGenerator() {
        switch (this.multi) {
            case MIN: return new MultiNoise.Min(this);
            case MAX: return new MultiNoise.Max(this);
            case AVG: return new MultiNoise.Avg(this);
            case MUL: return new MultiNoise.Mul(this);
            case DIV: return new MultiNoise.Div(this);
            case SUM: return new MultiNoise.Sum(this);
            default: return new MultiNoise.Function(this);
        }
    }

    private FastNoise applyWarp(final FastNoise reference) {
        switch (this.warp) {
            case BASIC_GRID: return new WarpedNoise.BasicGrid(this, reference);
            case SIMPLEX2: return new WarpedNoise.Simplex2(this, reference);
            case SIMPLEX2_REDUCED: return new WarpedNoise.Simplex2Reduced(this, reference);
            default: return reference;
        }
    }

    public FastNoise build() {
        return this.provider.apply(this);
    }

    public FastNoise buildLookup() {
        return this.noiseLookup == null ? this.buildReference() : this.noiseLookup.build();
    }

    public FastNoise buildReference() {
        return this.references.length == 0 ? FastNoise.dummy() : this.references[0].build();
    }

    public FastNoise[] buildReferences() {
        if (this.references.length == 0) return new FastNoise[] { FastNoise.dummy() };
        return Stream.of(this.references).map(NoiseBuilder::build).toArray(FastNoise[]::new);
    }

    @Override
    public NoiseBuilder clone() {
        try {
            final NoiseBuilder clone = (NoiseBuilder) super.clone();
            for (int i = 0; i < clone.references.length; i++) {
                clone.references[i] = clone.references[i].clone();
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
