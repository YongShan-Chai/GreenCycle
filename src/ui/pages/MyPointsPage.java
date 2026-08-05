package ui.pages;

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
 * MyPointsPage — MEMBER 3's recycling points and history screen.
 *
 * Lecture reference:
 *   Topic 2 — extends BasePage, @Override build(), polymorphism via getSummary()
 *   Part 1  — VBox, HBox, GridPane
 *   Part 4  — ListView for history display, Label, Button
 */
public class MyPointsPage extends BasePage {

    public MyPointsPage() { super(); }
    public MyPointsPage(Consumer<String> navigate) { super(navigate); }

    /** Topic 2 — @Override abstract build() from BasePage. */
    @Override
    public Node build() {
        String resId = Session.getLinkedResidentId();

        // ── Gather this resident's history ────────────────────────────────────
        int totalPts = 0, completedCount = 0, recyclables = 0, generalBulky = 0;
        ArrayList<Booking> history = new ArrayList<Booking>();

        for (Booking b : DataStore.bookings) {
            if (!b.getResidentId().equalsIgnoreCase(resId != null ? resId : "")) continue;
            history.add(b);
            if ("Completed".equals(b.getStatus())) {
                totalPts += b.getPoints();
                completedCount++;
                if ("Recyclables".equals(b.getWasteCategory())) recyclables++;
                else generalBulky++;
            }
        }

        String tier      = DataStore.getTier(totalPts);
        String tierColor = DataStore.getTierColor(totalPts);

        // ── Page title ────────────────────────────────────────────────────────
        Label lblTitle = new Label("My Recycling Points");
        lblTitle.setStyle(StyleHelper.pageTitle());
        Label lblSub = new Label("Track your eco contribution and tier progress.");
        lblSub.setStyle(StyleHelper.mutedLabel());
        VBox titleBox = new VBox(4, lblTitle, lblSub);

        // ── Points banner ─────────────────────────────────────────────────────
        VBox banner = buildPointsBanner(totalPts, tier, tierColor, completedCount);

        // ── Stat mini-cards — Part 1: HBox ────────────────────────────────────
        HBox statsRow = new HBox(16,
            miniCard("COMPLETED PICKUPS",  String.valueOf(completedCount), StyleHelper.SUCCESS),
            miniCard("RECYCLABLES",        String.valueOf(recyclables),    StyleHelper.SECONDARY),
            miniCard("GENERAL / BULKY",    String.valueOf(generalBulky),   StyleHelper.INFO),
            miniCard("TOTAL POINTS",       totalPts + " pts",              StyleHelper.PRIMARY)
        );
        for (Node n : statsRow.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        // ── Tier system explanation card ──────────────────────────────────────
        VBox tierCard = buildTierCard(totalPts, tier, tierColor);

        // ── Full pickup history — Part 4: ListView ────────────────────────────
        Label lblHistTitle = new Label("Full Pickup History");
        lblHistTitle.setStyle(StyleHelper.sectionTitle());

        Label lvHeader = makeListHeader(
            String.format("  %-7s  %-11s  %-13s  %-15s  %-10s  %s",
                "ID", "Date", "Category", "Location", "Status", "Points"));

        ListView<String> lvHistory = makeListView(280);

        if (history.isEmpty()) {
            lvHistory.getItems().add("  No pickup history yet. Book your first pickup!");
        } else {
            for (Booking b : history) {
                String ptsStr = "Completed".equals(b.getStatus()) ? b.getPoints() + " pts" : "—";
                lvHistory.getItems().add(String.format(
                    "  %-7s  %-11s  %-13s  %-15s  %-10s  %s",
                    b.getBookingId(), b.getDate(),
                    truncate(b.getWasteCategory(), 13),
                    truncate(b.getCollectionPoint(), 15),
                    b.getStatus(), ptsStr));
            }
        }

        VBox histCard = makeCard(14);
        histCard.getChildren().addAll(lvHeader, lvHistory);

        // ── Quick action button ───────────────────────────────────────────────
        Button btnBook = makeBtn("+ Book Another Pickup");
        // Part 2 — Lambda event handler
        btnBook.setOnAction(e -> goTo("book"));

        // ── Root — Part 1: VBox stacks all sections ───────────────────────────
        VBox root = new VBox(26,
            titleBox, banner, statsRow, tierCard,
            new VBox(14, lblHistTitle, histCard),
            btnBook
        );
        root.setPadding(new Insets(32, 36, 32, 36));
        root.setStyle("-fx-background-color:" + StyleHelper.BG + ";");
        return root;
    }

    // ── Large points banner ───────────────────────────────────────────────────
    private VBox buildPointsBanner(int pts, String tier, String tierColor, int completed) {
        Label lblPts = new Label(String.valueOf(pts));
        lblPts.setStyle("-fx-font-size:56px;-fx-font-weight:bold;-fx-text-fill:white;");
        Label lblPtsLabel = new Label("RECYCLING POINTS");
        lblPtsLabel.setStyle("-fx-font-size:12px;-fx-text-fill:rgba(255,255,255,0.70);-fx-font-weight:bold;");

        Label tierBadge = new Label("  " + tier + "  ");
        tierBadge.setStyle("-fx-background-color:rgba(255,255,255,0.22);" +
            "-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;" +
            "-fx-background-radius:20;-fx-padding:5 14;");

        Label lblDone = new Label(completed + " completed pickup(s)");
        lblDone.setStyle("-fx-font-size:13px;-fx-text-fill:rgba(255,255,255,0.75);");

        // Part 1 — VBox for left column
        VBox left = new VBox(6, lblPtsLabel, lblPts);
        left.setAlignment(Pos.CENTER_LEFT);

        // Part 1 — VBox for right column
        VBox right = new VBox(10, tierBadge, lblDone);
        right.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Part 1 — HBox arranges left and right columns
        HBox inner = new HBox(left, spacer, right);
        inner.setAlignment(Pos.CENTER_LEFT);

        VBox banner = new VBox(inner);
        banner.setPadding(new Insets(28, 32, 28, 32));
        banner.setStyle("-fx-background-color:" + StyleHelper.PRIMARY +
            ";-fx-background-radius:16;");
        return banner;
    }

    // ── Tier system card — Part 1: HBox for tier panels ──────────────────────
    private VBox buildTierCard(int pts, String currentTier, String tierColor) {
        Label lbl = new Label("Tier System");
        lbl.setStyle(StyleHelper.sectionTitle());

        String[][] tiers = {
            {"Starter",      "0 – 39 pts",   StyleHelper.SUCCESS},
            {"Recycler",     "40 – 99 pts",  StyleHelper.INFO},
            {"Eco Champion", "100+ pts",      "#E65100"}
        };

        // Part 1 — HBox holds the three tier panels side by side
        HBox tiersRow = new HBox(14);
        for (String[] t : tiers) {
            boolean active = currentTier.equals(t[0]);
            Label tName = new Label(t[0]);
            tName.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:" + t[2] + ";");
            Label tRange = new Label(t[1]);
            tRange.setStyle(StyleHelper.mutedLabel());

            VBox tCard = new VBox(6, tName, tRange);
            tCard.setPadding(new Insets(16, 20, 16, 20));
            tCard.setStyle(active
                ? "-fx-background-color:" + StyleHelper.ACCENT + ";" +
                  "-fx-background-radius:10;-fx-border-color:" + t[2] + ";" +
                  "-fx-border-radius:10;-fx-border-width:2;"
                : "-fx-background-color:" + StyleHelper.BG + ";-fx-background-radius:10;");
            HBox.setHgrow(tCard, Priority.ALWAYS);
            tiersRow.getChildren().add(tCard);
        }

        // Progress message
        String nextMsg;
        if (pts < 40)       nextMsg = "Earn " + (40 - pts) + " more points to reach Recycler tier.";
        else if (pts < 100) nextMsg = "Earn " + (100 - pts) + " more points to reach Eco Champion!";
        else                nextMsg = "You have reached the highest tier. Congratulations!";

        Label lblNext = new Label(nextMsg);
        lblNext.setStyle("-fx-font-size:13px;-fx-text-fill:" + tierColor +
            ";-fx-font-weight:bold;");

        VBox card = makeCard(16);
        card.getChildren().addAll(lbl, tiersRow, lblNext);
        return card;
    }

    // ── Mini stat card ────────────────────────────────────────────────────────
    private VBox miniCard(String title, String value, String colour) {
        Label lbl = new Label(title);
        lbl.setStyle(StyleHelper.statLabel());
        Label val = new Label(value);
        val.setStyle("-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" + colour + ";");
        Region bar = new Region(); bar.setPrefHeight(4);
        bar.setStyle("-fx-background-color:" + colour + ";-fx-background-radius:4 4 0 0;");
        VBox inner = new VBox(8, lbl, val);
        inner.setPadding(new Insets(16, 18, 16, 18));
        VBox card = new VBox(0, bar, inner);
        card.setStyle("-fx-background-color:white;-fx-background-radius:12;-fx-border-radius:12;" +
            "-fx-border-color:" + StyleHelper.BORDER + ";-fx-border-width:1;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.05),10,0,0,2);");
        return card;
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "." : s;
    }
}
