package com.alec.Bud_Cal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alec.Bud_Cal.model.User;
import com.alec.Bud_Cal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.data.mongodb.uri=mongodb://localhost:27017/budcal_test",
        "spring.data.mongodb.database=budcal_test"
})
class AuthFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clearUsers() {
        userRepository.deleteAll();
    }

    @Test
    void signupStoresHashedPasswordAndStartsSession() throws Exception {
        MvcResult result = mockMvc.perform(post("/signup")
                        .param("name", "Alec")
                        .param("email", "alec@example.com")
                        .param("password", "supersecure"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(request().sessionAttribute("userEmail", "alec@example.com"))
                .andReturn();

        User user = userRepository.findByEmail("alec@example.com").orElseThrow();
        assertThat(user.getHashedPassword()).isNotEqualTo("supersecure");
        assertThat(user.getHashedPassword()).startsWith("$2");
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(((MockHttpSession) result.getRequest().getSession(false))).isNotNull();
    }

    @Test
    void loginCreatesSessionForExistingUser() throws Exception {
        mockMvc.perform(post("/signup")
                .param("name", "Alec")
                .param("email", "alec@example.com")
                .param("password", "supersecure"));

        mockMvc.perform(post("/login")
                        .param("email", "alec@example.com")
                        .param("password", "supersecure"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(request().sessionAttribute("userEmail", "alec@example.com"));
    }

    @Test
    void dashboardRedirectsWhenSessionIsMissing() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void allocationCalculatorRedirectsWhenSessionIsMissing() throws Exception {
        mockMvc.perform(get("/allocation-calculator"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
