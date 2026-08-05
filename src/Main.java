import javafx.application.Application;
import javafx.stage.Stage;
import ui.LoginScreen;
import data.FileHandler;

/**
 * Main — JavaFX 8 application entry point.
 *
 * Launches the Login Screen. After login:
 *   Admin → AdminShell   (full management access)
 *   User  → UserShell    (personal booking & points access)
 *
 * Compile and run:
 *   Windows   : compile.bat
 *   Mac/Linux : ./compile.sh
 *
 * Requires Oracle JDK 8 — JavaFX is bundled, no extra libraries needed.
 */
public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
	    // File handling — load all data from files before showing login
	    // If files don't exist (first run), FileHandler creates them with sample data
	    FileHandler.loadAll();

	    new LoginScreen().show(primaryStage);

	    // File handling — save all data to files when the window is closed
	    primaryStage.setOnCloseRequest(e -> FileHandler.saveAll());
	}

    public static void main(String[] args) {
        launch(args);
    }
}
