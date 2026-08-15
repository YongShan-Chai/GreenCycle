package data;

import java.util.ArrayList;

// Resident — a registered community resident.
public class Resident extends Person {

    private String            unit;
    private String            phone;
    private ArrayList<String> wasteTypes;
    private String            remark = ""; // Stores admin remark / instructions

    /**
     * Constructor calls super() to initialise inherited fields,
     * then sets its own fields.
     */
    public Resident(String id, String name, String unit,
                    String phone, ArrayList<String> wasteTypes) {
        super(id, name);          
        this.unit       = unit;
        this.phone      = phone;
        this.wasteTypes = wasteTypes;
        this.remark     = "";
    }

    /**
     * Overloaded constructor that includes an initial remark.
     */
    public Resident(String id, String name, String unit,
                    String phone, ArrayList<String> wasteTypes, String remark) {
        super(id, name);          
        this.unit       = unit;
        this.phone      = phone;
        this.wasteTypes = wasteTypes;
        this.remark     = remark != null ? remark : "";
    }

    // getters for Resident-specific fields
    public String            getUnit()       { return unit; }
    public String            getPhone()      { return phone; }
    public ArrayList<String> getWasteTypes() { return wasteTypes; }
    public String            getRemark()     { return remark; }

    // setters
    public void setUnit(String unit)               { this.unit = unit; }
    public void setPhone(String phone)             { this.phone = phone; }
    public void setWasteTypes(ArrayList<String> w) { this.wasteTypes = w; }
    public void setRemark(String remark)           { this.remark = remark != null ? remark : ""; }

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
     * Provides a Resident-specific summary string.
     * When getSummary() is called on a Person reference
     * pointing to a Resident object, THIS version runs (late binding).
     */
    @Override
    public String getSummary() {
        return super.getSummary() +
               " | Unit: " + unit +
               " | Phone: " + phone +
               " | " + getWasteTypesString() +
               (remark.isEmpty() ? "" : " | Remark: " + remark);
    }

    // Returns a fixed-width formatted string for ListView display.
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