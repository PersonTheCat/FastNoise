package personthecat.fastnoise.data;

import org.jetbrains.annotations.Nullable;
import personthecat.fastnoise.util.EnumNamingService;

import java.util.regex.Pattern;

public enum CellularDistanceType {
    EUCLIDEAN {
        @Override
        public float getDistance(float x, float y) {
            return x * x + y * y;
        }

        @Override
        public float getDistance(float x, float y, float z) {
            return x * x + y * y + z * z;
        }
    },
    MANHATTAN {
        @Override
        public float getDistance(float x, float y) {
            return Math.abs(x) + Math.abs(y);
        }

        @Override
        public float getDistance(float x, float y, float z) {
            return Math.abs(x) + Math.abs(y) + Math.abs(z);
        }
    },
    NATURAL {
        @Override
        public float getDistance(float x, float y) {
            return Math.abs(x) + Math.abs(y) + x * x + y * y;
        }

        @Override
        public float getDistance(float x, float y, float z) {
            return Math.abs(x) + Math.abs(y) + Math.abs(z) + x * x + y * y + z * z;
        }
    };

    final Pattern pattern = EnumNamingService.createPattern(this);
    final String formatted = EnumNamingService.formatName(this);

    public abstract float getDistance(final float x, final float y);
    public abstract float getDistance(final float x, final float y, final float z);

    @Nullable
    public static CellularDistanceType from(final String s) {
        for (final CellularDistanceType t : values()) {
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
