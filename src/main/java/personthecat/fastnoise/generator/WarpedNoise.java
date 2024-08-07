package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.WarpType;
import personthecat.fastnoise.data.Float2;
import personthecat.fastnoise.data.Float3;
import personthecat.fastnoise.data.NoiseBuilder;
import personthecat.fastnoise.data.NoiseType;

import static personthecat.fastnoise.util.NoiseTables.CELL_2D;
import static personthecat.fastnoise.util.NoiseTables.CELL_2DL;
import static personthecat.fastnoise.util.NoiseTables.CELL_3D;
import static personthecat.fastnoise.util.NoiseTables.CELL_3DL;
import static personthecat.fastnoise.util.NoiseTables.GRAD_2DL;
import static personthecat.fastnoise.util.NoiseTables.GRAD_3DL;
import static personthecat.fastnoise.util.NoiseUtils.fastFloor;
import static personthecat.fastnoise.util.NoiseUtils.fastRound;
import static personthecat.fastnoise.util.NoiseUtils.hash2;
import static personthecat.fastnoise.util.NoiseUtils.hash3;
import static personthecat.fastnoise.util.NoiseUtils.interpolateHermite;
import static personthecat.fastnoise.util.NoiseUtils.lerp;
import static personthecat.fastnoise.util.NoiseValues.X_PRIME;
import static personthecat.fastnoise.util.NoiseValues.Y_PRIME;
import static personthecat.fastnoise.util.NoiseValues.Z_PRIME;
import static personthecat.fastnoise.util.NoiseValues.F2;
import static personthecat.fastnoise.util.NoiseValues.G2;
import static personthecat.fastnoise.util.NoiseValues.R3;

@SuppressWarnings("unused")
public abstract class WarpedNoise extends FastNoise {

    protected final FastNoise reference;
    protected final float warpAmplitudeX;
    protected final float warpAmplitudeY;
    protected final float warpAmplitudeZ;
    protected final float warpFrequencyX;
    protected final float warpFrequencyY;
    protected final float warpFrequencyZ;

    public WarpedNoise(final NoiseBuilder cfg, final FastNoise reference) {
        super(cfg);
        this.reference = reference;
        this.warpAmplitudeX = cfg.warpAmplitudeX();
        this.warpAmplitudeY = cfg.warpAmplitudeY();
        this.warpAmplitudeZ = cfg.warpAmplitudeZ();
        this.warpFrequencyX = cfg.warpFrequencyX();
        this.warpFrequencyY = cfg.warpFrequencyY();
        this.warpFrequencyZ = cfg.warpFrequencyZ();
    }

    public WarpedNoise(final int seed, final FastNoise reference) {
        this(FastNoise.builder().seed(seed), reference);
    }

    @Override
    public NoiseBuilder toBuilder() {
        return super.toBuilder()
            .type(NoiseType.WARPED)
            .reference(this.reference.toBuilder())
            .warpAmplitudeX(this.warpAmplitudeX)
            .warpAmplitudeY(this.warpAmplitudeY)
            .warpAmplitudeZ(this.warpAmplitudeZ)
            .warpFrequencyX(this.warpFrequencyX)
            .warpFrequencyY(this.warpFrequencyY)
            .warpFrequencyZ(this.warpFrequencyZ);
    }

    protected abstract Float2 warp(int seed, float x, float y);
    protected abstract Float3 warp(int seed, float x, float y, float z);

    @Override
    public float getSingle(int seed, float x) {
        return this.getSingle(seed, x, 1337);
    }

    @Override
    public float getSingle(int seed, float x, float y) {
        final Float2 vec = this.warp(seed, x, y);
        return this.reference.getSingle(seed, vec.x, vec.y);
    }

    @Override
    public float getSingle(int seed, float x, float y, float z) {
        final Float3 vec = this.warp(seed, x, y, z);
        return this.reference.getSingle(seed, vec.x, vec.y, vec.z);
    }

    @Override
    public float getNoise(float x) {
        return this.getNoise(x, 1337);
    }

    @Override
    public float getNoise(float x, float y) {
        x += this.offsetX;
        y += this.offsetY;
        final Float2 vec = this.warp(this.seed, x, y);
        x = vec.x * this.frequencyX;
        y = vec.y * this.frequencyY;
        return this.reference.getSingle(this.seed, x, y);
    }

    @Override
    public float getNoise(float x, float y, float z) {
        x += this.offsetX;
        y += this.offsetY;
        z += this.offsetZ;
        final Float3 vec = this.warp(this.seed, x, y, z);
        x = vec.x * this.frequencyX;
        y = vec.y * this.frequencyY;
        z = vec.z * this.frequencyZ;
        return this.reference.getSingle(this.seed, x, y, z);
    }

    public static class BasicGrid extends WarpedNoise {

        public BasicGrid(final NoiseBuilder cfg, final FastNoise reference) {
            super(cfg, reference);
        }

        @Override
        protected Float2 warp(int seed, float x, float y) {
            final float xf = x * this.warpFrequencyX;
            final float yf = y * this.warpFrequencyY;

            final int x0 = fastFloor(xf);
            final int y0 = fastFloor(yf);
            final int x1 = x0 + 1;
            final int y1 = y0 + 1;

            final float xs = interpolateHermite(xf - x0);
            final float ys = interpolateHermite(yf - y0);

            Float2 vec0 = CELL_2D[hash2(this.seed, x0, y0) & 255];
            Float2 vec1 = CELL_2D[hash2(this.seed, x1, y0) & 255];

            final float lx0x = lerp(vec0.x, vec1.x, xs);
            final float ly0x = lerp(vec0.y, vec1.y, xs);

            vec0 = CELL_2D[hash2(this.seed, x0, y1) & 255];
            vec1 = CELL_2D[hash2(this.seed, x1, y1) & 255];

            final float lx1x = lerp(vec0.x, vec1.x, xs);
            final float ly1x = lerp(vec0.y, vec1.y, xs);

            x += lerp(lx0x, lx1x, ys) * (this.warpAmplitudeX / 0.45F);
            y += lerp(ly0x, ly1x, ys) * (this.warpAmplitudeY / 0.45F);

            return new Float2(x, y);
        }

        @Override
        protected Float3 warp(int seed, float x, float y, float z) {
            final float xf = x * this.warpFrequencyX;
            final float yf = y * this.warpFrequencyY;
            final float zf = z * this.warpFrequencyZ;

            final int x0 = fastFloor(xf);
            final int y0 = fastFloor(yf);
            final int z0 = fastFloor(zf);
            final int x1 = x0 + 1;
            final int y1 = y0 + 1;
            final int z1 = z0 + 1;

            final float xs = interpolateHermite(xf - x0);
            final float ys = interpolateHermite(yf - y0);
            final float zs = interpolateHermite(zf - z0);

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

            x += lerp(lx0y, lerp(lx0x, lx1x, ys), zs) * (this.warpAmplitudeX / 0.45F);
            y += lerp(ly0y, lerp(ly0x, ly1x, ys), zs) * (this.warpAmplitudeY / 0.45F);
            z += lerp(lz0y, lerp(lz0x, lz1x, ys), zs) * (this.warpAmplitudeZ / 0.45F);

            return new Float3(x, y, z);
        }

        @Override
        public NoiseBuilder toBuilder() {
            return super.toBuilder().warp(WarpType.BASIC_GRID);
        }
    }

    public static class Simplex2 extends WarpedNoise {

        public Simplex2(final NoiseBuilder cfg, final FastNoise reference) {
            super(cfg, reference);
        }

        @Override
        protected Float2 warp(int seed, float x, float y) {
            float xs = x * this.warpFrequencyX;
            float ys = y * this.warpFrequencyY;

            final float s = (xs + ys) * F2;
            xs += s;
            ys += s;

            int i = fastFloor(xs);
            int j = fastFloor(ys);
            float xi = xs - i;
            float yi = ys - j;

            float t = (xi + yi) * G2;
            float x0 = xi - t;
            float y0 = yi - t;

            i *= X_PRIME;
            j *= Y_PRIME;

            float vx, vy;
            vx = vy = 0;

            float a = 0.5f - x0 * x0 - y0 * y0;
            if (a > 0) {
                float aaaa = (a * a) * (a * a);
                int hash = hash2(this.seed, i, j);
                int index1 = hash & (127 << 1);
                int index2 = (hash >> 7) & (255 << 1);
                float xg = GRAD_2DL[index1];
                float yg = GRAD_2DL[index1 | 1];
                float value = x0 * xg + y0 * yg;
                float xgo = CELL_2DL[index2];
                float ygo = CELL_2DL[index2 | 1];
                final float xo = value * xgo;
                final float yo = value * ygo;
                vx += aaaa * xo;
                vy += aaaa * yo;
            }
            float c = (2 * (1 - 2 * G2) * (1 / G2 - 2)) * t + ((-2 * (1 - 2 * G2) * (1 - 2 * G2)) + a);
            if (c > 0) {
                float x2 = x0 + (2 * G2 - 1);
                float y2 = y0 + (2 * G2 - 1);
                float cccc = (c * c) * (c * c);
                int hash = hash2(this.seed, i + X_PRIME, j + Y_PRIME);
                int index1 = hash & (127 << 1);
                int index2 = (hash >> 7) & (255 << 1);
                float xg = GRAD_2DL[index1];
                float yg = GRAD_2DL[index1 | 1];
                float value = x2 * xg + y2 * yg;
                float xgo = CELL_2DL[index2];
                float ygo = CELL_2DL[index2 | 1];
                float xo = value * xgo;
                float yo = value * ygo;
                vx += cccc * xo;
                vy += cccc * yo;
            }

            if (y0 > x0) {
                float x1 = x0 + G2;
                float y1 = y0 + (G2 - 1);
                float b = 0.5f - x1 * x1 - y1 * y1;
                if (b > 0) {
                    float bbbb = (b * b) * (b * b);
                    int hash = hash2(this.seed, i, j + Y_PRIME);
                    int index1 = hash & (127 << 1);
                    int index2 = (hash >> 7) & (255 << 1);
                    float xg = GRAD_2DL[index1];
                    float yg = GRAD_2DL[index1 | 1];
                    float value = x1 * xg + y1 * yg;
                    float xgo = CELL_2DL[index2];
                    float ygo = CELL_2DL[index2 | 1];
                    float xo = value * xgo;
                    float yo = value * ygo;
                    vx += bbbb * xo;
                    vy += bbbb * yo;
                }
            } else {
                float x1 = x0 + (G2 - 1);
                float y1 = y0 + G2;
                float b = 0.5f - x1 * x1 - y1 * y1;
                if (b > 0) {
                    float bbbb = (b * b) * (b * b);
                    int hash = hash2(this.seed, i + X_PRIME, j);
                    int index1 = hash & (127 << 1);
                    int index2 = (hash >> 7) & (255 << 1);
                    float xg = GRAD_2DL[index1];
                    float yg = GRAD_2DL[index1 | 1];
                    float value = x1 * xg + y1 * yg;
                    float xgo = CELL_2DL[index2];
                    float ygo = CELL_2DL[index2 | 1];
                    float xo = value * xgo;
                    float yo = value * ygo;
                    vx += bbbb * xo;
                    vy += bbbb * yo;
                }
            }

            x += vx * this.warpAmplitudeX * 38.283687591552734375f;
            y += vy * this.warpAmplitudeY * 38.283687591552734375f;

            return new Float2(x, y);
        }

        @Override
        protected Float3 warp(int seed, float x, float y, float z) {
            float xr = x * this.warpFrequencyX;
            float yr = y * this.warpFrequencyY;
            float zr = z * this.warpFrequencyZ;

            final float r = (xr + yr + zr) * R3; // Rotation, not skew
            xr = r - xr;
            yr = r - yr;
            zr = r - zr;

            int i = fastRound(xr);
            int j = fastRound(yr);
            int k = fastRound(zr);
            float x0 = xr - i;
            float y0 = yr - j;
            float z0 = zr - k;

            int xNSign = (int)(-x0 - 1.0f) | 1;
            int yNSign = (int)(-y0 - 1.0f) | 1;
            int zNSign = (int)(-z0 - 1.0f) | 1;

            float ax0 = xNSign * -x0;
            float ay0 = yNSign * -y0;
            float az0 = zNSign * -z0;

            i *= X_PRIME;
            j *= Y_PRIME;
            k *= Z_PRIME;

            float vx, vy, vz;
            vx = vy = vz = 0;

            float a = (0.6f - x0 * x0) - (y0 * y0 + z0 * z0);
            for (int l = 0; ; l++) {
                if (a > 0) {
                    float aaaa = (a * a) * (a * a);
                    int hash = hash3(seed, i, j, k);
                    int index1 = hash & (63 << 2);
                    int index2 = (hash >> 6) & (255 << 2);
                    float xg = GRAD_3DL[index1];
                    float yg = GRAD_3DL[index1 | 1];
                    float zg = GRAD_3DL[index1 | 2];
                    float value = x0 * xg + y0 * yg + z0 * zg;
                    float xgo = CELL_3DL[index2];
                    float ygo = CELL_3DL[index2 | 1];
                    float zgo = CELL_3DL[index2 | 2];
                    float xo = value * xgo;
                    float yo = value * ygo;
                    float zo = value * zgo;
                    vx += aaaa * xo;
                    vy += aaaa * yo;
                    vz += aaaa * zo;
                }

                float b = a;
                int i1 = i;
                int j1 = j;
                int k1 = k;
                float x1 = x0;
                float y1 = y0;
                float z1 = z0;

                if (ax0 >= ay0 && ax0 >= az0) {
                    x1 += xNSign;
                    b = b + ax0 + ax0;
                    i1 -= xNSign * X_PRIME;
                } else if (ay0 > ax0 && ay0 >= az0) {
                    y1 += yNSign;
                    b = b + ay0 + ay0;
                    j1 -= yNSign * Y_PRIME;
                } else {
                    z1 += zNSign;
                    b = b + az0 + az0;
                    k1 -= zNSign * Z_PRIME;
                }

                if (b > 1) {
                    b -= 1;
                    float bbbb = (b * b) * (b * b);
                    int hash = hash3(seed, i1, j1, k1);
                    int index1 = hash & (63 << 2);
                    int index2 = (hash >> 6) & (255 << 2);
                    float xg = GRAD_3DL[index1];
                    float yg = GRAD_3DL[index1 | 1];
                    float zg = GRAD_3DL[index1 | 2];
                    float value = x1 * xg + y1 * yg + z1 * zg;
                    float xgo = CELL_3DL[index2];
                    float ygo = CELL_3DL[index2 | 1];
                    float zgo = CELL_3DL[index2 | 2];
                    float xo = value * xgo;
                    float yo = value * ygo;
                    float zo = value * zgo;
                    vx += bbbb * xo;
                    vy += bbbb * yo;
                    vz += bbbb * zo;
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

                seed += 1293373;
            }

            x += vx * (this.warpAmplitudeX * 32.69428253173828125f);
            y += vy * (this.warpAmplitudeY * 32.69428253173828125f);
            z += vz * (this.warpAmplitudeZ * 32.69428253173828125f);

            return new Float3(x, y, z);
        }

        @Override
        public NoiseBuilder toBuilder() {
            return super.toBuilder().warp(WarpType.SIMPLEX2);
        }
    }

    public static class Simplex2Reduced extends WarpedNoise {

        public Simplex2Reduced(final NoiseBuilder cfg, final FastNoise reference) {
            super(cfg, reference);
        }

        @Override
        protected Float2 warp(int seed, float x, float y) {
            float xs = x * this.warpFrequencyX;
            float ys = y * this.warpFrequencyY;

            final float s = (xs + ys) * F2;
            xs += s;
            ys += s;

            int i = fastFloor(xs);
            int j = fastFloor(ys);
            float xi = xs - i;
            float yi = ys - j;

            float t = (xi + yi) * G2;
            float x0 = xi - t;
            float y0 = yi - t;

            i *= X_PRIME;
            j *= Y_PRIME;

            float vx, vy;
            vx = vy = 0;

            float a = 0.5f - x0 * x0 - y0 * y0;
            if (a > 0) {
                float aaaa = (a * a) * (a * a);
                int hash = hash2(this.seed, i, j) & (255 << 1);
                float xo = CELL_2DL[hash];
                float yo = CELL_2DL[hash | 1];
                vx += aaaa * xo;
                vy += aaaa * yo;
            }
            float c = (2 * (1 - 2 * G2) * (1 / G2 - 2)) * t + ((-2 * (1 - 2 * G2) * (1 - 2 * G2)) + a);
            if (c > 0) {
                float cccc = (c * c) * (c * c);
                int hash = hash2(this.seed, i + X_PRIME, j + Y_PRIME) & (255 << 1);
                float xo = CELL_2DL[hash];
                float yo = CELL_2DL[hash | 1];
                vx += cccc * xo;
                vy += cccc * yo;
            }

            if (y0 > x0) {
                float x1 = x0 + G2;
                float y1 = y0 + (G2 - 1);
                float b = 0.5f - x1 * x1 - y1 * y1;
                if (b > 0) {
                    float bbbb = (b * b) * (b * b);
                    int hash = hash2(this.seed, i, j + Y_PRIME) & (255 << 1);
                    float xo = CELL_2DL[hash];
                    float yo = CELL_2DL[hash | 1];
                    vx += bbbb * xo;
                    vy += bbbb * yo;
                }
            } else {
                float x1 = x0 + (G2 - 1);
                float y1 = y0 + G2;
                float b = 0.5f - x1 * x1 - y1 * y1;
                if (b > 0) {
                    float bbbb = (b * b) * (b * b);
                    int hash = hash2(this.seed, i + X_PRIME, j) & (255 << 1);
                    float xo = CELL_2DL[hash];
                    float yo = CELL_2DL[hash | 1];
                    vx += bbbb * xo;
                    vy += bbbb * yo;
                }
            }

            x += vx * this.warpAmplitudeX * 16.0f;
            y += vy * this.warpAmplitudeY * 16.0f;

            return new Float2(x, y);
        }

        @Override
        protected Float3 warp(int seed, float x, float y, float z) {
            float xr = x * this.warpFrequencyX;
            float yr = y * this.warpFrequencyY;
            float zr = z * this.warpFrequencyZ;

            final float r = (xr + yr + zr) * R3; // Rotation, not skew
            xr = r - xr;
            yr = r - yr;
            zr = r - zr;

            int i = fastRound(xr);
            int j = fastRound(yr);
            int k = fastRound(zr);
            float x0 = xr - i;
            float y0 = yr - j;
            float z0 = zr - k;

            int xNSign = (int)(-x0 - 1.0f) | 1;
            int yNSign = (int)(-y0 - 1.0f) | 1;
            int zNSign = (int)(-z0 - 1.0f) | 1;

            float ax0 = xNSign * -x0;
            float ay0 = yNSign * -y0;
            float az0 = zNSign * -z0;

            i *= X_PRIME;
            j *= Y_PRIME;
            k *= Z_PRIME;

            float vx, vy, vz;
            vx = vy = vz = 0;

            float a = (0.6f - x0 * x0) - (y0 * y0 + z0 * z0);
            for (int l = 0; ; l++) {
                if (a > 0) {
                    float aaaa = (a * a) * (a * a);
                    int hash = hash3(seed, i, j, k) & (255 << 2);
                    float xo = CELL_3DL[hash];
                    float yo = CELL_3DL[hash | 1];
                    float zo = CELL_3DL[hash | 2];
                    vx += aaaa * xo;
                    vy += aaaa * yo;
                    vz += aaaa * zo;
                }

                float b = a;
                int i1 = i;
                int j1 = j;
                int k1 = k;

                if (ax0 >= ay0 && ax0 >= az0) {
                    b = b + ax0 + ax0;
                    i1 -= xNSign * X_PRIME;
                } else if (ay0 > ax0 && ay0 >= az0) {
                    b = b + ay0 + ay0;
                    j1 -= yNSign * Y_PRIME;
                } else {
                    b = b + az0 + az0;
                    k1 -= zNSign * Z_PRIME;
                }

                if (b > 1) {
                    b -= 1;
                    float bbbb = (b * b) * (b * b);
                    int hash = hash3(seed, i1, j1, k1) & (255 << 2);
                    float xo = CELL_3DL[hash];
                    float yo = CELL_3DL[hash | 1];
                    float zo = CELL_3DL[hash | 2];
                    vx += bbbb * xo;
                    vy += bbbb * yo;
                    vz += bbbb * zo;
                }

                if (l == 1) break;

                ax0 = 0.5f - ax0;
                ay0 = 0.5f - ay0;
                az0 = 0.5f - az0;

                a += (0.75f - ax0) - (ay0 + az0);

                i += (xNSign >> 1) & X_PRIME;
                j += (yNSign >> 1) & Y_PRIME;
                k += (zNSign >> 1) & Z_PRIME;

                xNSign = -xNSign;
                yNSign = -yNSign;
                zNSign = -zNSign;

                seed += 1293373;
            }

            x += vx * (this.warpAmplitudeX * 7.71604938271605f);
            y += vy * (this.warpAmplitudeY * 7.71604938271605f);
            z += vz * (this.warpAmplitudeZ * 7.71604938271605f);

            return new Float3(x, y, z);
        }

        @Override
        public NoiseBuilder toBuilder() {
            return super.toBuilder().warp(WarpType.SIMPLEX2_REDUCED);
        }
    }

    public static class NoiseLookup extends WarpedNoise {

        private final FastNoise noiseLookup;

        public NoiseLookup(NoiseBuilder cfg, FastNoise reference) {
            super(cfg, reference);
            this.noiseLookup = cfg.buildLookup();
        }

        @Override
        protected Float2 warp(int seed, float x, float y) {
            x += this.noiseLookup.getSingle(seed, x * this.warpFrequencyX, y * this.warpFrequencyY) * this.warpAmplitudeX;
            y += this.noiseLookup.getSingle(seed + 1, x * this.warpFrequencyX, y * this.warpFrequencyY) * this.warpAmplitudeY;
            return new Float2(x, y);
        }

        @Override
        protected Float3 warp(int seed, float x, float y, float z) {
            x += this.noiseLookup.getSingle(seed, x * this.warpFrequencyX, y * this.warpFrequencyY, z * this.warpFrequencyZ) * this.warpAmplitudeX;
            y += this.noiseLookup.getSingle(seed + 1, x * this.warpFrequencyX, y * this.warpFrequencyY, z * this.warpFrequencyZ) * this.warpAmplitudeY;
            z += this.noiseLookup.getSingle(seed + 2, x * this.warpFrequencyX, y * this.warpFrequencyY, z * this.warpFrequencyZ) * this.warpAmplitudeZ;
            return new Float3(x, y, z);
        }

        @Override
        public NoiseBuilder toBuilder() {
            return super.toBuilder().warp(WarpType.NOISE_LOOKUP).noiseLookup(this.noiseLookup.toBuilder());
        }
    }
}
