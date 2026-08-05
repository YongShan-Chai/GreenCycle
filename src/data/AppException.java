package data;

/**
 * AppException — custom checked exception for GreenCycle input validation.
 *
 * Lecture reference: Topic 7 — Exception Handling
 *   - Creating custom exception classes by extending Exception
 *   - Thrown by DataStore validation methods
 *   - Caught in all form handlers using try-catch blocks
 */
public class AppException extends Exception {

    /**
     * Constructor passes the error message to the parent Exception class.
     * Lecture reference: Topic 2 — super keyword, Topic 7 — custom exceptions
     */
    public AppException(String message) {
        super(message);
    }
}
