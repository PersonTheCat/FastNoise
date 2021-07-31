package personthecat.fastnoise.generator;

import personthecat.fastnoise.data.Cellular3EdgeCustomizer;
import personthecat.fastnoise.data.Float2;
import personthecat.fastnoise.data.Float3;
import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.function.DistanceFunction2;
import personthecat.fastnoise.function.DistanceFunction3;
import personthecat.fastnoise.function.ReturnFunction3Edge;

import static personthecat.fastnoise.util.NoiseTables.CELL_2D;
import static personthecat.fastnoise.util.NoiseTables.CELL_3D;
import static personthecat.fastnoise.util.NoiseUtils.fastRound;
import static personthecat.fastnoise.util.NoiseUtils.hash2;
import static personthecat.fastnoise.util.NoiseUtils.hash3;

public class Cellular3EdgeNoise extends NoiseGenerator {

    private final DistanceFunction2 distance2;
    private final DistanceFunction3 distance3;
    private final ReturnFunction3Edge return3;
    private final float jitterX;
    private final float jitterY;
    private final float jitterZ;

    public Cellular3EdgeNoise(final NoiseDescriptor cfg, final Cellular3EdgeCustomizer functions) {
        super(cfg);
        this.distance2 = functions.distance2();
        this.distance3 = functions.distance3();
        this.return3 = functions.return3();
        this.jitterX = cfg.jitterX();
        this.jitterY = cfg.jitterY();
        this.jitterZ = cfg.jitterZ();
    }

    public static Cellular3EdgeNoise distance(final NoiseDescriptor cfg) {
        return withReturn(cfg, (d1, d2, d3) -> d3 - 1);
    }

    public static Cellular3EdgeNoise add(final NoiseDescriptor cfg) {
        return withReturn(cfg, (d1, d2, d3) -> d3 + d1 - 1);
    }

    public static Cellular3EdgeNoise sub(final NoiseDescriptor cfg) {
        return withReturn(cfg, (d1, d2, d3) -> d3 - d1 - 1);
    }

    public static Cellular3EdgeNoise mul(final NoiseDescriptor cfg) {
        return withReturn(cfg, (d1, d2, d3) -> d3 * d1 - 1);
    }

    public static Cellular3EdgeNoise div(final NoiseDescriptor cfg) {
        return withReturn(cfg, (d1, d2, d3) -> d1 / d3 - 1);
    }

    public static Cellular3EdgeNoise withReturn(final NoiseDescriptor cfg, final ReturnFunction3Edge return3) {
        return new Cellular3EdgeNoise(cfg, new Cellular3EdgeCustomizer(cfg.distance()).return3(return3));
    }

    @Override
    public float getNoise(float x) {
        return 0;
    }

    @Override
    public float getNoise(float x, float y) {
        int xr = fastRound(x);
        int yr = fastRound(y);

        float distance = 999999;
        float distance2 = 999999;
        float distance3 = 999999;

        for (int xi = xr - 1; xi <= xr + 1; xi++) {
            for (int yi = yr - 1; yi <= yr + 1; yi++) {
                Float2 vec = CELL_2D[hash2(this.seed, xi, yi) & 255];

                float vecX = xi - x + vec.x * this.jitterX;
                float vecY = yi - y + vec.y * this.jitterY;

                float newDistance = this.distance2.getDistance(vecX, vecY);

                distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                distance2 = Math.max(Math.min(distance2, newDistance), distance);
                distance = Math.min(distance, newDistance);
            }
        }

        return this.return3.getReturn(distance, distance2, distance3);
    }

    @Override
    public float getNoise(float x, float y, float z) {
        int xr = fastRound(x);
        int yr = fastRound(y);
        int zr = fastRound(z);

        float distance = 999999;
        float distance2 = 999999;
        float distance3 = 999999;

        for (int xi = xr - 1; xi <= xr + 1; xi++) {
            for (int yi = yr - 1; yi <= yr + 1; yi++) {
                for (int zi = zr - 1; zi <= zr + 1; zi++) {
                    Float3 vec = CELL_3D[hash3(this.seed, xi, yi, zi) & 255];

                    float vecX = xi - x + vec.x * this.jitterX;
                    float vecY = yi - y + vec.y * this.jitterY;
                    float vecZ = zi - z + vec.z * this.jitterZ;

                    float newDistance = this.distance3.getDistance(vecX, vecY, vecZ);

                    distance3 = Math.max(Math.min(distance3, newDistance), distance2);
                    distance2 = Math.max(Math.min(distance2, newDistance), distance);
                    distance = Math.min(distance, newDistance);
                }
            }
        }

        return this.return3.getReturn(distance, distance2, distance3);
    }
}
