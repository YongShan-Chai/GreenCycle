package ui.pages;

import data.Booking;
import data.DataStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.StyleHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * AdminDashboardPage — MEMBER 3's admin overview screen.
 *
 * Lecture reference:
 *   Topic 2   — extends BasePage (inheritance), @Override build() (polymorphism)
 *   Part 1    — VBox, HBox, GridPane for layout
 *   Part 2    — Lambda event handlers on stat cards and booking rows
 *   Part 4    — Label, Button, ListView for recent bookings display
 */
public class AdminDashboardPage extends BasePage {

    // Topic 2 — constructor calls super() from BasePage
    public AdminDashboardPage() { super(); }
    public AdminDashboardPage(Consumer<String> navigate) { super(navigate); }

    /**
     * Topic 2 — @Override: implements abstract build() from BasePage.
     * Polymorphism: when AdminShell calls page.build(), this version runs.
     */
    @Override
    public Node build() {
        String today = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));

        // ── Greeting ──────────────────────────────────────────────────────────
        Label lblGreeting = new Label("Good day, Administrator");
        lblGreeting.setStyle(StyleHelper.pageTitle());
        Label lblDate = new Label(today);
        lblDate.setStyle(StyleHelper.mutedLabel());
        VBox greetBox = new VBox(4, lblGreeting, lblDate);

        // ── Calculate stats ───────────────────────────────────────────────────
        int totalResidents = DataStore.residents.size();
        int totalBookings  = DataStore.bookings.size();
        int completed = 0, pending = 0, cancelled = 0;
        for (Booking b : DataStore.bookings) {
            if ("Completed".equals(b.getStatus()))       completed++;
            else if ("Pending".equals(b.getStatus()))    pending++;
            else if ("Cancelled".equals(b.getStatus()))  cancelled++;
        }

        // ── Clickable stat cards (Part 1 — HBox, Part 2 — Lambda click) ──────
        HBox statsRow = new HBox(16,
            statCard("TOTAL RESIDENTS",   String.valueOf(totalResidents), StyleHelper.PRIMARY,  "#EAF5F0", "residents"),
            statCard("TOTAL BOOKINGS",    String.valueOf(totalBookings),  StyleHelper.INFO,     "#EFF6FF", "bookings"),
            statCard("COMPLETED PICKUPS", String.valueOf(completed),      StyleHelper.SUCCESS,  "#F0FDF4", "bookings"),
            statCard("PENDING PICKUPS",   String.valueOf(pending),        StyleHelper.WARNING,  "#FFFBEB", "bookings")
        );
        for (Node n : statsRow.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        // ── Recent Bookings — Part 4: ListView ────────────────────────────────
        Label lblRecent = new Label("Recent Bookings");
        lblRecent.setStyle(StyleHelper.sectionTitle());

        Button btnViewAll = new Button("View All Bookings →");
        btnViewAll.setStyle("-fx-background-color:transparent;-fx-text-fill:" +
            StyleHelper.PRIMARY + ";-fx-cursor:hand;-fx-font-size:12px;-fx-font-weight:bold;-fx-padding:0;");
        // Part 2 — Lambda event handler
        btnViewAll.setOnAction(e -> goTo("bookings"));

        Region titleSpacer = new Region();
        HBox.setHgrow(titleSpacer, Priority.ALWAYS);
        HBox recentTitleRow = new HBox(lblRecent, titleSpacer, btnViewAll);
        recentTitleRow.setAlignment(Pos.CENTER_LEFT);

        // Column header above the ListView
        Label lvHeader = makeListHeader(
            String.format(" %-7s  %-18s  %-11s  %-18s  %-13s  %-12s  %-8s  %4s ",
                "ID", "Resident", "Date", "Time Slot", "Category", "Location", "Status", "Points"));

        // Part 4 — ListView showing recent bookings
        ListView<String> lvRecent = makeListView(210);
        
        // ── FIXES FOR TABLE VISIBILITY ───────────────────────────────────────
        lvRecent.setMinHeight(180);                  // Forces a minimum height so it won't collapse
        lvRecent.setPrefHeight(210);
        VBox.setVgrow(lvRecent, Priority.ALWAYS);     // Allows it to grow in VBox
        lvRecent.setStyle("-fx-font-family:'Courier New', monospace;-fx-font-size:12px;"); // Ensures clean alignment
        // ─────────────────────────────────────────────────────────────────────

        int start = Math.max(0, DataStore.bookings.size() - 10);
        for (int i = DataStore.bookings.size() - 1; i >= start; i--) {
            lvRecent.getItems().add(DataStore.bookings.get(i).getListEntry());
        }
        if (DataStore.bookings.isEmpty()) {
            lvRecent.getItems().add("  No bookings yet.");
        }
        // Part 2 — Lambda: clicking a booking row navigates to Manage Bookings
        lvRecent.setOnMouseClicked(e -> { if (e.getClickCount() >= 1) goTo("bookings"); });

        VBox recentCard = makeCard(14);
        recentCard.getChildren().addAll(lvHeader, lvRecent);

        // ── Info cards ────────────────────────────────────────────────────────
        Label lblInfo = new Label("System Overview");
        lblInfo.setStyle(StyleHelper.sectionTitle());

        HBox infoRow = new HBox(16,
            infoCard("Cancelled Bookings",     String.valueOf(cancelled),      "#DC2626"),
            infoCard("Community Points Total", String.valueOf(totalPoints()),   StyleHelper.PRIMARY),
            infoCard("Most Popular Category",  DataStore.getMostPopularCategory(), StyleHelper.SECONDARY)
        );
        for (Node n : infoRow.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        // ── Root — Part 1: VBox stacks all sections ───────────────────────────
        VBox root = new VBox(28,
            greetBox, statsRow,
            new VBox(14, recentTitleRow, recentCard),
            new VBox(14, lblInfo, infoRow)
        );
        root.setPadding(new Insets(32, 36, 32, 36));
        root.setStyle("-fx-background-color:" + StyleHelper.BG + ";");
        return root;
    }

    // ── Clickable stat card — Part 2: Lambda for click and hover ─────────────
    private VBox statCard(String title, String value,
                          String colour, String bgColour, final String navTarget) {
        Label lbl = new Label(title);
        lbl.setStyle(StyleHelper.statLabel());
        Label val = new Label(value);
        val.setStyle(StyleHelper.statNumber(colour));

        // Coloured accent bar at top
        Region bar = new Region();
        bar.setPrefHeight(4);
        bar.setStyle("-fx-background-color:" + colour + ";-fx-background-radius:4 4 0 0;");

        Label hint = new Label("Click to view →");
        hint.setStyle("-fx-font-size:10px;-fx-text-fill:" + StyleHelper.TEXT_MUTED + ";");

        VBox inner = new VBox(8, lbl, val, hint);
        inner.setPadding(new Insets(18, 20, 18, 20));
        VBox card = new VBox(0, bar, inner);
        card.setStyle("-fx-background-color:white;-fx-background-radius:14;-fx-border-radius:14;" +
            "-fx-border-color:" + StyleHelper.BORDER + ";-fx-border-width:1;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.06),12,0,0,3);");

        if (navigate != null && navTarget != null) {
            card.setCursor(Cursor.HAND);
            // Part 2 — Lambda event handlers for click and hover
            card.setOnMouseEntered(e -> card.setOpacity(0.82));
            card.setOnMouseExited(e  -> card.setOpacity(1.0));
            card.setOnMouseClicked(e -> goTo(navTarget));
        }
        return card;
    }

    private VBox infoCard(String title, String value, String colour) {
        Label lbl = new Label(title);
        lbl.setStyle(StyleHelper.mutedLabel());
        Label val = new Label(value);
        val.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:" + colour + ";");
        VBox card = new VBox(8, lbl, val);
        card.setPadding(new Insets(20));
        card.setStyle(StyleHelper.card());
        return card;
    }

    private int totalPoints() {
        int t = 0;
        for (Booking b : DataStore.bookings) t += b.getPoints();
        return t;
    }
}
