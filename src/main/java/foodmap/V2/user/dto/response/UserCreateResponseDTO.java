package foodmap.V2.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class UserCreateResponseDTO {
    private final String access;
    private final String email;
    private final String image;
    private final Boolean is_verified;
    private final String refresh;
    private final Boolean social;
    private final String username;
}
