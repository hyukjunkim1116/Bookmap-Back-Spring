package foodmap.V2.controller;

import foodmap.V2.dto.response.ChatResponse;
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
    public List<ChatResponse>  getChats() {
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
        webSocketService.setNotRead(Long.valueOf(not_id));
    }
}