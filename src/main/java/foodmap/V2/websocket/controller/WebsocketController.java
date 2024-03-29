package foodmap.V2.websocket.controller;

import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.user.service.UserService;
import foodmap.V2.websocket.dto.response.ChatResponseDTO;
import foodmap.V2.websocket.dto.response.NotificationResponseDTO;
import foodmap.V2.jwt.JwtService;
import foodmap.V2.websocket.service.WebSocketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebsocketController {
    private final WebSocketService webSocketService;
    private final UserService userService;
    @GetMapping("/api/webchat")
    public List<ChatResponseDTO>  getChats() {
        return webSocketService.getChatList();
    }
    @GetMapping("/api/notification")
    public List<NotificationResponseDTO> getNotifications(HttpServletRequest request) {
        UserInfo user = userService.getUserByRequest(request).orElseThrow(UserNotFound::new);
        return webSocketService.getNotificationList(user.getId());
    }
    @PatchMapping("/api/notification/{not_id}")
    public void setIsNotRead(HttpServletRequest request ,@PathVariable String not_id) {
        userService.getUserByRequest(request).orElseThrow(UserNotFound::new);
        webSocketService.setNotRead(Long.valueOf(not_id));
    }
}