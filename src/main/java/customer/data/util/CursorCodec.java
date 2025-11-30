package customer.data.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class CursorCodec {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String encode(Map<String, Object> payload) {
        try {
            byte[] json = MAPPER.writeValueAsBytes(payload);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to encode cursor", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> decode(String cursor) {
        if (cursor == null || cursor.isBlank())
            return null;
        try {
            byte[] json = Base64.getUrlDecoder().decode(cursor.getBytes(StandardCharsets.UTF_8));
            return MAPPER.readValue(json, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cursor", e);
        }
    }

}
