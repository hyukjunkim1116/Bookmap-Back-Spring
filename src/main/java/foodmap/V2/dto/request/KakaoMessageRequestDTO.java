package foodmap.V2.dto.request;

import lombok.Data;

@Data
public class KakaoMessageRequestDTO {
    private String objType;
    private String text;
    private String webUrl;
    private String mobileUrl;
    private String btnTitle;
}
