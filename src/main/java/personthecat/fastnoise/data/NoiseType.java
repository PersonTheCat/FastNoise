package personthecat.fastnoise.data;

import org.jetbrains.annotations.Nullable;
import personthecat.fastnoise.util.EnumNamingService;

import java.util.regex.Pattern;

public enum NoiseType {
    VALUE,
    VALUE_FRACTAL,
    PERLIN,
    PERLIN_FRACTAL,
    SIMPLEX,
    SIMPLEX_FRACTAL,
    CELLULAR,
    WHITE_NOISE,
    CUBIC,
    CUBIC_FRACTAL;

    final Pattern pattern = EnumNamingService.createPattern(this);
    final String formatted = EnumNamingService.formatName(this);

    @Nullable
    public static NoiseType from(final String s) {
        for (final NoiseType t : values()) {
            if (t.pattern.matcher(s).matches()) {
                return t;
            }
        }
        return null;
    }

    public String format() {
        return this.formatted;
    }
}
