package foodmap.V2.jwt;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponseDTO {
    private String access;
    private String refresh;
}
