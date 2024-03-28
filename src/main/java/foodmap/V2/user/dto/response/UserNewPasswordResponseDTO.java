package foodmap.V2.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class UserNewPasswordResponseDTO {
    private final String password;
}
