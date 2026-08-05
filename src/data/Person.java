package data;

//Person — base class for Resident and User.
public class Person {

    // Topic 1: private fields with encapsulation
    private String id;
    private String name;

    /**
     * Constructor uses 'this' keyword to assign fields.
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

    //Returns a summary string for this person
    public String getSummary() {
        return "[" + id + "] " + name;
    }
    
    //Delegates to getSummary() so subclass versions are used (polymorphism).
    @Override
    public String toString() {
        return getSummary();
    }
}
