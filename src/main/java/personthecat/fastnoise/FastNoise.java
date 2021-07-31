package personthecat.fastnoise;

import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.data.NoiseModifier;
import personthecat.fastnoise.function.NoiseFunction1;
import personthecat.fastnoise.function.NoiseFunction2;
import personthecat.fastnoise.function.NoiseFunction3;
import personthecat.fastnoise.function.NoiseWrapper;

public class FastNoise {

    public final NoiseFunction1 noise1;
    public final NoiseFunction2 noise2;
    public final NoiseFunction3 noise3;
    public final float minThreshold;
    public final float maxThreshold;
    public final float scaleAmplitude;
    public final float scaleOffset;
    public final boolean invert;

    public FastNoise(final NoiseWrapper wrapper, final NoiseModifier modifier) {
        this.noise1 = wrapper.wrapNoise1();
        this.noise2 = wrapper.wrapNoise2();
        this.noise3 = wrapper.wrapNoise3();
        this.minThreshold = modifier.minThreshold();
        this.maxThreshold = modifier.maxThreshold();
        this.scaleAmplitude = modifier.scaleAmplitude();
        this.scaleOffset = modifier.scaleOffset();
        this.invert = modifier.invert();
    }

    public FastNoise(final NoiseWrapper wrapper) {
        this(wrapper, NoiseModifier.DEFAULT_MODIFIER);
    }

    public static NoiseDescriptor createDescriptor() {
        return new NoiseDescriptor();
    }

    public float getNoise(final float x) {
        return this.noise1.getNoise(x);
    }

    public float getNoise(final float x, final float y) {
        return this.noise2.getNoise(x, y);
    }

    public float getNoise(final float x, final float y, final float z) {
        return this.noise3.getNoise(x, y, z);
    }

    public float getNoiseScaled(final float x) {
        return this.noise1.getNoise(x) * this.scaleAmplitude + this.scaleOffset;
    }

    public float getNoiseScaled(final float x, final float y) {
        return this.noise2.getNoise(x, y) * this.scaleAmplitude + this.scaleOffset;
    }

    public float getNoiseScaled(final float x, final float y, final float z) {
        return this.noise3.getNoise(x, y, z) * this.scaleAmplitude + this.scaleOffset;
    }

    public boolean getBoolean(final float x) {
        return this.invert != this.noise1.in(x, this.minThreshold, this.maxThreshold);
    }

    public boolean getBoolean(final float x, final float y) {
        return this.invert != this.noise2.in(x, y, this.minThreshold, this.maxThreshold);
    }

    public boolean getBoolean(final float x, final float y, final float z) {
        return this.invert != this.noise3.in(x, y, z, this.minThreshold, this.maxThreshold);
    }
}
