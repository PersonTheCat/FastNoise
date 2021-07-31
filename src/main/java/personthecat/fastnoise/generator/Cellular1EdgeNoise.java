package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.Cellular1EdgeCustomizer;
import personthecat.fastnoise.data.Float2;
import personthecat.fastnoise.data.Float3;
import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.function.DistanceFunction2;
import personthecat.fastnoise.function.DistanceFunction3;
import personthecat.fastnoise.function.ReturnFunction2;
import personthecat.fastnoise.function.ReturnFunction3;

import static personthecat.fastnoise.util.NoiseTables.CELL_2D;
import static personthecat.fastnoise.util.NoiseTables.CELL_3D;
import static personthecat.fastnoise.util.NoiseUtils.fastRound;
import static personthecat.fastnoise.util.NoiseUtils.hash2;
import static personthecat.fastnoise.util.NoiseUtils.hash3;
import static personthecat.fastnoise.util.NoiseUtils.value2;
import static personthecat.fastnoise.util.NoiseUtils.value3;

public class Cellular1EdgeNoise extends NoiseGenerator {

    private final DistanceFunction2 distance2;
    private final DistanceFunction3 distance3;
    private final ReturnFunction2 return2;
    private final ReturnFunction3 return3;
    private final float jitterX;
    private final float jitterY;
    private final float jitterZ;

    public Cellular1EdgeNoise(final NoiseDescriptor cfg, final Cellular1EdgeCustomizer functions) {
        super(cfg);
        this.distance2 = functions.distance2();
        this.distance3 = functions.distance3();
        this.return2 = functions.return2();
        this.return3 = functions.return3();
        this.jitterX = cfg.jitterX();
        this.jitterY = cfg.jitterY();
        this.jitterZ = cfg.jitterZ();
    }

    public static Cellular1EdgeNoise cellValue(final NoiseDescriptor cfg) {
        final Cellular1EdgeCustomizer functions = new Cellular1EdgeCustomizer(cfg.distance())
            .return2((x, y, d) -> value2(0, x, y))
            .return3((x, y, z, d) -> value3(0, x, y, z));
        return new Cellular1EdgeNoise(cfg, functions);
    }

    public static Cellular1EdgeNoise noiseLookup(final NoiseDescriptor cfg) {
        final int seed = cfg.seed();
        final float jitterX = cfg.jitterX();
        final float jitterY = cfg.jitterY();
        final float jitterZ = cfg.jitterZ();
        final FastNoise lookup = cfg.noiseLookup() != null
            ? cfg.noiseLookup().generate()
            : new NoiseDescriptor().generate();

        final Cellular1EdgeCustomizer functions = new Cellular1EdgeCustomizer(cfg.distance())
            .return2((x, y, d) -> {
                final Float2 vec = CELL_2D[hash2(seed, x, y) & 255];
                return lookup.getNoise(x + vec.x * jitterX, y + vec.y * jitterY);
            })
            .return3((x, y, z, d) -> {
                final Float3 vec = CELL_3D[hash3(seed, x, y, z) & 255];
                return lookup.getNoise(x + vec.x * jitterX, y + vec.y * jitterY, z + vec.z * jitterZ);
            });
        return new Cellular1EdgeNoise(cfg, functions);
    }

    public static Cellular1EdgeNoise distance(final NoiseDescriptor cfg) {
        final Cellular1EdgeCustomizer functions = new Cellular1EdgeCustomizer(cfg.distance())
            .return2((x, y, d) -> d - 1)
            .return3((x, y, z, d) -> d - 1);
        return new Cellular1EdgeNoise(cfg, functions);
    }

    @Override
    public float getNoise(final float x) {
        return 0;
    }

    @Override
    public float getNoise(final float x, final float y) {
        int xr = fastRound(x);
        int yr = fastRound(y);

        float distance = 999999;
        int xc = 0, yc = 0;

        for (int xi = xr - 1; xi <= xr + 1; xi++) {
            for (int yi = yr - 1; yi <= yr + 1; yi++) {
                Float2 vec = CELL_2D[hash2(this.seed, xi, yi) & 255];

                float vecX = xi - x + vec.x * this.jitterX;
                float vecY = yi - y + vec.y * this.jitterY;

                float newDistance = this.distance2.getDistance(vecX, vecY);

                if (newDistance < distance) {
                    distance = newDistance;
                    xc = xi;
                    yc = yi;
                }
            }
        }

        return this.return2.getReturn(xc, yc, distance);
    }

    @Override
    public float getNoise(final float x, final float y, final float z) {
        final int xr = fastRound(x);
        final int yr = fastRound(y);
        final int zr = fastRound(z);

        float distance = 999999;
        int xc = 0, yc = 0, zc = 0;

        for (int xi = xr - 1; xi <= xr + 1; xi++) {
            for (int yi = yr - 1; yi <= yr + 1; yi++) {
                for (int zi = zr - 1; zi <= zr + 1; zi++) {
                    Float3 vec = CELL_3D[hash3(this.seed, xi, yi, zi) & 255];

                    float vecX = xi - x + vec.x * this.jitterX;
                    float vecY = yi - y + vec.y * this.jitterY;
                    float vecZ = zi - z + vec.z * this.jitterZ;

                    float newDistance = this.distance3.getDistance(vecX, vecY, vecZ);

                    if (newDistance < distance) {
                        distance = newDistance;
                        xc = xi;
                        yc = yi;
                        zc = zi;
                    }
                }
            }
        }

        return this.return3.getReturn(xc, yc, zc, distance);
    }
}
