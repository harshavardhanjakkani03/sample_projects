package com.example.banking;

import com.example.banking.controller.IdempotencyController;
import com.example.banking.model.IdempotencyKey;
import com.example.banking.repository.IdempotencyKeyRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IdempotencyController.class)
public class IdempotencyControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IdempotencyKeyRepository repo;

    @Test
    public void listAndGetAndDelete() throws Exception {
        IdempotencyKey k1 = new IdempotencyKey();
        k1.setId(1L);
        k1.setKeyValue("key-1");
        k1.setUserId(42L);
        k1.setTransactionId(100L);
        k1.setCreatedAt(OffsetDateTime.now());
        k1.setResultJson("{\"transactionId\":100}");

        // mock paged responses
        org.springframework.data.domain.PageImpl<IdempotencyKey> page = new org.springframework.data.domain.PageImpl<>(List.of(k1), org.springframework.data.domain.PageRequest.of(0,20), 1);
        when(repo.findByCreatedAtAfter(any(java.time.OffsetDateTime.class), any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        when(repo.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);
        when(repo.findByKeyValue("key-1")).thenReturn(Optional.of(k1));

        // list
        mvc.perform(get("/api/idempotency?days=30")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].keyValue").value("key-1"));

        // get single (parsed result)
        mvc.perform(get("/api/idempotency/key-1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.keyValue").value("key-1"))
                .andExpect(jsonPath("$.result.transactionId").value(100));

        // delete existing
        when(repo.findByKeyValue("key-1")).thenReturn(Optional.of(k1));
        mvc.perform(delete("/api/idempotency/key-1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(1));
        verify(repo, atLeastOnce()).delete(k1);

        // delete missing
        when(repo.findByKeyValue("missing")).thenReturn(Optional.empty());
        mvc.perform(delete("/api/idempotency/missing")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.deleted").value(0));

        // cleanup: ensure repo.findAll() is used and deletes older ones
        IdempotencyKey old = new IdempotencyKey();
        old.setId(2L);
        old.setKeyValue("old");
        old.setCreatedAt(OffsetDateTime.now().minusDays(40));
        when(repo.findAll()).thenReturn(List.of(old));
        mvc.perform(delete("/api/idempotency/cleanup?days=30")).andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(1));
        verify(repo, atLeastOnce()).delete(old);
    }

        @Test
        public void getReturnsRawStringWhenResultJsonInvalid() throws Exception {
                IdempotencyKey bad = new IdempotencyKey();
                bad.setId(9L);
                bad.setKeyValue("bad-json");
                bad.setUserId(7L);
                bad.setCreatedAt(OffsetDateTime.now());
                bad.setResultJson("not-a-json");

                when(repo.findByKeyValue("bad-json")).thenReturn(Optional.of(bad));

                mvc.perform(get("/api/idempotency/bad-json")).andExpect(status().isOk())
                                .andExpect(jsonPath("$.keyValue").value("bad-json"))
                                .andExpect(jsonPath("$.result").value("not-a-json"));
        }
}
