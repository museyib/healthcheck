package llc.devlab.app;

import llc.devlab.app.service.ScheduleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceRegistryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScheduleService scheduleService;

    @Test
    void serviceCanBeCreatedReadUpdatedAndDeletedOverHttp() throws Exception {
        var createResult = mockMvc.perform(post("/api/services")
                        .with(httpBasic("integration", "integration-password"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "payments",
                                  "url": "https://payments.example",
                                  "checkInterval": 30,
                                  "critical": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("payments"))
                .andExpect(jsonPath("$.url").value("https://payments.example"))
                .andExpect(jsonPath("$.checkInterval").value(30))
                .andExpect(jsonPath("$.critical").value(true))
                .andReturn();
        long id = Long.parseLong(createResult.getResponse().getContentAsString()
                .replaceAll("(?s).*?\"id\"\\s*:\\s*(\\d+).*", "$1"));

        mockMvc.perform(get("/api/services/{id}", id)
                        .with(httpBasic("integration", "integration-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("payments"));

        mockMvc.perform(get("/api/services")
                        .with(httpBasic("integration", "integration-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value("payments"));

        mockMvc.perform(put("/api/services/{id}", id)
                        .with(httpBasic("integration", "integration-password"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": %d,
                                  "name": "payments-updated",
                                  "url": "https://payments-updated.example",
                                  "checkInterval": 60,
                                  "critical": false
                                }
                                """.formatted(id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("payments-updated"))
                .andExpect(jsonPath("$.url").value("https://payments-updated.example"))
                .andExpect(jsonPath("$.checkInterval").value(60))
                .andExpect(jsonPath("$.critical").value(false));

        mockMvc.perform(delete("/api/services/{id}", id)
                        .with(httpBasic("integration", "integration-password"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/services")
                        .with(httpBasic("integration", "integration-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
