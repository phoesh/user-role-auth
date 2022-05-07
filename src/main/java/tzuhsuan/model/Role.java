package tzuhsuan.model;

import java.util.HashSet;
import java.util.Set;

public class Role {
    private static final Set<String> ROLES = new HashSet<>();

    public static boolean addRole(String role) {
        return ROLES.add(role);
    }

    public static boolean removeRole(String role) {
        return ROLES.remove(role);
    }

    public static Set<String> getRoles() {
        return ROLES;
    }

    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

