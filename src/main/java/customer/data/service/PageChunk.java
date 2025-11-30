package customer.data.service;
import java.util.List;

public record PageChunk<T>(List<T> items, String nextCursor) {

}
