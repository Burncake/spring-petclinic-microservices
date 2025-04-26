package org.springframework.samples.petclinic.visits;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test class for {@link VisitsServiceApplication} to ensure the Spring context loads.
 */
@SpringBootTest // Loads the full application context
@ActiveProfiles("test") // Activate the test profile for configuration (e.g., disable Eureka, use HSQLDB)
class VisitsServiceApplicationTests {

    @Test
    void contextLoads() {
        // If this test runs without exceptions, it means the Spring application context
        // loaded successfully, exercising the @SpringBootApplication class.
    }

}