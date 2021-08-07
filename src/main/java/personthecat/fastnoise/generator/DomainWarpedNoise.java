package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.Float2;
import personthecat.fastnoise.data.Float3;
import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.function.NoiseProvider;

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
import static personthecat.fastnoise.util.NoiseUtils.lerp;
import static personthecat.fastnoise.util.NoiseValues.X_PRIME;
import static personthecat.fastnoise.util.NoiseValues.Y_PRIME;
import static personthecat.fastnoise.util.NoiseValues.Z_PRIME;
import static personthecat.fastnoise.util.NoiseValues.F2;
import static personthecat.fastnoise.util.NoiseValues.G2;
import static personthecat.fastnoise.util.NoiseValues.R3;

@SuppressWarnings("unused")
public abstract class DomainWarpedNoise extends FastNoise {

    protected final FastNoise reference;
    protected final float domainWarpAmplitudeX;
    protected final float domainWarpAmplitudeY;
    protected final float domainWarpAmplitudeZ;
    protected final float domainWarpFrequencyX;
    protected final float domainWarpFrequencyY;
    protected final float domainWarpFrequencyZ;

    public DomainWarpedNoise(final NoiseDescriptor cfg, final FastNoise reference) {
        super(cfg);
        this.reference = reference;
        this.domainWarpAmplitudeX = cfg.warpAmplitudeX();
        this.domainWarpAmplitudeY = cfg.warpAmplitudeY();
        this.domainWarpAmplitudeZ = cfg.warpAmplitudeZ();
        this.domainWarpFrequencyX = cfg.warpFrequencyX();
        this.domainWarpFrequencyY = cfg.warpFrequencyY();
        this.domainWarpFrequencyZ = cfg.warpFrequencyZ();
    }

    public static FastNoise create(final NoiseDescriptor cfg, final NoiseProvider provider) {
        return create(cfg, provider.apply(cfg));
    }

    public static FastNoise create(final NoiseDescriptor cfg, final FastNoise reference) {
        switch (cfg.warp()) {
            case BASIC_GRID: return new BasicGrid(cfg, reference);
            case SIMPLEX2: return new Simplex2(cfg, reference);
            case SIMPLEX2_REDUCED: return new Simplex2Reduced(cfg, reference);
            default: return reference;
        }
    }

    @Override
    public abstract float getNoise(final float x, final float y);

    @Override
    public abstract float getNoise(final float x, final float y, final float z);

    @Override
    public float getSingle(int seed, float x) {
        return this.reference.getSingle(seed, x);
    }

    @Override
    public float getSingle(int seed, float x, float y) {
        return this.reference.getSingle(seed, x, y);
    }

    @Override
    public float getSingle(int seed, float x, float y, float z) {
        return this.reference.getSingle(seed, x, y, z);
    }

    public static class BasicGrid extends DomainWarpedNoise {

        public BasicGrid(final NoiseDescriptor cfg, final FastNoise reference) {
            super(cfg, reference);
        }

        @Override
        public float getNoise(float x, float y) {
            final float xf = x * this.domainWarpFrequencyX;
            final float yf = y * this.domainWarpFrequencyY;

            final int x0 = fastFloor(xf);
            final int y0 = fastFloor(yf);
            final int x1 = x0 + 1;
            final int y1 = y0 + 1;

            final float xs = this.interpolate(x - x0);
            final float ys = this.interpolate(y - y0);

            Float2 vec0 = CELL_2D[hash2(this.seed, x0, y0) & 255];
            Float2 vec1 = CELL_2D[hash2(this.seed, x1, y0) & 255];

            final float lx0x = lerp(vec0.x, vec1.x, xs);
            final float ly0x = lerp(vec0.y, vec1.y, xs);

            vec0 = CELL_2D[hash2(this.seed, x0, y1) & 255];
            vec1 = CELL_2D[hash2(this.seed, x1, y1) & 255];

            final float lx1x = lerp(vec0.x, vec1.x, xs);
            final float ly1x = lerp(vec0.y, vec1.y, xs);

            x += lerp(lx0x, lx1x, ys) * this.domainWarpAmplitudeX;
            y += lerp(ly0x, ly1x, ys) * this.domainWarpAmplitudeY;

            x *= this.frequencyX;
            y *= this.frequencyY;
            return this.reference.getSingle(this.seed, x, y);
        }

        @Override
        public float getNoise(float x, float y, float z) {
            final float xf = x * this.domainWarpFrequencyX;
            final float yf = y * this.domainWarpFrequencyY;
            final float zf = z * this.domainWarpFrequencyZ;

            final int x0 = fastFloor(xf);
            final int y0 = fastFloor(yf);
            final int z0 = fastFloor(zf);
            final int x1 = x0 + 1;
            final int y1 = y0 + 1;
            final int z1 = z0 + 1;

            final float xs = this.interpolate(xf - x0);
            final float ys = this.interpolate(yf - y0);
            final float zs = this.interpolate(zf - z0);

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

            x += lerp(lx0y, lerp(lx0x, lx1x, ys), zs) * this.domainWarpAmplitudeX;
            y += lerp(ly0y, lerp(ly0x, ly1x, ys), zs) * this.domainWarpAmplitudeY;
            z += lerp(lz0y, lerp(lz0x, lz1x, ys), zs) * this.domainWarpAmplitudeZ;

            x += this.offsetX;
            y += this.offsetY;
            z += this.offsetZ;

            x *= this.frequencyX;
            y *= this.frequencyY;
            z *= this.frequencyZ;
            return this.reference.getSingle(this.seed, x, y, z);
        }
    }

    public static class Simplex2 extends DomainWarpedNoise {

        public Simplex2(final NoiseDescriptor cfg, final FastNoise reference) {
            super(cfg, reference);
        }

        @Override
        public float getNoise(float x, float y) {
            float xs = x * this.domainWarpFrequencyX;
            float ys = y * this.domainWarpFrequencyY;

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

            x += vx * this.domainWarpAmplitudeX * 38.283687591552734375f;
            y += vy * this.domainWarpAmplitudeY * 38.283687591552734375f;

            x *= this.frequencyX;
            y *= this.frequencyY;
            return this.reference.getSingle(this.seed, x, y);
        }

        @Override
        public float getNoise(float x, float y, float z) {
            float xr = x * this.domainWarpFrequencyX;
            float yr = y * this.domainWarpFrequencyY;
            float zr = y * this.domainWarpFrequencyZ;

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

            int seed = this.seed;
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

            x += this.offsetX;
            y += this.offsetY;
            z += this.offsetZ;

            x += vx * this.domainWarpAmplitudeX * 32.69428253173828125f;
            y += vy * this.domainWarpAmplitudeY * 32.69428253173828125f;
            z += vz * this.domainWarpAmplitudeZ * 32.69428253173828125f;

            x *= this.frequencyX;
            y *= this.frequencyY;
            z *= this.frequencyZ;
            return this.reference.getSingle(this.seed, x, y, z);
        }
    }

    public static class Simplex2Reduced extends DomainWarpedNoise {

        public Simplex2Reduced(final NoiseDescriptor cfg, final FastNoise reference) {
            super(cfg, reference);
        }

        @Override
        public float getNoise(float x, float y) {
            float xs = x * this.domainWarpFrequencyX;
            float ys = y * this.domainWarpFrequencyY;

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

            x += vx * this.domainWarpAmplitudeX * 16.0f;
            y += vy * this.domainWarpAmplitudeY * 16.0f;

            x *= this.frequencyX;
            y *= this.frequencyY;
            return this.reference.getSingle(this.seed, x, y);
        }

        @Override
        public float getNoise(float x, float y, float z) {
            float xr = x * this.domainWarpFrequencyX;
            float yr = y * this.domainWarpFrequencyY;
            float zr = y * this.domainWarpFrequencyZ;

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

            int seed = this.seed;
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

            x += this.offsetX;
            y += this.offsetY;
            z += this.offsetZ;

            x += vx * this.domainWarpAmplitudeX * 7.71604938271605f;
            y += vy * this.domainWarpAmplitudeY * 7.71604938271605f;
            z += vz * this.domainWarpAmplitudeZ * 7.71604938271605f;

            x *= this.frequencyX;
            y *= this.frequencyY;
            z *= this.frequencyZ;
            return this.reference.getSingle(this.seed, x, y, z);
        }
    }
}
