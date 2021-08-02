package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.NoiseDescriptor;

import static personthecat.fastnoise.util.NoiseUtils.*;
import static personthecat.fastnoise.util.NoiseUtils.interpolateQuintic;

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

        final float xs, ys;
        switch (this.interpolation) {
            case LINEAR:
                xs = x - x0;
                ys = y - y0;
                break;
            case HERMITE:
                xs = interpolateHermite(x - x0);
                ys = interpolateHermite(y - y0);
                break;
            default:
                xs = interpolateQuintic(x - x0);
                ys = interpolateQuintic(y - y0);
        }

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

        final float xs, ys, zs;
        switch (this.interpolation) {
            case LINEAR:
                xs = x - x0;
                ys = y - y0;
                zs = z - z0;
                break;
            case HERMITE:
                xs = interpolateHermite(x - x0);
                ys = interpolateHermite(y - y0);
                zs = interpolateHermite(z - z0);
                break;
            default:
                xs = interpolateQuintic(x - x0);
                ys = interpolateQuintic(y - y0);
                zs = interpolateQuintic(z - z0);
        }

        float xf00 = lerp(value3(seed, x0, y0, z0), value3(seed, x1, y0, z0), xs);
        float xf10 = lerp(value3(seed, x0, y1, z0), value3(seed, x1, y1, z0), xs);
        float xf01 = lerp(value3(seed, x0, y0, z1), value3(seed, x1, y0, z1), xs);
        float xf11 = lerp(value3(seed, x0, y1, z1), value3(seed, x1, y1, z1), xs);

        float yf0 = lerp(xf00, xf10, ys);
        float yf1 = lerp(xf01, xf11, ys);

        return lerp(yf0, yf1, zs);
    }
}
