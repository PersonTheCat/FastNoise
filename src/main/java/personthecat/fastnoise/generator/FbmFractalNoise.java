package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.function.NoiseProvider;

public class FbmFractalNoise extends FractalNoise {

    public FbmFractalNoise(final NoiseDescriptor cfg, final FastNoise reference) {
        super(cfg, reference);
    }

    public FbmFractalNoise(final NoiseDescriptor cfg, final NoiseProvider provider) {
        this(cfg, provider.apply(cfg));
    }

    @Override
    protected float fractal(float f) {
        return f;
    }
}
