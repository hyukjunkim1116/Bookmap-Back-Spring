package foodmap.V2.controller;

import foodmap.V2.dto.request.ReportRequestDTO;

import foodmap.V2.service.JwtService;
import foodmap.V2.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final JwtService jwtService;
    @PostMapping("/api/reports/{postId}")
    public void create(@RequestHeader("Authorization") String token, @RequestBody ReportRequestDTO reportRequestDTO, @PathVariable Long postId){
        String jwtToken = token.substring(7);
        String userId = jwtService.extractUserid(jwtToken);
        reportService.write(Long.valueOf(userId),postId,reportRequestDTO);
    }
}
//db에 신고 목록 저장	신고하기	CREATE	POST		{
//title: “title”,
//content: “content”,
//        }	{
//        ”HTTP Response” : “200 OK”
//        }