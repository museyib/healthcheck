package llc.devlab.app;

import com.sun.net.httpserver.HttpServer;
import llc.devlab.app.service.ScheduleService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HealthEndpointIntegrationTest {

    private static final AtomicInteger DEPENDENCY_STATUS = new AtomicInteger(200);
    private static HttpServer dependencyServer;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScheduleService scheduleService;

    @DynamicPropertySource
    static void dependencyUrl(DynamicPropertyRegistry registry) {
        registry.add("monitoring.dependency.url", () -> {
            startDependencyServer();
            return "http://127.0.0.1:" + dependencyServer.getAddress().getPort() + "/health";
        });
    }

    @AfterAll
    static void stopDependencyServer() {
        if (dependencyServer != null) {
            dependencyServer.stop(0);
            dependencyServer = null;
        }
    }

    @Test
    void healthIsUpWhenExternalDependencyIsHealthy() throws Exception {
        DEPENDENCY_STATUS.set(200);

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.externalHttp.status").value("UP"));
    }

    @Test
    void healthIsDownWhenExternalDependencyIsDegraded() throws Exception {
        DEPENDENCY_STATUS.set(503);

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.components.externalHttp.status").value("DOWN"));
    }

    private static synchronized void startDependencyServer() {
        if (dependencyServer != null) {
            return;
        }

        try {
            dependencyServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            dependencyServer.createContext("/health", exchange -> {
                exchange.sendResponseHeaders(DEPENDENCY_STATUS.get(), -1);
                exchange.close();
            });
            dependencyServer.start();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to start the health-test dependency", exception);
        }
    }
}
