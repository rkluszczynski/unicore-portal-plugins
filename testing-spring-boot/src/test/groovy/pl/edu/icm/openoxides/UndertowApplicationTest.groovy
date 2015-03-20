package pl.edu.icm.openoxides

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification


@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = UndertowApplication.class)
@WebAppConfiguration
@IntegrationTest('server.port:0')
@DirtiesContext
class UndertowApplicationTest extends Specification {
    @Value('${local.server.port}')
    private int port;

    void 'should test home'() {
        when:
        ResponseEntity entity = new RestTemplate().getForEntity("http://localhost:${port}/", String.class)

        then:
        entity.getStatusCode() == HttpStatus.OK
        entity.getBody() == 'Hello World'
    }

    def 'should test async'() {
        when:
        ResponseEntity entity = new RestTemplate().getForEntity("http://localhost:${this.port}/async", String.class)

        then:
        entity.getStatusCode() == HttpStatus.OK
        entity.getBody() == 'async: Hello World'
    }
}
