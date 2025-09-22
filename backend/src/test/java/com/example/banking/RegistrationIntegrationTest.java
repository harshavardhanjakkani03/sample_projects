package com.example.banking;

import com.example.banking.dto.RegisterRequest;
import com.example.banking.model.User;
import com.example.banking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.config.location=classpath:application-test.properties")
public class RegistrationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void registerCreatesPendingUser() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("jane@example.com");
        req.setName("Jane");
        req.setPassword("secret");

        ResponseEntity<User> resp = restTemplate.postForEntity("http://localhost:" + port + "/api/register", req, User.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        User u = resp.getBody();
        assertThat(u).isNotNull();
        assertThat(u.getId()).isNotNull();
        User db = userRepository.findById(u.getId()).orElseThrow();
        assertThat(db.isApproved()).isFalse();
    }
}
