package ui.pages;

import data.Booking;
import data.DataStore;
import data.Resident;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import ui.StyleHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * CommunitySummaryPage — MEMBER 3's community analytics screen.
 *
 * Lecture reference:
 *   Topic 2 — extends BasePage, @Override build()
 *   Part 1  — VBox, HBox, GridPane
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
        Label lblSub = new Label("Community-wide recycling statistics and resident tier contributions.");
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

        // ── Stat cards — Part 1: GridPane ───────────────────────────────────
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(14);
        statsGrid.setVgap(14);

        //Preset each column to 1/3 width of the grid, and allow it to grow with the window
        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 3);
            col.setHgrow(Priority.ALWAYS);
            statsGrid.getColumnConstraints().add(col);
        }

        // Row 0
        statsGrid.add(statCard("Total Residents",   String.valueOf(totalResidents), StyleHelper.PRIMARY, "#EAF5F0"), 0, 0);
        statsGrid.add(statCard("Total Bookings",    String.valueOf(totalBookings),  StyleHelper.INFO,    "#EFF6FF"), 1, 0);
        statsGrid.add(statCard("Community Points",  totalPts + " pts",              "#6D28D9",           "#F5F3FF"), 2, 0);

        // Row 1
        statsGrid.add(statCard("Pending Pickups",   String.valueOf(pending),        StyleHelper.WARNING, "#FFFBEB"), 0, 1);
        statsGrid.add(statCard("Completed Pickups", String.valueOf(completed),      StyleHelper.SUCCESS, "#F0FDF4"), 1, 1);
        statsGrid.add(statCard("Cancelled",         String.valueOf(cancelled),      StyleHelper.DANGER,  "#FFF1F2"), 2, 1);

        // ── Category Breakdown ────────────────
        Label lblCatTitle = new Label("Category Breakdown");
        lblCatTitle.setStyle(StyleHelper.sectionTitle());

        // Stacked Bar
        HBox stackedBar = new HBox(4);
        stackedBar.setPrefHeight(12);
        stackedBar.setMaxHeight(12);
        stackedBar.setMaxWidth(Double.MAX_VALUE);

        // Define a color palette for different categories, the unused colors leave for future scaling
        String[] palette = {"#10B981", "#3B82F6", "#F59E0B", "#8B5CF6", "#EC4899"};
        int colorIdx = 0;

        // Category legend card
        HBox legendBox = new HBox(16);
        legendBox.setPadding(new Insets(12, 0, 0, 0));

        for (Map.Entry<String, Integer> entry : catMap.entrySet()) {
            String color = palette[colorIdx % palette.length];
            double ratio = totalBookings > 0 ? (double) entry.getValue() / totalBookings : 0;
            int pct = (int) Math.round(ratio * 100);

            // Progress bar
            if (ratio > 0) {
                Region segment = new Region();
                segment.setMinHeight(12);
                segment.setPrefHeight(12);
                segment.prefWidthProperty().bind(stackedBar.widthProperty().multiply(ratio));
                segment.setMinWidth(Region.USE_PREF_SIZE);
                segment.setStyle("-fx-background-color:" + color + "; -fx-background-radius:6;");
                stackedBar.getChildren().add(segment);
            }

            // Category legend item (larger text and values)
            Circle dot = new Circle(5, Color.web(color));
            Label nameLbl = new Label(entry.getKey());
            nameLbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#1E293B;");
            HBox titleLine = new HBox(8, dot, nameLbl);
            titleLine.setAlignment(Pos.CENTER_LEFT);

            Label countLbl = new Label(entry.getValue() + " bookings (" + pct + "%)");
            countLbl.setStyle(StyleHelper.mutedLabel() + "-fx-font-size:13px;");

            VBox catItem = new VBox(6, titleLine, countLbl);
            catItem.setPadding(new Insets(12, 16, 12, 16));
            catItem.setStyle("-fx-background-color:#F8FAFC; -fx-background-radius:10; -fx-border-color:" + StyleHelper.BORDER + "; -fx-border-radius:10;");
            HBox.setHgrow(catItem, Priority.ALWAYS);

            legendBox.getChildren().add(catItem);
            colorIdx++;
        }

        VBox catCard = makeCard(16);
        catCard.getChildren().addAll(stackedBar, legendBox);

       // ── Tier Distribution (Community Engagement) ─────────────────────────
        Label lblTierTitle = new Label("Resident Tier Distribution");
        lblTierTitle.setStyle(StyleHelper.sectionTitle());

        // 1. Statistics for each tier
        int starterCount = 0, recyclerCount = 0, championCount = 0;
        for (Resident r : DataStore.residents) {
            int pts = DataStore.getPointsForResident(r.getId());
            String tier = DataStore.getTier(pts);
            if ("Starter".equalsIgnoreCase(tier))            starterCount++;
            else if ("Recycler".equalsIgnoreCase(tier))      recyclerCount++;
            else if ("Eco Champion".equalsIgnoreCase(tier))  championCount++;
        }

        // 2. Top Stacked Bar (Stacked Bar)
        HBox tierStackedBar = new HBox(4);
        tierStackedBar.setMinHeight(12);
        tierStackedBar.setPrefHeight(12);
        tierStackedBar.setMaxHeight(12);
        tierStackedBar.setMaxWidth(Double.MAX_VALUE);

        // 3. Tier List Definition (Name, Count, Color, Description)
        String[][] tiers = {
            {"Starter",      String.valueOf(starterCount),  "#2E7D32", "0 - 39 pts"},
            {"Recycler",     String.valueOf(recyclerCount), "#1565C0", "40 - 99 pts"},
            {"Eco Champion", String.valueOf(championCount), "#E65100", "100+ pts"}
        };

        HBox tierLegendBox = new HBox(16);
        tierLegendBox.setPadding(new Insets(12, 0, 0, 0));

        for (String[] t : tiers) {
            String name = t[0];
            int count = Integer.parseInt(t[1]);
            String color = t[2];
            String range = t[3];

            double ratio = totalResidents > 0 ? (double) count / totalResidents : 0;
            int pct = (int) Math.round(ratio * 100);

            // Cumulative Progress Bar Segment
            if (ratio > 0) {
                Region segment = new Region();
                segment.setMinHeight(12);
                segment.setPrefHeight(12);
                segment.prefWidthProperty().bind(tierStackedBar.widthProperty().multiply(ratio));
                segment.setMinWidth(Region.USE_PREF_SIZE);
                segment.setStyle("-fx-background-color:" + color + "; -fx-background-radius:6;");
                tierStackedBar.getChildren().add(segment);
            }

            // Bottom Legend Card
            Circle dot = new Circle(5, Color.web(color));
            Label nameLbl = new Label(name);
            nameLbl.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:#1E293B;");
            
            Label rangeLbl = new Label("(" + range + ")");
            rangeLbl.setStyle("-fx-font-size:11px; -fx-text-fill:#94A3B8;");
            
            HBox titleLine = new HBox(6, dot, nameLbl, rangeLbl);
            titleLine.setAlignment(Pos.CENTER_LEFT);

            Label countLbl = new Label(count + " residents (" + pct + "%)");
            countLbl.setStyle(StyleHelper.mutedLabel() + "-fx-font-size:13px;");

            VBox tierItem = new VBox(6, titleLine, countLbl);
            tierItem.setPadding(new Insets(14, 18, 14, 18));
            tierItem.setStyle("-fx-background-color:#F8FAFC; -fx-background-radius:10; -fx-border-color:" + StyleHelper.BORDER + "; -fx-border-radius:10;");
            HBox.setHgrow(tierItem, Priority.ALWAYS);

            tierLegendBox.getChildren().add(tierItem);
        }

        VBox tierCard = makeCard(16);
        tierCard.getChildren().addAll(tierStackedBar, tierLegendBox);
        // ── Root ──────────────────────────────────────────────────────────────
        VBox root = new VBox(24,
            titleBox,
            statsGrid,
            new VBox(14, lblCatTitle, catCard),
            new VBox(14, lblTierTitle, tierCard)
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
        
        // Ensure card in the same ratio of width and height, and grows nicely in HBox
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(0);
        card.setStyle("-fx-background-color:white;-fx-background-radius:14;-fx-border-radius:14;" +
            "-fx-border-color:" + StyleHelper.BORDER + ";-fx-border-width:1;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.06),12,0,0,3);");
        return card;
    }
}
