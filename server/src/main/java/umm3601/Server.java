package umm3601;

import java.util.Arrays;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.mongojack.JacksonCodecRegistry;

import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.ReDocOptions;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;

import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.path;

import umm3601.employee.Employee;
import umm3601.employee.EmployeeController;


public class Server {

    static String appName = "Project Template";
    static String appVersion = "0.0.1";

    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) {

        String mongoAddr = System.getenv().getOrDefault("MONGO_ADDR", "localhost");
        String databaseName = System.getenv().getOrDefault("MONGO_DB", "dev");
        
        MongoClient mongoClient = MongoClients.create(
            MongoClientSettings.builder()
                    .applyToClusterSettings(builder ->
                            builder.hosts(Arrays.asList(new ServerAddress(mongoAddr))))
                    .build());

        MongoDatabase database = mongoClient.getDatabase(databaseName);

        Javalin app = Javalin.create(config -> {
            config.registerPlugin(new OpenApiPlugin(new OpenApiOptions(new Info().version(appName).description(appName))
            .path("/api/swagger")
            .swagger(new SwaggerOptions("/api/docs").title(appName + " API Documentation"))
            .activateAnnotationScanningFor("umm3601")));
        }).start(4567);

        JacksonCodecRegistry jacksonCodecRegistry = JacksonCodecRegistry.withDefaultObjectMapper();

        // Utility routes
        app.get("/api", ctx -> ctx.result(appName + " API version " + appVersion));
        app.get("/api/mongo", ctx -> ctx.result(database.runCommand(new BsonDocument("buildinfo", new BsonString(""))).toJson()));


        jacksonCodecRegistry.addCodecForClass(Employee.class);
        MongoCollection<Employee> employeeCollection = database.getCollection("employees").withDocumentClass(Employee.class).withCodecRegistry(jacksonCodecRegistry);

        app.routes(() -> {
            crud("/api/employees/:id", new EmployeeController(employeeCollection));
        });

        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(e);
        });
    
    }
}
