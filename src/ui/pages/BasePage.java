package ui.pages;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.StyleHelper;

import java.util.function.Consumer;

/**
 * BasePage — abstract base class for all page content modules.
 *
 * Lecture reference:
 *   Topic 2 — Inheritance: all page classes extend BasePage
 *             Code reuse: shared helper methods defined once here
 *             Polymorphism: build() is abstract, each subclass implements it
 *             Abstract class: cannot be instantiated directly
 *
 * Each of the 8 page classes extends BasePage and overrides build().
 */
public abstract class BasePage {

    // Topic 2: protected field accessible to all subclasses
    protected Consumer<String> navigate;

    /** Default constructor — no navigation callback. */
    public BasePage() {
        this.navigate = null;
    }

    /** Constructor with navigation callback for page-switching. */
    public BasePage(Consumer<String> navigate) {
        this.navigate = navigate;
    }

    /**
     * Abstract method — MUST be overridden by every subclass.
     * Topic 2 — Abstract method / polymorphism:
     * AdminDashboardPage, ManageResidentsPage, etc. each provide
     * their own implementation of build().
     */
    public abstract Node build();

    // ── Shared helpers (Topic 2 — code reuse through inheritance) ─────────────

    /** Navigate to another page via the shell callback. */
    protected void goTo(String page) {
        if (navigate != null) navigate.accept(page);
    }

    /** Creates a pill-shaped primary (green) button with hover effect. */
    protected Button makeBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(StyleHelper.btnPrimary());
        // JavaFX Part 2 — Lambda expressions for event handling
        btn.setOnMouseEntered(e -> btn.setStyle(StyleHelper.btnPrimaryHover()));
        btn.setOnMouseExited(e  -> btn.setStyle(StyleHelper.btnPrimary()));
        return btn;
    }

    /** Creates a secondary (outlined) button with hover effect. */
    protected Button makeSecBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(StyleHelper.btnSecondary());
        btn.setOnMouseEntered(e -> btn.setStyle(StyleHelper.btnSecondaryHover()));
        btn.setOnMouseExited(e  -> btn.setStyle(StyleHelper.btnSecondary()));
        return btn;
    }

    /** Creates a danger (red) button with hover effect. */
    protected Button makeDangerBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(StyleHelper.btnDanger());
        btn.setOnMouseEntered(e -> btn.setStyle(StyleHelper.btnDangerHover()));
        btn.setOnMouseExited(e  -> btn.setStyle(StyleHelper.btnDangerHover()));
        return btn;
    }

    /** Creates a ghost (grey) button. */
    protected Button makeGhostBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(StyleHelper.btnGhost());
        return btn;
    }

    /** Creates a bold form label with minimum width for alignment. */
    protected Label makeFormLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-weight:bold;-fx-text-fill:" +
                     StyleHelper.TEXT_DARK + ";-fx-font-size:13px;");
        lbl.setMinWidth(160);
        return lbl;
    }

    /** Creates a styled TextField with green focus border. */
    protected TextField makeField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(260);
        tf.setStyle(StyleHelper.inputField());
        // JavaFX Part 2 — Lambda event: focus listener
        tf.focusedProperty().addListener((obs, old, focused) ->
            tf.setStyle(focused ? StyleHelper.inputFieldFocus() : StyleHelper.inputField()));
        return tf;
    }

    /** Creates a styled PasswordField with green focus border. */
    protected PasswordField makePasswordField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setPrefWidth(260);
        pf.setStyle(StyleHelper.inputField());
        pf.focusedProperty().addListener((obs, old, focused) ->
            pf.setStyle(focused ? StyleHelper.inputFieldFocus() : StyleHelper.inputField()));
        return pf;
    }

    /** Creates a white rounded card VBox with shadow. */
    protected VBox makeCard(int spacing) {
        VBox card = new VBox(spacing);
        card.setPadding(new Insets(24));
        card.setStyle(StyleHelper.card());
        return card;
    }

    /** Creates an error label (red text, wraps). */
    protected Label makeErrorLabel() {
        Label lbl = new Label("");
        lbl.setStyle("-fx-text-fill:" + StyleHelper.DANGER + ";-fx-font-size:12px;");
        lbl.setWrapText(true);
        return lbl;
    }

    /** Shows an Alert dialog. JavaFX Part 4 — Dialog. */
    protected void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Creates a ListView for displaying data records.
     * JavaFX Part 4 — ListView component.
     * Uses monospace font so formatted columns align correctly.
     */
    protected ListView<String> makeListView(int height) {
        ListView<String> lv = new ListView<String>();
        lv.setPrefHeight(height);
        lv.setStyle(StyleHelper.listView());
        return lv;
    }

    /**
     * Creates a header Label to sit above a ListView.
     * Shows column names in matching monospace font.
     */
    protected Label makeListHeader(String text) {
        Label lbl = new Label(text);
        lbl.setStyle(StyleHelper.listHeader());
        return lbl;
    }
}
