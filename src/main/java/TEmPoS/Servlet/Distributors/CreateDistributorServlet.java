package TEmPoS.Servlet.Distributors;

import TEmPoS.Model.Brand;
import TEmPoS.Util.RequestJson;
import TEmPoS.Util.ValidationFilter;
import TEmPoS.db.H2Brands;
import TEmPoS.db.H2Distributors;
import TEmPoS.db.H2User;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateDistributorServlet extends HttpServlet {

    private H2Distributors h2Distributors;
    private H2User h2User;
    private Map<String, String> requiredParams = new HashMap<>();

    public CreateDistributorServlet(){}

    public CreateDistributorServlet(H2Distributors h2Distributors, H2User h2User){
        this.h2Distributors = h2Distributors;
        this.h2User = h2User;

        requiredParams.put("distributor", "String");
        requiredParams.put("requestUser", "String");

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //read from request
        RequestJson requestParser = new RequestJson();
        JSONObject input = requestParser.parse(request);
        JSONObject responseJson = new JSONObject();

        ValidationFilter inputChecker = new ValidationFilter(requiredParams, input);

        if(inputChecker.isValid()) {

            String requestUser = input.getString("requestUser");

            if (h2User.isRegistered(requestUser)) {

                if (h2Distributors.createDistributor(input.getString("distributor"))) {
                    responseJson.put("response", "OK");
                    responseJson.put("error", "None.");
                } else {
                    //System.out.println("Error creating product");
                    responseJson.put("response", "false");
                    responseJson.put("error", "Failed to create new distributor.");
                }
            }
        }else{
            responseJson.put("response", "false");
            responseJson.put("error", "Missing required fields.");
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(responseJson);
        out.flush();
    }


}
