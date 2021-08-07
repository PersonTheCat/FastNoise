package personthecat.fastnoise.data;

import org.jetbrains.annotations.Nullable;
import personthecat.fastnoise.util.EnumNamingService;

import java.util.regex.Pattern;

public enum DomainWarpType {
    SIMPLEX2,
    SIMPLEX2_REDUCED,
    BASIC_GRID,
    NONE;

    final Pattern pattern = EnumNamingService.createPattern(this);
    final String formatted = EnumNamingService.formatName(this);

    @Nullable
    public static DomainWarpType from(final String s) {
        for (final DomainWarpType t : values()) {
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
