package foodmap.V2.config;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = FoodmapMockSecurityContext.class)
public @interface FoodmapMockUser {
    String username() default "kim";

    String email() default "1@1.com";

    String password() default "1";
}
