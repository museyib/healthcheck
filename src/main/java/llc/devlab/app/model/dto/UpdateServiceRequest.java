package llc.devlab.app.model.dto;

public record UpdateServiceRequest(
        long id,
        String name,
        String url,
        int checkInterval,
        boolean critical
) {
}
