package llc.devlab.app.model.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ServiceSummary(
        Long id,
        String name,
        String lastStatus,
        Instant lastCheckTime,
        Instant lastSuccessfulCheck,
        BigDecimal uptimePercentage,
        BigDecimal responseTimeAvg
) {
}
