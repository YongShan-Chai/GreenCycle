package data;

//Booking — stores one waste-collection pickup booking.

public class Booking {

    // private fields with encapsulation
    private String bookingId;
    private String residentId;
    private String residentName;
    private String date;            // format: yyyy-MM-dd
    private String timeSlot;
    private String wasteCategory;
    private String collectionPoint;
    private String status;          // "Pending" | "Completed" | "Cancelled"
    private int    points;          // recycling points awarded on completion

    /**
     * Constructor initialises all fields.
     * Status defaults to "Pending", points to 0.
     */
    public Booking(String bookingId, String residentId, String residentName,
                   String date, String timeSlot, String wasteCategory,
                   String collectionPoint) {
        this.bookingId       = bookingId;
        this.residentId      = residentId;
        this.residentName    = residentName;
        this.date            = date;
        this.timeSlot        = timeSlot;
        this.wasteCategory   = wasteCategory;
        this.collectionPoint = collectionPoint;
        this.status          = "Pending";
        this.points          = 0;
    }

    // Topic 1: getter methods
    public String getBookingId()       { return bookingId; }
    public String getResidentId()      { return residentId; }
    public String getResidentName()    { return residentName; }
    public String getDate()            { return date; }
    public String getTimeSlot()        { return timeSlot; }
    public String getWasteCategory()   { return wasteCategory; }
    public String getCollectionPoint() { return collectionPoint; }
    public String getStatus()          { return status; }
    public int    getPoints()          { return points; }

    // Topic 1: setter methods
    public void setStatus(String status) { this.status = status; }
    public void setPoints(int points)    { this.points = points; }

    /**
     * Returns a formatted string for ListView display.
     * JavaFX Part 4 — used to populate ListView items.
     */
    public String getListEntry() {
        String ptsStr = points > 0 ? " (" + points + " pts)" : "";
        return String.format("%-7s  %-18s  %-11s  %-18s  %-13s  %s%s",
            bookingId,
            truncate(residentName, 18),
            date,
            truncate(timeSlot, 18),
            truncate(wasteCategory, 13),
            status,
            ptsStr);
    }

    private String truncate(String s, int maxLen) {
        return s.length() > maxLen ? s.substring(0, maxLen - 1) + "." : s;
    }
}
