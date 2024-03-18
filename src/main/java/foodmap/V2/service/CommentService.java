package foodmap.V2.service;

import foodmap.V2.domain.Comment;
import foodmap.V2.domain.CommentEditor;
import foodmap.V2.domain.UserInfo;
import foodmap.V2.domain.post.Post;
import foodmap.V2.dto.request.comment.CommentCreate;
import foodmap.V2.dto.request.comment.CommentEdit;
import foodmap.V2.dto.request.comment.CommentSearch;
import foodmap.V2.dto.response.comment.CommentDetailResponseDTO;
import foodmap.V2.dto.response.comment.CommentListResponseDTO;

import foodmap.V2.exception.comment.CommentNotFound;
import foodmap.V2.exception.post.PostNotFound;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.repository.UserRepository;
import foodmap.V2.repository.comment.CommentRepository;
import foodmap.V2.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentDetailResponseDTO write(Long userId,Long postId, CommentCreate request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);
        UserInfo user =  userRepository.findById(userId).orElseThrow(
                UserNotFound::new
        );
        Comment comment = Comment.builder()
                .author(user)
                .comment(request.getComment())
                .build();
        post.addComment(comment);
        Comment savedComment = commentRepository.save(comment);
        return CommentDetailResponseDTO.builder()
                .author(savedComment.getAuthor().getId())
                .comment(savedComment.getComment())
                .created_at(String.valueOf(savedComment.getCreatedAt()))
                .id(savedComment.getId())
                .image(savedComment.getAuthor().getImage())
                .post(savedComment.getPost().getId())
                .updated_at(String.valueOf(savedComment.getCreatedAt()))
                .username(savedComment.getAuthor().getUsername())
                .build();
    }

    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFound::new);
        commentRepository.delete(comment);
    }

    @Transactional
    public CommentDetailResponseDTO edit(Long commentId, CommentEdit commentEdit) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFound::new);
            CommentEditor.CommentEditorBuilder editorBuilder = comment.toEditor();
        CommentEditor commentEditor = editorBuilder.comment(commentEdit.getComment())
                    .build();
        comment.edit(commentEditor);
        Comment savedComment = commentRepository.save(comment);
        return CommentDetailResponseDTO.builder()
                                .author(savedComment.getAuthor().getId())
                                .comment(savedComment.getComment())
                                .created_at(String.valueOf(savedComment.getCreatedAt()))
                                .id(savedComment.getId())
                                .image(savedComment.getAuthor().getImage())
                                .post(savedComment.getPost().getId())
                                .updated_at(String.valueOf(savedComment.getCreatedAt()))
                                .username(savedComment.getAuthor().getUsername())
                                .build();
        }
    public CommentListResponseDTO get(Long postId,CommentSearch commentSearch) {
        List<Comment> commentList = commentRepository.getList(postId,commentSearch);
        var commentCount = commentList.size();
        Boolean hasNext = commentRepository.hasNextPage(postId,commentSearch);

        List<CommentDetailResponseDTO> commentDetailResponseDTO =commentList.stream()
                .map(
                        comment -> CommentDetailResponseDTO.builder()
                                .author(comment.getAuthor().getId())
                                .comment(comment.getComment())
                                .created_at(String.valueOf(comment.getCreatedAt()))
                                .id(comment.getId())
                                .image(comment.getAuthor().getImage())
                                .post(comment.getPost().getId())
                                .updated_at(String.valueOf(comment.getCreatedAt()))
                                .username(comment.getAuthor().getUsername())
                                .build())
                .collect(Collectors.toList());
        return CommentListResponseDTO.builder()
                .count((long) commentCount)
                .next(hasNext)
                .results(commentDetailResponseDTO).build();
    }
}
