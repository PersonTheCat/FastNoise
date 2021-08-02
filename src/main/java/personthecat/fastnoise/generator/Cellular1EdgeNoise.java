package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.CellularDistanceType;
import personthecat.fastnoise.data.Float2;
import personthecat.fastnoise.data.Float3;
import personthecat.fastnoise.data.NoiseDescriptor;

import static personthecat.fastnoise.util.NoiseTables.CELL_2D;
import static personthecat.fastnoise.util.NoiseTables.CELL_3D;
import static personthecat.fastnoise.util.NoiseUtils.fastRound;
import static personthecat.fastnoise.util.NoiseUtils.hash2;
import static personthecat.fastnoise.util.NoiseUtils.hash3;
import static personthecat.fastnoise.util.NoiseUtils.value2;
import static personthecat.fastnoise.util.NoiseUtils.value3;

public abstract class Cellular1EdgeNoise extends FastNoise {

    protected final CellularDistanceType distance;
    protected final float jitterX;
    protected final float jitterY;
    protected final float jitterZ;

    public Cellular1EdgeNoise(final NoiseDescriptor cfg) {
        super(cfg);
        this.distance = cfg.distance();
        this.jitterX = cfg.jitterX();
        this.jitterY = cfg.jitterY();
        this.jitterZ = cfg.jitterZ();
    }

    public static Cellular1EdgeNoise create(final NoiseDescriptor cfg) {
        switch (cfg.cellularReturn()) {
            case DISTANCE: return new Distance(cfg);
            case NOISE_LOOKUP: return new NoiseLookup(cfg);
            default: return new CellValue(cfg);
        }
    }

    protected abstract float getReturn(final int x, final int y, final float distance);
    protected abstract float getReturn(final int x, final int y, final int z, final float distance);

    @Override
    public float getSingle(final int seed, final float x) {
        return 0;
    }

    @Override
    public float getSingle(final int seed, final float x, final float y) {
        int xr = fastRound(x);
        int yr = fastRound(y);

        float distance = 999999;
        int xc = 0, yc = 0;
        switch (this.distance) {
            case EUCLIDEAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        Float2 vec = CELL_2D[hash2(seed, xi, yi) & 255];

                        float vecX = xi - x + vec.x * this.jitterX;
                        float vecY = yi - y + vec.y * this.jitterY;

                        float newDistance = vecX * vecX + vecY * vecY;

                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                        }
                    }
                }
                break;
            case MANHATTAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        Float2 vec = CELL_2D[hash2(seed, xi, yi) & 255];

                        float vecX = xi - x + vec.x * this.jitterX;
                        float vecY = yi - y + vec.y * this.jitterY;

                        float newDistance = Math.abs(vecX) + Math.abs(vecY);

                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                        }
                    }
                }
                break;
            default:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        Float2 vec = CELL_2D[hash2(seed, xi, yi) & 255];

                        float vecX = xi - x + vec.x * this.jitterX;
                        float vecY = yi - y + vec.y * this.jitterY;

                        float newDistance = Math.abs(vecX) + Math.abs(vecY) + vecX * vecX + vecY * vecY;

                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                        }
                    }
                }
        }
        return this.getReturn(xc, yc, distance);
    }

    @Override
    public float getSingle(final int seed, final float x, final float y, final float z) {
        final int xr = fastRound(x);
        final int yr = fastRound(y);
        final int zr = fastRound(z);

        float distance = 999999;
        int xc = 0, yc = 0, zc = 0;
        switch (this.distance) {
            case EUCLIDEAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            Float3 vec = CELL_3D[hash3(seed, xi, yi, zi) & 255];

                            float vecX = xi - x + vec.x * this.jitterX;
                            float vecY = yi - y + vec.y * this.jitterY;
                            float vecZ = zi - z + vec.z * this.jitterZ;

                            float newDistance = vecX * vecX + vecY * vecY + vecZ * vecZ;

                            if (newDistance < distance) {
                                distance = newDistance;
                                xc = xi;
                                yc = yi;
                                zc = zi;
                            }
                        }
                    }
                }
                break;
            case MANHATTAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            Float3 vec = CELL_3D[hash3(seed, xi, yi, zi) & 255];

                            float vecX = xi - x + vec.x * this.jitterX;
                            float vecY = yi - y + vec.y * this.jitterY;
                            float vecZ = zi - z + vec.z * this.jitterZ;

                            float newDistance = Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ);

                            if (newDistance < distance) {
                                distance = newDistance;
                                xc = xi;
                                yc = yi;
                                zc = zi;
                            }
                        }
                    }
                }
                break;
            default:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        for (int zi = zr - 1; zi <= zr + 1; zi++) {
                            Float3 vec = CELL_3D[hash3(seed, xi, yi, zi) & 255];

                            float vecX = xi - x + vec.x * this.jitterX;
                            float vecY = yi - y + vec.y * this.jitterY;
                            float vecZ = zi - z + vec.z * this.jitterZ;

                            float newDistance = Math.abs(vecX) + Math.abs(vecY) + Math.abs(vecZ) + vecX * vecX + vecY * vecY + vecZ * vecZ;

                            if (newDistance < distance) {
                                distance = newDistance;
                                xc = xi;
                                yc = yi;
                                zc = zi;
                            }
                        }
                    }
                }
        }

        return this.getReturn(xc, yc, zc, distance);
    }

    public static class CellValue extends Cellular1EdgeNoise {

        public CellValue(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        protected float getReturn(final int x, final int y, final float distance) {
            return value2(0, x, y);
        }

        @Override
        protected float getReturn(final int x, final int y, final int z, final float distance) {
            return value3(0, x, y, z);
        }
    }

    public static class NoiseLookup extends Cellular1EdgeNoise {

        private final FastNoise lookup;

        public NoiseLookup(final NoiseDescriptor cfg) {
            super(cfg);
            this.lookup = cfg.noiseLookup() != null
                ? cfg.noiseLookup().generate()
                : new NoiseDescriptor().generate();
        }

        @Override
        protected float getReturn(final int x, final int y, final float distance) {
            final Float2 vec = CELL_2D[hash2(this.seed, x, y) & 255];
            return this.lookup.getNoise(x + vec.x * this.jitterX, y + vec.y * this.jitterY);
        }

        @Override
        protected float getReturn(final int x, final int y, final int z, final float distance) {
            final Float3 vec = CELL_3D[hash3(this.seed, x, y, z) & 255];
            return this.lookup.getNoise(x + vec.x * this.jitterX, y + vec.y * this.jitterY, z + vec.z * this.jitterZ);
        }
    }

    public static class Distance extends Cellular1EdgeNoise {

        public Distance(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        protected float getReturn(final int x, final int y, final float distance) {
            return 0;
        }

        @Override
        protected float getReturn(final int x, final int y, final int z, final float distance) {
            return 0;
        }
    }
}
