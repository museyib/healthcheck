package llc.devlab.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import llc.devlab.app.service.ScheduleService;

@SpringBootTest
class HealthcheckApplicationTests {

    @MockitoBean
    private ScheduleService scheduleService;

    @Test
    void contextLoads() {
    }

}
