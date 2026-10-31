package llc.devlab.app.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static llc.devlab.app.config.Constants.URL_REGEX;
import java.util.Set;

public record CreateServiceRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = URL_REGEX) String url,
        @Min(5) int checkInterval,
        boolean critical,
        boolean enabled,
        Set<Long> dependsOn
) {
}
