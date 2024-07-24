package personthecat.fastnoise;

import personthecat.fastnoise.data.NoiseBuilder;
import personthecat.fastnoise.generator.NoiseWrapper;

@SuppressWarnings("unused")
public abstract class FastNoise {
    protected static final FastNoise DUMMY = wrapper().generatePassthrough();

    protected final int seed;
    protected final float frequencyX;
    protected final float frequencyY;
    protected final float frequencyZ;
    protected final float offsetX;
    protected final float offsetY;
    protected final float offsetZ;
    protected final float scaleAmplitude;
    protected final float scaleOffset;
    protected final float minThreshold;
    protected final float maxThreshold;
    protected final boolean invert;

    public FastNoise(final NoiseBuilder cfg) {
        this.seed = cfg.seed();
        this.frequencyX = cfg.frequencyX();
        this.frequencyY = cfg.frequencyY();
        this.frequencyZ = cfg.frequencyZ();
        this.offsetX = cfg.offsetX();
        this.offsetY = cfg.offsetY();
        this.offsetZ = cfg.offsetZ();
        this.scaleAmplitude = cfg.scaleAmplitude();
        this.scaleOffset = cfg.scaleOffset();
        this.minThreshold = cfg.minThreshold();
        this.maxThreshold = cfg.maxThreshold();
        this.invert = cfg.invert();
    }

    public FastNoise(final int seed) {
        this(builder().seed(seed));
    }

    public static NoiseBuilder builder() {
        return new NoiseBuilder();
    }

    public static NoiseWrapper wrapper() {
        return new NoiseWrapper();
    }

    public static FastNoise dummy() {
        return DUMMY;
    }

    protected static <T> T get(final T t, final T def) {
        return t != null ? t : def;
    }

    public NoiseBuilder toBuilder() {
        return new NoiseBuilder()
            .seed(this.seed)
            .frequencyX(this.frequencyX)
            .frequencyY(this.frequencyY)
            .frequencyZ(this.frequencyZ)
            .offsetX(this.offsetX)
            .offsetY(this.offsetY)
            .offsetZ(this.offsetZ)
            .scaleAmplitude(this.scaleAmplitude)
            .scaleOffset(this.scaleOffset)
            .minThreshold(this.minThreshold)
            .maxThreshold(this.maxThreshold)
            .invert(this.invert);
    }

    public abstract float getSingle(final int seed, final float x);

    public abstract float getSingle(final int seed, final float x, final float y);

    public abstract float getSingle(final int seed, final float x, final float y, final float z);

    public float getNoise(float x) {
        return this.getSingle(this.seed, x * this.frequencyX);
    }

    public float getNoise(float x, float y) {
        x *= this.frequencyX;
        y *= this.frequencyY;

        return this.getSingle(this.seed, x, y);
    }

    public float getNoise(float x, float y, float z) {
        x += this.offsetX;
        y += this.offsetY;
        z += this.offsetZ;
        x *= this.frequencyX;
        y *= this.frequencyY;
        z *= this.frequencyZ;

        return this.getSingle(this.seed, x, y, z);
    }

    public float getNoiseScaled(final float x) {
        return this.getNoise(x) * this.scaleAmplitude + this.scaleOffset;
    }

    public float getNoiseScaled(final float x, final float y) {
        return this.getNoise(x, y) * this.scaleAmplitude + this.scaleOffset;
    }

    public float getNoiseScaled(final float x, final float y, final float z) {
        return this.getNoise(x, y, z) * this.scaleAmplitude + this.scaleOffset;
    }

    public boolean getBoolean(final float x) {
        final float noise = this.getNoise(x);
        return this.invert != (noise > this.minThreshold && noise < this.maxThreshold);
    }

    public boolean getBoolean(final float x, final float y) {
        final float noise = this.getNoise(x, y);
        return this.invert != (noise > this.minThreshold && noise < this.maxThreshold);
    }

    public boolean getBoolean(final float x, final float y, final float z) {
        final float noise = this.getNoise(x, y, z);
        return this.invert != (noise > this.minThreshold && noise < this.maxThreshold);
    }

    public boolean isInThreshold(final float noise) {
        return this.invert != (noise > this.minThreshold && noise < this.maxThreshold);
    }

    public boolean isInThreshold(final float noise, final float d) {
        return this.invert != (noise > this.minThreshold - d && noise < this.maxThreshold + d);
    }
}
