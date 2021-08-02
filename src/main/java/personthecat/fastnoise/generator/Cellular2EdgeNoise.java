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

public abstract class Cellular2EdgeNoise extends FastNoise {

    private final CellularDistanceType distance;
    private final float jitterX;
    private final float jitterY;
    private final float jitterZ;

    public Cellular2EdgeNoise(final NoiseDescriptor cfg) {
        super(cfg);
        this.distance = cfg.distance();
        this.jitterX = cfg.jitterX();
        this.jitterY = cfg.jitterY();
        this.jitterZ = cfg.jitterZ();
    }

    public static Cellular2EdgeNoise create(final NoiseDescriptor cfg) {
        switch (cfg.cellularReturn()) {
            case DISTANCE2_ADD: return new Add(cfg);
            case DISTANCE2_SUB: return new Sub(cfg);
            case DISTANCE2_MUL: return new Mul(cfg);
            case DISTANCE2_DIV: return new Div(cfg);
            default: return new Distance(cfg);
        }
    }

    public abstract float getReturn(final float distance, final float distance2);

    @Override
    public float getSingle(int seed, float x) {
        return 0;
    }

    @Override
    public float getSingle(int seed, float x, float y) {
        int xr = fastRound(x);
        int yr = fastRound(y);

        float distance = 999999;
        float distance2 = 999999;
        switch (this.distance) {
            case EUCLIDEAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        Float2 vec = CELL_2D[hash2(seed, xi, yi) & 255];

                        float vecX = xi - x + vec.x * this.jitterX;
                        float vecY = yi - y + vec.y * this.jitterY;

                        float newDistance = vecX * vecX + vecY * vecY;

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

                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
                        distance = Math.min(distance, newDistance);
                    }
                }
        }

        return this.getReturn(distance, distance2);
    }

    @Override
    public float getSingle(int seed, float x, float y, float z) {
        int xr = fastRound(x);
        int yr = fastRound(y);
        int zr = fastRound(z);

        float distance = 999999;
        float distance2 = 999999;
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

                            distance2 = Math.max(Math.min(distance2, newDistance), distance);
                            distance = Math.min(distance, newDistance);
                        }
                    }
                }
                break;
            default:
        }

        return this.getReturn(distance, distance2);
    }

    public static class Distance extends Cellular2EdgeNoise {

        public Distance(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getReturn(final float distance, final float distance2) {
            return distance2 - 1;
        }
    }

    public static class Add extends Cellular2EdgeNoise {

        public Add(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getReturn(final float distance, final float distance2) {
            return distance2 + distance - 1;
        }
    }

    public static class Sub extends Cellular2EdgeNoise {

        public Sub(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getReturn(final float distance, final float distance2) {
            return distance2 - distance - 1;
        }
    }

    public static class Mul extends Cellular2EdgeNoise {

        public Mul(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getReturn(final float distance, final float distance2) {
            return distance2 * distance - 1;
        }
    }

    public static class Div extends Cellular2EdgeNoise {

        public Div(final NoiseDescriptor cfg) {
            super(cfg);
        }

        @Override
        public float getReturn(final float distance, final float distance2) {
            return distance / distance2 - 1;
        }
    }
}
