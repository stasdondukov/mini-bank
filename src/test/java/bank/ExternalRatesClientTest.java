package bank;

import bank.adapters.dto.RateDto;
import bank.adapters.http.ExternalRatesClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExternalRatesClientTest {
    private static final String BODY = "{\"currency\":\"EUR\",\"value\":1.25}";
    private HttpServer server;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.start();
    }

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 201, 204})
    void returnsResponseBodyForSuccessfulGet(int status) throws IOException, InterruptedException {
        String url = respondWith(status);

        assertEquals(status == 204 ? "" : BODY, new ExternalRatesClient().get(url));
    }

    @ParameterizedTest
    @ValueSource(ints = {302, 404, 503})
    void rejectsNonSuccessResponses(int status) {
        String url = respondWith(status);

        IllegalStateException error = assertThrows(IllegalStateException.class,
            () -> new ExternalRatesClient().get(url));

        assertEquals("HTTP error: " + status, error.getMessage());
    }

    @Test
    void readsSuccessfulResponseAsRateDto() throws IOException, InterruptedException {
        String url = respondWith(200);

        assertEquals(new RateDto("EUR", 1.25), new ExternalRatesClient().getRate(url));
    }

    @Test
    void reportsMalformedJsonFromSuccessfulResponse() {
        String url = respondWith(200, "not JSON");

        assertThrows(JsonProcessingException.class, () -> new ExternalRatesClient().getRate(url));
    }

    @Test
    void rejectsHttpErrorBeforeParsingTheBody() {
        String url = respondWith(503, "not JSON");

        IllegalStateException error = assertThrows(IllegalStateException.class,
            () -> new ExternalRatesClient().getRate(url));
        assertEquals("HTTP error: 503", error.getMessage());
    }

    private String respondWith(int status) {
        return respondWith(status, BODY);
    }

    private String respondWith(int status, String responseBody) {
        server.createContext("/rates", exchange -> {
            try {
                if (!"GET".equals(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1);
                } else if (status == 204) {
                    exchange.sendResponseHeaders(status, -1);
                } else {
                    byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(status, body.length);
                    exchange.getResponseBody().write(body);
                }
            } finally {
                exchange.close();
            }
        });
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/rates";
    }
}
