package data;

/**
 * Person — base class for Resident and User.
 *
 * Lecture reference:
 *   Topic 1 — OOP: private fields, constructors, getters/setters, this keyword
 *   Topic 2 — Inheritance: superclass that Resident and User extend
 *             Polymorphism: getSummary() is overridden in each subclass
 */
public class Person {

    // Topic 1: private fields with encapsulation
    private String id;
    private String name;

    /**
     * Constructor uses 'this' keyword to assign fields.
     * Topic 1 — constructors and 'this' keyword
     */
    public Person(String id, String name) {
        this.id   = id;
        this.name = name;
    }

    // Topic 1: accessor (getter) methods
    public String getId()   { return id; }
    public String getName() { return name; }

    // Topic 1: mutator (setter) methods
    public void setId(String id)     { this.id = id; }
    public void setName(String name) { this.name = name; }

    /**
     * Returns a summary string for this person.
     * Topic 2 — Polymorphism: overridden in Resident and User
     * to return type-specific display strings.
     */
    public String getSummary() {
        return "[" + id + "] " + name;
    }

    /**
     * Topic 2 — Method overriding: toString() overridden from Object class.
     * Delegates to getSummary() so subclass versions are used (polymorphism).
     */
    @Override
    public String toString() {
        return getSummary();
    }
}
