package foodmap.V2.controller;

import foodmap.V2.dto.request.comment.CommentCreate;
import foodmap.V2.dto.request.comment.CommentEdit;
import foodmap.V2.dto.request.comment.CommentSearch;
import foodmap.V2.dto.response.comment.CommentDetailResponseDTO;
import foodmap.V2.dto.response.comment.CommentListResponseDTO;
import foodmap.V2.dto.response.comment.CommentResponse;
import foodmap.V2.service.CommentService;
import foodmap.V2.service.JwtService;
import groovy.util.logging.Slf4j;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class CommentController {
    private final CommentService commentService;
    private final JwtService jwtService;
@GetMapping("/{postId}/comment")
public CommentListResponseDTO get(@PathVariable Long postId, @ModelAttribute CommentSearch commentSearch) {
    return commentService.get(postId,commentSearch);
}
    @PostMapping("/{postId}/comment")
    public CommentDetailResponseDTO write(@RequestHeader("Authorization") String token, @PathVariable Long postId, @RequestBody @Valid CommentCreate request) {
        String accessToken = token.substring(7);
        Long userId= Long.valueOf(jwtService.extractUserid(accessToken));
        return commentService.write(userId,postId, request);
    }
    @PutMapping("/comment/{commentId}")
    public CommentDetailResponseDTO edit(@PathVariable Long commentId, @RequestBody @Valid CommentEdit request) {
        return commentService.edit(commentId,request);

    }
    @DeleteMapping("/comment/{commentId}")
    public void delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
    }
}
