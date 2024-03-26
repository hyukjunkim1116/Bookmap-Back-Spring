package foodmap.V2.config;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = BookmapMockSecurityContext.class)
public @interface BookmapMockUser {
    String username() default "kim";

    String email() default "kim@kim.com";

    String password() default "1";
}
