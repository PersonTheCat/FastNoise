package personthecat.fastnoise.generator;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import personthecat.fastnoise.data.NoiseDescriptor;
import personthecat.fastnoise.function.NoiseFunction1;
import personthecat.fastnoise.function.NoiseFunction2;
import personthecat.fastnoise.function.NoiseFunction3;

@Getter
@Setter
@Accessors(fluent = true)
public class DummyNoiseWrapper extends FastNoise {

    private NoiseFunction1 wrapNoise1 = (s, x) -> 0;
    private NoiseFunction2 wrapNoise2 = (s, x, y) -> 0;
    private NoiseFunction3 wrapNoise3 = (s, x, y, z) -> 0;

    public DummyNoiseWrapper(final NoiseDescriptor cfg) {
        super(cfg);
    }

    @Override
    public float getSingle(int seed, float x) {
        return this.wrapNoise1.getNoise(seed, x);
    }

    @Override
    public float getSingle(int seed, float x, float y) {
        return this.wrapNoise2.getNoise(seed, x, y);
    }

    @Override
    public float getSingle(int seed, float x, float y, float z) {
        return this.wrapNoise3.getNoise(seed, x, y, z);
    }
}

