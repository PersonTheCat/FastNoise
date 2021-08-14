package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.NoiseDescriptor;

import static personthecat.fastnoise.util.NoiseUtils.fastFloor;
import static personthecat.fastnoise.util.NoiseUtils.gradient2;
import static personthecat.fastnoise.util.NoiseUtils.gradient3;

import static personthecat.fastnoise.util.NoiseValues.X_PRIME;
import static personthecat.fastnoise.util.NoiseValues.Y_PRIME;
import static personthecat.fastnoise.util.NoiseValues.Z_PRIME;
import static personthecat.fastnoise.util.NoiseValues.F2;
import static personthecat.fastnoise.util.NoiseValues.G2;
import static personthecat.fastnoise.util.NoiseValues.R3;

public class OpenSimplex2SNoise extends FastNoise {

    private static final int X_PRIME_2 = X_PRIME << 1;
    private static final int Y_PRIME_2 = Y_PRIME << 1;
    private static final int Z_PRIME_2 = Z_PRIME << 1;

    public OpenSimplex2SNoise(final NoiseDescriptor cfg) {
        super(cfg);
    }

    public OpenSimplex2SNoise(final int seed) {
        super(seed);
    }

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
        return 0;
    }

    @Override
    public float getSingle(final int seed, final float x, final float y) {
        // 2D OpenSimplex2S case is a modified 2D simplex noise.

        int i = fastFloor(x);
        int j = fastFloor(y);
        float xi = x - i;
        float yi = y - j;

        i *= X_PRIME;
        j *= Y_PRIME;
        int i1 = i + X_PRIME;
        int j1 = j + Y_PRIME;

        float t = (xi + yi) * G2;
        float x0 = xi - t;
        float y0 = yi - t;

        float a0 = (2.0f / 3.0f) - x0 * x0 - y0 * y0;
        float value = (a0 * a0) * (a0 * a0) * gradient2(seed, i, j, x0, y0);

        float a1 = (2 * (1 - 2 * G2) * (1 / G2 - 2)) * t + ((-2 * (1 - 2 * G2) * (1 - 2 * G2)) + a0);
        float x1 = x0 - 1 - 2 * G2;
        float y1 = y0 - 1 - 2 * G2;
        value += (a1 * a1) * (a1 * a1) * gradient2(seed, i1, j1, x1, y1);

        // Nested conditionals were faster than compact bit logic/arithmetic.
        float xmyi = xi - yi;
        if (t > G2) {
            if (xi + xmyi > 1) {
                float x2 = x0 + 3 * G2 - 2;
                float y2 = y0 + 3 * G2 - 1;
                float a2 = (2.0f / 3.0f) - x2 * x2 - y2 * y2;
                if (a2 > 0) {
                    value += (a2 * a2) * (a2 * a2) * gradient2(seed, i + X_PRIME_2, j + Y_PRIME, x2, y2);
                }
            } else {
                float x2 = x0 + G2;
                float y2 = y0 + G2 - 1;
                float a2 = (2.0f / 3.0f) - x2 * x2 - y2 * y2;
                if (a2 > 0) {
                    value += (a2 * a2) * (a2 * a2) * gradient2(seed, i, j + Y_PRIME, x2, y2);
                }
            }

            if (yi - xmyi > 1) {
                float x3 = x0 + 3 * G2 - 1;
                float y3 = y0 + 3 * G2 - 2;
                float a3 = (2.0f / 3.0f) - x3 * x3 - y3 * y3;
                if (a3 > 0) {
                    value += (a3 * a3) * (a3 * a3) * gradient2(seed, i + X_PRIME, j + Y_PRIME_2, x3, y3);
                }
            } else {
                float x3 = x0 + G2 - 1;
                float y3 = y0 + G2;
                float a3 = (2.0f / 3.0f) - x3 * x3 - y3 * y3;
                if (a3 > 0) {
                    value += (a3 * a3) * (a3 * a3) * gradient2(seed, i + X_PRIME, j, x3, y3);
                }
            }
        } else {
            if (xi + xmyi < 0) {
                float x2 = x0 + 1 - G2;
                float y2 = y0 - G2;
                float a2 = (2.0f / 3.0f) - x2 * x2 - y2 * y2;
                if (a2 > 0) {
                    value += (a2 * a2) * (a2 * a2) * gradient2(seed, i - X_PRIME, j, x2, y2);
                }
            } else {
                float x2 = x0 + G2 - 1;
                float y2 = y0 + G2;
                float a2 = (2.0f / 3.0f) - x2 * x2 - y2 * y2;
                if (a2 > 0) {
                    value += (a2 * a2) * (a2 * a2) * gradient2(seed, i + X_PRIME, j, x2, y2);
                }
            }

            if (yi < xmyi) {
                float x2 = x0 - G2;
                float y2 = y0 - G2 - 1;
                float a2 = (2.0f / 3.0f) - x2 * x2 - y2 * y2;
                if (a2 > 0) {
                    value += (a2 * a2) * (a2 * a2) * gradient2(seed, i, j - Y_PRIME, x2, y2);
                }
            } else {
                float x2 = x0 + G2;
                float y2 = y0 + G2 - 1;
                float a2 = (2.0f / 3.0f) - x2 * x2 - y2 * y2;
                if (a2 > 0) {
                    value += (a2 * a2) * (a2 * a2) * gradient2(seed, i, j + Y_PRIME, x2, y2);
                }
            }
        }

        return value * 18.24196194486065f;
    }

    @Override
    public float getSingle(final int seed, final float x, final float y, final float z) {
        // 3D OpenSimplex2S case uses two offset rotated cube grids.

        int i = fastFloor(x);
        int j = fastFloor(y);
        int k = fastFloor(z);
        float xi = x - i;
        float yi = y - j;
        float zi = z - k;

        i *= X_PRIME;
        j *= Y_PRIME;
        k *= Z_PRIME;
        int seed2 = seed + 1293373;

        int xNMask = (int)(-0.5f - xi);
        int yNMask = (int)(-0.5f - yi);
        int zNMask = (int)(-0.5f - zi);

        float x0 = xi + xNMask;
        float y0 = yi + yNMask;
        float z0 = zi + zNMask;
        float a0 = 0.75f - x0 * x0 - y0 * y0 - z0 * z0;
        float value = (a0 * a0) * (a0 * a0) * gradient3(seed,
            i + (xNMask & X_PRIME), j + (yNMask & Y_PRIME), k + (zNMask & Z_PRIME), x0, y0, z0);

        float x1 = xi - 0.5f;
        float y1 = yi - 0.5f;
        float z1 = zi - 0.5f;
        float a1 = 0.75f - x1 * x1 - y1 * y1 - z1 * z1;
        value += (a1 * a1) * (a1 * a1) * gradient3(seed2,
            i + X_PRIME, j + Y_PRIME, k + Z_PRIME, x1, y1, z1);

        float xAFlipMask0 = ((xNMask | 1) << 1) * x1;
        float yAFlipMask0 = ((yNMask | 1) << 1) * y1;
        float zAFlipMask0 = ((zNMask | 1) << 1) * z1;
        float xAFlipMask1 = (-2 - (xNMask << 2)) * x1 - 1.0f;
        float yAFlipMask1 = (-2 - (yNMask << 2)) * y1 - 1.0f;
        float zAFlipMask1 = (-2 - (zNMask << 2)) * z1 - 1.0f;

        boolean skip5 = false;
        float a2 = xAFlipMask0 + a0;
        if (a2 > 0) {
            float x2 = x0 - (xNMask | 1);
            value += (a2 * a2) * (a2 * a2) * gradient3(seed,
                i + (~xNMask & X_PRIME), j + (yNMask & Y_PRIME), k + (zNMask & Z_PRIME), x2, y0, z0);
        } else {
            float a3 = yAFlipMask0 + zAFlipMask0 + a0;
            if (a3 > 0) {
                float y3 = y0 - (yNMask | 1);
                float z3 = z0 - (zNMask | 1);
                value += (a3 * a3) * (a3 * a3) * gradient3(seed,
                    i + (xNMask & X_PRIME), j + (~yNMask & Y_PRIME), k + (~zNMask & Z_PRIME), x0, y3, z3);
            }

            float a4 = xAFlipMask1 + a1;
            if (a4 > 0) {
                float x4 = (xNMask | 1) + x1;
                value += (a4 * a4) * (a4 * a4) * gradient3(seed2,
                    i + (xNMask & (X_PRIME * 2)), j + Y_PRIME, k + Z_PRIME, x4, y1, z1);
                skip5 = true;
            }
        }

        boolean skip9 = false;
        float a6 = yAFlipMask0 + a0;
        if (a6 > 0) {
            float y6 = y0 - (yNMask | 1);
            value += (a6 * a6) * (a6 * a6) * gradient3(seed,
                i + (xNMask & X_PRIME), j + (~yNMask & Y_PRIME), k + (zNMask & Z_PRIME), x0, y6, z0);
        } else {
            float a7 = xAFlipMask0 + zAFlipMask0 + a0;
            if (a7 > 0) {
                float x7 = x0 - (xNMask | 1);
                float z7 = z0 - (zNMask | 1);
                value += (a7 * a7) * (a7 * a7) * gradient3(seed,
                    i + (~xNMask & X_PRIME), j + (yNMask & Y_PRIME), k + (~zNMask & Z_PRIME), x7, y0, z7);
            }

            float a8 = yAFlipMask1 + a1;
            if (a8 > 0) {
                float y8 = (yNMask | 1) + y1;
                value += (a8 * a8) * (a8 * a8) * gradient3(seed2,
                    i + X_PRIME, j + (yNMask & Y_PRIME_2), k + Z_PRIME, x1, y8, z1);
                skip9 = true;
            }
        }

        boolean skipD = false;
        float aA = zAFlipMask0 + a0;
        if (aA > 0) {
            float zA = z0 - (zNMask | 1);
            value += (aA * aA) * (aA * aA) * gradient3(seed,
                i + (xNMask & X_PRIME), j + (yNMask & Y_PRIME), k + (~zNMask & Z_PRIME), x0, y0, zA);
        } else {
            float aB = xAFlipMask0 + yAFlipMask0 + a0;
            if (aB > 0) {
                float xB = x0 - (xNMask | 1);
                float yB = y0 - (yNMask | 1);
                value += (aB * aB) * (aB * aB) * gradient3(seed,
                    i + (~xNMask & X_PRIME), j + (~yNMask & Y_PRIME), k + (zNMask & Z_PRIME), xB, yB, z0);
            }

            float aC = zAFlipMask1 + a1;
            if (aC > 0) {
                float zC = (zNMask | 1) + z1;
                value += (aC * aC) * (aC * aC) * gradient3(seed2,
                    i + X_PRIME, j + Y_PRIME, k + (zNMask & Z_PRIME_2), x1, y1, zC);
                skipD = true;
            }
        }

        if (!skip5) {
            float a5 = yAFlipMask1 + zAFlipMask1 + a1;
            if (a5 > 0) {
                float y5 = (yNMask | 1) + y1;
                float z5 = (zNMask | 1) + z1;
                value += (a5 * a5) * (a5 * a5) * gradient3(seed2,
                    i + X_PRIME, j + (yNMask & Y_PRIME_2), k + (zNMask & Z_PRIME_2), x1, y5, z5);
            }
        }

        if (!skip9) {
            float a9 = xAFlipMask1 + zAFlipMask1 + a1;
            if (a9 > 0) {
                float x9 = (xNMask | 1) + x1;
                float z9 = (zNMask | 1) + z1;
                value += (a9 * a9) * (a9 * a9) * gradient3(seed2,
                    i + (xNMask & (X_PRIME * 2)), j + Y_PRIME, k + (zNMask & Z_PRIME_2), x9, y1, z9);
            }
        }

        if (!skipD) {
            float aD = xAFlipMask1 + yAFlipMask1 + a1;
            if (aD > 0) {
                float xD = (xNMask | 1) + x1;
                float yD = (yNMask | 1) + y1;
                value += (aD * aD) * (aD * aD) * gradient3(seed2,
                    i + (xNMask & (X_PRIME << 1)), j + (yNMask & Y_PRIME_2), k + Z_PRIME, xD, yD, z1);
            }
        }

        return value * 9.046026385208288f;
    }
}
