package customer.data.util;

import java.util.Map;
import java.util.Optional;


public class RequestUtils {

    /** One argument with single value map that will be fetched from request context. */
    public static Optional<String> first(Map<String, String> single, String key) {
        if (single != null && single.containsKey(key)) {
            return Optional.ofNullable(single.get(key));
        }
        return Optional.empty();
    }

    public static int parseInt(String raw, int def, int min, int max) {
        try {
            int v = raw == null ? def : Integer.parseInt(raw);
            return Math.max(min, Math.min(max, v));
        } catch (Exception e) {
            return def;
        }
    }

}
