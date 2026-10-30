package llc.devlab.app.repository;

import llc.devlab.app.model.entity.HealthCheckResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface HealthCheckResultRepository extends JpaRepository<HealthCheckResult, Long> {
    @Query("""
            SELECT h FROM HealthCheckResult h
            WHERE h.service.id = ?1
                        AND h.checkedAt BETWEEN ?2 AND ?3
            ORDER BY h.checkedAt DESC""")
    List<HealthCheckResult> getCheckResultHistory(long serviceId,
                                                  Instant checkedAtAfter,
                                                  Instant checkedAtBefore);
}
