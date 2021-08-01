package personthecat.fastnoise.generator;

import personthecat.fastnoise.data.NoiseDescriptor;

import static personthecat.fastnoise.util.NoiseUtils.castFloatToInt;
import static personthecat.fastnoise.util.NoiseUtils.value1;
import static personthecat.fastnoise.util.NoiseUtils.value2;
import static personthecat.fastnoise.util.NoiseUtils.value3;

public class WhiteNoise extends FastNoise {

    public WhiteNoise(final NoiseDescriptor cfg) {
        super(cfg);
    }

    @Override
    public float getSingle(int seed, final float x) {
        final int xi = castFloatToInt(x);

        return value1(seed, xi);
    }

    @Override
    public float getSingle(int seed, final float x, final float y) {
        final int xi = castFloatToInt(x);
        final int yi = castFloatToInt(y);

        return value2(seed, xi, yi);
    }

    @Override
    public float getSingle(int seed, final float x, final float y, final float z) {
        final int xi = castFloatToInt(x);
        final int yi = castFloatToInt(y);
        final int zi = castFloatToInt(z);

        return value3(seed, xi, yi, zi);
    }
}
