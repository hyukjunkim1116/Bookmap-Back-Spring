package foodmap.V2.service;
import foodmap.V2.domain.Report;
import foodmap.V2.domain.UserInfo;
import foodmap.V2.domain.post.Post;
import foodmap.V2.dto.request.ReportRequestDTO;
import foodmap.V2.exception.post.PostNotFound;
import foodmap.V2.exception.user.UserNotFound;
import foodmap.V2.repository.ReportRepository;
import foodmap.V2.repository.post.PostRepository;
import foodmap.V2.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final PostRepository postRepository;
    private final UserService userService;
    private final ReportRepository reportRepository;
    @Transactional
    public void write(Long userId,Long postId, ReportRequestDTO request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);
        UserInfo reportingUser = userService.getUserById(userId).orElseThrow(UserNotFound::new);
        Report report = Report.builder()
                        .title(request.getTitle())
                        .content(request.getContent())
                        .reportingUser(reportingUser.getId())
                        .reportedUser(post.getUserId())
                        .post(post)
                        .build();
        reportRepository.save(report);
    }
}
