package llc.devlab.app.model.dto;

public record ServiceResponse(
        long id,
        String name,
        String url,
        int checkInterval,
        boolean critical
) {
}
