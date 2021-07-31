package personthecat.fastnoise.generator;

import personthecat.fastnoise.data.Float2;
import personthecat.fastnoise.data.Float3;
import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.function.InterpolationFunction;
import personthecat.fastnoise.function.NoiseFunction1;
import personthecat.fastnoise.function.NoiseFunction2;
import personthecat.fastnoise.function.NoiseFunction3;
import personthecat.fastnoise.function.NoiseWrapper;

import static personthecat.fastnoise.util.NoiseTables.CELL_2D;
import static personthecat.fastnoise.util.NoiseTables.CELL_3D;
import static personthecat.fastnoise.util.NoiseUtils.fastFloor;
import static personthecat.fastnoise.util.NoiseUtils.hash2;
import static personthecat.fastnoise.util.NoiseUtils.hash3;
import static personthecat.fastnoise.util.NoiseUtils.lerp;

public abstract class NoiseGenerator implements NoiseWrapper {

    protected final int seed;
    protected final InterpolationFunction interpolation;
    protected final float frequencyX;
    protected final float frequencyY;
    protected final float frequencyZ;
    protected final float offset; // todo x, y, z after perf testing
    protected final boolean gradientPerturb;
    protected final float gradientPerturbAmplitude;
    protected final float gradientPerturbFrequency;
    protected final int hashCode;

    public NoiseGenerator(final NoiseDescriptor cfg) {
        this.seed = cfg.seed();
        this.interpolation = cfg.interpolation();
        this.frequencyX = cfg.frequencyX();
        this.frequencyY = cfg.frequencyY();
        this.frequencyZ = cfg.frequencyZ();
        this.offset = cfg.offset();
        this.gradientPerturb = cfg.gradientPerturb();
        this.gradientPerturbAmplitude = cfg.gradientPerturbAmplitude();
        this.gradientPerturbFrequency = cfg.gradientPerturbFrequency();
        this.hashCode = cfg.hashCode();
    }

    public abstract float getNoise(final float x);

    public abstract float getNoise(final float x, final float y);

    public abstract float getNoise(final float x, final float y, final float z);

    // Could theoretically modify these -- e.g. generate code to optimize.
    @Override
    public NoiseFunction1 wrapNoise1() {
        return x -> this.getNoise(this.frequencyX * x);
    }

    @Override
    public NoiseFunction2 wrapNoise2() {
        if (this.gradientPerturb) {
            return (x, y) -> this.perturb(x * this.frequencyX, y * this.frequencyY);
        }
        return (x, y) -> this.getNoise(x * this.frequencyX, y * this.frequencyY);
    }

    @Override
    public NoiseFunction3 wrapNoise3() {
        if (this.gradientPerturb) {
            return (x, y, z) ->
                this.perturb(x * this.frequencyX, (y + this.offset) * this.frequencyY, z * this.frequencyZ);
        }
        return (x, y, z) ->
            this.getNoise(x * this.frequencyX,  (y + this.offset) * this.frequencyY, z * this.frequencyZ);
    }

    protected float perturb(float x, float y) {
        final float xf = x * this.gradientPerturbFrequency;
        final float yf = y * this.gradientPerturbFrequency;

        final int x0 = fastFloor(xf);
        final int y0 = fastFloor(yf);
        final int x1 = x0 + 1;
        final int y1 = y0 + 1;

        final float xs = this.interpolation.interpolate(xf, x0);
        final float ys = this.interpolation.interpolate(yf, y0);

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
        return this.getNoise(x, y);
    }

    protected float perturb(float x, float y, float z) {
        final float xf = x * this.gradientPerturbFrequency;
        final float yf = y * this.gradientPerturbFrequency;
        final float zf = z * this.gradientPerturbFrequency;

        final int x0 = fastFloor(xf);
        final int y0 = fastFloor(yf);
        final int z0 = fastFloor(zf);
        final int x1 = x0 + 1;
        final int y1 = y0 + 1;
        final int z1 = z0 + 1;

        final float xs = this.interpolation.interpolate(xf, x0);
        final float ys = this.interpolation.interpolate(yf, y0);
        final float zs = this.interpolation.interpolate(zf, z0);

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
        return this.getNoise(x, y, z);
    }
}
