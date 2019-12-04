package umm3601.employee;

import java.time.LocalDate;
import java.util.ArrayList;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import org.bson.Document;
import org.bson.types.ObjectId;

import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.json.JavalinJson;
import io.javalin.plugin.openapi.annotations.*;

public class EmployeeController implements CrudHandler {

    static String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";


    private MongoCollection<Employee> employeeCollection;

    public EmployeeController(MongoCollection<Employee> employeeCollection) {
        this.employeeCollection = employeeCollection;
    }

    @OpenApi(
        summary = "Create employee",
        description = "Create a new employee",
        responses = {
            @OpenApiResponse(status = "200"),
            @OpenApiResponse(status = "400")
        },
        requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Employee.class))
    )
    @Override
    public void create(Context ctx) {
        Employee newEmployee = ctx.bodyValidator(Employee.class)
            .check((obj) -> obj.name != null && obj.name != "")
            .check((obj) -> obj.emailAddress.matches(emailRegex))
            .get();

        employeeCollection.insertOne(newEmployee);
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
        queryParams = {
            @OpenApiParam(name = "sortby", description = "The attribute to sort by, defaults to name"),
            @OpenApiParam(name = "sortasc", description = "If true, sorted in ascending order, false descending. If not present, it will not be sorted", type = Boolean.class)
        },
        responses = @OpenApiResponse(status = "200", 
        content = @OpenApiContent(from = Employee.class, isArray = true))
    )
    @Override
    public void getAll(Context ctx) {
        String sortBy = ctx.queryParam("sortby", "name"); //Sort by sort query param, default is name
        Boolean sortAsc = ctx.queryParam("sortasc", Boolean.class).getOrNull();

        ctx.json(employeeCollection.find()
            .sort(sortAsc == null ? null : sortAsc ? Sorts.ascending(sortBy) : Sorts.descending(sortBy))
            .projection(Projections.exclude("skills")).into(new ArrayList<>()));
    }

    @OpenApi(
        summary = "Get employee",
        description = "Get the record for a single employee",
        pathParams = @OpenApiParam(name = "id"),
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = Employee.class)),
            @OpenApiResponse(status = "404")
        }
    )
    @Override
    public void getOne(Context ctx, String resourceId) {
        Employee employee;
        try {
            employee = employeeCollection.find(eq("_id", new ObjectId(resourceId))).first();
        } catch(IllegalArgumentException e) {
            throw new BadRequestResponse();
        }
        if (employee == null) throw new NotFoundResponse();
        else ctx.json(employee);
    }

    @Override
    public void update(Context ctx, String resourceId) {
        
    }
    
}