package tzuhsuan.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import tzuhsuan.model.User;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Authorization {
    // The token will be expired in 2 hours.
    private static final long EXPIRED_TIME = 120L * 60L * 1000L;

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private static final String AES_PRIVATE_KEY = "test";

    public static String generateToken(String name) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userName", name);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(Instant.now().toEpochMilli() + EXPIRED_TIME))
                .signWith(key)
                .compact();
    }

    public static Claims getClaimsInJwt(String jwt) throws JwtException {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
    }

    public static Boolean validateJwt(String jwt, User user) throws JwtException {
        return user.isActive() && jwt.equals(user.getAuthToken()) && user.getName().equals(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody().get("userName"));
    }

    public static String aesEncode(String password) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] result = null;
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(AES_PRIVATE_KEY.getBytes());
        kgen.init(128, random);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] byteContent = password.getBytes();
        result = cipher.doFinal(byteContent);

        return parseByteToHexStr(result);
    }

    public static String aesDecode(String password) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] result = null;
        byte[] content = parseHexStrToByte(password);
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(AES_PRIVATE_KEY.getBytes());
        kgen.init(128, random);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        result = cipher.doFinal(content);

        return new String(result);
    }

    private static String parseByteToHexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    private static byte[] parseHexStrToByte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
