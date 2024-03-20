package foodmap.V2.dto.response;
import lombok.Data;

@Data
public class BookCrawlingResponseDTO {
    private String store;
    private String count;
    private double latitude;
    private double longitude;
    private String code;
    
    public BookCrawlingResponseDTO(String store, String count,double latitude,double longitude,String code) {
        this.store = store;
        this.count = count;
        this.latitude=latitude;
        this.longitude=longitude;
        this.code = code;
    }
}
