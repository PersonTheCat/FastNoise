package personthecat.fastnoise.generator;

import personthecat.fastnoise.data.NoiseDescriptor;

import static personthecat.fastnoise.util.NoiseUtils.fastFloor;
import static personthecat.fastnoise.util.NoiseUtils.gradient2;
import static personthecat.fastnoise.util.NoiseUtils.gradient3;

public class SimplexNoise extends FastNoise {

    private final static float F2 = (float) (1.0 / 2.0);
    private final static float G2 = (float) (1.0 / 4.0);
    private final static float F3 = (float) (1.0 / 3.0);
    private final static float G3 = (float) (1.0 / 6.0);
    private final static float G33 = G3 * 3 - 1;

    public SimplexNoise(final NoiseDescriptor cfg) {
        super(cfg);
    }

    @Override
    public float getSingle(int seed, float x) {
        return 0;
    }

    @Override
    public float getSingle(final int seed, final float x, final float y) {
        float t = (x + y) * F2;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);

        t = (i + j) * G2;
        float X0 = i - t;
        float Y0 = j - t;

        float x0 = x - X0;
        float y0 = y - Y0;

        int i1, j1;
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }

        float x1 = x0 - i1 + G2;
        float y1 = y0 - j1 + G2;
        float x2 = x0 - 1 + F2;
        float y2 = y0 - 1 + F2;

        float n0, n1, n2;

        t = (float) 0.5 - x0 * x0 - y0 * y0;
        if (t < 0) n0 = 0;
        else {
            t *= t;
            n0 = t * t * gradient2(seed, i, j, x0, y0);
        }

        t = (float) 0.5 - x1 * x1 - y1 * y1;
        if (t < 0) n1 = 0;
        else {
            t *= t;
            n1 = t * t * gradient2(seed, i + i1, j + j1, x1, y1);
        }

        t = (float) 0.5 - x2 * x2 - y2 * y2;
        if (t < 0) n2 = 0;
        else {
            t *= t;
            n2 = t * t * gradient2(seed, i + 1, j + 1, x2, y2);
        }

        return 50 * (n0 + n1 + n2);
    }

    @Override
    public float getSingle(final int seed, final float x, final float y, final float z) {
        float t = (x + y + z) * F3;
        int i = fastFloor(x + t);
        int j = fastFloor(y + t);
        int k = fastFloor(z + t);

        t = (i + j + k) * G3;
        float x0 = x - (i - t);
        float y0 = y - (j - t);
        float z0 = z - (k - t);

        int i1, j1, k1;
        int i2, j2, k2;

        if (x0 >= y0) {
            if (y0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            } else if (x0 >= z0) {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            } else { // x0 < z0
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        } else { // x0 < y0
            if (y0 < z0) {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } else if (x0 < z0) {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            } else { // x0 >= z0
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }
        }

        float x1 = x0 - i1 + G3;
        float y1 = y0 - j1 + G3;
        float z1 = z0 - k1 + G3;
        float x2 = x0 - i2 + F3;
        float y2 = y0 - j2 + F3;
        float z2 = z0 - k2 + F3;
        float x3 = x0 + G33;
        float y3 = y0 + G33;
        float z3 = z0 + G33;

        float n0, n1, n2, n3;

        t = (float) 0.6 - x0 * x0 - y0 * y0 - z0 * z0;
        if (t < 0) n0 = 0;
        else {
            t *= t;
            n0 = t * t * gradient3(seed, i, j, k, x0, y0, z0);
        }

        t = (float) 0.6 - x1 * x1 - y1 * y1 - z1 * z1;
        if (t < 0) n1 = 0;
        else {
            t *= t;
            n1 = t * t * gradient3(seed, i + i1, j + j1, k + k1, x1, y1, z1);
        }

        t = (float) 0.6 - x2 * x2 - y2 * y2 - z2 * z2;
        if (t < 0) n2 = 0;
        else {
            t *= t;
            n2 = t * t * gradient3(seed, i + i2, j + j2, k + k2, x2, y2, z2);
        }

        t = (float) 0.6 - x3 * x3 - y3 * y3 - z3 * z3;
        if (t < 0) n3 = 0;
        else {
            t *= t;
            n3 = t * t * gradient3(seed, i + 1, j + 1, k + 1, x3, y3, z3);
        }

        return 32 * (n0 + n1 + n2 + n3);
    }
}
