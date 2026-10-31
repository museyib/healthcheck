package llc.devlab.app.model.dto;

import java.util.List;

public record ServiceDependencyGraph(List<Node> nodes, List<Edge> edges) {
    public record Node(long id, String name) {}
    public record Edge(long serviceId, long dependencyId) {}
}
