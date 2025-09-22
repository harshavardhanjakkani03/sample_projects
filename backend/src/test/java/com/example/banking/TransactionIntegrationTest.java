package com.example.banking;

import com.example.banking.dto.RegisterRequest;
import com.example.banking.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.config.location=classpath:application-test.properties")
public class TransactionIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void idempotencyKeyPreventsDuplicate() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("sue@example.com");
        req.setName("Sue");
        req.setPassword("secret");
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/register", req, Object.class);
        // get user id from created resource
        Integer userId = ((java.util.LinkedHashMap)((java.util.Map)response.getBody()).get("id")).intValue();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Idempotency-Key", "abc-123");
        HttpEntity<?> entity = new HttpEntity<>(headers);

    ResponseEntity<Transaction> t1 = restTemplate.exchange("http://localhost:" + port + "/api/transactions?userId=" + userId + "&amount=10&type=deposit&channel=ATM", HttpMethod.POST, entity, Transaction.class);
    assertThat(t1.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(t1.getBody()).isNotNull();

    // repeat the same request with the same idempotency key -> should return the same transaction
    ResponseEntity<Transaction> t2 = restTemplate.exchange("http://localhost:" + port + "/api/transactions?userId=" + userId + "&amount=10&type=deposit&channel=ATM", HttpMethod.POST, entity, Transaction.class);
    assertThat(t2.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(t2.getBody()).isNotNull();
    assertThat(t2.getBody().getId()).isEqualTo(t1.getBody().getId());
    }
}
