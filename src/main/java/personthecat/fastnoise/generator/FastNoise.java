package personthecat.fastnoise.generator;

import personthecat.fastnoise.data.Float2;
import personthecat.fastnoise.data.Float3;
import personthecat.fastnoise.data.InterpolationType;
import personthecat.fastnoise.data.NoiseDescriptor;

import static personthecat.fastnoise.util.NoiseTables.CELL_2D;
import static personthecat.fastnoise.util.NoiseTables.CELL_3D;
import static personthecat.fastnoise.util.NoiseUtils.fastFloor;
import static personthecat.fastnoise.util.NoiseUtils.hash2;
import static personthecat.fastnoise.util.NoiseUtils.hash3;
import static personthecat.fastnoise.util.NoiseUtils.interpolateHermite;
import static personthecat.fastnoise.util.NoiseUtils.interpolateQuintic;
import static personthecat.fastnoise.util.NoiseUtils.lerp;

public abstract class FastNoise {

    protected final int seed;
    protected final InterpolationType interpolation;
    protected final float frequencyX;
    protected final float frequencyY;
    protected final float frequencyZ;
    protected final float offset; // todo x, y, z after perf testing
    protected final boolean gradientPerturb;
    protected final float gradientPerturbAmplitude;
    protected final float gradientPerturbFrequency;
    protected final float scaleAmplitude;
    protected final float scaleOffset;
    protected final float minThreshold;
    protected final float maxThreshold;
    protected final boolean invert;

    public FastNoise(final NoiseDescriptor cfg) {
        this.seed = cfg.seed();
        this.interpolation = cfg.interpolation();
        this.frequencyX = cfg.frequencyX();
        this.frequencyY = cfg.frequencyY();
        this.frequencyZ = cfg.frequencyZ();
        this.offset = cfg.offset();
        this.gradientPerturb = cfg.gradientPerturb();
        this.gradientPerturbAmplitude = cfg.gradientPerturbAmplitude();
        this.gradientPerturbFrequency = cfg.gradientPerturbFrequency();
        this.scaleAmplitude = cfg.scaleAmplitude();
        this.scaleOffset = cfg.scaleOffset();
        this.minThreshold = cfg.minThreshold();
        this.maxThreshold = cfg.maxThreshold();
        this.invert = cfg.invert();
    }

    public static NoiseDescriptor createDescriptor() {
        return new NoiseDescriptor();
    }

    public static DummyNoiseWrapper createWrapper() {
        return new DummyNoiseWrapper();
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

        if (this.gradientPerturb) {
            return this.perturb(this.seed, x, y);
        }
        return this.getSingle(this.seed, x, y);
    }

    public float getNoise(float x, float y, float z) {
        x *= this.frequencyX;
        y = y * this.frequencyY + this.offset;
        z *= this.frequencyZ;

        if (this.gradientPerturb) {
            return this.perturb(this.seed, x, y, z);
        }
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

    protected float perturb(int seed, float x, float y) {
        final float xf = x * this.gradientPerturbFrequency;
        final float yf = y * this.gradientPerturbFrequency;

        final int x0 = fastFloor(xf);
        final int y0 = fastFloor(yf);
        final int x1 = x0 + 1;
        final int y1 = y0 + 1;

        final float xs, ys;
        switch (this.interpolation) {
            case LINEAR:
                xs = xf - x0;
                ys = yf - y0;
                break;
            case HERMITE:
                xs = interpolateHermite(xf - x0);
                ys = interpolateHermite(yf - y0);
                break;
            default:
                xs = interpolateQuintic(xf - x0);
                ys = interpolateQuintic(yf - y0);
        }

        Float2 vec0 = CELL_2D[hash2(this.seed, x0, y0) & 255];
        Float2 vec1 = CELL_2D[hash2(this.seed, x1, y0) & 255];

        final float lx0x = lerp(vec0.x, vec1.x, xs);
        final float ly0x = lerp(vec0.y, vec1.y, xs);

        vec0 = CELL_2D[hash2(this.seed, x0, y1) & 255];
        vec1 = CELL_2D[hash2(this.seed, x1, y1) & 255];

        final float lx1x = lerp(vec0.x, vec1.x, xs);
        final float ly1x = lerp(vec0.y, vec1.y, xs);

        x += lerp(lx0x, lx1x, ys) * this.gradientPerturbAmplitude;
        y += lerp(ly0x, ly1x, ys) * this.gradientPerturbAmplitude;
        return this.getSingle(seed, x, y);
    }

    protected float perturb(int seed, float x, float y, float z) {
        final float xf = x * this.gradientPerturbFrequency;
        final float yf = y * this.gradientPerturbFrequency;
        final float zf = z * this.gradientPerturbFrequency;

        final int x0 = fastFloor(xf);
        final int y0 = fastFloor(yf);
        final int z0 = fastFloor(zf);
        final int x1 = x0 + 1;
        final int y1 = y0 + 1;
        final int z1 = z0 + 1;

        final float xs, ys, zs;
        switch (this.interpolation) {
            case LINEAR:
                xs = xf - x0;
                ys = yf - y0;
                zs = zf - z0;
                break;
            case HERMITE:
                xs = interpolateHermite(xf - x0);
                ys = interpolateHermite(yf - y0);
                zs = interpolateHermite(zf - z0);
                break;
            default:
                xs = interpolateQuintic(xf - x0);
                ys = interpolateQuintic(yf - y0);
                zs = interpolateQuintic(zf - z0);
        }

        Float3 vec0 = CELL_3D[hash3(this.seed, x0, y0, z0) & 255];
        Float3 vec1 = CELL_3D[hash3(this.seed, x1, y0, z0) & 255];

        float lx0x = lerp(vec0.x, vec1.x, xs);
        float ly0x = lerp(vec0.y, vec1.y, xs);
        float lz0x = lerp(vec0.z, vec1.z, xs);

        vec0 = CELL_3D[hash3(this.seed, x0, y1, z0) & 255];
        vec1 = CELL_3D[hash3(this.seed, x1, y1, z0) & 255];

        float lx1x = lerp(vec0.x, vec1.x, xs);
        float ly1x = lerp(vec0.y, vec1.y, xs);
        float lz1x = lerp(vec0.z, vec1.z, xs);

        float lx0y = lerp(lx0x, lx1x, ys);
        float ly0y = lerp(ly0x, ly1x, ys);
        float lz0y = lerp(lz0x, lz1x, ys);

        vec0 = CELL_3D[hash3(this.seed, x0, y0, z1) & 255];
        vec1 = CELL_3D[hash3(this.seed, x1, y0, z1) & 255];

        lx0x = lerp(vec0.x, vec1.x, xs);
        ly0x = lerp(vec0.y, vec1.y, xs);
        lz0x = lerp(vec0.z, vec1.z, xs);

        vec0 = CELL_3D[hash3(this.seed, x0, y1, z1) & 255];
        vec1 = CELL_3D[hash3(this.seed, x1, y1, z1) & 255];

        lx1x = lerp(vec0.x, vec1.x, xs);
        ly1x = lerp(vec0.y, vec1.y, xs);
        lz1x = lerp(vec0.z, vec1.z, xs);

        x += lerp(lx0y, lerp(lx0x, lx1x, ys), zs) * this.gradientPerturbAmplitude;
        y += lerp(ly0y, lerp(ly0x, ly1x, ys), zs) * this.gradientPerturbAmplitude;
        z += lerp(lz0y, lerp(lz0x, lz1x, ys), zs) * this.gradientPerturbAmplitude;
        return this.getSingle(seed, x, y, z);
    }
}
