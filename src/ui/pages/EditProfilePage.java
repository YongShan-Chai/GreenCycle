package ui.pages;

import data.AppException;
import data.Booking;
import data.DataStore;
import data.FileHandler;
import data.Resident;
import data.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.StyleHelper;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * EditProfilePage — MEMBER 4's edit profile screen.
 *
 * Allows a logged-in resident to update their profile information.
 * Pre-fills all fields with current values from DataStore.
 * On save: validates input, updates DataStore, writes to file immediately.
 *
 * Lecture reference:
 *   Topic 2  — extends BasePage, @Override build(), super() constructor
 *   Topic 7  — try-catch-finally, AppException for validation errors
 *   Part 1   — VBox, HBox, GridPane layout panes
 *   Part 2   — Lambda event handlers (setOnAction)
 *   Part 4   — TextField (pre-filled), CheckBox, Button, Label
 *   File     — FileHandler.saveAll() called after successful save
 */
public class EditProfilePage extends BasePage {

    // Part 4 — UI control fields
    private TextField txtName;
    private TextField txtUnit;
    private TextField txtPhone;
    private CheckBox  cbPaper;
    private CheckBox  cbPlastic;
    private CheckBox  cbGlass;
    private CheckBox  cbEWaste;
    private Label     lblFeedback;

    // Topic 2 — constructors call super() from BasePage
    public EditProfilePage() { super(); }
    public EditProfilePage(Consumer<String> navigate) { super(navigate); }

    /**
     * Topic 2 — @Override: implements the abstract build() from BasePage.
     * Polymorphism: when UserShell calls page.build(), this version executes.
     */
    @Override
    public Node build() {
        String resId = Session.getLinkedResidentId();
        Resident me  = resId != null ? DataStore.findResidentById(resId) : null;

        // Guard: no resident linked to this account
        if (me == null) {
            Label warn = new Label(
                "No resident profile is linked to your account.\n" +
                "Please contact an administrator.");
            warn.setStyle("-fx-text-fill:" + StyleHelper.DANGER +
                ";-fx-font-size:14px;");
            warn.setWrapText(true);
            VBox root = new VBox(warn);
            root.setPadding(new Insets(32, 36, 32, 36));
            root.setStyle("-fx-background-color:" + StyleHelper.BG + ";");
            return root;
        }

        // ── Page title ────────────────────────────────────────────────────────
        Label lblTitle = new Label("Edit Profile");
        lblTitle.setStyle(StyleHelper.pageTitle());
        Label lblSub = new Label(
            "Update your resident profile. Changes are saved to file immediately.");
        lblSub.setStyle(StyleHelper.mutedLabel());
        VBox titleBox = new VBox(4, lblTitle, lblSub);

        // ── Current profile summary banner ────────────────────────────────────
        int pts = DataStore.getPointsForResident(resId);
        Label lblCurrentId   = new Label("ID: " + me.getId());
        lblCurrentId.setStyle("-fx-font-size:13px;-fx-font-weight:bold;" +
            "-fx-text-fill:" + StyleHelper.PRIMARY + ";");
        Label lblCurrentTier = new Label(pts + " pts  •  " + DataStore.getTier(pts));
        lblCurrentTier.setStyle(
            "-fx-background-color:" + StyleHelper.ACCENT + ";" +
            "-fx-text-fill:" + StyleHelper.PRIMARY + ";" +
            "-fx-font-size:12px;-fx-font-weight:bold;" +
            "-fx-background-radius:20;-fx-padding:4 14;");

        Region bannerSpacer = new Region();
        HBox.setHgrow(bannerSpacer, Priority.ALWAYS);

        HBox bannerRow = new HBox(14, lblCurrentId, bannerSpacer, lblCurrentTier);
        bannerRow.setAlignment(Pos.CENTER_LEFT);

        VBox banner = new VBox(bannerRow);
        banner.setPadding(new Insets(16, 22, 16, 22));
        banner.setStyle(
            "-fx-background-color:white;" +
            "-fx-background-radius:12;-fx-border-radius:12;" +
            "-fx-border-color:" + StyleHelper.ACCENT + ";" +
            "-fx-border-width:2;");

        // ── Edit form ─────────────────────────────────────────────────────────
        Label lblFormTitle = new Label("Update Your Information");
        lblFormTitle.setStyle(StyleHelper.sectionTitle());

        // Resident ID — read-only display (cannot be changed)
        Label lblIdReadOnly = new Label(me.getId());
        lblIdReadOnly.setStyle(
            "-fx-font-size:13px;-fx-font-weight:bold;" +
            "-fx-text-fill:" + StyleHelper.TEXT_MUTED + ";" +
            "-fx-background-color:" + StyleHelper.BG + ";" +
            "-fx-background-radius:8;-fx-padding:10 14;");
        Label lblIdNote = new Label("(cannot be changed)");
        lblIdNote.setStyle(StyleHelper.mutedLabel());
        HBox idRow = new HBox(10, lblIdReadOnly, lblIdNote);
        idRow.setAlignment(Pos.CENTER_LEFT);

        // Part 4 — TextField: pre-filled with current values
        txtName  = makeField("Full name");
        txtUnit  = makeField("e.g. D-07");
        txtPhone = makeField("10 or 11 digit number");

        // Pre-fill with current resident data
        txtName.setText(me.getName());
        txtUnit.setText(me.getUnit());
        txtPhone.setText(me.getPhone());

        // Part 4 — CheckBox: pre-ticked based on current waste type preferences
        cbPaper   = new CheckBox("Paper");
        cbPlastic = new CheckBox("Plastic");
        cbGlass   = new CheckBox("Glass");
        cbEWaste  = new CheckBox("E-Waste");

        for (CheckBox cb : new CheckBox[]{cbPaper, cbPlastic, cbGlass, cbEWaste}) {
            cb.setStyle(StyleHelper.bodyText());
        }

        // Pre-tick checkboxes based on existing preferences
        ArrayList<String> currentTypes = me.getWasteTypes();
        cbPaper.setSelected(currentTypes.contains("Paper"));
        cbPlastic.setSelected(currentTypes.contains("Plastic"));
        cbGlass.setSelected(currentTypes.contains("Glass"));
        cbEWaste.setSelected(currentTypes.contains("E-Waste"));

        // Part 1 — HBox groups checkboxes horizontally
        HBox cbRow = new HBox(18, cbPaper, cbPlastic, cbGlass, cbEWaste);
        cbRow.setAlignment(Pos.CENTER_LEFT);

        // Part 1 — GridPane for aligned form layout
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(16);
        grid.add(makeFormLabel("Resident ID :"),  0, 0); grid.add(idRow,    1, 0);
        grid.add(makeFormLabel("Full Name :"),     0, 1); grid.add(txtName,  1, 1);
        grid.add(makeFormLabel("Unit / Block :"),  0, 2); grid.add(txtUnit,  1, 2);
        grid.add(makeFormLabel("Phone Number :"),  0, 3); grid.add(txtPhone, 1, 3);
        grid.add(makeFormLabel("Waste Types :"),   0, 4); grid.add(cbRow,    1, 4);

        // Feedback label (shows success or error message)
        lblFeedback = new Label("");
        lblFeedback.setStyle(StyleHelper.mutedLabel());
        lblFeedback.setWrapText(true);

        // Info note about file saving
        Label noteLabel = new Label(
            "Your changes will be saved to file and will persist " +
            "the next time you open the app.");
        noteLabel.setStyle(
            "-fx-font-size:11px;" +
            "-fx-text-fill:" + StyleHelper.TEXT_MUTED + ";" +
            "-fx-font-style:italic;");

        // Part 4 — Buttons; Part 2 — Lambda event handlers
        Button btnSave   = makeBtn("Save Changes");
        Button btnCancel = makeSecBtn("Cancel");

        // Part 2 — Lambda: save button triggers handleSave()
        btnSave.setOnAction(e -> handleSave(me));

        // Part 2 — Lambda: cancel navigates back to dashboard
        btnCancel.setOnAction(e -> goTo("dashboard"));

        HBox btnRow = new HBox(14, btnSave, btnCancel);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        // Card wrapper using inherited makeCard() from BasePage
        VBox formCard = makeCard(20);
        Label cardTitle = new Label("Update Your Information");
        cardTitle.setStyle(StyleHelper.sectionTitle());
        formCard.getChildren().addAll(
            cardTitle,
            new Separator(),
            grid,
            noteLabel,
            lblFeedback,
            btnRow
        );

        // ── Root — Part 1: VBox stacks all sections ───────────────────────────
        VBox root = new VBox(24, titleBox, banner, formCard);
        root.setPadding(new Insets(32, 36, 32, 36));
        root.setStyle("-fx-background-color:" + StyleHelper.BG + ";");
        return root;
    }

    // ── Save handler — Topic 7: try-catch-finally ─────────────────────────────
    private void handleSave(Resident me) {
        lblFeedback.setStyle(StyleHelper.mutedLabel());
        lblFeedback.setText("");

        try {
            String name  = txtName.getText().trim();
            String unit  = txtUnit.getText().trim();
            String phone = txtPhone.getText().trim();

            // Topic 7 — DataStore validation methods throw AppException
            DataStore.validateName(name);
            DataStore.validateNotEmpty(unit,  "Unit / Block");
            DataStore.validatePhone(phone);

            // Build updated waste types list
            ArrayList<String> wasteTypes = new ArrayList<String>();
            if (cbPaper.isSelected())   wasteTypes.add("Paper");
            if (cbPlastic.isSelected()) wasteTypes.add("Plastic");
            if (cbGlass.isSelected())   wasteTypes.add("Glass");
            if (cbEWaste.isSelected())  wasteTypes.add("E-Waste");

            if (wasteTypes.isEmpty()) {
                throw new AppException(
                    "Please select at least one waste type preference.");
            }

            // Update the resident record in DataStore
            // Topic 1 — setter methods from Resident class
            me.setName(name);
            me.setUnit(unit);
            me.setPhone(phone);
            me.setWasteTypes(wasteTypes);

            // Update resident name in linked bookings for consistency
            for (Booking b : DataStore.bookings) {
                if (b.getResidentId().equalsIgnoreCase(me.getId())) {
                    // Note: Booking stores name at time of booking.
                    // We don't update historical booking names — this is intentional.
                    // Only future bookings will use the new name.
                    break;
                }
            }

            // File handling — save all data to files immediately after update
            FileHandler.saveAll();

            // Show success feedback in green
            lblFeedback.setStyle(
                "-fx-text-fill:" + StyleHelper.SUCCESS + ";" +
                "-fx-font-size:13px;-fx-font-weight:bold;");
            lblFeedback.setText(
                "Profile updated and saved to file successfully!\n" +
                "Name: " + name + "  |  Unit: " + unit +
                "  |  Waste Types: " + joinList(wasteTypes));

        } catch (AppException e) {
            // Topic 7 — catch custom AppException (validation errors)
            showError(e.getMessage());

        } catch (Exception e) {
            // Topic 7 — catch any other unexpected exception
            showError("Unexpected error: " + e.getMessage());

        } finally {
            // Topic 7 — finally: always ensure feedback label is visible
            if (lblFeedback != null) lblFeedback.setVisible(true);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void showError(String msg) {
        lblFeedback.setStyle(
            "-fx-text-fill:" + StyleHelper.DANGER + ";-fx-font-size:13px;");
        lblFeedback.setText(msg);
    }

    private String joinList(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(list.get(i));
        }
        return sb.toString();
    }
}