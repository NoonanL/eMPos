package TEmPoS.db;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Iterator;

import static org.junit.Assert.*;

public class H2DepartmentsTest {

    static final Logger LOG = LoggerFactory.getLogger(H2CustomerTest.class);

    private H2Departments db;

    @Before
    public void setUp() {
        db = new H2Departments(new ConnectionSupplier(ConnectionSupplier.FILE));
    }

    @After
    public void tearDown() {
        try {
            db.close();
        } catch (SQLException e) {
            fail();
        }
    }

    @Test
    public void createDepartment() throws SQLException {
        System.out.println("=====================================");
        System.out.println("Creating Departments");
        System.out.println("=====================================");
        if(db.createDepartment("Sticks") &&
                db.createDepartment("Heads") &&
                db.createDepartment("Cymbals")
                ){
            System.out.println("Departments successfuly added");
        }else{
            System.out.println("Department creation failed failed");
        }
    }

    @Test
    public void getDepartments() {
        System.out.println("=====================================");
        System.out.println("Getting Departments");
        System.out.println("=====================================");
        JSONObject branches = db.getDepartments();
        for (Iterator it = branches.keys(); it.hasNext(); ) {
            System.out.println(branches.getString(it.next().toString()));
        }
    }

    @Test
    public void editDepartment() throws SQLException {
        System.out.println("=====================================");
        System.out.println("Testing Department editing");
        System.out.println("=====================================");
        if(db.editDepartment(1, "NEWNAME")){
            System.out.println("Department successfully edited.");
            System.out.println(db.getDepartments());
        }else{
            System.out.println("Failed to edit Department.");
        }
    }

    @Test
    public void deleteDeparment() {
        System.out.println("=====================================");
        System.out.println("Testing delete Department by id");
        System.out.println("=====================================");
        if(db.deleteDeparment(1)){
            System.out.println("Department successfully deleted");
        }else{
            System.out.println("Failed to delete Department");
        }

    }


}