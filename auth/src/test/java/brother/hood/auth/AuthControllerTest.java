package brother.hood.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import brother.hood.auth.application.service.dto.response.AllTokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import brother.hood.auth.application.service.dto.request.AuthUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.1")
        .withDatabaseName("testdb")
        .withUsername("root")
        .withPassword("root");

    @DynamicPropertySource
    static void registerMySqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Test
    void login_shouldReturnAllTokenResponse() throws Exception {

        AuthUserRequest testUser = new AuthUserRequest("login@example.com", "password123");

        MvcResult result = mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testUser)))
            .andExpect(status().isCreated())
            .andReturn();

        System.out.println("register: "+ result.getResponse().getContentAsString());

        result = mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testUser)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andReturn();

        System.out.println("login: " + result.getResponse().getContentAsString());
    }

    @Test
    void reissue_shouldReturnAccessToken() throws Exception {

        AuthUserRequest testUser = new AuthUserRequest("reissue@example.com", "password123");

        String registerResponseContent = mockMvc.perform(post("/auth/register")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(testUser)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        AllTokenResponse registerResponse = objectMapper.readValue(registerResponseContent, AllTokenResponse.class);

        mockMvc.perform(post("/auth/reissue")
                .contentType("application/json")
                .header("refresh-token", registerResponse.getRefreshToken()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void withdrawal_shouldReturnAccessToken() throws Exception {

        AuthUserRequest testUser = new AuthUserRequest("withrawl@example.com", "password123");

        String registerResponseContent = mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testUser)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        AllTokenResponse registerResponse = objectMapper.readValue(registerResponseContent, AllTokenResponse.class);

        mockMvc.perform(delete("/auth/withdrawal")
                .contentType("application/json")
                .header("Authorization", "Bearer " + registerResponse.getAccessToken()))
            .andExpect(status().isNoContent());
    }
}
