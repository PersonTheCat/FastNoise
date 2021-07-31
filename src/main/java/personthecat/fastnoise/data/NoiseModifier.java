package personthecat.fastnoise.data;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class NoiseModifier {
    private float minThreshold = 0.0F;
    private float maxThreshold = 1.0F;
    private float scaleAmplitude = 1.0F;
    private float scaleOffset = 0.0F;
    private boolean invert = false;

    public static final NoiseModifier DEFAULT_MODIFIER = new NoiseModifier();
}
