package personthecat.fastnoise.util;

import java.util.regex.Pattern;

public class EnumNamingService {

    public static <E extends Enum<E>> Pattern createPattern(final E e) {
        return Pattern.compile(e.name().replace("_", "_?"), Pattern.CASE_INSENSITIVE);
    }

    public static <E extends Enum<E>> String formatName(final E e) {
        final char[] chars = e.name().toCharArray();
        final StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(chars[0]));
        boolean word = false;

        for (int i = 1; i < chars.length; i++) {
            final char c = chars[i];
            if (c == '_') {
                word = true;
            } else if (word) {
                sb.append(Character.toUpperCase(c));
                word = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
