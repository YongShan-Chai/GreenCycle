package data;
public class AppException extends Exception {
    //Constructor passes the error message to the parent Exception class.
    public AppException(String message) {
        super(message);
    }
}
