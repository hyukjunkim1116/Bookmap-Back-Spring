package foodmap.V2.config;

import foodmap.V2.domain.UserInfo;
import foodmap.V2.repository.UserRepository;
import foodmap.V2.service.user.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
public class BookmapMockSecurityContext implements WithSecurityContextFactory<BookmapMockUser> {
    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    @Override
    public SecurityContext createSecurityContext(BookmapMockUser annotation) {
        var user = UserInfo.builder()
                .email(annotation.email())
                .username(annotation.username())
                .password(annotation.password())
                .build();
        userRepository.save(user);
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(user.getEmail());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        var context=SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationToken);
        return context;
    }
}
