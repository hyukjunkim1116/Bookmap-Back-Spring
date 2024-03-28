package foodmap.V2.book.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@Builder
@Getter
public class BookDetailRequestDTO {
    private String isbn;
    private double currentLatitude;
    private double currentLongitude;
}