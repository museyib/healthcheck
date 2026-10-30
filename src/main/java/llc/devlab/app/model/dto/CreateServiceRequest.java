package llc.devlab.app.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateServiceRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "^(https?://)?(www\\.)?([a-zA-Z0-9-]+)(\\.[a-zA-Z]{2,6})+(/[a-zA-Z0-9-._~:/?#\\[\\]@!$&'()*+,;=]*)?$") String url,
        @Min(5) int checkInterval,
        boolean critical
) {
}
