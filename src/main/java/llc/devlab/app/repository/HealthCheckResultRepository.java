package llc.devlab.app.repository;

import llc.devlab.app.model.dto.HealthAggregateData;
import llc.devlab.app.model.dto.ServiceSummary;
import llc.devlab.app.model.entity.HealthCheckResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface HealthCheckResultRepository extends JpaRepository<HealthCheckResult, Long> {

    @Query(value = """
            WITH l AS (
            SELECT DISTINCT ON (service_id)
                   service_id,
                   status,
                   checked_at
            FROM health_check_result
            ORDER BY service_id, checked_at DESC, id DESC)
            SELECT l.service_id AS id,
                   m.name AS name,
                   l.status AS lastStatus,
                   l.checked_at AS lastCheckTime,
                   a.lastSuccessfulCheck AS lastSuccessfulCheck,
                   a.uptimePercentage AS uptimePercentage,
                   a.responseTimeAvg AS responseTimeAvg
            FROM l
            JOIN (
                SELECT service_id,
                       MAX(CASE WHEN status = 'UP' THEN checked_at END) AS lastSuccessfulCheck,
                       COALESCE(SUM(CASE WHEN status = 'UP' THEN 1.0 ELSE 0.0 END) / COUNT(*) * 100.0, 0.0) AS uptimePercentage,
                       COALESCE(AVG(response_time_ms), 0.0) AS responseTimeAvg
                FROM health_check_result
                GROUP BY service_id
            ) a ON l.service_id = a.service_id
            JOIN monitored_service m ON l.service_id = m.id""",
    nativeQuery = true)
    List<ServiceSummary> getHealthAggregateData();

    @Query("""
            SELECT COALESCE(SUM(CASE WHEN h.status = 'UP' THEN 1.0 ELSE 0.0 END) / COUNT(h) * 100, 0) uptimePercentage,
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
                                                  Instant checkedAtBefore,
                                                  Pageable pageable);
}
