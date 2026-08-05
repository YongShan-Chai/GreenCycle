package ui.pages;

import data.AppException;
import data.Booking;
import data.DataStore;
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
 * MyBookingsPage — MEMBER 2's user booking history screen.
 *
 * Shows only the bookings belonging to the logged-in resident.
 *
 * Lecture reference:
 *   Topic 2 — extends BasePage, @Override build(), super()
 *   Topic 7 — try-catch-finally, AppException for cancel validation
 *   Part 1  — VBox, HBox layout panes
 *   Part 2  — Lambda event handlers for filter, refresh, cancel
 *   Part 4  — ListView (Part 4), ComboBox (filter), Button, Label
 */
public class MyBookingsPage extends BasePage {

    // Part 4 — UI control fields
    private ListView<String>  lvBookings;
    private ArrayList<Booking> displayedBookings = new ArrayList<Booking>();
    private ComboBox<String>   cbFilter;
    private Label              lblCount;
    private Label              lblPillPending;
    private Label              lblPillCompleted;
    private Label              lblPillCancelled;
    private String             resId;

    public MyBookingsPage() { super(); }
    public MyBookingsPage(Consumer<String> navigate) { super(navigate); }

    /** Topic 2 — @Override abstract build() from BasePage. */
    @Override
    public Node build() {
        resId = Session.getLinkedResidentId();

        // ── Page title ────────────────────────────────────────────────────────
        Label lblTitle = new Label("My Bookings");
        lblTitle.setStyle(StyleHelper.pageTitle());
        Label lblSub = new Label("View and manage your waste pickup bookings.");
        lblSub.setStyle(StyleHelper.mutedLabel());
        VBox titleBox = new VBox(4, lblTitle, lblSub);

        // ── Status summary pills ──────────────────────────────────────────────
        lblPillPending   = pill("Pending: "   + countByStatus("Pending"),   "#FEF3C7", "#D97706");
        lblPillCompleted = pill("Completed: " + countByStatus("Completed"),  "#DCFCE7", "#15803D");
        lblPillCancelled = pill("Cancelled: " + countByStatus("Cancelled"),  "#FEE2E2", "#B91C1C");
        HBox pills = new HBox(10, lblPillPending, lblPillCompleted, lblPillCancelled);

        // ── Toolbar — Part 4: ComboBox, Part 2: Lambda ────────────────────────
        Label lblFilterLbl = new Label("Filter by Status:");
        lblFilterLbl.setStyle(StyleHelper.bodyText());

        // Part 4 — ComboBox
        cbFilter = new ComboBox<String>();
        cbFilter.getItems().addAll("All", "Pending", "Completed", "Cancelled");
        cbFilter.setValue("All");
        cbFilter.setPrefWidth(150);
        // Part 2 — Lambda: reload on filter change
        cbFilter.setOnAction(e -> loadList());

        Button btnRefresh = makeSecBtn("Refresh");
        // Part 2 — Lambda
        btnRefresh.setOnAction(e -> loadList());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox toolbar = new HBox(12, lblFilterLbl, cbFilter, btnRefresh, spacer, pills);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // ── ListView — Part 4 ─────────────────────────────────────────────────
        Label lvHeader = makeListHeader(
            String.format("  %-7s  %-11s  %-18s  %-13s  %-15s  %-10s  %s",
                "ID", "Date", "Time Slot", "Category", "Location", "Status", "Points"));

        lvBookings = makeListView(390);

        // ── Count label ───────────────────────────────────────────────────────
        lblCount = new Label();
        lblCount.setStyle(StyleHelper.mutedLabel());

        // Load data AFTER lblCount is initialised (avoids NullPointerException)
        loadList();

        // ── Action buttons — Part 4: Button ───────────────────────────────────
        Button btnCancel = makeDangerBtn("Cancel Selected Booking");
        // Part 2 — Lambda event handler
        btnCancel.setOnAction(e -> handleCancel());

        Label lblHint = new Label("Only Pending bookings can be cancelled.");
        lblHint.setStyle(StyleHelper.mutedLabel());

        Region bSpacer = new Region();
        HBox.setHgrow(bSpacer, Priority.ALWAYS);
        HBox bottomBar = new HBox(12, lblCount, bSpacer, lblHint, btnCancel);
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        // ── Card wrapper ──────────────────────────────────────────────────────
        VBox card = makeCard(14);
        card.getChildren().addAll(toolbar, lvHeader, lvBookings,
            new Separator(), bottomBar);

        VBox root = new VBox(24, titleBox, card);
        root.setPadding(new Insets(32, 36, 32, 36));
        root.setStyle("-fx-background-color:" + StyleHelper.BG + ";");
        return root;
    }

    // ── Load ListView filtered by status and this user's resId ───────────────
    private void loadList() {
        String filter = cbFilter.getValue();
        lvBookings.getItems().clear();
        displayedBookings.clear();

        for (Booking b : DataStore.bookings) {
            boolean mine    = b.getResidentId().equalsIgnoreCase(resId != null ? resId : "");
            boolean matches = "All".equals(filter) || b.getStatus().equals(filter);
            if (mine && matches) {
                String ptsStr = b.getPoints() > 0 ? b.getPoints() + " pts" : "—";
                lvBookings.getItems().add(String.format(
                    "  %-7s  %-11s  %-18s  %-13s  %-15s  %-10s  %s",
                    b.getBookingId(), b.getDate(),
                    truncate(b.getTimeSlot(), 18),
                    truncate(b.getWasteCategory(), 13),
                    truncate(b.getCollectionPoint(), 15),
                    b.getStatus(), ptsStr));
                displayedBookings.add(b);
            }
        }
        lvBookings.getSelectionModel().clearSelection();
        if (lblCount != null) {
            lblCount.setText("Showing " + displayedBookings.size() +
                " of " + countTotal() + " booking(s).");
        }
        if (lblPillPending != null) {
            lblPillPending.setText("Pending: "   + countByStatus("Pending"));
            lblPillCompleted.setText("Completed: " + countByStatus("Completed"));
            lblPillCancelled.setText("Cancelled: " + countByStatus("Cancelled"));
        }
    }

    private Booking getSelected() {
        int idx = lvBookings.getSelectionModel().getSelectedIndex();
        return (idx >= 0 && idx < displayedBookings.size())
            ? displayedBookings.get(idx) : null;
    }

    // ── Cancel handler — Topic 7: try-catch-finally ───────────────────────────
    private void handleCancel() {
        Booking sel = null;
        try {
            // Topic 7 — throws AppException for invalid input
            sel = getSelected();
            if (sel == null) {
                throw new AppException("Please select a booking row first.");
            }
            if ("Completed".equals(sel.getStatus())) {
                throw new AppException("A completed booking cannot be cancelled.");
            }
            if ("Cancelled".equals(sel.getStatus())) {
                throw new AppException("This booking is already cancelled.");
            }

            final Booking toCancel = sel;
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Cancellation");
            confirm.setHeaderText(null);
            confirm.setContentText(
                "Cancel booking " + sel.getBookingId() + "?\n" +
                "Date: " + sel.getDate() + " at " + sel.getTimeSlot() + "\n\n" +
                "This action cannot be undone.");

            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    // Update in DataStore
                    for (Booking b : DataStore.bookings) {
                        if (b.getBookingId().equals(toCancel.getBookingId())) {
                            b.setStatus("Cancelled");
                            break;
                        }
                    }
                    // Reset to "All" so the Cancelled row stays visible
                    cbFilter.setValue("All");
                    loadList();
                    showAlert(Alert.AlertType.INFORMATION,
                        "Booking Cancelled",
                        "Booking " + toCancel.getBookingId() + " has been cancelled.");
                }
            });

        } catch (AppException e) {
            // Topic 7 — catch custom AppException
            showAlert(Alert.AlertType.WARNING, "Cannot Cancel", e.getMessage());

        } catch (Exception e) {
            // Topic 7 — catch any other unexpected exception
            showAlert(Alert.AlertType.ERROR, "Error",
                "An unexpected error occurred: " + e.getMessage());

        } finally {
            // Topic 7 — finally: always clear selection after any action attempt
            if (lvBookings != null) lvBookings.getSelectionModel().clearSelection();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private int countByStatus(String status) {
        int c = 0;
        for (Booking b : DataStore.bookings) {
            if (b.getResidentId().equalsIgnoreCase(resId != null ? resId : "")
                    && status.equals(b.getStatus())) c++;
        }
        return c;
    }

    private int countTotal() {
        int c = 0;
        for (Booking b : DataStore.bookings) {
            if (b.getResidentId().equalsIgnoreCase(resId != null ? resId : "")) c++;
        }
        return c;
    }

    private Label pill(String text, String bg, String fg) {
        Label l = new Label(text);
        l.setStyle("-fx-background-color:" + bg + ";-fx-text-fill:" + fg + ";" +
            "-fx-background-radius:20;-fx-padding:4 12;" +
            "-fx-font-size:12px;-fx-font-weight:bold;");
        return l;
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "." : s;
    }
}
