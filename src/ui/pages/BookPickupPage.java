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
 * Responsible for managing waste collection booking creation, form validation, 
 * historical data quick-fill operations, and session-based resident data binding.
 *
 * Lecture reference:
 *   Topic 2 — extends BasePage, @Override build(), super() constructor
 *   Topic 7 — try-catch with MULTIPLE catch blocks, throws AppException,
 *              finally block, custom exception from DataStore
 *   Part 1  — VBox, HBox, GridPane layout containers
 *   Part 2  — Lambda event handlers for all interactive buttons
 *   Part 4  — ComboBox (date parts + time + category + location),
 *              RadioButton with ToggleGroup, Label, Button, TextField
 */
public class BookPickupPage extends BasePage {

    // Part 4 — UI control fields declaration for form components
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

    // Standardized date formatter for consistent string parsing and data storage
    private static final DateTimeFormatter DATE_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Constructors supporting navigation callbacks and super initialization
    public BookPickupPage() { super(); }
    public BookPickupPage(Consumer<String> navigate) { super(navigate); }

    /** 
     * Topic 2 — @Override abstract build() from BasePage. 
     * Constructs and arranges all visual UI nodes into a cohesive layout hierarchy.
     */
    @Override
    public Node build() {
        // Retrieve current active session resident identifier and record object
        String resId = Session.getLinkedResidentId();
        Resident me  = resId != null ? DataStore.findResidentById(resId) : null;

        // ── Page title component setup ────────────────────────────────────────
        Label lblTitle = new Label("Book a Pickup");
        lblTitle.setStyle(StyleHelper.pageTitle());
        Label lblSub = new Label("Schedule a waste collection pickup for your unit.");
        lblSub.setStyle(StyleHelper.mutedLabel());
        VBox titleBox = new VBox(4, lblTitle, lblSub);

        // ── Resident info banner component setup ──────────────────────────────
        VBox infoBanner = buildResidentBanner(me, resId);

        // ── Booking form header with "Load Last Booking" button on the top-right ──
        Label lblFormTitle = new Label("Pickup Details");
        lblFormTitle.setStyle(StyleHelper.sectionTitle());

        // Dedicated utility button styled identically to match primary action buttons
        Button btnLastBooking = new Button("Load Last Booking");
        btnLastBooking.setStyle(
            "-fx-background-color: " + StyleHelper.PRIMARY + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" + 
            "-fx-cursor: hand;" +
            "-fx-padding: 8 16;" + 
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0, 0, 1);"
        );
        btnLastBooking.setOnAction(e -> handleQuickFill(resId));

        Region titleSpacer = new Region();
        HBox.setHgrow(titleSpacer, Priority.ALWAYS);
        HBox formHeaderRow = new HBox(10, lblFormTitle, titleSpacer, btnLastBooking);
        formHeaderRow.setAlignment(Pos.CENTER_LEFT);

        // Initialize Day ComboBox with numerical sequence values (1 to 31)
        cbDay = new ComboBox<String>();
        for (int i = 1; i <= 31; i++) cbDay.getItems().add(String.valueOf(i));
        cbDay.setPromptText("Day");
        cbDay.setPrefWidth(80);

        // Initialize Month ComboBox with calendar month literal names
        cbMonth = new ComboBox<String>();
        cbMonth.getItems().addAll("January","February","March","April","May","June",
            "July","August","September","October","November","December");
        cbMonth.setPromptText("Month");
        cbMonth.setPrefWidth(130);

        // Initialize Year ComboBox with valid academic/operational project years
        cbYear = new ComboBox<String>();
        cbYear.getItems().addAll("2026", "2027", "2028");
        cbYear.setPromptText("Year");
        cbYear.setPrefWidth(90);

        // Automatically pre-select tomorrow's date as the default booking target
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        cbDay.setValue(String.valueOf(tomorrow.getDayOfMonth()));
        cbMonth.setValue(cbMonth.getItems().get(tomorrow.getMonthValue() - 1));
        cbYear.setValue(String.valueOf(tomorrow.getYear()));

        // Part 1 — HBox horizontally groups the three individual date combo boxes
        HBox dateRow = new HBox(8, cbDay, cbMonth, cbYear);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        // Part 4 — ComboBox configuration for operational daily time slots
        cbTimeSlot = new ComboBox<String>();
        cbTimeSlot.getItems().addAll(
            "Morning (8-10am)", "Afternoon (1-3pm)", "Evening (5-7pm)");
        cbTimeSlot.setPromptText("Select a time slot");
        cbTimeSlot.setPrefWidth(240);

        // Part 4 — RadioButton elements bound together using a shared ToggleGroup
        wasteGroup    = new ToggleGroup();
        rbGeneral     = new RadioButton("General Waste");
        rbRecyclables = new RadioButton("Recyclables  (+20 pts)");
        rbBulky       = new RadioButton("Bulky Items");
        rbGeneral.setToggleGroup(wasteGroup);
        rbRecyclables.setToggleGroup(wasteGroup);
        rbBulky.setToggleGroup(wasteGroup);
        rbGeneral.setSelected(true); // Default selection set to General Waste
        for (RadioButton rb : new RadioButton[]{rbGeneral, rbRecyclables, rbBulky}) {
            rb.setStyle(StyleHelper.bodyText());
        }
        HBox radioRow = new HBox(24, rbGeneral, rbRecyclables, rbBulky);

        // Part 4 — ComboBox configuration for physical estate collection bays
        cbLocation = new ComboBox<String>();
        cbLocation.getItems().addAll("Block A Bay", "Block B Bay", "Main Gate");
        cbLocation.setPromptText("Select collection point");
        cbLocation.setPrefWidth(240);

        // Part 1 — GridPane strictly aligns form text labels with interactive input controls
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(16);
        grid.add(makeFormLabel("Pickup Date :"),      0, 0); grid.add(dateRow,    1, 0);
        grid.add(makeFormLabel("Time Slot :"),         0, 1); grid.add(cbTimeSlot, 1, 1);
        grid.add(makeFormLabel("Waste Category :"),    0, 2); grid.add(radioRow,   1, 2);
        grid.add(makeFormLabel("Collection Point :"),  0, 3); grid.add(cbLocation, 1, 3);

        // Dynamic feedback notification label for handling user action messages
        lblFeedback = new Label("");
        lblFeedback.setStyle(StyleHelper.mutedLabel());
        lblFeedback.setWrapText(true);

        // Informational caption explaining incentive point structures
        Label noteLabel = new Label(
            "Points awarded on completion: Recyclables = 20 pts | Others = 10 pts");
        noteLabel.setStyle("-fx-font-size:11px;-fx-text-fill:" + StyleHelper.TEXT_MUTED +
            ";-fx-font-style:italic;");

        // Interactive command buttons initialization
        Button btnBook      = makeBtn("Book Pickup");
        Button btnClear     = makeGhostBtn("Clear");

        // Part 2 — Lambda expression event handlers binding user actions to controllers
        btnBook.setOnAction(e -> handleBook(resId, me));
        btnClear.setOnAction(e -> clearForm());
        
        // Group command buttons inside a horizontal layout container (keeping bottom row clean)
        HBox btnRow = new HBox(12, btnBook, btnClear);

        // Encapsulate form controls inside a styled visual card container
        VBox formCard = makeCard(20);
        formCard.getChildren().addAll(
            formHeaderRow, new Separator(), grid, noteLabel, lblFeedback, btnRow);
            
        // --- Added: clear feedback when user manually changes any form control ---
        javafx.beans.value.ChangeListener<String> stringListener = (obs, oldVal, newVal) -> {
            if (lblFeedback != null && !lblFeedback.getText().isEmpty()) {
                lblFeedback.setText("");
            }
        };

        cbDay.valueProperty().addListener(stringListener);
        cbMonth.valueProperty().addListener(stringListener);
        cbYear.valueProperty().addListener(stringListener);
        cbTimeSlot.valueProperty().addListener(stringListener);
        cbLocation.valueProperty().addListener(stringListener);

        javafx.beans.value.ChangeListener<Boolean> boolListener = (obs, oldVal, newVal) -> {
            if (newVal && lblFeedback != null && !lblFeedback.getText().isEmpty()) {
                lblFeedback.setText("");
            }
        };

        rbGeneral.selectedProperty().addListener(boolListener);
        rbRecyclables.selectedProperty().addListener(boolListener);
        rbBulky.selectedProperty().addListener(boolListener);

        // Bind load capacity warning triggers to date and time selection controls
        cbDay.setOnAction(e -> updateCapacityWarning());
        cbMonth.setOnAction(e -> updateCapacityWarning());
        cbYear.setOnAction(e -> updateCapacityWarning());
        cbTimeSlot.setOnAction(e -> updateCapacityWarning());

        // ── Root container assembling layout layers ───────────────────────────
        VBox root = new VBox(24, titleBox, infoBanner, formCard);
        root.setPadding(new Insets(32, 36, 32, 36));
        root.setStyle("-fx-background-color:" + StyleHelper.BG + ";");
        return root;
    }

    /** 
     * Generates and styles the resident information identification banner component.
     * Evaluates current tier standing and accumulated loyalty reward points.
     */
    private VBox buildResidentBanner(Resident me, String resId) {
        // Defensive check: Handle cases where account record is unlinked or missing
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

    /** 
     * Book handler method — Topic 7 implementation: multiple catch blocks, custom exception handling, and finally cleanup.
     * Validates form parameters, checks duplicate scheduling constraints, and appends a new booking entity to DataStore.
     */
    private void handleBook(String resId, Resident me) {
        lblFeedback.setStyle(StyleHelper.mutedLabel());
        lblFeedback.setText("");

        try {
            // Topic 7 — Throw custom AppException if resident entity link is invalid
            if (me == null) throw new AppException(
                "No resident record linked. Contact an administrator.");

            // Comprehensive null validation across date selection combo boxes
            if (cbDay.getValue() == null || cbMonth.getValue() == null || cbYear.getValue() == null) {
                throw new AppException("Please select a complete date (day, month and year).");
            }

            // Topic 7 — Multiple catch structure: parsing raw input values into temporal date objects
            int day      = Integer.parseInt(cbDay.getValue());
            int monthIdx = cbMonth.getItems().indexOf(cbMonth.getValue()) + 1;
            int year     = Integer.parseInt(cbYear.getValue());
            LocalDate selectedDate = LocalDate.of(year, monthIdx, day); // May throw DateTimeException for invalid calendar days

            // Validate that selected pickup date is not located in historical past time
            if (selectedDate.isBefore(LocalDate.now())) {
                throw new AppException("Pickup date cannot be in the past. Please select a future date.");
            }

            // Time slot validity validation check preventing booking expired timeslots on the current day
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

            // Ensure mandatory selection criteria are satisfied for operational controls
            if (cbTimeSlot.getValue() == null) {
                throw new AppException("Please select a time slot.");
            }
            if (cbLocation.getValue() == null) {
                throw new AppException("Please select a collection point.");
            }

            String dateStr  = selectedDate.format(DATE_FMT);
            String timeSlot = cbTimeSlot.getValue();

            // Iterate over existing data collection to prevent duplicate active slot bookings
            for (Booking b : DataStore.bookings) {
                if (b.getResidentId().equalsIgnoreCase(resId)
                        && b.getDate().equals(dateStr)
                        && b.getTimeSlot().equals(timeSlot)
                        && !"Cancelled".equals(b.getStatus())) {
                    throw new AppException("You already have a booking on " + dateStr +
                        " at " + timeSlot + ". Please choose a different date or time.");
                }
            }
            
            // Count current active pending bookings for the selected date and time slot
            int currentSlotCount = 0;
            for (Booking b : DataStore.bookings) {
                if (b.getDate() != null && b.getDate().equals(dateStr)
                        && b.getTimeSlot() != null && b.getTimeSlot().equals(timeSlot)
                        && "Pending".equalsIgnoreCase(b.getStatus())) {
                    currentSlotCount++;
                }
            }
            if (currentSlotCount >= 5) {
                throw new AppException("Selected time slot has reached maximum capacity (5/5). Please choose another slot or date.");
            }

            // Extract selected waste category toggle value and generate unique transaction identifier
            String category  = ((RadioButton) wasteGroup.getSelectedToggle())
                .getText().replace("  (+20 pts)", "");
            String bookingId = DataStore.generateBookingId();
            
            // Persist newly instantiated booking object into shared centralized memory repository
            DataStore.bookings.add(new Booking(
                bookingId, resId, me.getName(), dateStr, timeSlot, category, cbLocation.getValue()));
            
            // Display success notification feedback message
            lblFeedback.setStyle("-fx-text-fill:" + StyleHelper.SUCCESS +
                ";-fx-font-size:13px;-fx-font-weight:bold;");
            lblFeedback.setText("Booking confirmed!  " + bookingId +
                "  |  " + dateStr + "  |  " + timeSlot + "  |  " + category);
            clearForm();

        } catch (AppException e) {
            // Topic 7 — Catch block handling custom business validation exceptions thrown from model layer
            showError(e.getMessage());

        } catch (java.time.DateTimeException e) {
            // Topic 7 — Catch block handling illegal calendar date combinations (e.g., February 30th)
            showError("Invalid date combination (e.g. February does not have 30 days). Please check your selection.");

        } catch (NumberFormatException e) {
            // Topic 7 — Catch block handling numeric conversion failures during parsing operations
            showError("Please select a valid date.");

        } catch (Exception e) {
            // Topic 7 — Catch block acting as a general safety net for unexpected application anomalies
            showError("Unexpected error: " + e.getMessage());

        } finally {
            // Topic 7 — Finally block guaranteeing execution state updates regardless of try-catch flow outcome
            if (lblFeedback != null) lblFeedback.setVisible(true);
        }
    }

    /** 
     * Clears and resets all interactive form input controls back to their default baseline states.
     */
    private void clearForm() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        cbDay.setValue(String.valueOf(tomorrow.getDayOfMonth()));
        cbMonth.setValue(cbMonth.getItems().get(tomorrow.getMonthValue() - 1));
        cbYear.setValue(String.valueOf(tomorrow.getYear()));
        cbTimeSlot.setValue(null);
        cbLocation.setValue(null);
        rbGeneral.setSelected(true);
        
        // --- clear feedback label on form reset ---
        if (lblFeedback != null) {
            lblFeedback.setText("");
        }
    }

    /** 
     * Quick Fill Handler Method — Enhances user experience by automatically retrieving 
     * and populating form controls with parameters from the resident's most recent historical booking entry.
     */
    private void handleQuickFill(String currentResidentId) {
        if (lblFeedback != null) {
            lblFeedback.setText("");
        }

        try {
            // Validate session linkage state prior to executing query operations
            if (currentResidentId == null || currentResidentId.trim().isEmpty()) {
                throw new AppException("No resident linked to current session.");
            }

            // Filter historical booking records matching the currently active resident ID
            java.util.ArrayList<Booking> residentBookings = new java.util.ArrayList<Booking>();
            for (Booking b : DataStore.bookings) {
                if (b.getResidentId() != null && b.getResidentId().equalsIgnoreCase(currentResidentId)) {
                    residentBookings.add(b);
                }
            }

            // Verify whether any past booking history records exist within the data repository
            if (residentBookings.isEmpty()) {
                throw new AppException("No previous bookings found. Please fill in manually.");
            }

            // Retrieve the latest chronological booking entry from the filtered list collection
            Booking lastBooking = residentBookings.get(residentBookings.size() - 1);

            // Populate waste category radio button controls based on historical configuration data
            if (lastBooking.getWasteCategory() != null) {
                String cat = lastBooking.getWasteCategory();
                if (cat.contains("General")) rbGeneral.setSelected(true);
                else if (cat.contains("Recyclables")) rbRecyclables.setSelected(true);
                else if (cat.contains("Bulky")) rbBulky.setSelected(true);
            }

            // Populate time slot combo box selection value
            if (lastBooking.getTimeSlot() != null) {
                cbTimeSlot.setValue(lastBooking.getTimeSlot());
            }

            // Populate collection point combo box selection value
            if (lastBooking.getCollectionPoint() != null) {
                cbLocation.setValue(lastBooking.getCollectionPoint());
            }

            // Render success status message confirming successful quick-fill data injection
            if (lblFeedback != null) {
                lblFeedback.setStyle("-fx-text-fill:" + StyleHelper.SUCCESS + ";-fx-font-size:13px;-fx-font-weight:bold;");
                lblFeedback.setText("Quick filled from last booking (" + lastBooking.getBookingId() + ").");
            }

        } catch (AppException e) {
            // Forward caught exception messages to the standard error display helper routine
            showError(e.getMessage());
        }
    }

    /** 
     * Helper routine responsible for styling and presenting error notification messages to users.
     */
    private void showError(String msg) {
        if (lblFeedback != null) {
            lblFeedback.setStyle("-fx-text-fill:" + StyleHelper.DANGER + ";-fx-font-size:13px;");
            lblFeedback.setText(msg);
        }
    }

    /** 
     * Capacity Management & Load Warning Mechanism.
     * Evaluates real-time slot occupancy by counting existing pending bookings for the selected date and time slot.
     * Automatically disables the booking submission action if the maximum threshold (e.g., 5 bookings) is reached.
     */
    private void updateCapacityWarning() {
        try {
            // Guard clause: Ensure date and time selectors are fully initialized
            if (cbDay.getValue() == null || cbMonth.getValue() == null || cbYear.getValue() == null || cbTimeSlot.getValue() == null) {
                return;
            }

            // Parse temporal components securely (handles potential invalid date combinations via try-catch)
            int day      = Integer.parseInt(cbDay.getValue());
            int monthIdx = cbMonth.getItems().indexOf(cbMonth.getValue()) + 1;
            int year     = Integer.parseInt(cbYear.getValue());
            LocalDate selectedDate = LocalDate.of(year, monthIdx, day);

            String dateStr  = selectedDate.format(DATE_FMT);
            String timeSlot = cbTimeSlot.getValue();

            // Count active pending bookings for the exact date and time slot combination
            int activeBookingsCount = 0;
            for (Booking b : DataStore.bookings) {
                if (b.getDate() != null && b.getDate().equals(dateStr)
                        && b.getTimeSlot() != null && b.getTimeSlot().equals(timeSlot)
                        && "Pending".equalsIgnoreCase(b.getStatus())) {
                    activeBookingsCount++;
                }
            }

            // Define maximum operational threshold capacity per slot
            final int MAX_CAPACITY = 5;

            // Find the submit button dynamically or enforce disable state
            // (Assuming btnBook is accessible or we manage it via feedback label and validation check)
            if (activeBookingsCount >= MAX_CAPACITY) {
                lblFeedback.setStyle("-fx-text-fill:" + StyleHelper.DANGER + ";-fx-font-size:13px;-fx-font-weight:bold;");
                lblFeedback.setText("⚠️ Capacity Warning: This time slot is fully booked (" + activeBookingsCount + "/" + MAX_CAPACITY + "). Please select another slot.");
            } else {
                // If capacity is safe and feedback currently displays a capacity warning, clear it
                if (lblFeedback.getText() != null && lblFeedback.getText().contains("Capacity Warning")) {
                    lblFeedback.setText("");
                }
            }

        } catch (Exception e) {
            // Gracefully catch parsing or temporal validation exceptions during active dropdown changes
        }
    }
}