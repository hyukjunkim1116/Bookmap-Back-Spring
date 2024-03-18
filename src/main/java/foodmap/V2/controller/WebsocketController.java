package foodmap.V2.controller;

import foodmap.V2.config.websocket.ChatResponse;
import foodmap.V2.dto.response.NotificationResponse;
import foodmap.V2.service.JwtService;
import foodmap.V2.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebsocketController {
    private final WebSocketService webSocketService;
    private final JwtService jwtService;
    @GetMapping("/api/webchat/")
    public List<ChatResponse>  getChats(@RequestHeader("Authorization") String token) {
        log.info("access");
        String accessToken = token.substring(7);
        Long userId= Long.valueOf(jwtService.extractUserid(accessToken));
        return webSocketService.getChatList();
    }
    @GetMapping("/api/notification/")
    public List<NotificationResponse> getNotifications(@RequestHeader("Authorization") String token) {
        log.info("access");
        String accessToken = token.substring(7);
        Long userId= Long.valueOf(jwtService.extractUserid(accessToken));
        return webSocketService.getNotificationList(userId);
    }
    @PatchMapping("/api/notification/{not_id}")
    public void setIsNotRead(@RequestHeader("Authorization") String token ,@PathVariable String not_id) {
        String accessToken = token.substring(7);
        Long userId= Long.valueOf(jwtService.extractUserid(accessToken));
        webSocketService.setNotRead(userId, Long.valueOf(not_id));
    }

}
//채팅 목록 반환	채팅 목록 조회	READ	GET	/api/webchat	-	[
//        {
//content: "content"
//id: chatId,
//sender: user.username,
//sender_id: uid,
//sender_image: "url",
//timestamp: "2024-02-20T15:46:00.732018+09:00"
//        },{},{},…
//        ]
//해당 유저의 알림 목록 반환
//알림 목록 조회	READ	GET	/api/notification	-	[
//        {
//created_at: "2024-02-20T15:45:54.993284+09:00"
//id: notId
//is_read: Boolean
//message: "message"
//reciever: uid
//},{},{},…
//        ]
//알림의 is_read=True로 설정후 db 저장	알림 읽음 확인	UPDATE	PATCH	/api/notification/<int:not_id>	-	{
//        ”HTTP Response” : “200 OK”
//        }