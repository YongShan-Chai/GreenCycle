package data;

import java.util.ArrayList;

/**
 * Resident — a registered community resident.
 *
 * Lecture reference:
 *   Topic 1 — private fields, constructors, getters/setters
 *   Topic 2 — extends Person (inheritance), super(), @Override (polymorphism)
 *
 * Member 1 owns this class.
 */
public class Resident extends Person {

    // Topic 1: additional private fields (on top of Person's id, name)
    private String            unit;
    private String            phone;
    private ArrayList<String> wasteTypes;

    /**
     * Constructor calls super() to initialise inherited fields,
     * then sets its own fields.
     * Topic 2 — super() keyword for calling parent constructor
     */
    public Resident(String id, String name, String unit,
                    String phone, ArrayList<String> wasteTypes) {
        super(id, name);          // Topic 2: call Person constructor
        this.unit       = unit;
        this.phone      = phone;
        this.wasteTypes = wasteTypes;
    }

    // Topic 1: getters for Resident-specific fields
    public String            getUnit()       { return unit; }
    public String            getPhone()      { return phone; }
    public ArrayList<String> getWasteTypes() { return wasteTypes; }

    // Topic 1: setters
    public void setUnit(String unit)               { this.unit = unit; }
    public void setPhone(String phone)             { this.phone = phone; }
    public void setWasteTypes(ArrayList<String> w) { this.wasteTypes = w; }

    /** Returns waste types as a comma-separated string for display. */
    public String getWasteTypesString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wasteTypes.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(wasteTypes.get(i));
        }
        return sb.toString();
    }

    /**
     * Topic 2 — @Override and Polymorphism:
     * Provides a Resident-specific summary string.
     * When getSummary() is called on a Person reference
     * pointing to a Resident object, THIS version runs (late binding).
     */
    @Override
    public String getSummary() {
        return super.getSummary() +
               " | Unit: " + unit +
               " | Phone: " + phone +
               " | " + getWasteTypesString();
    }

    /**
     * Returns a fixed-width formatted string for ListView display.
     * Topic 4 — used by ListView to show resident records.
     */
    public String getListEntry() {
        return String.format("%-7s  %-22s  %-8s  %-14s  %s",
            getId(),
            truncate(getName(), 22),
            unit,
            phone,
            getWasteTypesString());
    }

    /** Truncates a string to maxLen and appends "…" if needed. */
    private String truncate(String s, int maxLen) {
        return s.length() > maxLen ? s.substring(0, maxLen - 1) + "." : s;
    }
}
