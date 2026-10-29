package llc.devlab.app.model.dto;

import java.util.List;

public record HealthResponse(
        long id,
        String name,
        String from,
        String to,
        double uptimePercentage,
        double responseTimeAvg,
        List<CheckItem> checks
) {
    public record CheckItem(
            String checkedAt,
            String status,
            int responseTimeMs
    ) {
    }
}
