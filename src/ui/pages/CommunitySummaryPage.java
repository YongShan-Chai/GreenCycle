package ui.pages;

import data.Booking;
import data.DataStore;
import data.Resident;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.StyleHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * CommunitySummaryPage — MEMBER 3's community analytics screen.
 *
 * Lecture reference:
 *   Topic 2 — extends BasePage, @Override build()
 *   Part 1  — VBox, HBox, GridPane, FlowPane (for category pills)
 *   Part 4  — ListView for leaderboard display, Label, Button
 */
public class CommunitySummaryPage extends BasePage {

    public CommunitySummaryPage() { super(); }
    public CommunitySummaryPage(Consumer<String> navigate) { super(navigate); }

    /** Topic 2 — @Override: implements abstract build() from BasePage. */
    @Override
    public Node build() {
        // ── Page title ────────────────────────────────────────────────────────
        Label lblTitle = new Label("Community Summary");
        lblTitle.setStyle(StyleHelper.pageTitle());
        Label lblSub = new Label("Community-wide recycling statistics and resident leaderboard.");
        lblSub.setStyle(StyleHelper.mutedLabel());
        VBox titleBox = new VBox(4, lblTitle, lblSub);

        // ── Calculate stats ───────────────────────────────────────────────────
        int totalResidents = DataStore.residents.size();
        int totalBookings  = DataStore.bookings.size();
        int completed = 0, pending = 0, cancelled = 0, totalPts = 0;
        Map<String, Integer> catMap = new HashMap<String, Integer>();

        for (Booking b : DataStore.bookings) {
            if ("Completed".equals(b.getStatus()))      { completed++; totalPts += b.getPoints(); }
            else if ("Pending".equals(b.getStatus()))     pending++;
            else if ("Cancelled".equals(b.getStatus()))   cancelled++;

            String cat = b.getWasteCategory();
            catMap.put(cat, catMap.containsKey(cat) ? catMap.get(cat) + 1 : 1);
        }

        String topCat = "N/A"; int topCount = 0;
        for (Map.Entry<String, Integer> e : catMap.entrySet()) {
            if (e.getValue() > topCount) { topCount = e.getValue(); topCat = e.getKey(); }
        }

        // ── Stat cards — Part 1: HBox rows ───────────────────────────────────
        HBox row1 = new HBox(14,
            statCard("Total Residents",    String.valueOf(totalResidents), StyleHelper.PRIMARY,  "#EAF5F0"),
            statCard("Total Bookings",     String.valueOf(totalBookings),  StyleHelper.INFO,     "#EFF6FF"),
            statCard("Completed Pickups",  String.valueOf(completed),      StyleHelper.SUCCESS,  "#F0FDF4")
        );
        HBox row2 = new HBox(14,
            statCard("Pending Pickups",    String.valueOf(pending),    StyleHelper.WARNING, "#FFFBEB"),
            statCard("Cancelled",          String.valueOf(cancelled),  StyleHelper.DANGER,  "#FFF1F2"),
            statCard("Community Points",   totalPts + " pts",          "#6D28D9",           "#F5F3FF")
        );
        for (Node n : row1.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);
        for (Node n : row2.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        // ── Most popular category highlight ───────────────────────────────────
        Label topCatLbl = new Label("Most Popular Waste Category");
        topCatLbl.setStyle(StyleHelper.statLabel());
        Label topCatVal = new Label(topCat + "  (" + topCount + " bookings)");
        topCatVal.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:" + StyleHelper.PRIMARY + ";");
        VBox topCatCard = new VBox(8, topCatLbl, topCatVal);
        topCatCard.setPadding(new Insets(20, 28, 20, 28));
        topCatCard.setStyle("-fx-background-color:" + StyleHelper.ACCENT + ";-fx-background-radius:14;");

        // ── Category breakdown — Part 1: FlowPane for pills ──────────────────
        Label lblCatTitle = new Label("Category Breakdown");
        lblCatTitle.setStyle(StyleHelper.sectionTitle());

        // JavaFX Part 1 — FlowPane: wraps category pills automatically
        FlowPane catFlow = new FlowPane();
        catFlow.setHgap(10);
        catFlow.setVgap(10);
        catFlow.setPadding(new Insets(10));
        for (Map.Entry<String, Integer> entry : catMap.entrySet()) {
            int pct = totalBookings > 0 ? (entry.getValue() * 100) / totalBookings : 0;
            Label pill = new Label(entry.getKey() + ": " + entry.getValue() + " (" + pct + "%)");
            pill.setStyle("-fx-background-color:" + StyleHelper.ACCENT + ";-fx-text-fill:" +
                StyleHelper.PRIMARY + ";-fx-background-radius:20;-fx-padding:6 14;" +
                "-fx-font-size:12px;-fx-font-weight:bold;");
            catFlow.getChildren().add(pill);
        }

        VBox catCard = makeCard(14);
        catCard.getChildren().addAll(catFlow);

        // ── Leaderboard — Part 4: ListView ────────────────────────────────────
        Label lblLeader = new Label("Resident Leaderboard — Top 5 by Recycling Points");
        lblLeader.setStyle(StyleHelper.sectionTitle());

        ArrayList<Resident> sorted = new ArrayList<Resident>(DataStore.residents);
        sorted.sort((a, b) ->
            DataStore.getPointsForResident(b.getId()) -
            DataStore.getPointsForResident(a.getId()));

        Label lvLeaderHeader = makeListHeader(
                String.format("  %-4s  %-8s  %-22s  %-8s  %-10s  %s",
                    "Rank", "ID", "Name", "Unit", "Points", "Tier"));

            ListView<String> lvLeader = makeListView(220);

            // ── FIXES FOR TABLE VISIBILITY ───────────────────────────────────────
            lvLeader.setMinHeight(200);                  // Prevents VBox from squeezing the leaderboard to 0 height
            lvLeader.setPrefHeight(220);
            VBox.setVgrow(lvLeader, Priority.ALWAYS);     // Expands nicely inside the card
            lvLeader.setStyle("-fx-font-family:'Courier New', monospace;-fx-font-size:12px;");
            // ─────────────────────────────────────────────────────────────────────

            int rank = 1;
            for (Resident r : sorted) {
                if (rank > 5) break;
                int pts = DataStore.getPointsForResident(r.getId());
                String medal = rank == 1 ? "🥇" : rank == 2 ? "🥈" : rank == 3 ? "🥉" : " " + rank + " ";
                lvLeader.getItems().add(String.format("  %-4s  %-8s  %-22s  %-8s  %-10s  %s",
                    medal, r.getId(), truncate(r.getName(), 22),
                    r.getUnit(), pts + " pts", DataStore.getTier(pts)));
                rank++;
            }

            VBox leaderCard = makeCard(14);
            leaderCard.getChildren().addAll(lvLeaderHeader, lvLeader);

        // ── Root ──────────────────────────────────────────────────────────────
        VBox root = new VBox(24,
            titleBox,
            new VBox(14, row1, row2),
            topCatCard,
            new VBox(14, lblCatTitle, catCard),
            new VBox(14, lblLeader, leaderCard)
        );
        root.setPadding(new Insets(32, 36, 32, 36));
        root.setStyle("-fx-background-color:" + StyleHelper.BG + ";");
        return root;
    }

    // ── Stat card ─────────────────────────────────────────────────────────────
    private VBox statCard(String title, String value, String colour, String bg) {
        Label lbl = new Label(title.toUpperCase());
        lbl.setStyle(StyleHelper.statLabel());
        Label val = new Label(value);
        val.setStyle(StyleHelper.statNumber(colour));
        Region bar = new Region();
        bar.setPrefHeight(4);
        bar.setStyle("-fx-background-color:" + colour + ";-fx-background-radius:4 4 0 0;");
        VBox inner = new VBox(8, lbl, val);
        inner.setPadding(new Insets(18, 20, 18, 20));
        VBox card = new VBox(0, bar, inner);
        card.setStyle("-fx-background-color:white;-fx-background-radius:14;-fx-border-radius:14;" +
            "-fx-border-color:" + StyleHelper.BORDER + ";-fx-border-width:1;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.06),12,0,0,3);");
        return card;
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "." : s;
    }
}
