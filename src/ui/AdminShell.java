package ui;

import data.Session;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import ui.pages.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AdminShell — main window shell for the Administrator role.
 */
public class AdminShell {

    // JavaFX Part 1 — BorderPane as the root layout
    private BorderPane mainLayout;
    private Label      lblPageTitle;

    // Track which page is currently active for sidebar highlighting
    private String            currentPage = "dashboard";
    private Map<String, HBox> menuItemMap = new HashMap<String, HBox>();

    public void show(Stage stage) {
        
        stage.setTitle("GreenCycle - Admin Panel");

        // 1. Remember if the window was maximized on the Login screen
        boolean wasMaximized = stage.isMaximized();

        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color:" + StyleHelper.BG + ";");

        // JavaFX Part 1 — BorderPane regions
        mainLayout.setLeft(buildSidebar(stage));
        mainLayout.setTop(buildHeader());
        mainLayout.setCenter(wrapScroll(new AdminDashboardPage(this::navigateTo).build()));

        // 2. Reuse the existing Scene if it already exists to maintain window properties,
        // or create a new smaller scene (1000 x 650) if launching directly.
        if (stage.getScene() != null) {
            stage.getScene().setRoot(mainLayout);
        } else {
            // Default smaller initial size: 1000 x 650 (Changed from 1200 x 750)
            Scene scene = new Scene(mainLayout, 1000, 650);
            stage.setScene(scene);
        }

        stage.setResizable(true);

        // 3. Restore the maximized state if the login page was maximized
        if (wasMaximized) {
            stage.setMaximized(true);
        }

        stage.show();
        
        Platform.runLater(() -> mainLayout.requestLayout());
    }

    // ── Sidebar (JavaFX Part 1 — VBox) ───────────────────────────────────────
    private VBox buildSidebar(Stage stage) {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(225);
        sidebar.setStyle("-fx-background-color:" + StyleHelper.PRIMARY + ";");

        // Logo area
        Circle logoCircle = new Circle(20);
        logoCircle.setFill(Color.web("rgba(255,255,255,0.15)"));
        Label logoLbl = new Label("GC");
        logoLbl.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:white;");
        StackPane logoIcon = new StackPane(logoCircle, logoLbl);

        Label appName = new Label("GreenCycle");
        appName.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:white;");
        Label roleTag = new Label("Administrator");
        roleTag.setStyle("-fx-font-size:11px;-fx-text-fill:rgba(255,255,255,0.60);-fx-font-weight:bold;");

        HBox logoRow = new HBox(12, logoIcon, new VBox(2, appName, roleTag));
        logoRow.setAlignment(Pos.CENTER_LEFT);

        VBox logoArea = new VBox(logoRow);
        logoArea.setPadding(new Insets(28, 20, 28, 24));

        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color:rgba(255,255,255,0.15);");

        Label menuHeader = new Label("MAIN MENU");
        menuHeader.setStyle("-fx-font-size:10px;-fx-font-weight:bold;-fx-text-fill:rgba(255,255,255,0.45);");
        VBox menuHeaderBox = new VBox(menuHeader);
        menuHeaderBox.setPadding(new Insets(18, 20, 6, 24));

        // Menu items
        HBox miDash      = createMenuItem("dashboard", "  Dashboard");
        HBox miResidents = createMenuItem("residents", "  Manage Residents");
        HBox miBookings  = createMenuItem("bookings",  "  Manage Bookings");
        HBox miSummary   = createMenuItem("summary",   "  Community Summary");

        VBox menuBox = new VBox(4, miDash, miResidents, miBookings, miSummary);
        menuBox.setPadding(new Insets(0, 12, 0, 12));

        setMenuActive("dashboard");

        // Spacer pushes logout to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color:rgba(255,255,255,0.15);");

        // Signed-in user info + logout
        Label lblSignedAs = new Label("Signed in as");
        lblSignedAs.setStyle("-fx-font-size:10px;-fx-text-fill:rgba(255,255,255,0.50);");
        Label lblUsername = new Label(Session.getDisplayName());
        lblUsername.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:white;");

        Button btnLogout = new Button("Sign Out");
        btnLogout.setPrefWidth(185);
        final String logoutNormal = "-fx-background-color:rgba(255,255,255,0.12);" +
            "-fx-text-fill:white;-fx-background-radius:25;-fx-padding:8 0;-fx-font-size:12px;-fx-cursor:hand;";
        final String logoutHover = "-fx-background-color:rgba(255,255,255,0.22);" +
            "-fx-text-fill:white;-fx-background-radius:25;-fx-padding:8 0;-fx-font-size:12px;-fx-cursor:hand;";
        btnLogout.setStyle(logoutNormal);

        btnLogout.setOnMouseEntered(e -> btnLogout.setStyle(logoutHover));
        btnLogout.setOnMouseExited(e  -> btnLogout.setStyle(logoutNormal));
        btnLogout.setOnAction(e -> { Session.logout(); new LoginScreen().show(stage); });

        VBox bottomArea = new VBox(10, new VBox(2, lblSignedAs, lblUsername), btnLogout);
        bottomArea.setPadding(new Insets(16, 12, 24, 12));

        sidebar.getChildren().addAll(
            logoArea, sep1, menuHeaderBox, menuBox,
            spacer, sep2, bottomArea);
        return sidebar;
    }

    private HBox createMenuItem(final String pageId, String label) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:rgba(255,255,255,0.85);-fx-font-size:13px;");
        lbl.setPrefWidth(175);

        HBox item = new HBox(lbl);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(11, 16, 11, 16));
        item.setStyle(StyleHelper.sidebarNormal());

        item.setOnMouseEntered(e -> {
            if (!currentPage.equals(pageId)) item.setStyle(StyleHelper.sidebarHover());
        });
        item.setOnMouseExited(e -> {
            if (!currentPage.equals(pageId)) item.setStyle(StyleHelper.sidebarNormal());
        });
        item.setOnMouseClicked(e -> navigateTo(pageId));

        menuItemMap.put(pageId, item);
        return item;
    }

    private void setMenuActive(String pageId) {
        for (Map.Entry<String, HBox> entry : menuItemMap.entrySet()) {
            entry.getValue().setStyle(
                entry.getKey().equals(pageId)
                    ? StyleHelper.sidebarActive()
                    : StyleHelper.sidebarNormal());
        }
        currentPage = pageId;
    }

    public void navigateTo(String pageId) {
        setMenuActive(pageId);
        Node page;
        if ("dashboard".equals(pageId)) {
            lblPageTitle.setText("Dashboard");
            page = new AdminDashboardPage(this::navigateTo).build();
        } else if ("residents".equals(pageId)) {
            lblPageTitle.setText("Manage Residents");
            page = new ManageResidentsPage(this::navigateTo).build();
        } else if ("bookings".equals(pageId)) {
            lblPageTitle.setText("Manage Bookings");
            page = new ManageBookingsAdminPage(this::navigateTo).build();
        } else {
            lblPageTitle.setText("Community Summary");
            page = new CommunitySummaryPage(this::navigateTo).build();
        }
        mainLayout.setCenter(wrapScroll(page));
    }

    private HBox buildHeader() {
        lblPageTitle = new Label("Dashboard");
        lblPageTitle.setStyle(StyleHelper.pageTitle());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Circle av = new Circle(18);
        av.setFill(Color.web(StyleHelper.ACCENT));
        Label avLbl = new Label(Session.getDisplayName().substring(0, 1).toUpperCase());
        avLbl.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:" + StyleHelper.PRIMARY + ";");
        StackPane avatar = new StackPane(av, avLbl);

        Label lblUser = new Label(Session.getDisplayName());
        lblUser.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:" + StyleHelper.TEXT_DARK + ";");
        Label lblRoleTag = new Label("Admin");
        lblRoleTag.setStyle("-fx-font-size:10px;-fx-font-weight:bold;-fx-text-fill:" + StyleHelper.PRIMARY + ";" +
            "-fx-background-color:" + StyleHelper.ACCENT + ";-fx-background-radius:8;-fx-padding:2 8;");

        HBox header = new HBox(18, lblPageTitle, spacer, avatar, new VBox(2, lblUser, lblRoleTag));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 30, 0, 30));
        header.setPrefHeight(65);
        header.setStyle("-fx-background-color:white;" +
            "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.06),8,0,0,2);");
        return header;
    }

    private ScrollPane wrapScroll(Node content) {
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        // 4. Added fitToHeight to ensure content scales properly vertically
        sp.setFitToHeight(true);
        sp.setStyle("-fx-background-color:" + StyleHelper.BG + ";-fx-border-color:transparent;");
        return sp;
    }
}
