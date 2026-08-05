package data;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * FileHandler — MEMBER 4's file handling module.
 *
 * Saves and loads all application data to/from plain text files
 * so data persists between app sessions.
 *
 * Lecture reference:
 *   File class lecture — File, BufferedReader, BufferedWriter,
 *                        FileReader, FileWriter, PrintWriter
 *   Topic 7            — try-catch for IOException,
 *                        multiple catch blocks, finally
 *
 * File structure (greencycle-data/ folder):
 *   users.txt     — one user account per line
 *   residents.txt — one resident record per line
 *   bookings.txt  — one booking record per line
 *
 * CSV format (comma-separated fields):
 *   users.txt     : username,password,role,linkedResidentId
 *   residents.txt : id,name,unit,phone,wasteType1;wasteType2
 *   bookings.txt  : bookingId,residentId,residentName,date,
 *                   timeSlot,wasteCategory,collectionPoint,status,points
 *
 * Note: waste types use semicolon (;) as separator inside their field
 *       so they don't conflict with the comma (,) CSV separator.
 *       null values are written as the literal string "null".
 */
public class FileHandler {

    // ── File path constants ───────────────────────────────────────────────────
    private static final String DATA_DIR       = "greencycle-data";
    private static final String USERS_FILE     = DATA_DIR + "/users.txt";
    private static final String RESIDENTS_FILE = DATA_DIR + "/residents.txt";
    private static final String BOOKINGS_FILE  = DATA_DIR + "/bookings.txt";

    // ── Load all data from files (called in Main.java on app start) ───────────
    /**
     * Loads users, residents and bookings from text files into DataStore.
     * If files do not exist (first run), creates them with sample data.
     *
     * Lecture — File class: new File(path), dir.exists(), dir.mkdir()
     * Topic 7 — IOException caught separately from other exceptions
     */
    public static void loadAll() {
        // File class — check if data directory exists
        File dir = new File(DATA_DIR);

        if (!dir.exists()) {
            // First run — create directory and populate with sample data
            dir.mkdir();
            insertSampleData();
            saveAll();
            return;
        }

        // File class — check each individual file
        File usersFile     = new File(USERS_FILE);
        File residentsFile = new File(RESIDENTS_FILE);
        File bookingsFile  = new File(BOOKINGS_FILE);

        if (!usersFile.exists() || !residentsFile.exists() || !bookingsFile.exists()) {
            // One or more files missing — recreate with sample data
            insertSampleData();
            saveAll();
            return;
        }

        // All files exist — load them
        loadUsers();
        loadResidents();
        loadBookings();

        System.out.println("FileHandler: Data loaded successfully.");
        System.out.println("  Users    : " + DataStore.users.size());
        System.out.println("  Residents: " + DataStore.residents.size());
        System.out.println("  Bookings : " + DataStore.bookings.size());
    }

    // ── Save all data to files (called in Main.java on app close) ─────────────
    /**
     * Writes all DataStore data to text files.
     * Called when the user closes the application window.
     *
     * Lecture — File class: new File(path), dir.exists(), dir.mkdir()
     * Topic 7 — IOException caught in each save method
     */
    public static void saveAll() {
        // Ensure directory exists before writing
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdir();

        saveUsers();
        saveResidents();
        saveBookings();

        System.out.println("FileHandler: Data saved successfully.");
    }

    // ── Load users from users.txt ─────────────────────────────────────────────
    /**
     * Reads users.txt line by line using BufferedReader.
     * Lines starting with # are comments and are skipped.
     *
     * Lecture — BufferedReader, FileReader, readLine()
     * Topic 7 — IOException caught, NumberFormatException caught separately
     */
    private static void loadUsers() {
        DataStore.users.clear();
        BufferedReader reader = null;

        try {
            // File class lecture — FileReader wraps the File path
            reader = new BufferedReader(new FileReader(USERS_FILE));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip empty lines and comment lines
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Split by comma — limit -1 keeps trailing empty fields
                String[] parts = line.split(",", -1);
                if (parts.length < 4) continue;  // skip malformed lines

                String username         = parts[0].trim();
                String password         = parts[1].trim();
                String role             = parts[2].trim();
                // "null" literal → actual null for linkedResidentId
                String linkedResidentId = "null".equals(parts[3].trim())
                    ? null : parts[3].trim();

                DataStore.users.add(
                    new User(username, password, role, linkedResidentId));
            }

        } catch (IOException e) {
            // Topic 7 — catch IOException separately from other exceptions
            System.out.println("FileHandler: Error reading users.txt — " +
                e.getMessage());

        } catch (Exception e) {
            // Topic 7 — catch any other unexpected exception
            System.out.println("FileHandler: Unexpected error loading users — " +
                e.getMessage());

        } finally {
            // Topic 7 — finally: always close the reader
            if (reader != null) {
                try { reader.close(); }
                catch (IOException e) {
                    System.out.println("FileHandler: Error closing users reader.");
                }
            }
        }
    }

    // ── Load residents from residents.txt ─────────────────────────────────────
    /**
     * Reads residents.txt line by line.
     * Waste types field uses semicolon separator (e.g. "Paper;Plastic").
     *
     * Lecture — BufferedReader, FileReader, readLine(), String.split()
     * Topic 7 — try-catch-finally, IOException caught
     */
    private static void loadResidents() {
        DataStore.residents.clear();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(RESIDENTS_FILE));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 5) continue;

                String id    = parts[0].trim();
                String name  = parts[1].trim();
                String unit  = parts[2].trim();
                String phone = parts[3].trim();

                // Waste types stored as semicolon-separated within field
                String[] types = parts[4].trim().split(";");
                ArrayList<String> wasteTypes = new ArrayList<String>(
                    Arrays.asList(types));

                DataStore.residents.add(
                    new Resident(id, name, unit, phone, wasteTypes));
            }

        } catch (IOException e) {
            System.out.println("FileHandler: Error reading residents.txt — " +
                e.getMessage());

        } catch (Exception e) {
            System.out.println("FileHandler: Unexpected error loading residents — " +
                e.getMessage());

        } finally {
            if (reader != null) {
                try { reader.close(); }
                catch (IOException e) {
                    System.out.println("FileHandler: Error closing residents reader.");
                }
            }
        }
    }

    // ── Load bookings from bookings.txt ───────────────────────────────────────
    /**
     * Reads bookings.txt line by line.
     * Also restores the booking counter so new IDs continue from the last one.
     *
     * Lecture — BufferedReader, FileReader, Integer.parseInt()
     * Topic 7 — try-catch-finally, NumberFormatException caught separately
     */
    private static void loadBookings() {
        DataStore.bookings.clear();
        int maxCounter = 0;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(BOOKINGS_FILE));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 9) continue;

                String bookingId       = parts[0].trim();
                String residentId      = parts[1].trim();
                String residentName    = parts[2].trim();
                String date            = parts[3].trim();
                String timeSlot        = parts[4].trim();
                String wasteCategory   = parts[5].trim();
                String collectionPoint = parts[6].trim();
                String status          = parts[7].trim();

                // Topic 7 — NumberFormatException caught below
                int points = Integer.parseInt(parts[8].trim());

                Booking b = new Booking(
                    bookingId, residentId, residentName,
                    date, timeSlot, wasteCategory, collectionPoint);
                b.setStatus(status);
                b.setPoints(points);
                DataStore.bookings.add(b);

                // Track highest booking number to restore counter correctly
                try {
                    int num = Integer.parseInt(bookingId.replace("BK", ""));
                    if (num > maxCounter) maxCounter = num;
                } catch (NumberFormatException ignore) {
                    // Non-standard ID format — skip counter tracking
                }
            }

        } catch (IOException e) {
            System.out.println("FileHandler: Error reading bookings.txt — " +
                e.getMessage());

        } catch (NumberFormatException e) {
            // Topic 7 — NumberFormatException: caught if points field is invalid
            System.out.println("FileHandler: Invalid number in bookings.txt — " +
                e.getMessage());

        } catch (Exception e) {
            System.out.println("FileHandler: Unexpected error loading bookings — " +
                e.getMessage());

        } finally {
            if (reader != null) {
                try { reader.close(); }
                catch (IOException e) {
                    System.out.println("FileHandler: Error closing bookings reader.");
                }
            }
        }

        // Restore booking counter so next booking continues from correct number
        DataStore.setBookingCounter(maxCounter + 1);
    }

    // ── Save users to users.txt ───────────────────────────────────────────────
    /**
     * Writes all users to users.txt using PrintWriter and BufferedWriter.
     *
     * Lecture — PrintWriter, BufferedWriter, FileWriter, println()
     * Topic 7 — IOException caught, finally closes writer
     */
    private static void saveUsers() {
        PrintWriter writer = null;

        try {
            // File class lecture — FileWriter creates/overwrites the file
            writer = new PrintWriter(
                new BufferedWriter(new FileWriter(USERS_FILE)));

            // Write a comment header line
            writer.println("# GreenCycle Users");
            writer.println("# Format: username,password,role,linkedResidentId");

            for (User u : DataStore.users) {
                writer.println(
                    u.getUsername() + "," +
                    u.getPassword() + "," +
                    u.getRole()     + "," +
                    (u.getLinkedResidentId() != null
                        ? u.getLinkedResidentId() : "null")
                );
            }

        } catch (IOException e) {
            System.out.println("FileHandler: Error saving users.txt — " +
                e.getMessage());

        } finally {
            // Topic 7 — finally: always close the writer
            if (writer != null) writer.close();
        }
    }

    // ── Save residents to residents.txt ───────────────────────────────────────
    /**
     * Writes all residents to residents.txt.
     * Waste types joined with semicolon separator.
     *
     * Lecture — PrintWriter, BufferedWriter, FileWriter
     * Topic 7 — try-catch-finally
     */
    private static void saveResidents() {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                new BufferedWriter(new FileWriter(RESIDENTS_FILE)));

            writer.println("# GreenCycle Residents");
            writer.println("# Format: id,name,unit,phone,wasteTypes(semicolon-separated)");

            for (Resident r : DataStore.residents) {
                // Join waste types with semicolon
                ArrayList<String> types = r.getWasteTypes();
                StringBuilder wt = new StringBuilder();
                for (int i = 0; i < types.size(); i++) {
                    if (i > 0) wt.append(";");
                    wt.append(types.get(i));
                }

                writer.println(
                    r.getId()    + "," +
                    r.getName()  + "," +
                    r.getUnit()  + "," +
                    r.getPhone() + "," +
                    wt.toString()
                );
            }

        } catch (IOException e) {
            System.out.println("FileHandler: Error saving residents.txt — " +
                e.getMessage());

        } finally {
            if (writer != null) writer.close();
        }
    }

    // ── Save bookings to bookings.txt ─────────────────────────────────────────
    /**
     * Writes all bookings to bookings.txt.
     *
     * Lecture — PrintWriter, BufferedWriter, FileWriter
     * Topic 7 — try-catch-finally
     */
    private static void saveBookings() {
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(
                new BufferedWriter(new FileWriter(BOOKINGS_FILE)));

            writer.println("# GreenCycle Bookings");
            writer.println("# Format: bookingId,residentId,residentName,date," +
                           "timeSlot,wasteCategory,collectionPoint,status,points");

            for (Booking b : DataStore.bookings) {
                writer.println(
                    b.getBookingId()       + "," +
                    b.getResidentId()      + "," +
                    b.getResidentName()    + "," +
                    b.getDate()            + "," +
                    b.getTimeSlot()        + "," +
                    b.getWasteCategory()   + "," +
                    b.getCollectionPoint() + "," +
                    b.getStatus()          + "," +
                    b.getPoints()
                );
            }

        } catch (IOException e) {
            System.out.println("FileHandler: Error saving bookings.txt — " +
                e.getMessage());

        } finally {
            if (writer != null) writer.close();
        }
    }

    // ── Sample data for first run ─────────────────────────────────────────────
    /**
     * Populates DataStore with sample data when the app runs for the first time
     * and no data files exist yet. After this, saveAll() writes them to files.
     */
    private static void insertSampleData() {
        DataStore.users.clear();
        DataStore.residents.clear();
        DataStore.bookings.clear();

        // Sample user accounts
        DataStore.users.add(new User("admin", "admin123", "admin", null));
        DataStore.users.add(new User("ahmad", "pass123",  "user",  "R001"));
        DataStore.users.add(new User("siti",  "pass123",  "user",  "R002"));
        DataStore.users.add(new User("chen",  "pass123",  "user",  "R003"));

        // Sample residents (uses inheritance — Resident extends Person)
        DataStore.residents.add(new Resident("R001", "Ahmad Bin Ali",
            "A-12", "0123456789",
            new ArrayList<String>(Arrays.asList("Paper", "Plastic"))));
        DataStore.residents.add(new Resident("R002", "Siti Binti Rahmat",
            "B-05", "0198765432",
            new ArrayList<String>(Arrays.asList("Glass", "E-Waste"))));
        DataStore.residents.add(new Resident("R003", "Chen Wei Ming",
            "C-08", "0112233445",
            new ArrayList<String>(Arrays.asList("Paper", "Plastic", "Glass"))));

        // Sample bookings
        Booking b1 = new Booking("BK001","R001","Ahmad Bin Ali",
            "2026-06-10","Morning (8-10am)","Recyclables","Block A Bay");
        b1.setStatus("Completed"); b1.setPoints(20);

        Booking b2 = new Booking("BK002","R001","Ahmad Bin Ali",
            "2026-06-15","Afternoon (1-3pm)","General Waste","Block A Bay");
        b2.setStatus("Completed"); b2.setPoints(10);

        Booking b3 = new Booking("BK003","R002","Siti Binti Rahmat",
            "2026-06-18","Evening (5-7pm)","General Waste","Main Gate");
        b3.setStatus("Completed"); b3.setPoints(10);

        Booking b4 = new Booking("BK004","R003","Chen Wei Ming",
            "2026-06-20","Morning (8-10am)","Recyclables","Block B Bay");
        b4.setStatus("Completed"); b4.setPoints(20);

        Booking b5 = new Booking("BK005","R003","Chen Wei Ming",
            "2026-06-22","Afternoon (1-3pm)","Recyclables","Block B Bay");
        b5.setStatus("Completed"); b5.setPoints(20);

        DataStore.bookings.add(b1);
        DataStore.bookings.add(b2);
        DataStore.bookings.add(b3);
        DataStore.bookings.add(b4);
        DataStore.bookings.add(b5);

        DataStore.bookings.add(new Booking("BK006","R001","Ahmad Bin Ali",
            "2026-07-05","Morning (8-10am)","Bulky Items","Block A Bay"));
        DataStore.bookings.add(new Booking("BK007","R002","Siti Binti Rahmat",
            "2026-07-08","Evening (5-7pm)","Recyclables","Main Gate"));

        DataStore.setBookingCounter(8);

        System.out.println("FileHandler: Sample data loaded for first run.");
    }
}