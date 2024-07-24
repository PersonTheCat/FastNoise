package personthecat.fastnoise.data;

import org.jetbrains.annotations.Nullable;
import personthecat.fastnoise.util.EnumNamingService;

import java.util.regex.Pattern;

public enum CellularReturnType {
    CELL_VALUE,
    NOISE_LOOKUP,
    DISTANCE,
    DISTANCE2,
    DISTANCE2_ADD,
    DISTANCE2_SUB,
    DISTANCE2_MUL,
    DISTANCE2_DIV,
    DISTANCE3,
    DISTANCE3_ADD,
    DISTANCE3_SUB,
    DISTANCE3_MUL,
    DISTANCE3_DIV,
    FUNCTION;

    final Pattern pattern = EnumNamingService.createPattern(this);
    final String formatted = EnumNamingService.formatName(this);

    @Nullable
    public static CellularReturnType from(final String s) {
        for (final CellularReturnType t : values()) {
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
