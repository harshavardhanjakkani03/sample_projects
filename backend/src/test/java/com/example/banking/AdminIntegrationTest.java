package com.example.banking;

import com.example.banking.dto.RegisterRequest;
import com.example.banking.dto.UserSummary;
import com.example.banking.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.config.location=classpath:application-test.properties")
public class AdminIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void pendingAndApproveFlow() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("bob@example.com");
        req.setName("Bob");
        req.setPassword("secret");
        ResponseEntity<User> r = restTemplate.postForEntity("http://localhost:" + port + "/api/register", req, User.class);
        User u = r.getBody();

        ResponseEntity<UserSummary[]> pending = restTemplate.getForEntity("http://localhost:" + port + "/api/admin/pending-users", UserSummary[].class);
        assertThat(pending.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(pending.getBody()).extracting(UserSummary::getEmail).contains("bob@example.com");

        ResponseEntity<UserSummary> approved = restTemplate.postForEntity("http://localhost:" + port + "/api/admin/users/" + u.getId() + "/approve", null, UserSummary.class);
        assertThat(approved.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(approved.getBody().isApproved()).isTrue();
    }
}
