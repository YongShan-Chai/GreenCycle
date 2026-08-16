package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * DataStore — shared in-memory data layer for all modules.
 * Contains three ArrayLists (users, residents, bookings) plus
 * validation helper methods used across all form screens.
 */
public class DataStore {

    // static ArrayLists shared by all modules
    public static ArrayList<User>     users     = new ArrayList<User>();
    public static ArrayList<Resident> residents = new ArrayList<Resident>();
    public static ArrayList<Booking>  bookings  = new ArrayList<Booking>();

    private static int bookingCounter = 1;
    public static int nextResidentIdNumber = 1;
    // ── Lookup helpers ────────────────────────────────────────────────────────

    /** Finds a User by username + password. Returns null if not matched. */
    public static User authenticate(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username.trim())
                    && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    /** Generates a new Resident ID. */
    public static String generateResidentId() {
        return String.format("R%03d", nextResidentIdNumber++);
    }

    /** Initializes the resident ID counter based on existing residents. */
    public static void initResidentCounter() {
        int maxId = 0;
        for (Resident r : residents) {
            if (r.getId() != null && r.getId().startsWith("R")) {
                try {
                    int currentNum = Integer.parseInt(r.getId().substring(1));
                    if (currentNum > maxId) {
                        maxId = currentNum;
                    }
                } catch (Exception e) {
                    // Ignore IDs that don't match the expected format
                }
            }
        }
        nextResidentIdNumber = maxId + 1;
    }

    /** Finds a Resident by ID (case-insensitive). Returns null if not found. */
    public static Resident findResidentById(String id) {
        for (Resident r : residents) {
            if (r.getId().equalsIgnoreCase(id.trim())) return r;
        }
        return null;
    }

    /** Returns true if a Resident with the given ID already exists. */
    public static boolean isResidentIdTaken(String id) {
        return findResidentById(id) != null;
    }

    /** Returns true if a User with the given username already exists. */
    public static boolean isUsernameTaken(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username.trim())) return true;
        }
        return false;
    }

    /** Generates the next sequential Booking ID, e.g. BK008. */
    public static String generateBookingId() {
        return String.format("BK%03d", bookingCounter++);
    }
    
    /**
     * Restores the booking counter when loading from file.
     * Called by FileHandler after reading all bookings.
     */
    public static void setBookingCounter(int value) {
        bookingCounter = value;
    }

    // ── Points & tier helpers ─────────────────────────────────────────────────

    /** Sums all points earned from completed bookings for a resident. */
    public static int getPointsForResident(String residentId) {
        int total = 0;
        for (Booking b : bookings) {
            if (b.getResidentId().equalsIgnoreCase(residentId)
                    && "Completed".equals(b.getStatus())) {
                total += b.getPoints();
            }
        }
        return total;
    }

    /** Returns tier label based on points. */
    public static String getTier(int pts) {
        if (pts >= 100) return "Eco Champion";
        if (pts >= 40)  return "Recycler";
        return "Starter";
    }

    /** Returns tier colour hex for display. */
    public static String getTierColor(int pts) {
        if (pts >= 100) return "#E65100";
        if (pts >= 40)  return "#1565C0";
        return "#2E7D32";
    }

    /** Returns the waste category with the most bookings. */
    public static String getMostPopularCategory() {
        Map<String, Integer> counts = new HashMap<String, Integer>();
        for (Booking b : bookings) {
            String cat = b.getWasteCategory();
            counts.put(cat, counts.containsKey(cat) ? counts.get(cat) + 1 : 1);
        }
        String top = "N/A"; int max = 0;
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            if (e.getValue() > max) { max = e.getValue(); top = e.getKey(); }
        }
        return top;
    }

    // ── Validation methods — throw AppException ────────────────────
    //Validates a Resident ID — throws AppException on failure.
    public static void validateResidentId(String id) throws AppException {
        if (id == null || id.trim().isEmpty()) {
            throw new AppException("Resident ID cannot be empty.");
        }
        if (isResidentIdTaken(id)) {
            throw new AppException("Resident ID \"" + id.trim() +
                    "\" is already taken. Please use a different ID.");
        }
    }

    //Validates a full name — must be non-empty，at least 3 characters and only letters/spaces.
    public static void validateName(String name) throws AppException {
        validateNotEmpty(name, "Full Name");
        if (name.trim().length() < 3) {
            throw new AppException("Full Name must be at least 3 characters long.");
        }
        if (!name.trim().matches("^[a-zA-Z\\s]+$")) {
            throw new AppException("Full Name can only contain letters and spaces.");
        }
    }

    //Validates a phone number — must be 10 or 11 digits.
    public static void validatePhone(String phone) throws AppException {
        if (phone == null || phone.trim().isEmpty()) {
            throw new AppException("Phone number cannot be empty.");
        }
        if (!phone.trim().matches("\\d{10,11}")) {
            throw new AppException(
                "Phone number must be 10 or 11 digits (numbers only, no spaces or dashes).");
        }
    }

    //Validates a username — must be non-empty and not already taken.
    public static void validateUsername(String username) throws AppException {
        if (username == null || username.trim().isEmpty()) {
            throw new AppException("Username cannot be empty.");
        }
        if (username.trim().length() < 3) {
            throw new AppException("Username must be at least 3 characters long.");
        }
        if (isUsernameTaken(username)) {
            throw new AppException("Username \"" + username.trim() +
                    "\" is already taken. Please choose another.");
        }
    }

    //Validates a password — must be at least 6 characters and match confirmation.
    public static void validatePassword(String password, String confirm) throws AppException {
        if (password == null || password.isEmpty()) {
            throw new AppException("Password cannot be empty.");
        }
        if (password.length() < 6) {
            throw new AppException("Password must be at least 6 characters long.");
        }
        if (!password.equals(confirm)) {
            throw new AppException("Passwords do not match. Please re-enter.");
        }
    }

    //Validates that a required text field is not empty.
    public static void validateNotEmpty(String value, String fieldName) throws AppException {
        if (value == null || value.trim().isEmpty()) {
            throw new AppException(fieldName + " cannot be empty.");
        }
    }
}
