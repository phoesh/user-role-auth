package tzuhsuan.controller;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tzuhsuan.http.Response;
import tzuhsuan.model.Role;
import tzuhsuan.util.JSONUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class UserControllerTest {

    UserController userController = new UserController();
    static Map<String, Object> requestMap = new HashMap();

    static {
        requestMap.put("name", "tzuhsuan");
        requestMap.put("password", "12345678");
        requestMap.put("role", "ADMIN");
        requestMap.put("authToken", "");
    }

    @Test
    public void getTestRequest() throws Exception {
        System.out.println("--- getTestRequest ---");
        System.out.println(JSONUtil.objToJson(requestMap));
    }

    @Before
    public void createUser() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        System.out.println("--- createUser ---");
        String request = JSONUtil.objToJson(requestMap);
        Response response = userController.createUser(request);
        System.out.println(JSONUtil.objToJson(response));

        assert userController.getUser("tzuhsuan").isActive() == true;
        System.out.println(userController.getUser("tzuhsuan"));
        System.out.println(userController.getUserTable());
    }

    @Test
    public void deleteUser() throws IOException {
        System.out.println("--- deleteUser ---");
        String request = JSONUtil.objToJson(requestMap);
        Response response = userController.deleteUser(request);
        System.out.println(JSONUtil.objToJson(response));
        assert userController.getUser("tzuhsuan").isActive() == false;
        System.out.println(userController.getUser("tzuhsuan"));
        System.out.println(userController.getUserTable());
    }

    @Test
    public void createRole() throws Exception {
        System.out.println("--- createRole ---");
        String createRoleRequest = "{\"role\":\"ADMIN\"}";
        Response response = userController.createRole(createRoleRequest);
        System.out.println(JSONUtil.objToJson(response));
        // Show existing roles.
        System.out.println(Role.getRoles());
    }

    @After
    public void createUserAgain() throws Exception {
        System.out.println("--- createUserAgain ---");
        String request = JSONUtil.objToJson(requestMap);
        Response response = userController.createUser(request);
        System.out.println(JSONUtil.objToJson(response));
        assert userController.getUser("tzuhsuan").isActive() == true;
        System.out.println(userController.getUser("tzuhsuan"));
        System.out.println(userController.getUserTable());
    }

    @After
    public void getAuthToken() throws Exception {
        System.out.println("--- getAuthToken ---");
        String request = JSONUtil.objToJson(requestMap);
        Response response = userController.auth(request);
        System.out.println(JSONUtil.objToJson(response));
    }

    @After
    public void getAllRolesAndInvalidateAuthToken() throws Exception {
        System.out.println("--- getAllRolesAndInvalidateAuthToken ---");
        // Create the role "DEV".
        String createRoleRequest = "{\"role\":\"DEV\"}";
        Response response = userController.createRole(createRoleRequest);
        assert response.getStatusCode() == 201 || response.getStatusCode() == 500;
        System.out.println(JSONUtil.objToJson(response));
        // Add role for the user.
        String addUserRoleRequest = "{\"name\":\"tzuhsuan\",\"role\":\"DEV\"}";
        response = userController.addUserRole(addUserRoleRequest);
        assert response.getStatusCode() == 200;
        System.out.println(JSONUtil.objToJson(response));
        String request = JSONUtil.objToJson(requestMap);
        // Generate the token.
        response = userController.auth(request);
        assert StringUtils.isNotBlank(String.valueOf(((Map) response.getData()).get("authToken")));
        System.out.println(JSONUtil.objToJson(response));
        String jwt = JSONUtil.objToJson(response.getData());
        // Have the permission to access all the roles for the user.
        response = userController.getAllRoles(jwt);
        assert response.getStatusCode() == 200;
        System.out.println(JSONUtil.objToJson(response));
        // Invalidate the token.
        response = userController.invalidate(jwt);
        assert response.getStatusCode() == 200;
        System.out.println(JSONUtil.objToJson(response));
        // Use invalid token to get all the roles for the user.
        response = userController.getAllRoles(jwt);
        assert response.getStatusCode() == 403;
        System.out.println(JSONUtil.objToJson(response));
    }

}
