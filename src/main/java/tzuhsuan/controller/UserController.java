package tzuhsuan.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tzuhsuan.http.Response;
import tzuhsuan.http.annotation.Controller;
import tzuhsuan.http.annotation.RequestMapping;
import tzuhsuan.model.Role;
import tzuhsuan.model.User;
import tzuhsuan.security.Authorization;
import tzuhsuan.util.JSONUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Controller
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private static final AtomicLong id = new AtomicLong(1);
    private static final ConcurrentHashMap<String, User> userData = new ConcurrentHashMap();

    public Map<String, User> getUserTable() {
        return userData;
    }

    public User getUser(String name) {
        return userData.get(name);
    }

    @RequestMapping(endpoint = "/user/create", method = "POST")
    public Response createUser(String request) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Map<String, Object> requestMap = JSONUtil.jsonToMap(request);

        String userName = String.valueOf(requestMap.get("name"));
        String password = String.valueOf(requestMap.get("password"));
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            return new Response().setStatusCode(400).setToast("User name or Password is Empty.");
        }
        if (password.length() < 8) {
            return new Response().setStatusCode(400).setToast("Password is less than 8.");
        }
        User user = userData.get(userName);

        if (null != user && !user.isActive() && password.equals(Authorization.aesDecode(user.getPassword()))) {
            user.setActive(true);
            userData.put(userName, user);
            LOG.info("Re-activate user {}", user);
            return new Response<>().setStatusCode(200).setToast("Re-activate the user.");
        } else if (null != user) {
            LOG.warn("The username is used. {}", user);
            return new Response<>().setStatusCode(403).setToast("The username is used.");
        }
        user = new User();
        password = Authorization.aesEncode(password);
        String userId = String.valueOf(id.get());
        user.setName(userName);
        user.setPassword(password);
        user.setId(userId);
        user.setActive(true);
        userData.put(userName, user);
        LOG.info("Created user {}", user);
        id.addAndGet(1L);
        return new Response<>().setToast("Create the user successfully.");
    }

    @RequestMapping(endpoint = "/user/delete_user", method = "POST")
    public Response deleteUser(String request) throws IOException {
        Map<String, Object> requestMap = JSONUtil.jsonToMap(request);
        Response response = new Response();
        String userName = String.valueOf(requestMap.get("name"));
        if (StringUtils.isBlank(userName)) {
            return response.setStatusCode(400).setToast("User name is Empty.");
        }

        // Soft deletion.
        if (null != userData.get(userName)) {
            User user = userData.get(userName);
            user.setActive(false);
            userData.put(userName, user);
            LOG.error("Deleted the user {}", userData.get(userName));
            response.setStatusCode(204);
        } else {
            response.setStatusCode(404);
            LOG.error("The user {} does not exist.", userName);
            response.setToast("The user does not exist.");
        }
        return response;
    }

    @RequestMapping(endpoint = "/user/add_user_role", method = "POST")
    public Response addUserRole(String request) throws IOException {
        Map<String, Object> requestMap = JSONUtil.jsonToMap(request);
        String userName = String.valueOf(requestMap.get("name"));
        String role = String.valueOf(requestMap.get("role"));
        Response response = new Response();
        if (null != userData.get(userName) && userData.get(userName).isActive()) {
            User user = userData.get(userName);
            user.getRoles().add(role);
            userData.put(userName, user);
            LOG.info("Add role {} to the user {}.", role, userName);
        } else {
            LOG.error("The user {} does not exist.", userName);
            response.setStatusCode(404).setToast("The user does not exist.");
        }
        return response;
    }

    @RequestMapping(endpoint = "/user/delete_role", method = "POST")
    public Response deleteRole(String request) throws IOException {
        Map<String, Object> requestMap = JSONUtil.jsonToMap(request);
        String role = String.valueOf(requestMap.get("role"));
        boolean isRemoved = Role.removeRole(role);
        Response response = new Response();
        if (isRemoved) {
            LOG.info("The role {} is removed.", role);
            response.setStatusCode(204);
        } else {
            LOG.error("The role {} does not exist.", role);
            response.setToast("The role does not exist.");
            response.setStatusCode(404);
        }
        return response;
    }

    @RequestMapping(endpoint = "/user/create_role", method = "POST")
    public Response createRole(String request) throws IOException {
        Map<String, Object> requestMap = JSONUtil.jsonToMap(request);
        String role = String.valueOf(requestMap.get("role"));
        boolean isAdded = Role.addRole(role);
        Response response = new Response();
        if (isAdded) {
            LOG.info("Created the role {}.", role);
            response.setStatusCode(201);
        } else {
            LOG.error("Failed to create the role {}.", role);
            response.setStatusCode(500).setToast("Error.");
        }
        return response;
    }

    @RequestMapping(endpoint = "/user/auth", method = "POST")
    public Response<String> auth(String request) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Map<String, Object> requestMap = JSONUtil.jsonToMap(request);
        String userName = String.valueOf(requestMap.get("name"));
        String password = String.valueOf(requestMap.get("password"));
        User user = userData.get(userName);
        if (null != user) {
            if (!user.isActive()) {
                LOG.error("The user {} is removed.", userName);
                return new Response<>().setStatusCode(404).setToast("The user does not exist.");
            }
            if (password.equals(Authorization.aesDecode(userData.get(userName).getPassword()))) {
                String authToken = Authorization.generateToken(userName);
                user.setAuthToken(authToken);
                userData.put(userName, user);
                Map<String, String> data = new HashMap<>();
                data.put("authToken", authToken);
                LOG.info("User {} login successfully.", userName);
                return new Response<>().setData(data);
            }
        }
        LOG.error("Illegal request {}.", request);
        return new Response<>().setStatusCode(403).setToast("Permission Denied.");
    }

    @RequestMapping(endpoint = "/user/invalidate_token", method = "POST")
    public Response invalidate(String request) throws IOException {
        Map<String, Object> requestMap = JSONUtil.jsonToMap(request);
        String authToken = String.valueOf(requestMap.get("authToken"));
        if (StringUtils.isBlank(authToken)) {
            LOG.error("The token is empty.");
            return new Response();
        }
        try {
            Claims body = Authorization.getClaimsInJwt(authToken);
            String userName = String.valueOf(body.get("userName"));
            if (StringUtils.isNotBlank(userName) && null != userData.get(userName)) {
                User user = userData.get(userName);
                user.setAuthToken("");
                userData.put(userName, user);
                LOG.info("Invalidated {}'s the token.", userName);
            }
        } catch (JwtException e) {
            LOG.error("Illegal token {}.", authToken);
        }
        return new Response<>();
    }

    @RequestMapping(endpoint = "/user/check_role", method = "POST")
    public Response<Boolean> checkRole(String request) throws IOException {
        Map<String, Object> requestMap = JSONUtil.jsonToMap(request);
        String authToken = String.valueOf(requestMap.get("authToken"));
        Claims body = Authorization.getClaimsInJwt(authToken);
        String userName = String.valueOf(body.get("userName"));
        User user = userData.get(userName);
        if (null != user && !Authorization.validateJwt(authToken, user)) {
            LOG.error("Illegal token {}.", authToken);
            return new Response<>().setStatusCode(403).setToast("Permission Denied");
        }
        String role = String.valueOf(requestMap.get("role"));

        if (null != user) {
            return new Response<Boolean>().setData(user.getRoles().contains(role));
        }

        return new Response<Boolean>().setData(false);
    }

    @RequestMapping(endpoint = "/user/all_user_roles", method = "POST")
    public Response getAllRoles(String request) throws IOException {
        Map<String, Object> requestMap = JSONUtil.jsonToMap(request);
        String authToken = String.valueOf(requestMap.get("authToken"));
        Claims body = Authorization.getClaimsInJwt(authToken);
        String userName = String.valueOf(body.get("userName"));
        User user = userData.get(userName);
        if (null == user || null != user && !Authorization.validateJwt(authToken, user)) {
            LOG.error("Illegal token {}.", authToken);
            return new Response<>().setStatusCode(403).setToast("Permission Denied");
        }

        Map<String, Set<String>> data = new HashMap<>();
        data.put("roles", user.getRoles());
        LOG.info("Get all the roles for the user {}.", userName);
        return new Response<>().setData(data);
    }

}
