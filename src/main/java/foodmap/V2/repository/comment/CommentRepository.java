package foodmap.V2.repository.comment;

import foodmap.V2.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>,CommentRepositoryCustom{
}
