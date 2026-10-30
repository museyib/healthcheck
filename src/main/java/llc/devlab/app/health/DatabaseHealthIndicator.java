package llc.devlab.app.health;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component("database")
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Override
    public @Nullable Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                return Health.up().build();
            }

            return Health.down()
                    .withDetail("error", "Database connection is invalid")
                    .build();

        } catch (SQLException e) {
            return Health.down(e)
                    .withDetail("error", "Database is unavailable")
                    .build();
        }
    }
}
