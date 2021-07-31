package personthecat.fastnoise.data;

import org.jetbrains.annotations.Nullable;
import personthecat.fastnoise.function.FractalFunction;
import personthecat.fastnoise.util.EnumNamingService;

import java.util.regex.Pattern;

public enum FractalType implements FractalFunction {
    FBM {
        @Override
        public float apply(final float f) {
            return f;
        }
    },
    BILLOW {
        @Override
        public float apply(final float f) {
            return Math.abs(f) * 2 - 1;
        }
    },
    RIGID_MULTI {
        @Override
        public float apply(final float f) {
            return 1 - Math.abs(f);
        }
    };

    final Pattern pattern = EnumNamingService.createPattern(this);
    final String formatted = EnumNamingService.formatName(this);

    @Nullable
    public static FractalType from(final String s) {
        for (final FractalType t : values()) {
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
