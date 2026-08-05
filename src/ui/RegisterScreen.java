package ui;

import data.AppException;
import data.DataStore;
import data.Resident;
import data.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * Creates both a User account and a linked Resident record in one form.
 *
 * JavaFX components (Part 1, 2, 4):
 *   Part 1  : VBox, HBox, GridPane, ScrollPane
 *   Part 2  : Lambda event handlers
 *   Part 4  : TextField, PasswordField, CheckBox, Button, Label, ScrollPane
 *
 *   try-catch-finally block
 *   Multiple catch blocks
 *   AppException thrown from DataStore validation methods
 */
public class RegisterScreen {

    // JavaFX Part 4 — account credential fields
    private TextField     txtUsername;
    private PasswordField txtPassword;
    private PasswordField txtConfirm;

    // JavaFX Part 4 — resident information fields
    private TextField txtResidentId;
    private TextField txtName;
    private TextField txtUnit;
    private TextField txtPhone;

    // JavaFX Part 4 — CheckBox for multi-select waste types
    private CheckBox cbPaper;
    private CheckBox cbPlastic;
    private CheckBox cbGlass;
    private CheckBox cbEWaste;

    private Label lblError;

    public void show(Stage stage) {
        stage.setTitle("GreenCycle — Create Account");
        stage.setResizable(true);

        // 1. Remember window maximized state BEFORE switching root/scene
        boolean wasMaximized = stage.isMaximized();

        // ── Page header ───────────────────────────────────────────────────────
        Label lblTitle = new Label("Create Your Account");
        lblTitle.setStyle(StyleHelper.pageTitle());
        Label lblSub   = new Label("Register as a resident to start scheduling waste pickups.");
        lblSub.setStyle(StyleHelper.mutedLabel());
        VBox header = new VBox(6, lblTitle, lblSub);

        // ── Account credentials card ──────────────────────────────────────────
        VBox accountCard = buildSectionCard("Account Credentials", buildAccountGrid());

        // ── Resident information card ─────────────────────────────────────────
        VBox residentCard = buildSectionCard("Resident Information", buildResidentGrid());

        // ── Error label ───────────────────────────────────────────────────────
        lblError = new Label("");
        lblError.setStyle("-fx-text-fill:" + StyleHelper.DANGER + ";-fx-font-size:12px;");
        lblError.setWrapText(true);

        // ── Action buttons ────────────────────────────────────────────────────
        Button btnCreate = new Button("Create Account");
        btnCreate.setStyle(StyleHelper.btnPrimary());
        // JavaFX Part 2 — Lambda event handler
        btnCreate.setOnMouseEntered(e -> btnCreate.setStyle(StyleHelper.btnPrimaryHover()));
        btnCreate.setOnMouseExited(e  -> btnCreate.setStyle(StyleHelper.btnPrimary()));
        btnCreate.setOnAction(e -> handleRegister(stage));

        Button btnBack = new Button("Back to Login");
        btnBack.setStyle(StyleHelper.btnSecondary());
        btnBack.setOnMouseEntered(e -> btnBack.setStyle(StyleHelper.btnSecondaryHover()));
        btnBack.setOnMouseExited(e  -> btnBack.setStyle(StyleHelper.btnSecondary()));
        btnBack.setOnAction(e -> new LoginScreen().show(stage));

        HBox btnRow = new HBox(14, btnCreate, btnBack);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        // JavaFX Part 1 — VBox layout for page content
        VBox content = new VBox(22, header, accountCard, residentCard, lblError, btnRow);
        content.setPadding(new Insets(40, 60, 40, 60));
        content.setStyle("-fx-background-color:" + StyleHelper.BG + ";");

        // JavaFX Part 4 — ScrollPane wraps content for scrollability
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background-color:" + StyleHelper.BG + ";-fx-border-color:transparent;");

        // 2. Reuse current scene if existing to retain window dimensions/state
        if (stage.getScene() != null) {
            stage.getScene().setRoot(scroll);
        } else {
            Scene scene = new Scene(scroll, 1000, 650); // Default size 1000 x 650
            stage.setScene(scene);
        }

        stage.setResizable(true);

        // 3. Re-apply maximized status if window was previously maximized
        if (wasMaximized) {
            stage.setMaximized(true);
        }

        if (!stage.isShowing()) {
            stage.show();
        }
    }

    // ── Account credentials form ──────────────────────────────────────────────
    private GridPane buildAccountGrid() {
        txtUsername = styledField("Choose a username (min. 3 characters)");
        txtPassword = new PasswordField();
        txtPassword.setPromptText("Choose a password (min. 6 characters)");
        txtPassword.setPrefWidth(300);
        txtPassword.setStyle(StyleHelper.inputField());
        txtPassword.focusedProperty().addListener((o, old, f) ->
            txtPassword.setStyle(f ? StyleHelper.inputFieldFocus() : StyleHelper.inputField()));

        txtConfirm = new PasswordField();
        txtConfirm.setPromptText("Re-enter your password");
        txtConfirm.setPrefWidth(300);
        txtConfirm.setStyle(StyleHelper.inputField());
        txtConfirm.focusedProperty().addListener((o, old, f) ->
            txtConfirm.setStyle(f ? StyleHelper.inputFieldFocus() : StyleHelper.inputField()));

        // JavaFX Part 1 — GridPane for aligned form
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(14);
        grid.add(fLabel("Username :"),         0, 0); grid.add(txtUsername, 1, 0);
        grid.add(fLabel("Password :"),         0, 1); grid.add(txtPassword, 1, 1);
        grid.add(fLabel("Confirm Password :"), 0, 2); grid.add(txtConfirm,  1, 2);
        return grid;
    }

    // ── Resident information form ─────────────────────────────────────────────
    private GridPane buildResidentGrid() {
        txtResidentId = styledField("e.g. R004");
        txtName       = styledField("Your full name");
        txtUnit       = styledField("e.g. D-07");
        txtPhone      = styledField("10 or 11 digit number");

        // JavaFX Part 4 — CheckBox for multi-select (waste type preferences)
        cbPaper   = new CheckBox("Paper");
        cbPlastic = new CheckBox("Plastic");
        cbGlass   = new CheckBox("Glass");
        cbEWaste  = new CheckBox("E-Waste");
        for (CheckBox cb : new CheckBox[]{cbPaper, cbPlastic, cbGlass, cbEWaste}) {
            cb.setStyle(StyleHelper.bodyText());
        }

        // JavaFX Part 1 — HBox groups the checkboxes horizontally
        HBox cbRow = new HBox(18, cbPaper, cbPlastic, cbGlass, cbEWaste);
        cbRow.setAlignment(Pos.CENTER_LEFT);

        // JavaFX Part 1 — GridPane form layout
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(14);
        grid.add(fLabel("Resident ID :"),  0, 0); grid.add(txtResidentId, 1, 0);
        grid.add(fLabel("Full Name :"),    0, 1); grid.add(txtName,       1, 1);
        grid.add(fLabel("Unit / Block :"), 0, 2); grid.add(txtUnit,       1, 2);
        grid.add(fLabel("Phone Number :"), 0, 3); grid.add(txtPhone,      1, 3);
        grid.add(fLabel("Waste Types :"),  0, 4); grid.add(cbRow,         1, 4);
        return grid;
    }

    // ── Register handler — Topic 7: try-catch-finally ─────────────────────────
    private void handleRegister(Stage stage) {
        lblError.setText("");

        try {
            // ── Account validation — Topic 7: DataStore throws AppException ───
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText();
            String confirm  = txtConfirm.getText();

            DataStore.validateUsername(username);       // throws AppException
            DataStore.validatePassword(password, confirm); // throws AppException

            // ── Resident validation ───────────────────────────────────────────
            String id    = txtResidentId.getText().trim();
            String name  = txtName.getText().trim();
            String unit  = txtUnit.getText().trim();
            String phone = txtPhone.getText().trim();

            DataStore.validateResidentId(id);           // throws AppException
            DataStore.validateNotEmpty(name,  "Full Name");
            DataStore.validateNotEmpty(unit,  "Unit / Block");
            DataStore.validatePhone(phone);             // throws AppException

            // At least one waste type must be ticked
            ArrayList<String> wasteTypes = new ArrayList<String>();
            if (cbPaper.isSelected())   wasteTypes.add("Paper");
            if (cbPlastic.isSelected()) wasteTypes.add("Plastic");
            if (cbGlass.isSelected())   wasteTypes.add("Glass");
            if (cbEWaste.isSelected())  wasteTypes.add("E-Waste");

            if (wasteTypes.isEmpty()) {
                throw new AppException("Please tick at least one waste type preference.");
            }

            // ── All validation passed: save records ───────────────────────────
            DataStore.residents.add(new Resident(id, name, unit, phone, wasteTypes));
            DataStore.users.add(new User(username, password, "user", id));

            // show success message
            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Registration Successful");
            ok.setHeaderText(null);
            ok.setContentText(
                "Account created successfully!\n\n" +
                "Username    : " + username + "\n" +
                "Resident ID : " + id + "\n\n" +
                "You can now sign in with your new account.");
            ok.showAndWait();

            new LoginScreen().show(stage);

        } catch (AppException e) {
            // catch custom AppException (validation errors)
            lblError.setText(e.getMessage());

        } catch (Exception e) {
            // catch any other unexpected exceptions
            lblError.setText("An unexpected error occurred: " + e.getMessage());

        } finally {
            // finally block: always runs regardless of exception
            // Here used to ensure the error label is visible
            lblError.setVisible(true);
        }
    }

    // ── UI helpers ────────────────────────────────────────────────────────────
    private VBox buildSectionCard(String title, GridPane form) {
        Label lbl = new Label(title);
        lbl.setStyle(StyleHelper.sectionTitle());
        VBox card = new VBox(16, lbl, new Separator(), form);
        card.setPadding(new Insets(24));
        card.setStyle(StyleHelper.card());
        return card;
    }

    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(300);
        tf.setStyle(StyleHelper.inputField());
        tf.focusedProperty().addListener((o, old, f) ->
            tf.setStyle(f ? StyleHelper.inputFieldFocus() : StyleHelper.inputField()));
        return tf;
    }

    private Label fLabel(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-weight:bold;-fx-text-fill:" + StyleHelper.TEXT_DARK + ";-fx-font-size:13px;");
        l.setMinWidth(145);
        return l;
    }
}
