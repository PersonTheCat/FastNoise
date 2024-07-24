package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.DistanceType;
import personthecat.fastnoise.data.ReturnType;
import personthecat.fastnoise.data.Float2;
import personthecat.fastnoise.data.Float3;
import personthecat.fastnoise.data.NoiseBuilder;
import personthecat.fastnoise.data.NoiseType;
import personthecat.fastnoise.function.DistanceFunction;
import personthecat.fastnoise.function.ReturnFunction;

import static personthecat.fastnoise.util.NoiseTables.CELL_2D;
import static personthecat.fastnoise.util.NoiseTables.CELL_3D;
import static personthecat.fastnoise.util.NoiseUtils.fastRound;
import static personthecat.fastnoise.util.NoiseUtils.hash2;
import static personthecat.fastnoise.util.NoiseUtils.hash3;
import static personthecat.fastnoise.util.NoiseUtils.value2;
import static personthecat.fastnoise.util.NoiseUtils.value3;

public class CellularNoise extends FastNoise {

    private final DistanceType distanceType;
    private final ReturnType returnType;
    private final DistanceFunction distanceFunction;
    private final ReturnFunction returnFunction;
    private final FastNoise lookup;
    private final float jitterX;
    private final float jitterY;
    private final float jitterZ;

    public CellularNoise(final NoiseBuilder cfg) {
        super(cfg);
        this.distanceType = cfg.distance();
        this.returnType = cfg.cellularReturn();
        this.distanceFunction = cfg.distanceFunction();
        this.returnFunction = cfg.returnFunction();
        this.lookup = cfg.buildLookup();
        this.jitterX = cfg.jitterX();
        this.jitterY = cfg.jitterY();
        this.jitterZ = cfg.jitterZ();
    }

    public CellularNoise(final int seed) {
        this(FastNoise.builder().seed(seed));
    }

    @Override
    public NoiseBuilder toBuilder() {
        return super.toBuilder()
            .type(NoiseType.CELLULAR)
            .distance(this.distanceType)
            .cellularReturn(this.returnType)
            .distanceFunction(this.distanceFunction)
            .returnFunction(this.returnFunction)
            .noiseLookup(this.lookup != DUMMY ? this.lookup.toBuilder() : null)
            .jitterX(this.jitterX)
            .jitterY(this.jitterY)
            .jitterZ(this.jitterZ);
    }

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
        int xc = 0, yc = 0;
        switch (this.distanceType) {
            case EUCLIDEAN:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        Float2 vec = CELL_2D[hash2(seed, xi, yi) & 255];

                        float vecX = xi - x + vec.x * this.jitterX;
                        float vecY = yi - y + vec.y * this.jitterY;

                        float newDistance = vecX * vecX + vecY * vecY;

                        distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
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

                        distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                        }
                    }
                }
                break;
            case NATURAL:
                for (int xi = xr - 1; xi <= xr + 1; xi++) {
                    for (int yi = yr - 1; yi <= yr + 1; yi++) {
                        Float2 vec = CELL_2D[hash2(seed, xi, yi) & 255];

                        float vecX = xi - x + vec.x * this.jitterX;
                        float vecY = yi - y + vec.y * this.jitterY;

                        float newDistance = Math.abs(vecX) + Math.abs(vecY) + vecX * vecX + vecY * vecY;

                        distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
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

                        float newDistance = this.distanceFunction.getDistance(vecX, vecY);

                        distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                        distance2 = Math.max(Math.min(distance2, newDistance), distance);
                        if (newDistance < distance) {
                            distance = newDistance;
                            xc = xi;
                            yc = yi;
                        }
                    }
                }
        }
        switch (this.returnType) {
            case CELL_VALUE: return value2(0, xc, yc);
            case NOISE_LOOKUP:
                final Float2 vec = CELL_2D[hash2(this.seed, xc, yc) & 255];
                return this.lookup.getNoise(xc + vec.x * this.jitterX, yc + vec.y * this.jitterY);
            case DISTANCE: return distance - 1;
            case DISTANCE2: return distance2 - 1;
            case DISTANCE2_ADD: return distance2 + distance - 1;
            case DISTANCE2_SUB: return distance2 - distance - 1;
            case DISTANCE2_MUL: return distance2 * distance - 1;
            case DISTANCE2_DIV: return distance / distance2 - 1;
            case DISTANCE3: return distance3 - 1;
            case DISTANCE3_ADD: return distance3 + distance - 1;
            case DISTANCE3_SUB: return distance3 - distance - 1;
            case DISTANCE3_MUL: return distance3 * distance - 1;
            case DISTANCE3_DIV: return distance / distance3 - 1;
            default: return this.returnFunction.getReturn(xc, yc, distance, distance2, distance3);
        }
    }

    @Override
    public float getSingle(int seed, float x, float y, float z) {
        int xr = fastRound(x);
        int yr = fastRound(y);
        int zr = fastRound(z);

        float distance = 999999;
        float distance2 = 999999;
        float distance3 = 999999;
        int xc = 0, yc = 0, zc = 0;
        switch (this.distanceType) {
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

                            distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                            distance2 = Math.max(Math.min(distance2, newDistance), distance);
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
            case NATURAL:
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

                            float newDistance = this.distanceFunction.getDistance(vecX, vecY, vecZ);

                            distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                            distance2 = Math.max(Math.min(distance2, newDistance), distance);
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
        switch (this.returnType) {
            case CELL_VALUE: return value3(0, xc, yc, zc);
            case NOISE_LOOKUP:
                final Float3 vec = CELL_3D[hash3(this.seed, xc, yc, zc) & 255];
                return this.lookup.getNoise(xc + vec.x * this.jitterX, yc + vec.y * this.jitterY, zc + vec.z * this.jitterZ);
            case DISTANCE: return distance - 1;
            case DISTANCE2: return distance2 - 1;
            case DISTANCE2_ADD: return distance2 + distance - 1;
            case DISTANCE2_SUB: return distance2 - distance - 1;
            case DISTANCE2_MUL: return distance2 * distance - 1;
            case DISTANCE2_DIV: return distance / distance2 - 1;
            case DISTANCE3: return distance3 - 1;
            case DISTANCE3_ADD: return distance3 + distance - 1;
            case DISTANCE3_SUB: return distance3 - distance - 1;
            case DISTANCE3_MUL: return distance3 * distance - 1;
            case DISTANCE3_DIV: return distance / distance3 - 1;
            default: return this.returnFunction.getReturn(xc, yc, zc, distance, distance2, distance3);
        }
    }
}
