package personthecat.fastnoise.performance;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.FastNoiseLite;
import personthecat.fastnoise.OriginalFastNoise;
import personthecat.fastnoise.data.FractalType;
import personthecat.fastnoise.data.NoiseType;

import java.util.concurrent.TimeUnit;

public class Benchmarks {
    private static final int TEST_SIZE = 1_000;
    private static final int NUM_FORKS = 1;
    private static final int NUM_THREADS = 4;

    public static void main(final String... args) throws Exception {
        LocalBenchmarkRunner.runIfEnabled();
    }

    @Enabled(false)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float simplex_2D() {
        final FastNoise n = FastNoise.builder().type(NoiseType.SIMPLEX).build();
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.getNoise(i, 0);
        }
        return acc;
    }

    @Enabled(false)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float simplex_2D_legacy() {
        final OriginalFastNoise n = new OriginalFastNoise().SetNoiseType(OriginalFastNoise.NoiseType.Simplex);
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.GetNoise(i, 0);
        }
        return acc;
    }

    @Enabled(false)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float openSimplex_2D() {
        final FastNoise n = FastNoise.builder().type(NoiseType.SIMPLEX2).build();
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.getNoise(i, 0);
        }
        return acc;
    }

    @Enabled(false)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float openSimplex_2D_lite() {
        final FastNoiseLite n = new FastNoiseLite();
        n.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.GetNoise(i, 0);
        }
        return acc;
    }

    @Enabled(false)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float openSimplex_3D() {
        final FastNoise n = FastNoise.builder().type(NoiseType.SIMPLEX2).build();
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.getNoise(i, 0, 0);
        }
        return acc;
    }

    @Enabled(false)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float openSimplex_3D_lite() {
        final FastNoiseLite n = new FastNoiseLite();
        n.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.GetNoise(i, 0, 0);
        }
        return acc;
    }

    @Enabled(false)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float fractalFbm_simplex_2D() {
        final FastNoise n = FastNoise.builder().type(NoiseType.SIMPLEX).fractal(FractalType.FBM).build();
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.getNoise(i, 0);
        }
        return acc;
    }

    @Enabled(false)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float fractalFbm_simplex_2D_legacy() {
        final OriginalFastNoise n = new OriginalFastNoise()
            .SetNoiseType(OriginalFastNoise.NoiseType.SimplexFractal)
            .SetFractalType(OriginalFastNoise.FractalType.FBM);
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.GetNoise(i, 0);
        }
        return acc;
    }

    @Enabled(false)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float fractalFbm_openSimplex_2D() {
        final FastNoise n = FastNoise.builder().type(NoiseType.SIMPLEX2).fractal(FractalType.FBM).build();
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.getNoise(i, 0);
        }
        return acc;
    }

    @Enabled(false)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float fractalFbm_openSimplex_2D_lite() {
        final FastNoiseLite n = new FastNoiseLite();
        n.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        n.SetFractalType(FastNoiseLite.FractalType.FBm);
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.GetNoise(i, 0);
        }
        return acc;
    }

    @Enabled(false)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float fractalFbm_openSimplex_3D() {
        final FastNoise n = FastNoise.builder().type(NoiseType.SIMPLEX2).fractal(FractalType.FBM).build();
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.getNoise(i, 0, 0);
        }
        return acc;
    }

    @Enabled(false)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float fractalFbm_openSimplex_3D_lite() {
        final FastNoiseLite n = new FastNoiseLite();
        n.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        n.SetFractalType(FastNoiseLite.FractalType.FBm);
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.GetNoise(i, 0, 0);
        }
        return acc;
    }


    @Enabled(true)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float cubic_3D() {
        final FastNoise n = FastNoise.builder().type(NoiseType.CUBIC).build();
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.getNoise(i, 0, 0);
        }
        return acc;
    }

    @Enabled(true)
    @Benchmark
    @Fork(NUM_FORKS)
    @Threads(NUM_THREADS)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public float cubic_3D_lite() {
        final FastNoiseLite n = new FastNoiseLite();
        n.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        float acc = 0;
        for (int i = 0; i < TEST_SIZE; i++) {
            acc += n.GetNoise(i, 0, 0);
        }
        return acc;
    }
}
