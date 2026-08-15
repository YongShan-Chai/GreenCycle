package ui.pages;

import data.AppException;
import data.DataStore;
import data.Resident;
import data.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import ui.StyleHelper;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * ManageResidentsPage — Admin resident management screen.
 *   Part 1 — VBox, HBox layout panes
 *   Part 2 — Lambda event handlers for search, selection, remark saving, delete
 *   Part 4 — ListView, TextField, TextArea, Button, Label
 *   Topic 7 — try-catch for AppException validation
 */
public class ManageResidentsPage extends BasePage {

    // Part 4 — UI components as instance fields
    private ListView<String>    lvResidents;
    private ArrayList<Resident> displayedResidents = new ArrayList<Resident>();
    private TextField           txtSearch;
    private Label               lblCount;

    // JavaFX Part 4 — TextArea for Admin Remarks / Special Collection Instructions
    private TextArea            txaRemarks;
    private Label               lblRemarksHeader;
    private Button              btnSaveRemark;

    public ManageResidentsPage() { super(); }
    public ManageResidentsPage(Consumer<String> navigate) { super(navigate); }

    @Override
    public Node build() {
        // ── Page title ────────────────────────────────────────────────────────
        Label lblTitle = new Label("Manage Residents");
        lblTitle.setStyle(StyleHelper.pageTitle());
        Label lblSub = new Label("View, search, and manage special collection remarks for community residents.");
        lblSub.setStyle(StyleHelper.mutedLabel());
        VBox titleBox = new VBox(4, lblTitle, lblSub);

        // ── Toolbar — Part 1: HBox, Part 4: TextField, Button ─────────────────
        txtSearch = makeField("Search by name or Resident ID...");
        txtSearch.setPrefWidth(280);
        txtSearch.setOnAction(e -> handleSearch());

        Button btnSearch  = makeSecBtn("Search");
        Button btnShowAll = makeGhostBtn("Show All");

        btnSearch.setOnAction(e -> handleSearch());
        btnShowAll.setOnAction(e -> { txtSearch.clear(); loadList(DataStore.residents); });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox toolbar = new HBox(10, txtSearch, btnSearch, btnShowAll);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // ── ListView — Part 4: ListView component ─────────────────────────────
        Label lvHeader = makeListHeader(
            String.format("  %-7s  %-22s  %-8s  %-14s  %-18s  %-8s  %s",
                "ID", "Name", "Unit", "Phone", "Waste Types", "Points", "Tier"));

        lvResidents = makeListView(250);
        lvResidents.setMinHeight(180);
        VBox.setVgrow(lvResidents, Priority.ALWAYS);

        // ── JavaFX Part 4 — TextArea Section (Admin Remarks) ───────────────────
        lblRemarksHeader = new Label("Admin Remarks & Special Collection Instructions:");
        lblRemarksHeader.setStyle("-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:" + StyleHelper.TEXT_DARK + ";");

        txaRemarks = new TextArea();
        txaRemarks.setPromptText("Select a resident from the table above to view or write admin remarks (e.g. gate access, collection instructions)...");
        txaRemarks.setPrefRowCount(3);
        txaRemarks.setPrefHeight(85);
        txaRemarks.setMinHeight(75);
        txaRemarks.setWrapText(true);
        txaRemarks.setStyle(
            "-fx-background-color:white;-fx-border-color:" + StyleHelper.BORDER + ";" +
            "-fx-border-radius:8;-fx-background-radius:8;-fx-font-size:12px;");

        btnSaveRemark = makeSecBtn("Save Remark");
        btnSaveRemark.setOnAction(e -> handleSaveRemark());

        Region remarksSpacer = new Region();
        HBox.setHgrow(remarksSpacer, Priority.ALWAYS);
        HBox remarksBar = new HBox(10, lblRemarksHeader, remarksSpacer, btnSaveRemark);
        remarksBar.setAlignment(Pos.CENTER_LEFT);

        VBox remarksBox = new VBox(6, remarksBar, txaRemarks);

        // Part 2 — Lambda: selecting a row loads the resident's saved remark directly
        lvResidents.setOnMouseClicked(e -> {
            Resident r = getSelected();
            if (r != null) {
                lblRemarksHeader.setText("Admin Remarks for: " + r.getName() + " (" + r.getId() + ")");
                txaRemarks.setText(r.getRemark());
            }
        });

        // ── Count label and delete button ─────────────────────────────────────
        lblCount = new Label();
        lblCount.setStyle(StyleHelper.mutedLabel());

        Button btnDelete = makeDangerBtn("Delete Selected");
        btnDelete.setOnAction(e -> handleDelete());

        Region bSpacer = new Region();
        HBox.setHgrow(bSpacer, Priority.ALWAYS);
        HBox bottomBar = new HBox(10, lblCount, bSpacer, btnDelete);
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        // ── Card wrapper ──────────────────────────────────────────────────────
        VBox card = makeCard(14);
        card.getChildren().addAll(toolbar, lvHeader, lvResidents, new Separator(), remarksBox, new Separator(), bottomBar);

        // Load data AFTER lblCount is initialised
        loadList(DataStore.residents);

        VBox root = new VBox(24, titleBox, card);
        root.setPadding(new Insets(32, 36, 32, 36));
        root.setStyle("-fx-background-color:" + StyleHelper.BG + ";");
        return root;
    }

    // ── Save Remark handler — Topic 7: try-catch ─────────────────────────────
    private void handleSaveRemark() {
        try {
            Resident sel = getSelected();
            if (sel == null) {
                throw new AppException("Please select a resident from the table first.");
            }
            String remark = txaRemarks.getText().trim();
            // Saved directly to the Resident object in DataStore.residents
            sel.setRemark(remark);

            showAlert(Alert.AlertType.INFORMATION, "Remark Saved",
                "Admin remark for resident \"" + sel.getName() + "\" (" + sel.getId() + ") has been saved.");
        } catch (AppException e) {
            showAlert(Alert.AlertType.WARNING, "Save Remark", e.getMessage());
        }
    }

    // ── Load residents into ListView ──────────────────────────────────────────
    private void loadList(java.util.List<Resident> list) {
        lvResidents.getItems().clear();
        displayedResidents.clear();
        for (Resident r : list) {
            int  pts  = DataStore.getPointsForResident(r.getId());
            String tier = DataStore.getTier(pts);
            String line = String.format("  %-7s  %-22s  %-8s  %-14s  %-18s  %-8s  %s",
                r.getId(), truncate(r.getName(), 22),
                r.getUnit(), r.getPhone(),
                truncate(r.getWasteTypesString(), 18),
                pts + " pts", tier);
            lvResidents.getItems().add(line);
            displayedResidents.add(r);
        }
        lblCount.setText("Showing " + list.size() + " of " +
                         DataStore.residents.size() + " resident(s).");
    }

    private Resident getSelected() {
        int idx = lvResidents.getSelectionModel().getSelectedIndex();
        return (idx >= 0 && idx < displayedResidents.size())
            ? displayedResidents.get(idx) : null;
    }

    // ── Search handler — Topic 7: try-catch ───────────────────────────────────
    private void handleSearch() {
        try {
            String q = txtSearch.getText().trim().toLowerCase();
            DataStore.validateNotEmpty(q, "Search term");

            ArrayList<Resident> filtered = new ArrayList<Resident>();
            for (Resident r : DataStore.residents) {
                if (r.getName().toLowerCase().contains(q)
                        || r.getId().toLowerCase().contains(q)) {
                    filtered.add(r);
                }
            }
            loadList(filtered);
            if (filtered.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Results",
                    "No residents match: \"" + txtSearch.getText().trim() + "\"");
            }
        } catch (AppException e) {
            showAlert(Alert.AlertType.WARNING, "Search", e.getMessage());
        }
    }

    // ── Delete handler ────────────────────────────────────────────────────────
    private void handleDelete() {
        try {
            Resident sel = getSelected();
            if (sel == null) {
                throw new AppException("Please select a resident row first.");
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText(null);
            confirm.setContentText("Delete resident \"" + sel.getName() +
                "\" (ID: " + sel.getId() + ")?\n\nTheir booking history will remain.\nThis cannot be undone.");
            confirm.showAndWait().ifPresent(r -> {
                if (r == ButtonType.OK) {
                    DataStore.residents.remove(sel);

                    // Also remove linked user account
                    User toRemove = null;
                    for (User u : DataStore.users) {
                        if (sel.getId().equals(u.getLinkedResidentId())) { toRemove = u; break; }
                    }
                    if (toRemove != null) DataStore.users.remove(toRemove);

                    loadList(DataStore.residents);
                    txaRemarks.clear();
                    lblRemarksHeader.setText("Admin Remarks & Special Collection Instructions:");
                    showAlert(Alert.AlertType.INFORMATION, "Deleted",
                        "Resident \"" + sel.getName() + "\" removed.");
                }
            });
        } catch (AppException e) {
            showAlert(Alert.AlertType.WARNING, "Delete", e.getMessage());
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max - 1) + "." : s;
    }
}