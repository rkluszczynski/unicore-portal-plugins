package pl.edu.icm.openoxides

import org.apache.http.client.HttpClient
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.SSLContextBuilder
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.impl.client.HttpClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.boot.test.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import spock.lang.Specification

@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = OpenOxidesWebServer.class)
@WebAppConfiguration
@IntegrationTest('server.port:0')
@DirtiesContext
class OpenOxidesWebServerTest extends Specification {
    @Value('${local.server.port}')
    private int port;

    private TestRestTemplate restTemplate
    private String rootAddress

    def setup() {
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                new SSLContextBuilder().loadTrustMaterial(null,
                        new TrustSelfSignedStrategy()).build());
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory)
                .build();
        restTemplate = new TestRestTemplate();
        ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory())
                .setHttpClient(httpClient);

        rootAddress = "https://localhost:${port}"
    }

    def 'should test home'() {
        when:
        ResponseEntity entity = restTemplate.getForEntity("${rootAddress}/", String.class)

        then:
        entity.getStatusCode() == HttpStatus.OK
        entity.getBody() == 'Hello World'
    }

    def 'should test async'() {
        when:
        ResponseEntity entity = restTemplate.getForEntity("${rootAddress}/async", String.class)

        then:
        entity.getStatusCode() == HttpStatus.OK
        entity.getBody() == 'async: Hello World'
    }
}
