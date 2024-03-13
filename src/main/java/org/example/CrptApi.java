package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.apache.log4j.Priority.INFO;


public class CrptApi {
    private final Logger logger = Logger.getLogger(CrptApi.class);
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private int currentRequests = 0;
    private long lastRequestTime = 0;
    private final Object lock = new Object();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String URL = "https://ismp.crpt.ru/api/v3/lk/documents/create";

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
    }

    public void createDocument(Document document, String signature) throws IOException {
        synchronized (lock) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastRequestTime;

            if (elapsedTime < timeUnit.toMillis(1)) {
                if (currentRequests >= requestLimit) {
                    logger.log(INFO, "Превышен лимит запросов");
                    throw new IOException("Превышен лимит запросов");
                }
            } else {
                currentRequests = 0;
            }
            currentRequests++;
            lastRequestTime = currentTime;
        }


        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(URL);

        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Signature", signature);

        String jsonDocument = objectMapper.writeValueAsString(document);

        StringEntity entity = new StringEntity(jsonDocument);
        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity != null) {
            System.out.println("Response status: " + response.getStatusLine());
            logger.log(INFO, "Response status: " + response.getStatusLine());
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Document {
        @JsonProperty("description")
        private Description description;
        @JsonProperty("doc_id")
        private String docId;
        @JsonProperty("doc_status")
        private String docStatus;
        @JsonProperty("doc_type")
        private String docType;
        @JsonProperty("importRequest")
        private boolean importRequest;
        @JsonProperty("owner_inn")
        private String ownerInn;
        @JsonProperty("participant_inn")
        private String participantInn;
        @JsonProperty("producer_inn")
        private String producerInn;
        @JsonProperty("production_date")
        private String productionDate;
        @JsonProperty("production_type")
        private String productionType;
        @JsonProperty("products")
        private List<Product> products;
        @JsonProperty("reg_date")
        private String regDate;
        @JsonProperty("reg_number")
        private String regNumber;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Description {
        private String participantInn;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Product {
        @JsonProperty("certificate_document")
        private String certificateDocument;
        @JsonProperty("certificate_document_date")
        private String certificateDocumentDate;
        @JsonProperty("certificate_document_number")
        private String certificateDocumentNumber;
        @JsonProperty("owner_inn")
        private String ownerInn;
        @JsonProperty("producer_inn")
        private String producerInn;
        @JsonProperty("production_date")
        private String productionDate;
        @JsonProperty("tnved_code")
        private String tnvedCode;
        @JsonProperty("uit_code")
        private String uitCode;
        @JsonProperty("uitu_code")
        private String uituCode;
    }
}
