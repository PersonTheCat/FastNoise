package personthecat.fastnoise.generator;

import personthecat.fastnoise.data.NoiseDescriptor;

import static personthecat.fastnoise.util.NoiseUtils.fastFloor;
import static personthecat.fastnoise.util.NoiseUtils.lerp;
import static personthecat.fastnoise.util.NoiseUtils.value2;
import static personthecat.fastnoise.util.NoiseUtils.value3;

public class ValueNoise extends FastNoise {

    public ValueNoise(NoiseDescriptor cfg) {
        super(cfg);
    }

    @Override
    public float getSingle(int seed, float x) {
        return 0;
    }

    @Override
    public float getSingle(final int seed, final float x, final float y) {
        int x0 = fastFloor(x);
        int y0 = fastFloor(y);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        final float xs = this.interpolation.interpolate(x, x0);
        final float ys = this.interpolation.interpolate(y, y0);

        float xf0 = lerp(value2(seed, x0, y0), value2(seed, x1, y0), xs);
        float xf1 = lerp(value2(seed, x0, y1), value2(seed, x1, y1), xs);

        return lerp(xf0, xf1, ys);
    }

    @Override
    public float getSingle(final int seed, final float x, final float y, final float z) {
        int x0 = fastFloor(x);
        int y0 = fastFloor(y);
        int z0 = fastFloor(z);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        int z1 = z0 + 1;

        final float xs = this.interpolation.interpolate(x, x0);
        final float ys = this.interpolation.interpolate(y, y0);
        final float zs = this.interpolation.interpolate(y, y0);

        float xf00 = lerp(value3(seed, x0, y0, z0), value3(seed, x1, y0, z0), xs);
        float xf10 = lerp(value3(seed, x0, y1, z0), value3(seed, x1, y1, z0), xs);
        float xf01 = lerp(value3(seed, x0, y0, z1), value3(seed, x1, y0, z1), xs);
        float xf11 = lerp(value3(seed, x0, y1, z1), value3(seed, x1, y1, z1), xs);

        float yf0 = lerp(xf00, xf10, ys);
        float yf1 = lerp(xf01, xf11, ys);

        return lerp(yf0, yf1, zs);
    }
}
