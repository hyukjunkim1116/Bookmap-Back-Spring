package foodmap.V2.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Getter
@Component
public class LocationData {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Location {
        private String name;
        private double latitude;
        private double longitude;
    };

    private final String jsonData = "[{\"name\":\"광화문\",\"latitude\":37.5706,\"longitude\":126.978}," +
            "{\"name\":\"가든파이브\",\"latitude\":37.4771,\"longitude\":127.1223}," +
            "{\"name\":\"강남\",\"latitude\":37.4979,\"longitude\":127.0276}," +
            "{\"name\":\"건대\",\"latitude\":37.5408,\"longitude\":127.0693}," +
            "{\"name\":\"동대문\",\"latitude\":37.5663,\"longitude\":127.0094}," +
            "{\"name\":\"신도림 디큐브\",\"latitude\":37.5086,\"longitude\":126.8914}," +
            "{\"name\":\"목동\",\"latitude\":37.5263,\"longitude\":126.8707}," +
            "{\"name\":\"서울대\",\"latitude\":37.4602,\"longitude\":126.9513}," +
            "{\"name\":\"수유\",\"latitude\":37.6405,\"longitude\":127.0261}," +
            "{\"name\":\"신논현역 팝업 스토어\",\"latitude\":37.5044,\"longitude\":127.0244}," +
            "{\"name\":\"영등포\",\"latitude\":37.5172,\"longitude\":126.9111}," +
            "{\"name\":\"은평\",\"latitude\":37.607,\"longitude\":126.9299}," +
            "{\"name\":\"이화여대\",\"latitude\":37.5594,\"longitude\":126.9459}," +
            "{\"name\":\"잠실\",\"latitude\":37.5121,\"longitude\":127.1008}," +
            "{\"name\":\"천호\",\"latitude\":37.5376,\"longitude\":127.1246}," +
            "{\"name\":\"청량리\",\"latitude\":37.5814,\"longitude\":127.0465}," +
            "{\"name\":\"합정\",\"latitude\":37.5494,\"longitude\":126.9105}," +
            "{\"name\":\"광교\",\"latitude\":37.2839,\"longitude\":127.0475}," +
            "{\"name\":\"부천\",\"latitude\":37.5045,\"longitude\":126.7613}," +
            "{\"name\":\"분당\",\"latitude\":37.3747,\"longitude\":127.126}," +
            "{\"name\":\"송도\",\"latitude\":37.3894,\"longitude\":126.6463}," +
            "{\"name\":\"인천\",\"latitude\":37.4563,\"longitude\":126.7041}," +
            "{\"name\":\"일산\",\"latitude\":37.6553,\"longitude\":126.7685}," +
            "{\"name\":\"판교\",\"latitude\":37.3943,\"longitude\":127.1112}," +
            "{\"name\":\"평촌\",\"latitude\":37.3903,\"longitude\":126.951}," +
            "{\"name\":\"경성대ㆍ부경대\",\"latitude\":35.1357,\"longitude\":129.1018}," +
            "{\"name\":\"광주상무\",\"latitude\":35.1469,\"longitude\":126.9153}," +
            "{\"name\":\"대구\",\"latitude\":35.869,\"longitude\":128.5985}," +
            "{\"name\":\"대전\",\"latitude\":36.3504,\"longitude\":127.3845}," +
            "{\"name\":\"부산\",\"latitude\":35.1563,\"longitude\":129.0593}," +
            "{\"name\":\"세종\",\"latitude\":36.4795,\"longitude\":127.2581}," +
            "{\"name\":\"센텀시티\",\"latitude\":35.1707,\"longitude\":129.1325}," +
            "{\"name\":\"울산\",\"latitude\":35.5466,\"longitude\":129.2627}," +
            "{\"name\":\"전북대\",\"latitude\":35.821,\"longitude\":127.148}," +
            "{\"name\":\"전주\",\"latitude\":35.8181,\"longitude\":127.1534}," +
            "{\"name\":\"창원\",\"latitude\":35.2215,\"longitude\":128.6811}," +
            "{\"name\":\"천안\",\"latitude\":36.8106,\"longitude\":127.1535}," +
            "{\"name\":\"칠곡\",\"latitude\":35.9756,\"longitude\":128.5046}," +
            "{\"name\":\"해운대 팝업 스토어\",\"latitude\":35.1641,\"longitude\":129.1605}]";

    private final ObjectMapper objectMapper = new ObjectMapper();
    // JSON 데이터를 리스트 객체로 변환
    public List<Location> getLocationList() throws IOException {
        return objectMapper.readValue(jsonData, new TypeReference<>() {
        });
    }
}
