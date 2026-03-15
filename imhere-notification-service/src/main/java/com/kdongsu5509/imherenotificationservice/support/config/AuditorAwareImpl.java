package com.kdongsu5509.imherenotificationservice.support.config;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {
    private static final String IMHERE_SERVICE = "IMHERE_INNER_SYSTEM";
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String ANONYMOUS = "ANONYMOUS";

    @Override
    public Optional<String> getCurrentAuditor() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return Optional.of(IMHERE_SERVICE);
        }

        String userId = attributes.getRequest().getHeader(USER_ID_HEADER);

        return Optional.ofNullable(userId)
                .filter(s -> !s.isBlank())
                .or(() -> Optional.of(ANONYMOUS));
    }
}