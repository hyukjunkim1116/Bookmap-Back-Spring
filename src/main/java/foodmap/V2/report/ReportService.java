package foodmap.V2.report;
import foodmap.V2.user.domain.UserInfo;
import foodmap.V2.post.domain.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    @Transactional
    public void write(UserInfo reportingUser,Post post, ReportRequestDTO request) {
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
