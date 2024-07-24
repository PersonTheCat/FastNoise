package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.NoiseBuilder;
import personthecat.fastnoise.function.ScaleFunction;

public class ScaledNoise extends FastNoise {

    private final FastNoise reference;
    private final ScaleFunction scaleFunction;

    public ScaledNoise(final FastNoise reference, final ScaleFunction scaleFunction) {
        super(0);
        this.reference = reference;
        this.scaleFunction = scaleFunction;
    }

    @Override
    public NoiseBuilder toBuilder() {
        return this.reference.toBuilder().scaleFunction(this.scaleFunction);
    }

    @Override
    public float getSingle(int seed, float x) {
        return this.scaleFunction.scale(this.reference.getSingle(seed, x));
    }

    @Override
    public float getSingle(int seed, float x, float y) {
        return this.scaleFunction.scale(this.reference.getSingle(seed, x, y));
    }

    @Override
    public float getSingle(int seed, float x, float y, float z) {
        return this.scaleFunction.scale(this.reference.getSingle(seed, x, y, z));
    }

    @Override
    public float getNoise(float x) {
        return this.scaleFunction.scale(this.reference.getNoise(x));
    }

    @Override
    public float getNoise(float x, float y) {
        return this.scaleFunction.scale(this.reference.getNoise(x, y));
    }

    @Override
    public float getNoise(float x, float y, float z) {
        return this.scaleFunction.scale(this.reference.getNoise(x, y, z));
    }

    @Override
    public float getNoiseScaled(float x) {
        return this.scaleFunction.scale(this.reference.getNoiseScaled(x));
    }

    @Override
    public float getNoiseScaled(float x, float y) {
        return this.scaleFunction.scale(this.reference.getNoiseScaled(x, y));
    }

    @Override
    public float getNoiseScaled(float x, float y, float z) {
        return this.scaleFunction.scale(this.reference.getNoiseScaled(x, y, z));
    }
}
