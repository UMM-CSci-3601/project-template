/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package umm3601;

import static spark.Spark.*;

public class Server {
    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) {
        get("/", (req, res) -> "Hello World");
    }
}
