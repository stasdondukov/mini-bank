package bank;

import bank.application.TransferService;
import bank.domain.AccountNumber;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnnotationInspectorTest {
    @Test
    void printsClassNameAuditFlagAndDeclaredMethods() {
        List<String> lines = inspect(TransferService.class);

        assertTrue(lines.contains("Class: bank.application.TransferService"));
        assertTrue(lines.contains("Audited: true"));
        assertTrue(lines.contains("Declared methods:"));
        assertTrue(lines.stream().anyMatch(line -> line.contains("TransferService.transfer(")));
        assertFalse(lines.stream().anyMatch(line -> line.contains("java.lang.Object.toString(")));
    }

    @Test
    void printsRolesOnlyForAnnotatedMethods() throws NoSuchMethodException {
        List<String> lines = inspect(BankService.class);

        assertTrue(lines.contains("Class: bank.BankService"));
        assertTrue(lines.contains("Audited: false"));
        assertTrue(lines.contains(BankService.class.getDeclaredMethod("closeAccount", AccountNumber.class)
            .toGenericString()));
        assertEquals(List.of("closeAccount -> ADMIN"),
            lines.stream().filter(line -> line.contains(" -> ")).toList());
    }

    private List<String> inspect(Class<?> type) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(buffer, false, StandardCharsets.UTF_8)) {
            new AnnotationInspector(output).inspect(type);
        }
        return buffer.toString(StandardCharsets.UTF_8).lines().toList();
    }
}
