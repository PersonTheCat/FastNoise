package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.NoiseDescriptor;

import static personthecat.fastnoise.util.NoiseUtils.cubicLerp;
import static personthecat.fastnoise.util.NoiseUtils.fastFloor;
import static personthecat.fastnoise.util.NoiseUtils.value2;
import static personthecat.fastnoise.util.NoiseUtils.value3;

public class CubicNoise extends FastNoise {

    private final static float CUBIC_2D_BOUNDING = 1 / (float) (1.5 * 1.5);
    private final static float CUBIC_3D_BOUNDING = 1 / (float) (1.5 * 1.5 * 1.5);

    public CubicNoise(final NoiseDescriptor cfg) {
        super(cfg);
    }

    @Override
    public float getSingle(final int seed, final float x) {
        return 0;
    }

    @Override
    public float getSingle(final int seed, final float x, final float y) {
        int x1 = fastFloor(x);
        int y1 = fastFloor(y);

        int x0 = x1 - 1;
        int y0 = y1 - 1;
        int x2 = x1 + 1;
        int y2 = y1 + 1;
        int x3 = x1 + 2;
        int y3 = y1 + 2;

        float xs = x - (float) x1;
        float ys = y - (float) y1;

        return cubicLerp(
            cubicLerp(value2(seed, x0, y0), value2(seed, x1, y0), value2(seed, x2, y0), value2(seed, x3, y0), xs),
            cubicLerp(value2(seed, x0, y1), value2(seed, x1, y1), value2(seed, x2, y1), value2(seed, x3, y1), xs),
            cubicLerp(value2(seed, x0, y2), value2(seed, x1, y2), value2(seed, x2, y2), value2(seed, x3, y2), xs),
            cubicLerp(value2(seed, x0, y3), value2(seed, x1, y3), value2(seed, x2, y3), value2(seed, x3, y3), xs),
            ys
        ) * CUBIC_2D_BOUNDING;
    }

    @Override
    public float getSingle(final int seed, final float x, final float y, final float z) {
        int x1 = fastFloor(x);
        int y1 = fastFloor(y);
        int z1 = fastFloor(z);

        int x0 = x1 - 1;
        int y0 = y1 - 1;
        int z0 = z1 - 1;
        int x2 = x1 + 1;
        int y2 = y1 + 1;
        int z2 = z1 + 1;
        int x3 = x1 + 2;
        int y3 = y1 + 2;
        int z3 = z1 + 2;

        float xs = x - (float) x1;
        float ys = y - (float) y1;
        float zs = z - (float) z1;

        return cubicLerp(
            cubicLerp(
                cubicLerp(value3(seed, x0, y0, z0), value3(seed, x1, y0, z0), value3(seed, x2, y0, z0), value3(seed, x3, y0, z0), xs),
                cubicLerp(value3(seed, x0, y1, z0), value3(seed, x1, y1, z0), value3(seed, x2, y1, z0), value3(seed, x3, y1, z0), xs),
                cubicLerp(value3(seed, x0, y2, z0), value3(seed, x1, y2, z0), value3(seed, x2, y2, z0), value3(seed, x3, y2, z0), xs),
                cubicLerp(value3(seed, x0, y3, z0), value3(seed, x1, y3, z0), value3(seed, x2, y3, z0), value3(seed, x3, y3, z0), xs),
                ys),
            cubicLerp(
                cubicLerp(value3(seed, x0, y0, z1), value3(seed, x1, y0, z1), value3(seed, x2, y0, z1), value3(seed, x3, y0, z1), xs),
                cubicLerp(value3(seed, x0, y1, z1), value3(seed, x1, y1, z1), value3(seed, x2, y1, z1), value3(seed, x3, y1, z1), xs),
                cubicLerp(value3(seed, x0, y2, z1), value3(seed, x1, y2, z1), value3(seed, x2, y2, z1), value3(seed, x3, y2, z1), xs),
                cubicLerp(value3(seed, x0, y3, z1), value3(seed, x1, y3, z1), value3(seed, x2, y3, z1), value3(seed, x3, y3, z1), xs),
                ys),
            cubicLerp(
                cubicLerp(value3(seed, x0, y0, z2), value3(seed, x1, y0, z2), value3(seed, x2, y0, z2), value3(seed, x3, y0, z2), xs),
                cubicLerp(value3(seed, x0, y1, z2), value3(seed, x1, y1, z2), value3(seed, x2, y1, z2), value3(seed, x3, y1, z2), xs),
                cubicLerp(value3(seed, x0, y2, z2), value3(seed, x1, y2, z2), value3(seed, x2, y2, z2), value3(seed, x3, y2, z2), xs),
                cubicLerp(value3(seed, x0, y3, z2), value3(seed, x1, y3, z2), value3(seed, x2, y3, z2), value3(seed, x3, y3, z2), xs),
                ys),
            cubicLerp(
                cubicLerp(value3(seed, x0, y0, z3), value3(seed, x1, y0, z3), value3(seed, x2, y0, z3), value3(seed, x3, y0, z3), xs),
                cubicLerp(value3(seed, x0, y1, z3), value3(seed, x1, y1, z3), value3(seed, x2, y1, z3), value3(seed, x3, y1, z3), xs),
                cubicLerp(value3(seed, x0, y2, z3), value3(seed, x1, y2, z3), value3(seed, x2, y2, z3), value3(seed, x3, y2, z3), xs),
                cubicLerp(value3(seed, x0, y3, z3), value3(seed, x1, y3, z3), value3(seed, x2, y3, z3), value3(seed, x3, y3, z3), xs),
                ys),
            zs
        ) * CUBIC_3D_BOUNDING;
    }
}
