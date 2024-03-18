package foodmap.V2.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reciever;
    private Boolean is_read;
    private String message;
    private String created_at;
    public void setIs_read() {
        this.is_read = true;
    }
}
