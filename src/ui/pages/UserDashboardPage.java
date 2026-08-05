package ui.pages;

import data.Booking;
import data.DataStore;
import data.Resident;
import data.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;  // JavaFX Part 3 — Shape
import ui.StyleHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * UserDashboardPage — MEMBER 3's personal resident overview screen.
 *
 * Lecture reference:
 *   Topic 2 — extends BasePage, @Override build()
 *   Part 1  — VBox, HBox, StackPane (avatar), GridPane (tier milestones)
 *   Part 2  — Lambda handlers on stat cards and booking rows
 *   Part 3  — Circle and Color for avatar icon
 *   Part 4  — ListView for recent bookings, Label, Button
 */
public class UserDashboardPage extends BasePage {

    public UserDashboardPage() { super(); }
    public UserDashboardPage(Consumer<String> navigate) { super(navigate); }

    /** Topic 2 — @Override abstract build() from BasePage. */
    @Override
    public Node build() {
        String resId = Session.getLinkedResidentId();
        Resident me  = resId != null ? DataStore.findResidentById(resId) : null;
        String name  = me != null ? me.getName() : Session.getDisplayName();

        String today = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));

        int myPts        = resId != null ? DataStore.getPointsForResident(resId) : 0;
        String tier      = DataStore.getTier(myPts);
        String tierColor = DataStore.getTierColor(myPts);

        // ── Welcome banner — Part 3: Circle, Part 1: StackPane + HBox ─────────
        VBox banner = buildWelcomeBanner(name, today, tier, me);

        // ── Collect my bookings ───────────────────────────────────────────────
        int completed = 0, pending = 0, cancelled = 0;
        ArrayList<Booking> myBookings = new ArrayList<Booking>();
        for (Booking b : DataStore.bookings) {
            if (b.getResidentId().equalsIgnoreCase(resId != null ? resId : "")) {
                myBookings.add(b);
                if ("Completed".equals(b.getStatus()))       completed++;
                else if ("Pending".equals(b.getStatus()))    pending++;
                else if ("Cancelled".equals(b.getStatus()))  cancelled++;
            }
        }

        // ── Stat cards — Part 2: Lambda click navigation ──────────────────────
        HBox statsRow = new HBox(16,
            statCard("MY POINTS",         myPts + " pts",          StyleHelper.PRIMARY, "#EAF5F0", null),
            statCard("COMPLETED PICKUPS", String.valueOf(completed), StyleHelper.SUCCESS, "#F0FDF4", "bookings"),
            statCard("PENDING PICKUPS",   String.valueOf(pending),   StyleHelper.WARNING, "#FFFBEB", "bookings"),
            statCard("CANCELLED",         String.valueOf(cancelled), StyleHelper.DANGER,  "#FFF1F2", "bookings")
        );
        for (Node n : statsRow.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        // ── Tier progress card ────────────────────────────────────────────────
        VBox tierCard = buildTierCard(myPts, tier, tierColor);

        // ── Recent bookings — Part 4: ListView ────────────────────────────────
        Label lblRecent = new Label("Recent Bookings");
        lblRecent.setStyle(StyleHelper.sectionTitle());

        Button btnViewAll = new Button("View All My Bookings →");
        btnViewAll.setStyle("-fx-background-color:transparent;-fx-text-fill:" +
            StyleHelper.PRIMARY + ";-fx-cursor:hand;-fx-font-size:12px;-fx-font-weight:bold;-fx-padding:0;");
        btnViewAll.setOnAction(e -> goTo("bookings"));

        Region ts = new Region(); HBox.setHgrow(ts, Priority.ALWAYS);
        HBox recentTitleRow = new HBox(lblRecent, ts, btnViewAll);
        recentTitleRow.setAlignment(Pos.CENTER_LEFT);

        Label lvHeader = makeListHeader(
            String.format("  %-7s  %-11s  %-13s  %-15s  %-10s  %s",
                "ID", "Date", "Category", "Location", "Status", "Points"));

        ListView<String> lvRecent = makeListView(220);
        if (myBookings.isEmpty()) {
            lvRecent.getItems().add("  No bookings yet. Book your first pickup!");
        } else {
            int start = Math.max(0, myBookings.size() - 5);
            for (int i = myBookings.size() - 1; i >= start; i--) {
                Booking b = myBookings.get(i);
                String ptsStr = "Completed".equals(b.getStatus()) ? b.getPoints() + " pts" : "—";
                lvRecent.getItems().add(String.format("  %-7s  %-11s  %-13s  %-15s  %-10s  %s",
                    b.getBookingId(), b.getDate(),
                    truncate(b.getWasteCategory(), 13),
                    truncate(b.getCollectionPoint(), 15),
                    b.getStatus(), ptsStr));
            }
            // Part 2 — Lambda: click row navigates to My Bookings
            lvRecent.setOnMouseClicked(e -> goTo("bookings"));
            lvRecent.setCursor(Cursor.HAND);
        }

        VBox recentCard = makeCard(14);
        recentCard.getChildren().addAll(lvHeader, lvRecent);

        // ── Quick action buttons ──────────────────────────────────────────────
        Button btnBook   = makeBtn("+ Book a Pickup");
        Button btnPoints = makeSecBtn("My Points & History");
        // Part 2 — Lambda event handlers
        btnBook.setOnAction(e -> goTo("book"));
        btnPoints.setOnAction(e -> goTo("points"));
        HBox quickActions = new HBox(14, btnBook, btnPoints);

        // ── Root ──────────────────────────────────────────────────────────────
        VBox root = new VBox(26,
            banner, statsRow, tierCard,
            new VBox(14, recentTitleRow, recentCard),
            quickActions
        );
        root.setPadding(new Insets(32, 36, 32, 36));
        root.setStyle("-fx-background-color:" + StyleHelper.BG + ";");
        return root;
    }

    // ── Welcome banner — Part 3: Circle, Part 1: StackPane ───────────────────
    private VBox buildWelcomeBanner(String name, String date, String tier, Resident me) {
        // JavaFX Part 3 — Circle shape
        Circle c = new Circle(28);
        c.setFill(Color.web(StyleHelper.ACCENT));
        Label avL = new Label(name.substring(0, 1).toUpperCase());
        avL.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:" + StyleHelper.PRIMARY + ";");
        // JavaFX Part 1 — StackPane overlays label on circle
        StackPane avatar = new StackPane(c, avL);

        Label lblHello = new Label("Welcome back, " + name.split(" ")[0] + "!");
        lblHello.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:white;");
        Label lblDate  = new Label(date);
        lblDate.setStyle("-fx-font-size:12px;-fx-text-fill:rgba(255,255,255,0.75);");
        Label tierBadge = new Label("  " + tier + "  ");
        tierBadge.setStyle("-fx-background-color:rgba(255,255,255,0.20);" +
            "-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:bold;" +
            "-fx-background-radius:20;-fx-padding:4 12;");

        // Part 1 — HBox arranges date and badge side by side
        HBox dateRow = new HBox(10, lblDate, tierBadge);
        dateRow.setAlignment(Pos.CENTER_LEFT);

        VBox textCol;
        if (me != null) {
            Label unitLbl = new Label("Unit " + me.getUnit() + "  |  " + me.getWasteTypesString());
            unitLbl.setStyle("-fx-font-size:12px;-fx-text-fill:rgba(255,255,255,0.70);");
            textCol = new VBox(6, lblHello, dateRow, unitLbl);
        } else {
            textCol = new VBox(6, lblHello, dateRow);
        }

        HBox inner = new HBox(20, avatar, textCol);
        inner.setAlignment(Pos.CENTER_LEFT);

        VBox banner = new VBox(inner);
        banner.setPadding(new Insets(28, 30, 28, 30));
        banner.setStyle("-fx-background-color:" + StyleHelper.PRIMARY + ";-fx-background-radius:16;");
        return banner;
    }

    // ── Tier progress card ────────────────────────────────────────────────────
    private VBox buildTierCard(int pts, String tier, String tierColor) {
        Label lbl = new Label("Your Recycling Tier");
        lbl.setStyle(StyleHelper.sectionTitle());

        Label tierName = new Label(tier);
        tierName.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:" + tierColor + ";");

        String nextMsg;
        if (pts < 40)       nextMsg = "Earn " + (40 - pts) + " more pts to reach Recycler";
        else if (pts < 100) nextMsg = "Earn " + (100 - pts) + " more pts to reach Eco Champion!";
        else                nextMsg = "You have reached the highest tier!";

        Label lblNext = new Label(nextMsg);
        lblNext.setStyle(StyleHelper.mutedLabel());

        // Progress bar
        double pct = Math.min(pts / 100.0, 1.0);
        Region filled = new Region(); filled.setPrefHeight(10);
        filled.setPrefWidth(pct * 500);
        filled.setStyle("-fx-background-color:" + tierColor + ";-fx-background-radius:8 0 0 8;");
        Region empty = new Region(); empty.setPrefHeight(10);
        HBox.setHgrow(empty, Priority.ALWAYS);
        empty.setStyle("-fx-background-color:" + StyleHelper.BORDER + ";-fx-background-radius:0 8 8 0;");
        HBox bar = new HBox(0, filled, empty);

        // Part 1 — HBox for milestone labels
        Label lS = new Label("Starter\n0 pts");   lS.setStyle(StyleHelper.mutedLabel());
        Label lR = new Label("Recycler\n40 pts"); lR.setStyle(StyleHelper.mutedLabel());
        Label lC = new Label("Eco Champion\n100 pts"); lC.setStyle(StyleHelper.mutedLabel());
        Region s1 = new Region(); HBox.setHgrow(s1, Priority.ALWAYS);
        Region s2 = new Region(); HBox.setHgrow(s2, Priority.ALWAYS);
        HBox milestones = new HBox(lS, s1, lR, s2, lC);

        VBox card = makeCard(14);
        card.getChildren().addAll(lbl, new HBox(14, tierName, lblNext), bar, milestones);
        return card;
    }

    // ── Clickable stat card — Part 2: Lambda ──────────────────────────────────
    private VBox statCard(String title, String value, String colour,
                          String bgColour, final String navTarget) {
        Label lbl = new Label(title);
        lbl.setStyle(StyleHelper.statLabel());
        Label val = new Label(value);
        val.setStyle(StyleHelper.statNumber(colour));
        Region bar = new Region(); bar.setPrefHeight(4);
        bar.setStyle("-fx-background-color:" + colour + ";-fx-background-radius:4 4 0 0;");
        VBox inner = new VBox(8, lbl, val);
        inner.setPadding(new Insets(18, 20, 18, 20));
        VBox card = new VBox(0, bar, inner);
        card.setStyle("-fx-background-color:white;-fx-background-radius:14;-fx-border-radius:14;" +
            "-fx-border-color:" + StyleHelper.BORDER + ";-fx-border-width:1;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.06),12,0,0,3);");
        if (navigate != null && navTarget != null) {
            card.setCursor(Cursor.HAND);
            // Part 2 — Lambda event handlers
            card.setOnMouseEntered(e -> card.setOpacity(0.82));
            card.setOnMouseExited(e  -> card.setOpacity(1.0));
            card.setOnMouseClicked(e -> goTo(navTarget));
            Label hint = new Label("Click to view →");
            hint.setStyle("-fx-font-size:10px;-fx-text-fill:" + StyleHelper.TEXT_MUTED + ";");
            inner.getChildren().add(hint);
        }
        return card;
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "." : s;
    }
}
