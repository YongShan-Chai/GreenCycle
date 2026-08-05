package data;

/**
 * Session — static holder for the currently logged-in user.
 *
 * Lecture reference: Topic 1 — static variables and methods
 * All members use this class to know who is logged in.
 */
public class Session {

    // Topic 1: static field shared across all references
    private static User currentUser = null;

    /** Saves the logged-in user. Called on successful login. */
    public static void login(User user)   { currentUser = user; }

    /** Clears the session. Called on logout. */
    public static void logout()           { currentUser = null; }

    /** Returns the current User object, or null if not logged in. */
    public static User getCurrentUser()   { return currentUser; }

    /** Returns true if a user is currently logged in. */
    public static boolean isLoggedIn()    { return currentUser != null; }

    /** Returns true if the logged-in user is an admin. */
    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    /** Returns the resident ID linked to the logged-in user, or null. */
    public static String getLinkedResidentId() {
        return currentUser != null ? currentUser.getLinkedResidentId() : null;
    }

    /** Returns the logged-in user's username for display. */
    public static String getDisplayName() {
        return currentUser != null ? currentUser.getUsername() : "Guest";
    }
}
