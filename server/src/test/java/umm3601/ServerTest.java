/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package umm3601;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    @Test void serverHasAGreeting() {
        Server classUnderTest = new Server();
        assertNotNull(classUnderTest.getGreeting(), "server should have a greeting");
    }
}
