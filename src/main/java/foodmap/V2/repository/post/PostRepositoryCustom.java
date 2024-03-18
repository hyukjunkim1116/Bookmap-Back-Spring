package foodmap.V2.repository.post;

import foodmap.V2.domain.post.Post;
import foodmap.V2.dto.request.post.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {
    List<Post> getList(PostSearch postSearch);
    Boolean hasNextPage(PostSearch postSearch);
}
