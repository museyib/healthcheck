package llc.devlab.app.repository;

import llc.devlab.app.model.entity.MonitoredService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, Long> {
    @Query(value = """
                    SELECT * FROM monitored_service s
                     WHERE s.last_checked_at IS NULL
                            OR s.last_checked_at < (CURRENT_TIMESTAMP - (s.check_interval || ' second')::interval)""",
            nativeQuery = true)
    List<MonitoredService> findServicesDueForCheck();
}
