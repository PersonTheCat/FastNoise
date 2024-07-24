package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.NoiseBuilder;
import personthecat.fastnoise.data.NoiseType;

import static personthecat.fastnoise.util.NoiseUtils.fastFloor;
import static personthecat.fastnoise.util.NoiseUtils.fastRound;
import static personthecat.fastnoise.util.NoiseUtils.gradient2L;
import static personthecat.fastnoise.util.NoiseUtils.gradient3;

import static personthecat.fastnoise.util.NoiseValues.X_PRIME;
import static personthecat.fastnoise.util.NoiseValues.Y_PRIME;
import static personthecat.fastnoise.util.NoiseValues.Z_PRIME;
import static personthecat.fastnoise.util.NoiseValues.F2;
import static personthecat.fastnoise.util.NoiseValues.G2;
import static personthecat.fastnoise.util.NoiseValues.R3;

// We may or may not remove one of these classes.
// Still testing performance.
public class OpenSimplex2Noise extends FastNoise {

    public OpenSimplex2Noise(final NoiseBuilder cfg) {
        super(cfg);
    }

    public OpenSimplex2Noise(final int seed) {
        super(seed);
    }

    @Override
    public NoiseBuilder toBuilder() {
        return super.toBuilder().type(NoiseType.SIMPLEX2);
    }

    // Moved these calculations before frequency from FastNoiseLite. Looks better (?)
    @Override
    public float getNoise(final float x, final float y) {
        final float s = (x + y) * F2;
        return super.getNoise(x + s, y + s);
    }

    @Override
    public float getNoise(final float x, final float y, final float z) {
        final float r = (x + y + z) * R3; // Rotation, not skew
        return super.getNoise(r - x, r - y, r - z);
    }

    @Override
    public float getSingle(final int seed, final float x) {
        return this.getSingle(seed, x, 1337);
    }

    @Override
    public float getSingle(final int seed, float x, float y) {
        // 2D OpenSimplex2 case uses the same algorithm as ordinary Simplex.

        int i = fastFloor(x);
        int j = fastFloor(y);
        final float xi = x - i;
        final float yi = y - j;

        final float t = (xi + yi) * G2;
        final float x0 = xi - t;
        final float y0 = yi - t;

        i *= X_PRIME;
        j *= Y_PRIME;

        final float n0, n1, n2;

        float a = 0.5f - x0 * x0 - y0 * y0;
        if (a <= 0) {
            n0 = 0;
        } else {
            n0 = (a * a) * (a * a) * gradient2L(seed, i, j, x0, y0);
        }

        final float c = (2 * (1 - 2 * G2) * (1 / G2 - 2)) * t + ((-2 * (1 - 2 * G2) * (1 - 2 * G2)) + a);
        if (c <= 0) {
            n2 = 0;
        } else {
            final float x2 = x0 + (2 * G2 - 1);
            final float y2 = y0 + (2 * G2 - 1);
            n2 = (c * c) * (c * c) * gradient2L(seed, i + X_PRIME, j + Y_PRIME, x2, y2);
        }

        if (y0 > x0) {
            final float x1 = x0 + G2;
            final float y1 = y0 + G2 - 1;
            final float b = 0.5f - x1 * x1 - y1 * y1;
            if (b <= 0) n1 = 0;
            else {
                n1 = (b * b) * (b * b) * gradient2L(seed, i, j + Y_PRIME, x1, y1);
            }
        } else {
            final float x1 = x0 + G2 - 1;
            final float y1 = y0 + G2;
            final float b = 0.5f - x1 * x1 - y1 * y1;
            if (b <= 0) {
                n1 = 0;
            } else {
                n1 = (b * b) * (b * b) * gradient2L(seed, i + X_PRIME, j, x1, y1);
            }
        }

        return (n0 + n1 + n2) * 99.83685446303647f;
    }

    @Override
    public float getSingle(int seed, float x, float y, float z) {
         // 3D OpenSimplex2 case uses two offset rotated cube grids.

        int i = fastRound(x);
        int j = fastRound(y);
        int k = fastRound(z);
        float x0 = x - i;
        float y0 = y - j;
        float z0 = z - k;

        int xNSign = (int) (-1.0f - x0) | 1;
        int yNSign = (int) (-1.0f - y0) | 1;
        int zNSign = (int) (-1.0f - z0) | 1;

        float ax0 = xNSign * -x0;
        float ay0 = yNSign * -y0;
        float az0 = zNSign * -z0;

        i *= X_PRIME;
        j *= Y_PRIME;
        k *= Z_PRIME;

        float value = 0;
        float a = (0.6f - x0 * x0) - (y0 * y0 + z0 * z0);

        for (int l = 0; ; l++) {
            if (a > 0) {
                value += (a * a) * (a * a) * gradient3(seed, i, j, k, x0, y0, z0);
            }

            if (ax0 >= ay0 && ax0 >= az0) {
                float b = a + ax0 + ax0;
                if (b > 1) {
                    b -= 1;
                    value += (b * b) * (b * b) * gradient3(seed, i - xNSign * X_PRIME, j, k, x0 + xNSign, y0, z0);
                }
            } else if (ay0 > ax0 && ay0 >= az0) {
                float b = a + ay0 + ay0;
                if (b > 1) {
                    b -= 1;
                    value += (b * b) * (b * b) * gradient3(seed, i, j - yNSign * Y_PRIME, k, x0, y0 + yNSign, z0);
                }
            } else {
                float b = a + az0 + az0;
                if (b > 1) {
                    b -= 1;
                    value += (b * b) * (b * b) * gradient3(seed, i, j, k - zNSign * Z_PRIME, x0, y0, z0 + zNSign);
                }
            }

            if (l == 1) break;

            ax0 = 0.5f - ax0;
            ay0 = 0.5f - ay0;
            az0 = 0.5f - az0;

            x0 = xNSign * ax0;
            y0 = yNSign * ay0;
            z0 = zNSign * az0;

            a += (0.75f - ax0) - (ay0 + az0);

            i += (xNSign >> 1) & X_PRIME;
            j += (yNSign >> 1) & Y_PRIME;
            k += (zNSign >> 1) & Z_PRIME;

            xNSign = -xNSign;
            yNSign = -yNSign;
            zNSign = -zNSign;

            seed = ~seed;
        }

        return value * 32.69428253173828125f;
    }
}
