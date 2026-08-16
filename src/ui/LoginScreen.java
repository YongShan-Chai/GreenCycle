package ui;

import data.AppException;
import data.DataStore;
import data.Session;
import data.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle; 
import javafx.stage.Stage;

/**
 * JavaFX components used (Part 1, 2, 4):
 *   Part 1  : HBox, VBox, StackPane, GridPane layout panes
 *   Part 2  : Lambda event handlers (setOnAction, setOnMouseEntered/Exited)
 *   Part 3  : Circle shape for logo icon, Color for styling
 *   Part 4  : TextField, PasswordField, Button, Label
 *
 *   try-catch blocks around login logic
 *   AppException caught and displayed to user
 */
public class LoginScreen {

    // JavaFX Part 4 — UI Control fields
    private TextField      txtUsername;
    private PasswordField txtPassword;
    private Label          lblError;

    public void show(Stage stage) {
        stage.setTitle("GreenCycle - Sign In");
        stage.setResizable(true);

        // 1. Save maximized state before updating the scene
        boolean wasMaximized = stage.isMaximized();

        // ── Left branding panel ───────────────────────────────────────────────
        VBox leftPanel = buildBrandingPanel();

        // ── Right login form ──────────────────────────────────────────────────
        VBox rightPanel = buildLoginPanel(stage);

        // JavaFX Part 1 — HBox layout pane: places panels side by side
        HBox root = new HBox(leftPanel, rightPanel);
        HBox.setHgrow(leftPanel,  Priority.NEVER);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // 2. Reuse scene if existing, or create smaller scene (1000 x 650)
        if (stage.getScene() != null) {
            stage.getScene().setRoot(root);
        } else {
            Scene scene = new Scene(root, 1000, 650); // Changed from 1200 x 750
            stage.setScene(scene);
        }

        stage.setResizable(true);

        // 3. Re-apply maximized state if it was maximized
        if (wasMaximized) {
            stage.setMaximized(true);
        }

        if (!stage.isShowing()) stage.show();
    }

    // ── Left branding panel ───────────────────────────────────────────────────
    private VBox buildBrandingPanel() {
        // JavaFX Part 3 — Circle shape for logo icon
        Circle logoCircle = new Circle(42);
        logoCircle.setFill(Color.web("rgba(255,255,255,0.15)")); // Part 3 — Color

        Label logoText = new Label("GC");
        logoText.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:white;");

        // JavaFX Part 1 — StackPane: overlays logoText on top of Circle
        StackPane logoStack = new StackPane(logoCircle, logoText);

        Label lblTitle = new Label("GreenCycle");
        lblTitle.setStyle("-fx-font-size:30px;-fx-font-weight:bold;-fx-text-fill:white;");

        Label lblSub = new Label("Community Recycling &\nWaste Collection Scheduler");
        lblSub.setStyle("-fx-font-size:14px;-fx-text-fill:rgba(255,255,255,0.80);-fx-text-alignment:center;");
        lblSub.setAlignment(Pos.CENTER);

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:rgba(255,255,255,0.25);");
        sep.setPrefWidth(180);

        VBox features = buildFeatureBullets();

        // JavaFX Part 1 — VBox layout
        VBox panel = new VBox(20, logoStack, lblTitle, lblSub, sep, features);
        panel.setPrefWidth(390);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(60, 50, 60, 50));
        panel.setStyle("-fx-background-color:" + StyleHelper.PRIMARY + ";");
        return panel;
    }

    private VBox buildFeatureBullets() {
        VBox box = new VBox(14);
        box.setAlignment(Pos.CENTER_LEFT);
        String[][] items = {
            {"✦", "Register and manage residents"},
            {"✦", "Schedule waste pickups"},
            {"✦", "Track recycling points and tiers"},
            {"✦", "Community summary dashboard"}
        };
        for (String[] item : items) {
            Label dot  = new Label(item[0]);
            dot.setStyle("-fx-text-fill:" + StyleHelper.SECONDARY + ";-fx-font-size:12px;");
            Label text = new Label(item[1]);
            text.setStyle("-fx-text-fill:rgba(255,255,255,0.85);-fx-font-size:13px;");
            HBox row = new HBox(10, dot, text);
            row.setAlignment(Pos.CENTER_LEFT);
            box.getChildren().add(row);
        }
        return box;
    }

    // ── Right login form ──────────────────────────────────────────────────────
    private VBox buildLoginPanel(Stage stage) {
        // Login card
        Label lblTitle = new Label("Welcome Back");
        lblTitle.setStyle(StyleHelper.pageTitle());

        Label lblSub = new Label("Sign in to your GreenCycle account");
        lblSub.setStyle(StyleHelper.mutedLabel());

        // JavaFX Part 4 — TextField for username
        txtUsername = new TextField();
        txtUsername.setPromptText("Enter your username");
        txtUsername.setPrefWidth(280);
        txtUsername.setStyle(StyleHelper.inputField());
        // JavaFX Part 2 — Lambda for focus event
        txtUsername.focusedProperty().addListener((obs, old, f) ->
            txtUsername.setStyle(f ? StyleHelper.inputFieldFocus() : StyleHelper.inputField()));

        // JavaFX Part 4 — PasswordField
        txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter your password");
        txtPassword.setPrefWidth(280);
        txtPassword.setStyle(StyleHelper.inputField());
        txtPassword.focusedProperty().addListener((obs, old, f) ->
            txtPassword.setStyle(f ? StyleHelper.inputFieldFocus() : StyleHelper.inputField()));
        // JavaFX Part 2 — Lambda: press Enter to login
        txtPassword.setOnAction(e -> handleLogin(stage));

        // Form layout using GridPane (JavaFX Part 1)
        GridPane grid = new GridPane();
        grid.setHgap(16); grid.setVgap(14);
        grid.add(fLabel("Username :"), 0, 0); grid.add(txtUsername, 1, 0);
        grid.add(fLabel("Password :"), 0, 1); grid.add(txtPassword, 1, 1);

        // Error label for displaying AppException messages
        lblError = new Label("");
        lblError.setStyle("-fx-text-fill:" + StyleHelper.DANGER + ";-fx-font-size:12px;");
        lblError.setWrapText(true);

        // JavaFX Part 4 — Button; Part 2 — Lambda event handler
        Button btnLogin = new Button("Sign In");
        btnLogin.setPrefWidth(280);
        btnLogin.setStyle(StyleHelper.btnPrimary());
        btnLogin.setOnMouseEntered(e -> btnLogin.setStyle(StyleHelper.btnPrimaryHover()));
        btnLogin.setOnMouseExited(e  -> btnLogin.setStyle(StyleHelper.btnPrimary()));
        btnLogin.setOnAction(e -> handleLogin(stage)); // Part 2 — Lambda

        // Register link
        Label lblPrompt = new Label("New resident?");
        lblPrompt.setStyle(StyleHelper.mutedLabel());
        Button btnRegister = new Button("Create an account");
        btnRegister.setStyle("-fx-background-color:transparent;-fx-text-fill:" +
            StyleHelper.PRIMARY + ";-fx-cursor:hand;-fx-font-size:12px;-fx-font-weight:bold;-fx-padding:0;");
        btnRegister.setOnAction(e -> new RegisterScreen().show(stage)); // Part 2 — Lambda
        HBox regRow = new HBox(6, lblPrompt, btnRegister);
        regRow.setAlignment(Pos.CENTER);

        VBox card = new VBox(18, lblTitle, lblSub, new Separator(),
            grid, lblError, btnLogin, regRow);
        card.setMaxWidth(380);
        card.setPadding(new Insets(40));
        card.setStyle(StyleHelper.card());

        VBox panel = new VBox(card);
        panel.setAlignment(Pos.CENTER);
        panel.setStyle("-fx-background-color:" + StyleHelper.BG + ";");
        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }

    // ── Login handler ──────────────────
private void handleLogin(Stage stage) {
    // Clear any previous error message.
    lblError.setText("");

    try {
        // Get the values entered by the user.
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // Check that both fields contain values.
        DataStore.validateNotEmpty(username, "Username");
        DataStore.validateNotEmpty(password, "Password");

        // Search for an account matching the username and password.
        User user = DataStore.authenticate(username, password);

        if (user == null) {
            throw new AppException(
                "Incorrect username or password. Please try again."
            );
        }

        // Save the authenticated user as the current logged-in user.
        Session.login(user);

        // Open the correct interface based on the user's stored role.
        if (user.isAdmin()) {
            new AdminShell().show(stage);
        } else {
            new UserShell().show(stage);
        }

    } catch (AppException e) {
        // Display expected validation or authentication errors.
        lblError.setText(e.getMessage());

    } catch (Exception e) {
        // Log unexpected errors to the console for debugging.
        System.out.println("Critical Error in Login: ");
        e.printStackTrace();

        // Display an error to the user if something unexpected happens.
        lblError.setText(
            "An unexpected error occurred. Please restart the application and try again."
        );
    }
}

    private Label fLabel(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-weight:bold;-fx-text-fill:" + StyleHelper.TEXT_DARK + ";-fx-font-size:13px;");
        l.setMinWidth(90);
        return l;
    }
}
