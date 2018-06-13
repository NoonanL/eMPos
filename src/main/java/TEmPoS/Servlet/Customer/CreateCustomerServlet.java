package TEmPoS.Servlet.Customer;

import TEmPoS.Util.RequestJson;
import TEmPoS.db.H2Customer;
import TEmPoS.db.H2User;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

public class CreateCustomerServlet extends HttpServlet {

    private H2Customer h2Customer;
    private H2User h2User;

    public CreateCustomerServlet(){}

    public CreateCustomerServlet(H2Customer h2Customer, H2User h2User){
        this.h2Customer = h2Customer;
        this.h2User = h2User;
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //read from request
        RequestJson requestParser = new RequestJson();
        JSONObject input = requestParser.parse(request);
        String firstname = input.getString("firstname");
        String surname = input.getString("surname");
        String requestUser = input.getString("requestUser");


        JSONObject responseJson = new JSONObject();
        try {
            if(h2User.isRegistered(requestUser)){
                if(h2Customer.createCustomer(firstname,surname)){
                    //System.out.println("New user created.");
                    responseJson.put("response", "OK");
                }else{
                    //System.out.println("Error creating user");
                    responseJson.put("response", "false");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(responseJson);
        out.flush();
    }

    }
