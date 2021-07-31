package personthecat.fastnoise.util;

import lombok.experimental.UtilityClass;
import personthecat.fastnoise.data.Float2;
import personthecat.fastnoise.data.Float3;

@UtilityClass
public class NoiseUtils {

    private static final int X_PRIME = 1619;
    private static final int Y_PRIME = 31337;
    private static final int Z_PRIME = 6971;

    public static int fastFloor(final float f) {
        return (f >= 0 ? (int) f : (int) f - 1);
    }

    public static int fastRound(final float f) {
        return (f >= 0) ? (int) (f + (float) 0.5) : (int) (f - (float) 0.5);
    }

    public static float lerp(final float a, final float b, final float t) {
        return a + t * (b - a);
    }

    public static float cubicLerp(final float a, final float b, final float c, final float d, final float t) {
        float p = (d - c) - (a - b);
        return t * t * t * p + t * t * ((a - b) - p) + t * (c - a) + b;
    }

    public static int castFloatToInt(final float f) {
        int i = Float.floatToRawIntBits(f);
        return i ^ (i >> 16);
    }

    public static float getFractalBounding(final float gain, final int octaves) {
        float amp = gain;
        float ampFractal = 1.0F;
        for (int i = 1; i < octaves; i++) {
            ampFractal += amp;
            amp *= gain;
        }
        return 1 / ampFractal;
    }

    public static int hash2(final int seed, final int x, final int y) {
        int hash = seed;
        hash ^= X_PRIME * x;
        hash ^= Y_PRIME * y;

        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;

        return hash;
    }

    public static int hash3(final int seed, final int x, final int y, final int z) {
        int hash = seed;
        hash ^= X_PRIME * x;
        hash ^= Y_PRIME * y;
        hash ^= Z_PRIME * z;

        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;

        return hash;
    }

    public static float value1(final int seed, final float x) {
        return (seed & 1) == 0 ? x : -x;
    }

    public static float value2(final int seed, final int x, final int y) {
        int n = seed;
        n ^= X_PRIME * x;
        n ^= Y_PRIME * y;

        return (n * n * n * 60493) / (float) 2147483648.0;
    }

    public static float value3(final int seed, final int x, final int y, final int z) {
        int n = seed;
        n ^= X_PRIME * x;
        n ^= Y_PRIME * y;
        n ^= Z_PRIME * z;

        return (n * n * n * 60493) / (float) 2147483648.0;
    }

    public static float gradient2(final int seed, final int x, final int y, final float xd, final float yd) {
        int hash = seed;
        hash ^= X_PRIME * x;
        hash ^= Y_PRIME * y;

        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;

        Float2 g = NoiseTables.GRAD_2D[hash & 7];

        return xd * g.x + yd * g.y;
    }

    public static float gradient3(final int seed, final int x, final int y, final int z, final float xd, final float yd, final float zd) {
        int hash = seed;
        hash ^= X_PRIME * x;
        hash ^= Y_PRIME * y;
        hash ^= Z_PRIME * z;

        hash = hash * hash * hash * 60493;
        hash = (hash >> 13) ^ hash;

        Float3 g = NoiseTables.GRAD_3D[hash & 15];

        return xd * g.x + yd * g.y + zd * g.z;
    }
}
