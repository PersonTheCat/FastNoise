package personthecat.fastnoise.data;

import org.jetbrains.annotations.Nullable;
import personthecat.fastnoise.util.EnumNamingService;

import java.util.regex.Pattern;

public enum DistanceType {
    EUCLIDEAN,
    MANHATTAN,
    NATURAL,
    FUNCTION;

    final Pattern pattern = EnumNamingService.createPattern(this);
    final String formatted = EnumNamingService.formatName(this);

    @Nullable
    public static DistanceType from(final String s) {
        for (final DistanceType t : values()) {
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
