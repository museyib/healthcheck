package llc.devlab.app.repository;

import llc.devlab.app.model.dto.HealthAggregateData;
import llc.devlab.app.model.entity.HealthCheckResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface HealthCheckResultRepository extends JpaRepository<HealthCheckResult, Long> {
    @Query("""
            SELECT COALESCE(SUM(CASE WHEN h.status = 'UP' THEN 1 ELSE 0 END) / COUNT(h) * 100, 0) uptimePercentage,
                   COALESCE(AVG(h.responseTimeMs), 0)    responseTimeAvg
            FROM HealthCheckResult h
            WHERE h.service.id = ?1
                        AND h.checkedAt BETWEEN ?2 AND ?3""")
    HealthAggregateData getHealthAggregateData(long serviceId,
                                    Instant checkedAtAfter,
                                    Instant checkedAtBefore);

    @Query("""
            SELECT h FROM HealthCheckResult h
            WHERE h.service.id = ?1
                        AND h.checkedAt BETWEEN ?2 AND ?3
            ORDER BY h.checkedAt DESC""")
    List<HealthCheckResult> getCheckResultHistory(long serviceId,
                                                  Instant checkedAtAfter,
                                                  Instant checkedAtBefore);
}
