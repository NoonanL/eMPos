package TEmPoS.Servlet.Product;

import TEmPoS.Model.Product;
import TEmPoS.Servlet.Customer.CreateCustomerServlet;
import TEmPoS.Util.RequestJson;
import TEmPoS.db.H2Products;
import TEmPoS.db.H2User;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class CreateProductServlet extends HttpServlet {

    private H2Products h2Products;
    private H2User h2User;

    public CreateProductServlet(){}

    public CreateProductServlet(H2Products h2Products, H2User h2User){
        this.h2Products = h2Products;
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
        Product newProduct = new Product();

        newProduct.setSKU(input.getString("SKU"));
        newProduct.setName(input.getString("name"));
        newProduct.setRRP(input.getDouble("RRP"));
        newProduct.setCost(input.getDouble("cost"));
        newProduct.setDepartment(input.getString("department"));
        newProduct.setBrand(input.getString("brand"));
        newProduct.setDescription(input.getString("description"));

        String requestUser = input.getString("requestUser");

        JSONObject responseJson = new JSONObject();
        if(h2User.isRegistered(requestUser)){

            if(h2Products.createProduct(newProduct)){
                //System.out.println("New product created.");
                responseJson.put("response", "OK");
            }else{
                //System.out.println("Error creating product");
                responseJson.put("response", "false");
            }

        }
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print(responseJson);
        out.flush();
    }

}
