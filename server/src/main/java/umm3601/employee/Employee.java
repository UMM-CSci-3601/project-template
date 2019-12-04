package umm3601.employee;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.mongojack.Id;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

public class Employee {
    
    @Id
    public String id;
    
    public String name;
    public String emailAddress;
    public long salary = 0;
    //public LocalDate hiredDate;
    @JsonInclude(Include.NON_NULL)
    public String[] skills;
    
}
