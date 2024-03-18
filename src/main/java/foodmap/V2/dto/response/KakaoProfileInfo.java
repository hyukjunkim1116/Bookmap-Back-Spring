package foodmap.V2.dto.response;
import lombok.Data;

@Data
public class KakaoProfileInfo {
    private String nickname;
    private String thumbnail_image_url;
    private String profile_image_url;
    private Boolean is_default_image;
}