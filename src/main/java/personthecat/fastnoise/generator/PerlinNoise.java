package personthecat.fastnoise.generator;

import personthecat.fastnoise.data.NoiseDescriptor;

import static personthecat.fastnoise.util.NoiseUtils.fastFloor;
import static personthecat.fastnoise.util.NoiseUtils.gradient2;
import static personthecat.fastnoise.util.NoiseUtils.gradient3;
import static personthecat.fastnoise.util.NoiseUtils.lerp;
import static personthecat.fastnoise.util.NoiseUtils.value1;

public class PerlinNoise extends FastNoise {

    public PerlinNoise(final NoiseDescriptor cfg) {
        super(cfg);
    }

    @Override
    public final float getSingle(final int seed, float x) {
        x -= fastFloor(x);
        float u = this.interpolation.interpolate(x, 0);
        float gradA = value1(seed, x);
        float gradB = value1(seed + 1, x - 1);
        return lerp(u, gradA, gradB);
    }

    @Override
    public float getSingle(final int seed, final float x, final float y) {
        final int x0 = fastFloor(x);
        final int y0 = fastFloor(y);
        final int x1 = x0 + 1;
        final int y1 = y0 + 1;

        final float xs = this.interpolation.interpolate(x, x0);
        final float ys = this.interpolation.interpolate(y, y0);

        final float xd0 = x - x0;
        final float yd0 = y - y0;
        final float xd1 = xd0 - 1;
        final float yd1 = yd0 - 1;

        final float xf0 = lerp(gradient2(seed, x0, y0, xd0, yd0), gradient2(seed, x1, y0, xd1, yd0), xs);
        final float xf1 = lerp(gradient2(seed, x0, y1, xd0, yd1), gradient2(seed, x1, y1, xd1, yd1), xs);

        return lerp(xf0, xf1, ys);
    }

    @Override
    public float getSingle(final int seed, final float x, final float y, final float z) {
        final int x0 = fastFloor(x);
        final int y0 = fastFloor(y);
        final int z0 = fastFloor(z);
        final int x1 = x0 + 1;
        final int y1 = y0 + 1;
        final int z1 = z0 + 1;

        final float xs = this.interpolation.interpolate(x, x0);
        final float ys = this.interpolation.interpolate(y, y0);
        final float zs = this.interpolation.interpolate(z, z0);

        final float xd0 = x - x0;
        final float yd0 = y - y0;
        final float zd0 = z - z0;
        final float xd1 = xd0 - 1;
        final float yd1 = yd0 - 1;
        final float zd1 = zd0 - 1;

        final float xf00 = lerp(gradient3(seed, x0, y0, z0, xd0, yd0, zd0), gradient3(seed, x1, y0, z0, xd1, yd0, zd0), xs);
        final float xf10 = lerp(gradient3(seed, x0, y1, z0, xd0, yd1, zd0), gradient3(seed, x1, y1, z0, xd1, yd1, zd0), xs);
        final float xf01 = lerp(gradient3(seed, x0, y0, z1, xd0, yd0, zd1), gradient3(seed, x1, y0, z1, xd1, yd0, zd1), xs);
        final float xf11 = lerp(gradient3(seed, x0, y1, z1, xd0, yd1, zd1), gradient3(seed, x1, y1, z1, xd1, yd1, zd1), xs);

        final float yf0 = lerp(xf00, xf10, ys);
        final float yf1 = lerp(xf01, xf11, ys);

        return lerp(yf0, yf1, zs);
    }
}