package tzuhsuan.security;

import io.jsonwebtoken.Claims;
import org.junit.Test;
import tzuhsuan.model.User;

public class AuthorizationTest {

    static User user = new User();
    static {
        user.setId("1");
        user.setName("tzuhsuan");
        user.setActive(true);
    }

    @Test
    public void aesTest() throws Exception {
        String password = "12345678";
        String encode = Authorization.aesEncode(password);
        user.setPassword(encode);
        System.out.println(encode);
        String decode = Authorization.aesDecode(encode);
        System.out.println(decode);
    }

    @Test
    public void generateJwt() throws Exception {
        String jwt = Authorization.generateToken("tzuhsuan");
        user.setAuthToken(jwt);
        System.out.println(jwt);
    }

    @Test
    public void getClaims() {
        String jwt = Authorization.generateToken("tzuhsuan");
        Claims claims = Authorization.getClaimsInJwt(jwt);
        System.out.println(claims);
    }

    @Test
    public void validateJwt() throws Exception {
        String jwt = Authorization.generateToken("tzuhsuan");
        user.setAuthToken(jwt);
        System.out.println(user);
        System.out.println(Authorization.validateJwt(user.getAuthToken(), user));
    }

}
