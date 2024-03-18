package foodmap.V2.repository.comment;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import foodmap.V2.domain.Comment;
import foodmap.V2.domain.post.Post;
import foodmap.V2.dto.request.comment.CommentSearch;
import foodmap.V2.exception.post.PostNotFound;
import foodmap.V2.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import static foodmap.V2.domain.QComment.comment1;

@Slf4j
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final PostRepository postRepository;
    public JPAQuery<Comment> getInitialComment(Long postId,CommentSearch commentSearch) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);
        return jpaQueryFactory.selectFrom(comment1)
                .where(comment1.post.eq(post))
                .orderBy(comment1.id.desc());
    }

    @Override
    public List<Comment> getList(Long postId,CommentSearch commentSearch) {
        var initialList = getInitialComment(postId,commentSearch);
        var list = initialList
                .limit(commentSearch.getSize())
                .offset(commentSearch.getOffset())
                .fetch();
        log.info("listasd,{}",list);
            return list;
        }

    public Boolean hasNextPage(Long postId,CommentSearch commentSearch) {
        JPAQuery<Comment> initialList = getInitialComment(postId,commentSearch);
        var totalCount = initialList.stream().count();
        int currentPage = commentSearch.getPage();
        int pageSize = commentSearch.getSize();
        // 총 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        return currentPage < totalPages;
    }
    }
