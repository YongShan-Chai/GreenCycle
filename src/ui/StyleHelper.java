package ui;

/**
 * StyleHelper — centralised design system for GreenCycle V3.
 *
 * All colour constants and reusable CSS style strings are defined here
 * so every screen shares a consistent visual language.
 *
 * Color palette (modern eco-themed):
 *   Primary  : #1B5E4A  (Dark Forest Green)
 *   Secondary: #7CB342  (Fresh Green)
 *   Accent   : #C8E6C9  (Light Green)
 *   BG       : #F5F7F5  (Soft White)
 *   Card     : #FFFFFF  (White)
 *   Text     : #2D2D2D  (Dark Gray)
 *   Border   : #E5E7EB  (Light Gray)
 */
public class StyleHelper {

    // ── Colour constants ──────────────────────────────────────────────────────
    public static final String PRIMARY      = "#1B5E4A";
    public static final String PRIMARY_DARK = "#134438";
    public static final String SECONDARY    = "#7CB342";
    public static final String ACCENT       = "#C8E6C9";
    public static final String BG           = "#F5F7F5";
    public static final String CARD_BG      = "#FFFFFF";
    public static final String TEXT_DARK    = "#2D2D2D";
    public static final String TEXT_MUTED   = "#6B7280";
    public static final String BORDER       = "#E5E7EB";
    public static final String DANGER       = "#DC2626";
    public static final String WARNING      = "#D97706";
    public static final String SUCCESS      = "#16A34A";
    public static final String INFO         = "#2563EB";

    // ── Button styles ─────────────────────────────────────────────────────────
    public static String btnPrimary() {
        return "-fx-background-color:" + PRIMARY + ";-fx-text-fill:white;" +
               "-fx-background-radius:25;-fx-padding:10 26 10 26;" +
               "-fx-font-size:13px;-fx-font-weight:bold;-fx-cursor:hand;";
    }
    public static String btnPrimaryHover() {
        return "-fx-background-color:" + PRIMARY_DARK + ";-fx-text-fill:white;" +
               "-fx-background-radius:25;-fx-padding:10 26 10 26;" +
               "-fx-font-size:13px;-fx-font-weight:bold;-fx-cursor:hand;";
    }
    public static String btnSecondary() {
        return "-fx-background-color:white;-fx-text-fill:" + PRIMARY + ";" +
               "-fx-background-radius:25;-fx-padding:9 24 9 24;" +
               "-fx-font-size:13px;-fx-font-weight:bold;-fx-cursor:hand;" +
               "-fx-border-color:" + PRIMARY + ";-fx-border-radius:25;-fx-border-width:1.5;";
    }
    public static String btnSecondaryHover() {
        return "-fx-background-color:" + ACCENT + ";-fx-text-fill:" + PRIMARY + ";" +
               "-fx-background-radius:25;-fx-padding:9 24 9 24;" +
               "-fx-font-size:13px;-fx-font-weight:bold;-fx-cursor:hand;" +
               "-fx-border-color:" + PRIMARY + ";-fx-border-radius:25;-fx-border-width:1.5;";
    }
    public static String btnDanger() {
        return "-fx-background-color:" + DANGER + ";-fx-text-fill:white;" +
               "-fx-background-radius:25;-fx-padding:10 26 10 26;" +
               "-fx-font-size:13px;-fx-font-weight:bold;-fx-cursor:hand;";
    }
    public static String btnDangerHover() {
        return "-fx-background-color:#B91C1C;-fx-text-fill:white;" +
               "-fx-background-radius:25;-fx-padding:10 26 10 26;" +
               "-fx-font-size:13px;-fx-font-weight:bold;-fx-cursor:hand;";
    }
    public static String btnGhost() {
        return "-fx-background-color:" + BORDER + ";-fx-text-fill:" + TEXT_DARK + ";" +
               "-fx-background-radius:25;-fx-padding:10 26 10 26;" +
               "-fx-font-size:13px;-fx-cursor:hand;";
    }

    // ── Input field styles ────────────────────────────────────────────────────
    public static String inputField() {
        return "-fx-background-color:white;-fx-border-color:" + BORDER + ";" +
               "-fx-border-radius:8;-fx-background-radius:8;" +
               "-fx-padding:10 14 10 14;-fx-font-size:13px;-fx-text-fill:" + TEXT_DARK + ";";
    }
    public static String inputFieldFocus() {
        return "-fx-background-color:white;-fx-border-color:" + PRIMARY + ";" +
               "-fx-border-radius:8;-fx-background-radius:8;" +
               "-fx-padding:10 14 10 14;-fx-font-size:13px;-fx-text-fill:" + TEXT_DARK + ";";
    }

    // ── Card styles ───────────────────────────────────────────────────────────
    public static String card() {
        return "-fx-background-color:white;-fx-background-radius:16;" +
               "-fx-border-radius:16;-fx-border-color:" + BORDER + ";-fx-border-width:1;" +
               "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.07),14,0,0,3);";
    }

    // ── Label / typography styles ─────────────────────────────────────────────
    public static String pageTitle() {
        return "-fx-font-size:22px;-fx-font-weight:bold;-fx-text-fill:" + TEXT_DARK + ";";
    }
    public static String sectionTitle() {
        return "-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:" + TEXT_DARK + ";";
    }
    public static String bodyText() {
        return "-fx-font-size:13px;-fx-text-fill:" + TEXT_DARK + ";";
    }
    public static String mutedLabel() {
        return "-fx-font-size:12px;-fx-text-fill:" + TEXT_MUTED + ";";
    }
    public static String statLabel() {
        return "-fx-font-size:11px;-fx-text-fill:" + TEXT_MUTED + ";-fx-font-weight:bold;";
    }
    public static String statNumber(String color) {
        return "-fx-font-size:28px;-fx-font-weight:bold;-fx-text-fill:" + color + ";";
    }

    // ── Status pill styles ────────────────────────────────────────────────────
    public static String statusPill(String status) {
        String color, bg;
        if ("Completed".equals(status))  { color = "#15803D"; bg = "#DCFCE7"; }
        else if ("Cancelled".equals(status)) { color = "#B91C1C"; bg = "#FEE2E2"; }
        else                             { color = "#D97706"; bg = "#FEF3C7"; }
        return "-fx-background-color:" + bg + ";-fx-text-fill:" + color + ";" +
               "-fx-background-radius:20;-fx-padding:3 10 3 10;" +
               "-fx-font-size:11px;-fx-font-weight:bold;";
    }

    // ── Sidebar styles ────────────────────────────────────────────────────────
    public static String sidebarNormal() {
        return "-fx-background-color:transparent;-fx-cursor:hand;";
    }
    public static String sidebarHover() {
        return "-fx-background-color:rgba(255,255,255,0.09);-fx-background-radius:10;-fx-cursor:hand;";
    }
    public static String sidebarActive() {
        return "-fx-background-color:rgba(255,255,255,0.18);-fx-background-radius:10;-fx-cursor:hand;";
    }

    // ── ListView style ────────────────────────────────────────────────────────
    /** Monospaced font so formatted strings align in columns. */
    public static String listView() {
        return "-fx-font-family:'Courier New';-fx-font-size:12px;" +
               "-fx-border-color:" + BORDER + ";-fx-border-radius:8;" +
               "-fx-background-radius:8;";
    }

    /** Header label above a ListView — matches the monospace format. */
    public static String listHeader() {
        return "-fx-font-family:'Courier New';-fx-font-size:12px;-fx-font-weight:bold;" +
               "-fx-text-fill:" + TEXT_MUTED + ";-fx-padding:6 12 6 16;" +
               "-fx-background-color:" + BG + ";";
    }
}
