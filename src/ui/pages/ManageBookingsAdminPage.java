package ui.pages;

import data.AppException;
import data.Booking;
import data.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.StyleHelper;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * ManageBookingsAdminPage — MEMBER 2's admin booking management screen.
 *
 * Lecture reference:
 *   Topic 2 — extends BasePage, @Override build()
 *   Topic 7 — try-catch, AppException for action validation
 *   Part 1  — VBox, HBox layout panes
 *   Part 2  — Lambda event handlers for filter, complete, cancel
 *   Part 4  — ListView, ComboBox (filter), Button, Label
 */
public class ManageBookingsAdminPage extends BasePage {

    // Part 4 — UI control fields
    private ListView<String>  lvBookings;
    private ArrayList<Booking> displayedBookings = new ArrayList<Booking>();
    private ComboBox<String>   cbFilter;
    private Label              lblCount;
    private Label              lblPillPending;
    private Label              lblPillCompleted;
    private Label              lblPillCancelled;
    private TextField          txtSearch;

    public ManageBookingsAdminPage() { super(); }
    public ManageBookingsAdminPage(Consumer<String> navigate) { super(navigate); }

    /** Topic 2 — @Override abstract build() from BasePage. */
    @Override
    public Node build() {
        // ── Page title ────────────────────────────────────────────────────────
        Label lblTitle = new Label("Manage Bookings");
        lblTitle.setStyle(StyleHelper.pageTitle());
        Label lblSub = new Label("Review, complete, or cancel waste collection bookings.");
        lblSub.setStyle(StyleHelper.mutedLabel());
        VBox titleBox = new VBox(4, lblTitle, lblSub);

        // ── Summary pills — Part 1: HBox ──────────────────────────────────────
        lblPillPending   = pill("Pending: "   + count("Pending"),   "#FEF3C7", "#D97706");
        lblPillCompleted = pill("Completed: " + count("Completed"),  "#DCFCE7", "#15803D");
        lblPillCancelled = pill("Cancelled: " + count("Cancelled"),  "#FEE2E2", "#B91C1C");
        HBox pills = new HBox(10, lblPillPending, lblPillCompleted, lblPillCancelled);

        // ── Toolbar — Part 4: ComboBox for filter ─────────────────────────────
        Label lblFilterLbl = new Label("Filter:");
        lblFilterLbl.setStyle(StyleHelper.bodyText());
        txtSearch = makeField("Search by resident name or booking ID...");
        txtSearch.setPrefWidth(255);
        txtSearch.setOnAction(e -> handleSearch());   // press Enter to search

        // Part 4 — ComboBox
        cbFilter = new ComboBox<String>();
        cbFilter.getItems().addAll("All", "Pending", "Completed", "Cancelled");
        cbFilter.setValue("All");
        cbFilter.setPrefWidth(150);
        // Part 2 — Lambda: re-load list on filter change
        cbFilter.setOnAction(e -> loadList());

        Button btnRefresh = makeSecBtn("Refresh");
        btnRefresh.setOnAction(e -> loadList());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnSearch = makeSecBtn("Search");
        Button btnClearS = makeGhostBtn("Clear");
        btnSearch.setOnAction(e -> handleSearch());
        btnClearS.setOnAction(e -> { txtSearch.clear(); cbFilter.setValue("All"); loadList(); });
        
        // Row 1: search controls
        HBox searchRow = new HBox(10, txtSearch, btnSearch, btnClearS);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        // Row 2: filter + pills
        HBox filterRow = new HBox(10, lblFilterLbl, cbFilter, btnRefresh, spacer, pills);
        filterRow.setAlignment(Pos.CENTER_LEFT);

        // Stack both rows vertically
        VBox toolbar = new VBox(10, searchRow, filterRow);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // ── ListView — Part 4 ─────────────────────────────────────────────────
        Label lvHeader = makeListHeader(
            String.format("  %-7s  %-18s  %-11s  %-18s  %-13s  %-10s  %s",
                "ID", "Resident", "Date", "Time Slot", "Category", "Status", "Pts"));

        lvBookings = makeListView(400);
        lblCount = new Label();
        lblCount.setStyle(StyleHelper.mutedLabel());

        // Load data AFTER lblCount is initialised
        loadList();

        // ── Action buttons — Part 4: Button ───────────────────────────────────
        Button btnComplete = makeBtn("Mark as Completed");
        Button btnCancel   = makeDangerBtn("Cancel Booking");
        Label  lblHint     = new Label("Select a row, then click an action.");
        lblHint.setStyle(StyleHelper.mutedLabel());

        // Part 2 — Lambda event handlers
        btnComplete.setOnAction(e -> handleMarkComplete());
        btnCancel.setOnAction(e   -> handleCancel());

        Region bSpacer = new Region();
        HBox.setHgrow(bSpacer, Priority.ALWAYS);
        HBox actionBar = new HBox(12, lblHint, bSpacer, btnComplete, btnCancel);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        // ── Card wrapper ──────────────────────────────────────────────────────
        VBox card = makeCard(14);
        card.getChildren().addAll(toolbar, lvHeader, lvBookings, lblCount,
            new Separator(), actionBar);

        VBox root = new VBox(24, titleBox, card);
        root.setPadding(new Insets(32, 36, 32, 36));
        root.setStyle("-fx-background-color:" + StyleHelper.BG + ";");
        return root;
    }

    // ── Load / reload ListView with current filter ────────────────────────────
    private void loadList() {
        String filter = cbFilter.getValue();
        lvBookings.getItems().clear();
        displayedBookings.clear();
        for (Booking b : DataStore.bookings) {
            if ("All".equals(filter) || b.getStatus().equals(filter)) {
                lvBookings.getItems().add(b.getListEntry());
                displayedBookings.add(b);
            }
        }
        lvBookings.getSelectionModel().clearSelection();
        if (lblCount != null) {
            lblCount.setText("Showing " + displayedBookings.size() +
                " of " + DataStore.bookings.size() + " booking(s).");
        }
        
        if (lblPillPending != null) {
            lblPillPending.setText("Pending: "   + count("Pending"));
            lblPillCompleted.setText("Completed: " + count("Completed"));
            lblPillCancelled.setText("Cancelled: " + count("Cancelled"));
        }
    }

    private Booking getSelected() {
        int idx = lvBookings.getSelectionModel().getSelectedIndex();
        return (idx >= 0 && idx < displayedBookings.size())
            ? displayedBookings.get(idx) : null;
    }

    // ── Mark Complete — Topic 7: try-catch, AppException ─────────────────────
    private void handleMarkComplete() {
        try {
            Booking sel = getSelected();
            if (sel == null) throw new AppException("Please select a booking row first.");
            if ("Completed".equals(sel.getStatus()))
                throw new AppException("This booking is already marked as Completed.");
            if ("Cancelled".equals(sel.getStatus()))
                throw new AppException("A cancelled booking cannot be marked as Completed.");

            int pts = "Recyclables".equals(sel.getWasteCategory()) ? 20 : 10;
            // Update the actual object in DataStore
            for (Booking b : DataStore.bookings) {
                if (b.getBookingId().equals(sel.getBookingId())) {
                    b.setStatus("Completed");
                    b.setPoints(pts);
                    break;
                }
            }
            // Reset filter to "All" so updated row stays visible
            cbFilter.setValue("All");
            loadList();
            showAlert(Alert.AlertType.INFORMATION, "Booking Completed",
                "Booking " + sel.getBookingId() + " marked as Completed.\n" +
                pts + " recycling points awarded to " + sel.getResidentName() + ".");

        } catch (AppException e) {
            showAlert(Alert.AlertType.WARNING, "Cannot Complete", e.getMessage());
        }
    }

    // ── Cancel booking — Topic 7: try-catch, multiple catch, finally ──────────
    private void handleCancel() {
        Booking sel = null;
        try {
            sel = getSelected();
            if (sel == null) throw new AppException("Please select a booking row first.");
            if ("Cancelled".equals(sel.getStatus()))
                throw new AppException("This booking is already cancelled.");
            if ("Completed".equals(sel.getStatus()))
                throw new AppException("A completed booking cannot be cancelled.");

            final Booking toCancel = sel;
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Cancellation");
            confirm.setHeaderText(null);
            confirm.setContentText("Cancel booking " + sel.getBookingId() +
                " for " + sel.getResidentName() + "?\nThis cannot be undone.");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    for (Booking b : DataStore.bookings) {
                        if (b.getBookingId().equals(toCancel.getBookingId())) {
                            b.setStatus("Cancelled"); break;
                        }
                    }
                    cbFilter.setValue("All");
                    loadList();
                    showAlert(Alert.AlertType.INFORMATION, "Cancelled",
                        "Booking " + toCancel.getBookingId() + " has been cancelled.");
                }
            });

        } catch (AppException e) {
            // Topic 7 — catch AppException
            showAlert(Alert.AlertType.WARNING, "Cannot Cancel", e.getMessage());
        } catch (Exception e) {
            // Topic 7 — catch any other unexpected exception
            showAlert(Alert.AlertType.ERROR, "Error", "Unexpected error: " + e.getMessage());
        } finally {
            // Topic 7 — finally: always clear the selection after any action
            if (lvBookings != null) lvBookings.getSelectionModel().clearSelection();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private int count(String status) {
        int c = 0;
        for (Booking b : DataStore.bookings) if (status.equals(b.getStatus())) c++;
        return c;
    }
    
    private void handleSearch() {
        String q = txtSearch.getText().trim().toLowerCase();
        if (q.isEmpty()) { loadList(); return; }

        String filter = cbFilter.getValue();
        lvBookings.getItems().clear();
        displayedBookings.clear();

        for (Booking b : DataStore.bookings) {
            boolean matchesFilter = "All".equals(filter) || b.getStatus().equals(filter);
            boolean matchesSearch  = b.getResidentName().toLowerCase().contains(q)
                                   || b.getBookingId().toLowerCase().contains(q);
            if (matchesFilter && matchesSearch) {
                lvBookings.getItems().add(b.getListEntry());
                displayedBookings.add(b);
            }
        }
        lvBookings.getSelectionModel().clearSelection();
        if (lblCount != null) {
            lblCount.setText("Search results: " + displayedBookings.size() +
                " booking(s) matching \"" + txtSearch.getText().trim() + "\"");
        }
        // Update pills to always reflect full dataset totals
        if (lblPillPending != null) {
            lblPillPending.setText("Pending: "   + count("Pending"));
            lblPillCompleted.setText("Completed: " + count("Completed"));
            lblPillCancelled.setText("Cancelled: " + count("Cancelled"));
        }
        if (displayedBookings.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Results",
                "No bookings match: \"" + txtSearch.getText().trim() + "\"");
        }
    }

    private Label pill(String text, String bg, String fg) {
        Label l = new Label(text);
        l.setStyle("-fx-background-color:" + bg + ";-fx-text-fill:" + fg + ";" +
            "-fx-background-radius:20;-fx-padding:4 12;-fx-font-size:12px;-fx-font-weight:bold;");
        return l;
    }
}
