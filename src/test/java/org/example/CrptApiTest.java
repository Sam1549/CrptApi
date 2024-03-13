package org.example;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class CrptApiTest {

    @Test
    void testCreateDocumentWithinLimit() {
        CrptApi crptApi = new CrptApi(TimeUnit.MINUTES, 10);
        CrptApi.Document document = createSampleDocument();
        String signature = "sampleSignature";

        assertDoesNotThrow(() -> crptApi.createDocument(document, signature));
    }

    @Test
    void testCreateDocumentExceedLimit() {
        CrptApi crptApi = new CrptApi(TimeUnit.MINUTES, 1);
        CrptApi.Document document = createSampleDocument();
        String signature = "sampleSignature";

        for (int i = 0; i < 1; i++) {
            assertDoesNotThrow(() -> crptApi.createDocument(document, signature));
        }

        assertThrows(IOException.class, () -> crptApi.createDocument(document, signature));
    }

    private CrptApi.Document createSampleDocument() {
        CrptApi.Description description = new CrptApi.Description("sampleParticipantInn");
        CrptApi.Product product = new CrptApi.Product("sampleCertificateDocument", "2020-01-23",
                "sampleCertificateDocumentNumber", "sampleOwnerInn", "sampleProducerInn", "2020-01-23",
                "sampleTnvedCode", "sampleUitCode", "sampleUituCode");

        return new CrptApi.Document(description, "sampleDocId", "sampleDocStatus", "LP_INTRODUCE_GOODS",
                true, "sampleOwnerInn", "sampleParticipantInn", "sampleProducerInn", "2020-01-23",
                "sampleProductionType", List.of(product), "2020-01-23", "sampleRegNumber");
    }
}