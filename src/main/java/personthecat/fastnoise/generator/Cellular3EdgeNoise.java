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

public abstract class Cellular3EdgeNoise extends FastNoise {

    private final CellularDistanceType distance;
    private final float jitterX;
    private final float jitterY;
    private final float jitterZ;

    public Cellular3EdgeNoise(final NoiseDescriptor cfg) {
        super(cfg);
        this.distance = cfg.distance();
        this.jitterX = cfg.jitterX();
        this.jitterY = cfg.jitterY();
        this.jitterZ = cfg.jitterZ();
    }

    public Cellular3EdgeNoise(final int seed) {
        super(seed);
        this.distance = CellularDistanceType.EUCLIDEAN;
        this.jitterX = 1.0F;
        this.jitterY = 1.0F;
        this.jitterZ = 1.0F;
    }

    public static Cellular3EdgeNoise create(final NoiseDescriptor cfg) {
        switch (cfg.cellularReturn()) {
            case DISTANCE3_ADD: return new Add(cfg);
            case DISTANCE3_SUB: return new Sub(cfg);
            case DISTANCE3_MUL: return new Mul(cfg);
            case DISTANCE3_DIV: return new Div(cfg);
            default: return new Distance(cfg);
        }
    }

    public abstract float getReturn(final float distance, final float distance2, final float distance3);

    @Override
    public float getSingle(int seed, float x) {
        return this.getSingle(seed, x, 1337);
    }

    @Override
    public float getSingle(int seed, float x, float y) {
        int xr = fastRound(x);
        int yr = fastRound(y);

        float distance = 999999;
        float distance2 = 999999;
        float distance3 = 999999;
        switch (this.distance) {
            case EUCLIDEAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        Float2 vec = CELL_2D[hash2(seed, xi, yi) & 255];

                        float vecX = xi - x + vec.x * this.jitterX;
                        float vecY = yi - y + vec.y * this.jitterY;

                        float newDistance = vecX * vecX + vecY * vecY;

                        distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
                        distance = Math.min(distance, newDistance);
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

                        distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
                        distance = Math.min(distance, newDistance);
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

                        distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
                        distance = Math.min(distance, newDistance);
                    }
                }
        }
        return this.getReturn(distance, distance2, distance3);
    }

    @Override
    public float getSingle(int seed, float x, float y, float z) {
        int xr = fastRound(x);
        int yr = fastRound(y);
        int zr = fastRound(z);

        float distance = 999999;
        float distance2 = 999999;
        float distance3 = 999999;
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

                            distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                            distance2 = Math.max(Math.min(distance2, newDistance), distance);
                            distance = Math.min(distance, newDistance);
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

                            distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                            distance2 = Math.max(Math.min(distance2, newDistance), distance);
                            distance = Math.min(distance, newDistance);
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

                            distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                            distance2 = Math.max(Math.min(distance2, newDistance), distance);
                            distance = Math.min(distance, newDistance);
                        }
                    }
                }
        }
        return this.getReturn(distance, distance2, distance3);
    }

    public static class Distance extends Cellular3EdgeNoise {

        public Distance(final NoiseDescriptor cfg) {
            super(cfg);
        }

        public Distance(final int seed) {
            super(seed);
        }

        @Override
        public float getReturn(final float distance, final float distance2, final float distance3) {
            return distance3 - 1;
        }
    }

    public static class Add extends Cellular3EdgeNoise {

        public Add(final NoiseDescriptor cfg) {
            super(cfg);
        }

        public Add(final int seed) {
            super(seed);
        }

        @Override
        public float getReturn(final float distance, final float distance2, final float distance3) {
            return distance3 + distance - 1;
        }
    }

    public static class Sub extends Cellular3EdgeNoise {

        public Sub(final NoiseDescriptor cfg) {
            super(cfg);
        }

        public Sub(final int seed) {
            super(seed);
        }

        @Override
        public float getReturn(final float distance, final float distance2, final float distance3) {
            return distance3 - distance - 1;
        }
    }

    public static class Mul extends Cellular3EdgeNoise {

        public Mul(final NoiseDescriptor cfg) {
            super(cfg);
        }

        public Mul(final int seed) {
            super(seed);
        }

        @Override
        public float getReturn(final float distance, final float distance2, final float distance3) {
            return distance3 * distance - 1;
        }
    }

    public static class Div extends Cellular3EdgeNoise {

        public Div(final NoiseDescriptor cfg) {
            super(cfg);
        }

        public Div(final int seed) {
            super(seed);
        }

        @Override
        public float getReturn(final float distance, final float distance2, final float distance3) {
            return distance / distance3 - 1;
        }
    }
}
