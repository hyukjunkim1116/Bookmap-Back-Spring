package foodmap.V2.book.dto;

import lombok.*;


@AllArgsConstructor
@Builder
@Getter
public class BookSearchRequestDTO {
    private Long start;
    private String query;
}
