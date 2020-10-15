package uk.gov.hmcts.reform.sendletter.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class ExecusionServiceTest {
    private ExecusionService service = new ExecusionService();

    @Test
    void testSuccess() {
        AtomicInteger counter = new AtomicInteger(0);
        service.run(() -> counter.set(10),
            () -> {
                throw new RuntimeException("This should not be invoked"); });

        assertThat(counter.get()).isEqualTo(10);
    }

    @Test
    void testException() {
        assertDoesNotThrow(() -> service.run(() -> {
            throw new RuntimeException("Error"); },
            () -> {
                throw new RuntimeException("This should not be invoked"); }));
    }

    @Test
    void shouldExecutedData() {
        AtomicInteger counter = new AtomicInteger(0);
        service.run(() -> {
            throw new DataIntegrityViolationException("Error"); },
            counter::incrementAndGet);
        assertThat(counter.get()).isEqualTo(1);
    }
}