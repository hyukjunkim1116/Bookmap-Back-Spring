package foodmap.V2.jwt;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenRequestDTO {
    private String refresh;
}
