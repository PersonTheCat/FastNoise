package personthecat.fastnoise.generator;

import lombok.Data;
import lombok.experimental.Accessors;
import personthecat.fastnoise.function.NoiseFunction1;
import personthecat.fastnoise.function.NoiseFunction2;
import personthecat.fastnoise.function.NoiseFunction3;
import personthecat.fastnoise.function.NoiseWrapper;

@Data
@Accessors(fluent = true)
public class DummyNoiseWrapper implements NoiseWrapper {
    private NoiseFunction1 wrapNoise1 = (x) -> 0;
    private NoiseFunction2 wrapNoise2 = (x, y) -> 0;
    private NoiseFunction3 wrapNoise3 = (x, y, z) -> 0;
}
