package umm3601.employee;

import java.time.LocalDate;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import org.bson.Document;
import org.bson.types.ObjectId;

import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import io.javalin.plugin.json.JavalinJson;
import io.javalin.plugin.openapi.annotations.*;

public class EmployeeController implements CrudHandler {

    static String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";


    private final MongoCollection<Document> employeeCollection;

    public EmployeeController(MongoDatabase database) {
        employeeCollection = database.getCollection("employees");
    }

    @OpenApi(
        summary = "Create employee",
        description = "Create a new employee",
        responses = @OpenApiResponse(status = "200"),
        requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Employee.class))
    )
    @Override
    public void create(Context ctx) {
        ctx.bodyValidator(Employee.class)
        .check((obj) -> obj.name != null && obj.name != "")
        .check((obj) -> obj.emailAddress.matches(emailRegex));

        Document newDoc = Document.parse(ctx.body());

        employeeCollection.insertOne(newDoc);
    }

    @OpenApi(
        summary = "Delete employee",
        description = "Delete an employee by ID",
        pathParams = @OpenApiParam(name = "id"),
        responses = @OpenApiResponse(status = "200")
    )
    @Override
    public void delete(Context ctx, String resourceId) {
        employeeCollection.deleteOne(eq("_id", new ObjectId(resourceId)));

    }
    
    @OpenApi(
        summary = "Get all employees",
        description = "Get an array of all employees",
        responses = @OpenApiResponse(status = "200", 
        content = @OpenApiContent(from = Employee.class, isArray = true))
    )
    @Override
    public void getAll(Context ctx) {
        ctx.json(employeeCollection.find());
    }

    @OpenApi(
        summary = "Get employee",
        description = "Get the record for a single employee",
        pathParams = @OpenApiParam(name = "id"),
        responses = @OpenApiResponse(status = "200", content = @OpenApiContent(from = Employee.class))
    )
    @Override
    public void getOne(Context ctx, String resourceId) {
        ctx.result(employeeCollection.find(eq("_id", new ObjectId(resourceId))).first().toJson());

    }

    @Override
    public void update(Context ctx, String resourceId) {
        
    }
    
}