package data;

/**
 * User — a login account for the GreenCycle system.
 * Roles: "admin" (full access) | "user" (resident portal)
 */
public class User extends Person {

    private String password;
    private String role;            // "admin" or "user"
    private String linkedResidentId; // links user account to a Resident record

    //The username is stored as both the id and name in the parent.
    public User(String username, String password, String role, String linkedResidentId) {
        super(username, username);  // Topic 2: call Person constructor
        this.password         = password;
        this.role             = role;
        this.linkedResidentId = linkedResidentId;
    }

    // getters
    public String getUsername()         { return getId(); }
    public String getPassword()         { return password; }
    public String getRole()             { return role; }
    public String getLinkedResidentId() { return linkedResidentId; }

    // setters
    public void setPassword(String password)                   { this.password = password; }
    public void setLinkedResidentId(String linkedResidentId)   { this.linkedResidentId = linkedResidentId; }

    /** Returns true if this account has admin privileges. */
    public boolean isAdmin() { return "admin".equals(role); }

    /**
     * @Override: User-specific summary string.
     * Calls super.getSummary() then appends role info.
     */
    @Override
    public String getSummary() {
        return "Username: " + getId() + " | Role: " + role +
               (linkedResidentId != null ? " | ResID: " + linkedResidentId : "");
    }
}
