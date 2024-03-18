package foodmap.V2.repository.comment;

import foodmap.V2.domain.Comment;
import foodmap.V2.dto.request.comment.CommentSearch;
import foodmap.V2.dto.request.post.PostSearch;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> getList(Long postId,CommentSearch commentSearch);
    Boolean hasNextPage(Long postId,CommentSearch commentSearch);
}
