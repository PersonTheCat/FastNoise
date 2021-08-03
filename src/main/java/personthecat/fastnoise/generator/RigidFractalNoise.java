package personthecat.fastnoise.generator;

import personthecat.fastnoise.FastNoise;
import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.function.NoiseProvider;

public class RigidFractalNoise extends FractalNoise {

    public RigidFractalNoise(final NoiseDescriptor cfg, final FastNoise reference) {
        super(cfg, reference);
    }

    public RigidFractalNoise(final NoiseDescriptor cfg, final NoiseProvider provider) {
        this(cfg, provider.apply(cfg));
    }

    @Override
    protected float fractal(float f) {
        return 1 - Math.abs(f);
    }
}
