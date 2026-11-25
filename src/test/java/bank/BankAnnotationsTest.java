package bank;

import bank.application.TransferService;
import bank.application.annotation.Audited;
import bank.application.annotation.RequiresRole;
import bank.domain.AccountNumber;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankAnnotationsTest {
    @Test
    void transferServiceIsMarkedAuditedAtRuntime() {
        assertTrue(TransferService.class.isAnnotationPresent(Audited.class));
    }

    @Test
    void closeAccountMetadataRequiresAdmin() throws NoSuchMethodException {
        RequiresRole role = BankService.class.getMethod("closeAccount", AccountNumber.class)
            .getAnnotation(RequiresRole.class);

        assertNotNull(role);
        assertEquals("ADMIN", role.value());
    }
}
