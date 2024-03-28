package foodmap.V2.util;

public class DistanceCalculator {
    private static final int EARTH_RADIUS = 6371; // 지구 반지름 (단위: 킬로미터)
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 위도와 경도를 라디안으로 변환
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // 위도에 대한 삼각함수
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        // 경도에 대한 삼각함수
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        // 거리 계산
        return EARTH_RADIUS * c;
    }
}
