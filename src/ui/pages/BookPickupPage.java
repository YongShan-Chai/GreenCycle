package ui.pages;

import data.AppException;
import data.Booking;
import data.DataStore;
import data.Resident;
import data.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.StyleHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * BookPickupPage — MEMBER 2's user pickup booking screen.
 *
 * Lecture reference:
 *   Topic 2 — extends BasePage, @Override build(), super() constructor
 *   Topic 7 — try-catch with MULTIPLE catch blocks, throws AppException,
 *              finally block, custom exception from DataStore
 *   Part 1  — VBox, HBox, GridPane
 *   Part 2  — Lambda event handlers for all buttons
 *   Part 4  — ComboBox (date parts + time + category + location),
 *              RadioButton with ToggleGroup, Label, Button, TextField (read-only)
 *
 * NOTE: Uses THREE ComboBoxes for date (day/month/year) instead of DatePicker,
 *       which is within the lecture scope (Part 4 — ComboBox).
 */
public class BookPickupPage extends BasePage {

    // Part 4 — UI control fields
    private ComboBox<String> cbDay;
    private ComboBox<String> cbMonth;
    private ComboBox<String> cbYear;
    private ComboBox<String> cbTimeSlot;
    private ComboBox<String> cbLocation;
    private RadioButton      rbGeneral;
    private RadioButton      rbRecyclables;
    private RadioButton      rbBulky;
    private ToggleGroup      wasteGroup;
    private Label            lblFeedback;

    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BookPickupPage() { super(); }
    public BookPickupPage(Consumer<String> navigate) { super(navigate); }

    /** Topic 2 — @Override abstract build() from BasePage. */
    @Override
    public Node build() {
        String resId = Session.getLinkedResidentId();
        Resident me  = resId != null ? DataStore.findResidentById(resId) : null;

        // ── Page title ────────────────────────────────────────────────────────
        Label lblTitle = new Label("Book a Pickup");
        lblTitle.setStyle(StyleHelper.pageTitle());
        Label lblSub = new Label("Schedule a waste collection pickup for your unit.");
        lblSub.setStyle(StyleHelper.mutedLabel());
        VBox titleBox = new VBox(4, lblTitle, lblSub);

        // ── Resident info banner ──────────────────────────────────────────────
        VBox infoBanner = buildResidentBanner(me, resId);

        // ── Booking form ──────────────────────────────────────────────────────
        Label lblFormTitle = new Label("Pickup Details");
        lblFormTitle.setStyle(StyleHelper.sectionTitle());

        // Date selection using THREE ComboBoxes (Part 4 — ComboBox)
        cbDay = new ComboBox<String>();
        for (int i = 1; i <= 31; i++) cbDay.getItems().add(String.valueOf(i));
        cbDay.setPromptText("Day");
        cbDay.setPrefWidth(80);

        cbMonth = new ComboBox<String>();
        cbMonth.getItems().addAll("January","February","March","April","May","June",
            "July","August","September","October","November","December");
        cbMonth.setPromptText("Month");
        cbMonth.setPrefWidth(130);

        cbYear = new ComboBox<String>();
        cbYear.getItems().addAll("2026", "2027", "2028");
        cbYear.setPromptText("Year");
        cbYear.setPrefWidth(90);

        // Pre-select tomorrow
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        cbDay.setValue(String.valueOf(tomorrow.getDayOfMonth()));
        cbMonth.setValue(cbMonth.getItems().get(tomorrow.getMonthValue() - 1));
        cbYear.setValue(String.valueOf(tomorrow.getYear()));

        // Part 1 — HBox groups the three date combo boxes
        HBox dateRow = new HBox(8, cbDay, cbMonth, cbYear);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        // Part 4 — ComboBox for time slot
        cbTimeSlot = new ComboBox<String>();
        cbTimeSlot.getItems().addAll(
            "Morning (8-10am)", "Afternoon (1-3pm)", "Evening (5-7pm)");
        cbTimeSlot.setPromptText("Select a time slot");
        cbTimeSlot.setPrefWidth(240);

        // Part 4 — RadioButton with ToggleGroup for waste category
        wasteGroup    = new ToggleGroup();
        rbGeneral     = new RadioButton("General Waste");
        rbRecyclables = new RadioButton("Recyclables  (+20 pts)");
        rbBulky       = new RadioButton("Bulky Items");
        rbGeneral.setToggleGroup(wasteGroup);
        rbRecyclables.setToggleGroup(wasteGroup);
        rbBulky.setToggleGroup(wasteGroup);
        rbGeneral.setSelected(true);
        for (RadioButton rb : new RadioButton[]{rbGeneral, rbRecyclables, rbBulky}) {
            rb.setStyle(StyleHelper.bodyText());
        }
        HBox radioRow = new HBox(24, rbGeneral, rbRecyclables, rbBulky);

        // Part 4 — ComboBox for collection point
        cbLocation = new ComboBox<String>();
        cbLocation.getItems().addAll("Block A Bay", "Block B Bay", "Main Gate");
        cbLocation.setPromptText("Select collection point");
        cbLocation.setPrefWidth(240);

        // Part 1 — GridPane aligns form labels and controls
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(16);
        grid.add(makeFormLabel("Pickup Date :"),      0, 0); grid.add(dateRow,    1, 0);
        grid.add(makeFormLabel("Time Slot :"),         0, 1); grid.add(cbTimeSlot, 1, 1);
        grid.add(makeFormLabel("Waste Category :"),    0, 2); grid.add(radioRow,   1, 2);
        grid.add(makeFormLabel("Collection Point :"),  0, 3); grid.add(cbLocation, 1, 3);

        // Feedback label
        lblFeedback = new Label("");
        lblFeedback.setStyle(StyleHelper.mutedLabel());
        lblFeedback.setWrapText(true);

        Label noteLabel = new Label(
            "Points awarded on completion: Recyclables = 20 pts | Others = 10 pts");
        noteLabel.setStyle("-fx-font-size:11px;-fx-text-fill:" + StyleHelper.TEXT_MUTED +
            ";-fx-font-style:italic;");

        Button btnBook  = makeBtn("Book Pickup");
        Button btnClear = makeGhostBtn("Clear");
        // Part 2 — Lambda event handlers
        btnBook.setOnAction(e -> handleBook(resId, me));
        btnClear.setOnAction(e -> clearForm());

        HBox btnRow = new HBox(12, btnBook, btnClear);

        VBox formCard = makeCard(20);
        formCard.getChildren().addAll(
            lblFormTitle, new Separator(), grid, noteLabel, lblFeedback, btnRow);

        // ── Root ──────────────────────────────────────────────────────────────
        VBox root = new VBox(24, titleBox, infoBanner, formCard);
        root.setPadding(new Insets(32, 36, 32, 36));
        root.setStyle("-fx-background-color:" + StyleHelper.BG + ";");
        return root;
    }

    // ── Resident info banner ──────────────────────────────────────────────────
    private VBox buildResidentBanner(Resident me, String resId) {
        if (me == null) {
            Label warn = new Label("No resident record linked to your account. Contact an administrator.");
            warn.setStyle("-fx-text-fill:" + StyleHelper.DANGER + ";-fx-font-size:13px;");
            VBox box = new VBox(warn);
            box.setPadding(new Insets(16, 20, 16, 20));
            box.setStyle("-fx-background-color:#FEE2E2;-fx-background-radius:10;");
            return box;
        }
        int pts = DataStore.getPointsForResident(resId);

        Label name    = new Label(me.getName());
        name.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:" + StyleHelper.PRIMARY + ";");
        Label details = new Label("ID: " + me.getId() + "   |   Unit: " + me.getUnit());
        details.setStyle(StyleHelper.mutedLabel());

        Label ptsBadge = new Label(pts + " pts  •  " + DataStore.getTier(pts));
        ptsBadge.setStyle("-fx-background-color:" + StyleHelper.ACCENT + ";-fx-text-fill:" +
            StyleHelper.PRIMARY + ";-fx-font-size:12px;-fx-font-weight:bold;" +
            "-fx-background-radius:20;-fx-padding:4 14;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(16, new VBox(4, name, details), spacer, ptsBadge);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox banner = new VBox(row);
        banner.setPadding(new Insets(18, 22, 18, 22));
        banner.setStyle("-fx-background-color:white;-fx-background-radius:12;-fx-border-radius:12;" +
            "-fx-border-color:" + StyleHelper.ACCENT + ";-fx-border-width:2;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.05),10,0,0,2);");
        return banner;
    }

    // ── Book handler — Topic 7: multiple catch blocks, finally ────────────────
    private void handleBook(String resId, Resident me) {
        lblFeedback.setStyle(StyleHelper.mutedLabel());
        lblFeedback.setText("");

        try {
            // Topic 7 — throws AppException on validation failure
            if (me == null) throw new AppException(
                "No resident record linked. Contact an administrator.");

            // Validate date selection
            if (cbDay.getValue() == null || cbMonth.getValue() == null || cbYear.getValue() == null) {
                throw new AppException("Please select a complete date (day, month and year).");
            }

            // Topic 7 — multiple catch: DateTimeException caught separately below
            int day      = Integer.parseInt(cbDay.getValue());
            int monthIdx = cbMonth.getItems().indexOf(cbMonth.getValue()) + 1;
            int year     = Integer.parseInt(cbYear.getValue());
            LocalDate selectedDate = LocalDate.of(year, monthIdx, day); // may throw DateTimeException

            if (selectedDate.isBefore(LocalDate.now())) {
                throw new AppException("Pickup date cannot be in the past. Please select a future date.");
            }

            if (selectedDate.isEqual(LocalDate.now()) && cbTimeSlot.getValue() != null) {
                int currentHour = java.time.LocalTime.now().getHour();
                String slot = cbTimeSlot.getValue();
                if (slot.contains("Morning") && currentHour >= 10) {
                    throw new AppException("Morning slot has already passed for today. Please choose another slot or date.");
                } else if (slot.contains("Afternoon") && currentHour >= 15) {
                    throw new AppException("Afternoon slot has already passed for today. Please choose another slot or date.");
                } else if (slot.contains("Evening") && currentHour >= 19) {
                    throw new AppException("Evening slot has already passed for today. Please choose tomorrow or later.");
                }
            }

            if (cbTimeSlot.getValue() == null) {
                throw new AppException("Please select a time slot.");
            }
            if (cbLocation.getValue() == null) {
                throw new AppException("Please select a collection point.");
            }

            String dateStr  = selectedDate.format(DATE_FMT);
            String timeSlot = cbTimeSlot.getValue();

            // Duplicate booking check
            for (Booking b : DataStore.bookings) {
                if (b.getResidentId().equalsIgnoreCase(resId)
                        && b.getDate().equals(dateStr)
                        && b.getTimeSlot().equals(timeSlot)
                        && !"Cancelled".equals(b.getStatus())) {
                    throw new AppException("You already have a booking on " + dateStr +
                        " at " + timeSlot + ". Please choose a different date or time.");
                }
            }

            String category  = ((RadioButton) wasteGroup.getSelectedToggle())
                .getText().replace("  (+20 pts)", "");
            String bookingId = DataStore.generateBookingId();
            DataStore.bookings.add(new Booking(
                bookingId, resId, me.getName(), dateStr, timeSlot, category, cbLocation.getValue()));

            lblFeedback.setStyle("-fx-text-fill:" + StyleHelper.SUCCESS +
                ";-fx-font-size:13px;-fx-font-weight:bold;");
            lblFeedback.setText("Booking confirmed!  " + bookingId +
                "  |  " + dateStr + "  |  " + timeSlot + "  |  " + category);
            clearForm();

        } catch (AppException e) {
            // Topic 7 — catch custom AppException (validation errors)
            showError(e.getMessage());

        } catch (java.time.DateTimeException e) {
            // Topic 7 — catch invalid date combination (e.g. Feb 30)
            showError("Invalid date combination (e.g. February does not have 30 days). Please check your selection.");

        } catch (NumberFormatException e) {
            // Topic 7 — catch number format issues from Integer.parseInt
            showError("Please select a valid date.");

        } catch (Exception e) {
            // Topic 7 — catch any other unexpected exception
            showError("Unexpected error: " + e.getMessage());

        } finally {
            // Topic 7 — finally: always make the feedback label visible
            if (lblFeedback != null) lblFeedback.setVisible(true);
        }
    }

    private void clearForm() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        cbDay.setValue(String.valueOf(tomorrow.getDayOfMonth()));
        cbMonth.setValue(cbMonth.getItems().get(tomorrow.getMonthValue() - 1));
        cbYear.setValue(String.valueOf(tomorrow.getYear()));
        cbTimeSlot.setValue(null);
        cbLocation.setValue(null);
        rbGeneral.setSelected(true);
    }

    private void showError(String msg) {
        lblFeedback.setStyle("-fx-text-fill:" + StyleHelper.DANGER + ";-fx-font-size:13px;");
        lblFeedback.setText(msg);
    }
}
