package llc.devlab.app.model.dto;

import java.util.Set;

public record ServiceResponse(
        long id,
        String name,
        String url,
        int checkInterval,
        boolean critical,
        boolean enabled,
        Set<Long> dependsOn
) {
}
