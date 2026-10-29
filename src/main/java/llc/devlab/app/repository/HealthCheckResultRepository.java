package llc.devlab.app.repository;

import llc.devlab.app.model.entity.HealthCheckResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface HealthCheckResultRepository extends JpaRepository<HealthCheckResult, Long> {
    List<HealthCheckResult> findAllByServiceIdAndCheckedAtBetween(long serviceId, Instant checkedAtAfter, Instant checkedAtBefore);
}
