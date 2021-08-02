package personthecat.fastnoise;

import personthecat.fastnoise.data.FractalType;
import personthecat.fastnoise.data.InterpolationType;
import personthecat.fastnoise.data.NoiseType;
import personthecat.fastnoise.data.CellularDistanceType;
import personthecat.fastnoise.data.CellularReturnType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PerformanceTest {
    private static final int NUM_GENERATORS = 100;
    private static final int NUM_TESTS = 10000;
    private static final int TEST_SIZE = 100; // 1000
    private static final int MAX_OCTAVES = 10;
    private static final int UPDATE_INTERVAL = 500;
    private static final Random RAND = new Random();

    public static void main(final String[] args) {
        BigInteger newTime = BigInteger.ZERO;
        BigInteger oldTime = BigInteger.ZERO;

        for (int i = 0; i < NUM_TESTS; i++) {
            final TestContext ctx = new TestContext();
            if (RAND.nextBoolean()) {
                newTime = newTime.add(BigInteger.valueOf(ctx.runNewGenerators()));
                oldTime = oldTime.add(BigInteger.valueOf(ctx.runOldGenerators()));
            } else {
                oldTime = oldTime.add(BigInteger.valueOf(ctx.runOldGenerators()));
                newTime = newTime.add(BigInteger.valueOf(ctx.runNewGenerators()));
            }
            if (i % UPDATE_INTERVAL == 0) {
                System.out.println("Progress: " + (((float) i / (float) NUM_TESTS) * 100) + "%");
            }
        }

        final BigInteger newAvg = newTime.divide(BigInteger.valueOf(NUM_TESTS));
        final BigInteger oldAvg = oldTime.divide(BigInteger.valueOf(NUM_TESTS));
        System.out.println("New generator avg nanos: " + newAvg);
        System.out.println("Old generator avg nanos: " + oldAvg);
        if (newAvg.compareTo(oldAvg) < 0) {
            final float ratio = newAvg.floatValue() / oldAvg.floatValue();
            System.out.println("New generator is " + (100F - (ratio * 100F)) + "% faster.");
        } else {
            final float ratio = oldAvg.floatValue() / newAvg.floatValue();
            System.out.println("Old generator is " + (100F - (ratio * 100F)) + "% faster.");
        }
    }

    private static class TestContext {
        final List<FastNoise> newGenerators = new ArrayList<>(NUM_GENERATORS);
        final List<OriginalFastNoise> oldGenerators = new ArrayList<>(NUM_GENERATORS);
        final float[][] image = new float[TEST_SIZE][TEST_SIZE];

        TestContext() {
            for (int i = 0; i < NUM_GENERATORS; i++) {
                this.newGenerators.add(this.createNewGenerator());
                this.oldGenerators.add(this.createOldGenerator());
            }
        }

        FastNoise createNewGenerator() {
            return FastNoise.createDescriptor()
//                .noise(NoiseType.CELLULAR)
                .noise(this.randomEnum(NoiseType.class))
                .fractal(this.randomEnum(FractalType.class))
                .interpolation(this.randomEnum(InterpolationType.class))
                .distance(this.randomEnum(CellularDistanceType.class))
                .cellularReturn(this.randomEnum(CellularReturnType.class))
//                .cellularReturn(CellularReturnType.DISTANCE)
                .seed(RAND.nextInt())
                .frequency(RAND.nextFloat())
                .octaves(RAND.nextInt(MAX_OCTAVES) + 1)
                .lacunarity(RAND.nextFloat())
                .gain(RAND.nextFloat())
                .jitter(RAND.nextFloat())
                .gradientPerturb(RAND.nextBoolean())
                .gradientPerturbAmplitude(RAND.nextFloat())
                .gradientPerturbFrequency(RAND.nextFloat())
                .offset(RAND.nextInt())
                .invert(RAND.nextBoolean())
                .range(-RAND.nextFloat(), RAND.nextFloat())
                .threshold(-RAND.nextFloat(), RAND.nextFloat())
                .generate();
        }

        OriginalFastNoise createOldGenerator() {
            return new OriginalFastNoise(RAND.nextInt())
//                .SetNoiseType(OriginalFastNoise.NoiseType.Cellular)
                .SetNoiseType(this.randomEnum(OriginalFastNoise.NoiseType.class))
                .SetFractalType(this.randomEnum(OriginalFastNoise.FractalType.class))
                .SetInterp(this.randomEnum(OriginalFastNoise.Interp.class))
                .SetCellularDistanceFunction(this.randomEnum(OriginalFastNoise.CellularDistanceFunction.class))
                .SetCellularReturnType(this.randomEnum(OriginalFastNoise.CellularReturnType.class))
//                .SetCellularReturnType(OriginalFastNoise.CellularReturnType.Distance)
                .SetCellularNoiseLookup(new OriginalFastNoise())
                .SetFrequency(RAND.nextFloat())
                .SetFractalOctaves(RAND.nextInt(MAX_OCTAVES) + 1)
                .SetFractalLacunarity(RAND.nextFloat())
                .SetFractalGain(RAND.nextFloat())
                .SetCellularJitter(RAND.nextFloat())
                .SetGradientPerturb(RAND.nextBoolean())
                .SetGradientPerturbAmp(RAND.nextFloat())
                .SetGradientPerturbFrequency(RAND.nextFloat())
                .SetOffset(RAND.nextInt())
                .SetInvert(RAND.nextBoolean())
                .SetRange(-RAND.nextFloat(), RAND.nextFloat())
                .SetThreshold(-RAND.nextFloat(), RAND.nextFloat());
        }

        <E extends Enum<E>> E randomEnum(final Class<E> e) {
            final E[] values = e.getEnumConstants();
            return values[RAND.nextInt(values.length)];
        }

        long runNewGenerators() {
            final long start = System.nanoTime();
            for (int h = 0; h < TEST_SIZE; h++) {
                for (int w = 0; w < TEST_SIZE; w++) {
                    this.image[h][w] = this.randomNewGenerator()
                        .getNoise(RAND.nextInt(), RAND.nextInt(), RAND.nextInt());
                }
            }
            return System.nanoTime() - start;
        }

        long runOldGenerators() {
            final long start = System.nanoTime();
            for (int h = 0; h < TEST_SIZE; h++) {
                for (int w = 0; w < TEST_SIZE; w++) {
                    this.image[h][w] = this.randomOldGenerator()
                        .GetNoise(RAND.nextInt(), RAND.nextInt(), RAND.nextInt());
                }
            }
            return System.nanoTime() - start;
        }

        FastNoise randomNewGenerator() {
            return this.newGenerators.get(RAND.nextInt(NUM_GENERATORS));
        }

        OriginalFastNoise randomOldGenerator() {
            return this.oldGenerators.get(RAND.nextInt(NUM_GENERATORS));
        }
    }
}
