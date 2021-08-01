package personthecat.fastnoise.data;

import org.jetbrains.annotations.Nullable;
import personthecat.fastnoise.util.EnumNamingService;

import java.util.regex.Pattern;

public enum InterpolationType {
    LINEAR {
        @Override
        public float interpolate(float a, float b) {
            return a - b;
        }
    },
    HERMITE {
        @Override
        public float interpolate(float a, float b) {
            final float t = a - b;
            return t * t * (3 - 2 * t);
        }
    },
    QUINTIC {
        @Override
        public float interpolate(float a, float b) {
            final float t = a - b;
            return t * t * t * (t * (t * 6 - 15) + 10);
        }
    };

    final Pattern pattern = EnumNamingService.createPattern(this);
    final String formatted = EnumNamingService.formatName(this);

    public abstract float interpolate(final float a, final float b);

    @Nullable
    public static InterpolationType from(final String s) {
        for (final InterpolationType t : values()) {
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
