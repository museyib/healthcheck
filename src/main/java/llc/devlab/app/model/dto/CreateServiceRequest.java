package llc.devlab.app.model.dto;

public record CreateServiceRequest(
        String name,
        String url,
        int checkInterval,
        boolean critical
) {
}
