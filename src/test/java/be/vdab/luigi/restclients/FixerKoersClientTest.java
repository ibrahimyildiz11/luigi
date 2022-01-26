package be.vdab.luigi.restclients;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@PropertySource("application.properties")
@Import(FixerKoersClient.class) //Spring maakt een FixerKoersClient bean in de IOC container. Dit is de te testen bean.
                                // Verwijder de method beforeEach.
class FixerKoersClientTest {
    private final FixerKoersClient client;

    FixerKoersClientTest(FixerKoersClient client) {
        this.client = client;
    }

    /*    @BeforeEach
        void beforeEach() {
            client = new FixerKoersClient();
        }*/
    @Test
    void deKoersIsPositief() {
        assertThat(client.getDollarKoers()).isPositive();
    }
}