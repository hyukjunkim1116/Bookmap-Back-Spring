package foodmap.V2.jwt;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Embeddable
public class RefreshToken {
    private String refresh;
    private Instant expiryDate;
}
