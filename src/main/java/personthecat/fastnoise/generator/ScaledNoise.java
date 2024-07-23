package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.function.NoiseScalar;

public class ScaledNoise extends FastNoise {

    private final FastNoise reference;
    private final NoiseScalar scalar;

    public ScaledNoise(final FastNoise reference, final NoiseScalar scalar) {
        super(0);
        this.reference = reference;
        this.scalar = scalar;
    }

    public static FastNoise create(final NoiseDescriptor cfg, final FastNoise reference) {
        if (cfg.scalar() != null) {
            return new ScaledNoise(reference, cfg.scalar());
        }
        return reference;
    }

    @Override // this will get broken by NoiseType.SCALED due to loss of noiseLookup
    public NoiseDescriptor toDescriptor() {
        final NoiseDescriptor reference = this.reference.toDescriptor();
        return super.toDescriptor().noise(reference.noise()).scalar(this.scalar);
    }

    @Override
    public float getSingle(int seed, float x) {
        return this.scalar.scale(this.reference.getSingle(seed, x));
    }

    @Override
    public float getSingle(int seed, float x, float y) {
        return this.scalar.scale(this.reference.getSingle(seed, x, y));
    }

    @Override
    public float getSingle(int seed, float x, float y, float z) {
        return this.scalar.scale(this.reference.getSingle(seed, x, y, z));
    }

    @Override
    public float getNoise(float x) {
        return this.scalar.scale(this.reference.getNoise(x));
    }

    @Override
    public float getNoise(float x, float y) {
        return this.scalar.scale(this.reference.getNoise(x, y));
    }

    @Override
    public float getNoise(float x, float y, float z) {
        return this.scalar.scale(this.reference.getNoise(x, y, z));
    }

    @Override
    public float getNoiseScaled(float x) {
        return this.scalar.scale(this.reference.getNoiseScaled(x));
    }

    @Override
    public float getNoiseScaled(float x, float y) {
        return this.scalar.scale(this.reference.getNoiseScaled(x, y));
    }

    @Override
    public float getNoiseScaled(float x, float y, float z) {
        return this.scalar.scale(this.reference.getNoiseScaled(x, y, z));
    }
}
