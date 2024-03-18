package foodmap.V2.repository.post;


import com.querydsl.jpa.impl.JPAQuery;
import foodmap.V2.domain.post.Post;
import foodmap.V2.dto.request.post.PostSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import static foodmap.V2.domain.post.QPost.post;
@Slf4j
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    public JPAQuery<Post> getInitialPost(PostSearch postSearch) {
        // query 변수를 한 번만 선언하고 초기화
        return jpaQueryFactory.selectFrom(post)
                .where(post.title.containsIgnoreCase(postSearch.getSearch()));
    }
    public List<Post> getQuery(JPAQuery<Post> query,PostSearch postSearch) {
        return switch (postSearch.getSort()) {
            case "likeCount" -> {
                query.orderBy(post.likedUser.size().desc());
                yield query.fetch();
            }
            case "readCount" -> {
                query.orderBy(post.readCount.desc());
                yield query.fetch();
            }
            case "bookMark" -> {
                query.orderBy(post.bookmark.size().desc());
                yield query.fetch();
            }
            default -> {
                query.orderBy(post.createdAt.desc());
                yield query.fetch();
            }
        };
    }
    @Override
    public List<Post> getList(PostSearch postSearch) {
        log.info("postsearchPostrepo,{},{},{},{}",postSearch.getSearch(),postSearch.getSort(),postSearch.getPage(),postSearch.getOffset());
        JPAQuery<Post> query = getInitialPost(postSearch).limit(postSearch.getSize())
                .offset(postSearch.getOffset());
        return getQuery(query,postSearch);
    }

    @Override
    public Boolean hasNextPage(PostSearch postSearch) {
        JPAQuery<Post> query = getInitialPost(postSearch);
        List<Post> totalPosts = getQuery(query,postSearch);
        var totalCount = totalPosts.size();
        int currentPage = postSearch.getPage();
        int pageSize = postSearch.getSize();
        // 총 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        return currentPage < totalPages;
    }
}
