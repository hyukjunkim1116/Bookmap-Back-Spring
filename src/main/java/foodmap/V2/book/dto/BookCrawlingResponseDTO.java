package foodmap.V2.book.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public class BookCrawlingResponseDTO {
    private String store;
    private String count;
    private double latitude;
    private double longitude;
    private String code;
}
