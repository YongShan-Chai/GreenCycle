# GreenCycle V3 — Community Recycling & Waste Collection Scheduler
Java II Group Assignment | JavaFX 8 | Oracle JDK 8 | No Maven

---

## How to Run
  Windows  : double-click compile.bat
  Mac/Linux: chmod +x compile.sh && ./compile.sh

Requires Oracle JDK 8. JavaFX is bundled — no extra downloads needed.

---

## Demo Accounts

  Role   | Username | Password
  -------|----------|----------
  Admin  | admin    | admin123
  User   | ahmad    | pass123
  User   | siti     | pass123
  User   | chen     | pass123

---

## Project File Structure

  GreenCycleV3/
  ├── compile.bat / compile.sh
  └── src/
      ├── Main.java
      ├── data/
      │   ├── AppException.java      ← Custom exception (Topic 7)
      │   ├── Person.java            ← Base class (Topic 1 + 2)
      │   ├── Resident.java          ← extends Person (Topic 2)
      │   ├── User.java              ← extends Person (Topic 2)
      │   ├── Booking.java           ← Model class (Topic 1)
      │   ├── DataStore.java         ← Static data + validation (Topic 7)
      │   └── Session.java           ← Static session holder (Topic 1)
      └── ui/
          ├── StyleHelper.java       ← Design constants
          ├── LoginScreen.java       ← Member 1 (Part 1,2,3,4 + Topic 7)
          ├── RegisterScreen.java    ← Member 1 (Part 1,2,4 + Topic 7)
          ├── AdminShell.java        ← Shared (Part 1,2,3)
          ├── UserShell.java         ← Shared (Part 1,2,3)
          └── pages/
              ├── BasePage.java              ← Abstract base (Topic 2)
              ├── AdminDashboardPage.java    ← Member 3 (Part 1,2,4)
              ├── ManageResidentsPage.java   ← Member 1 (Part 1,2,4 + Topic 7)
              ├── ManageBookingsAdminPage.java← Member 2 (Part 1,2,4 + Topic 7)
              ├── CommunitySummaryPage.java  ← Member 3 (Part 1,4)
              ├── UserDashboardPage.java     ← Member 3 (Part 1,2,3,4)
              ├── BookPickupPage.java        ← Member 2 (Part 1,2,4 + Topic 7)
              ├── MyBookingsPage.java        ← Member 2 (Part 1,2,4 + Topic 7)
              └── MyPointsPage.java          ← Member 3 (Part 1,4)

---

## Lecture Content Mapping

### Topic 1 — Objects and Classes
  - Person, Resident, User, Booking: private fields, constructors, this keyword
  - Getters (getId, getName, getStatus...) and setters (setStatus, setPoints...)
  - DataStore, Session: static fields and methods
  - Visibility modifiers (private, public, protected) used throughout

### Topic 2 — Inheritance and Polymorphism
  - Person is the superclass; Resident and User extend Person
  - super() called in Resident and User constructors
  - @Override on getSummary() in Resident and User — polymorphism
  - BasePage is abstract; all 8 page classes extend it and @Override build()
  - When AdminShell calls page.build(), the subclass version runs (late binding)
  - Code reuse: makeBtn(), makeField(), showAlert() defined once in BasePage,
    inherited by all 8 page subclasses

### Topic 7 — Exception Handling
  - AppException.java: custom exception class (extends Exception)
  - DataStore validation methods: validateResidentId(), validatePhone(),
    validateUsername(), validatePassword(), validateNotEmpty()
    — all declare 'throws AppException' and throw explicitly
  - All form handlers use try-catch to catch AppException and display to user
  - Multiple catch blocks in BookPickupPage (AppException, DateTimeException,
    NumberFormatException, Exception)
  - finally blocks in ManageBookingsAdminPage, MyBookingsPage, BookPickupPage,
    RegisterScreen — used to clear selection or ensure label visibility

### JavaFX Part 1 — Layout Panes
  - BorderPane: root layout in AdminShell and UserShell (left/top/center)
  - VBox: sidebar, page content stacks, cards
  - HBox: toolbar rows, button rows, header bar
  - StackPane: avatar icon (Circle + Label overlay) in both shells
  - GridPane: all form layouts (login, register, booking form)
  - FlowPane: category breakdown pills in CommunitySummaryPage

### JavaFX Part 2 — Event-Driven Programming
  - setOnAction(e -> ...) on all buttons (lambda expressions)
  - setOnMouseEntered / setOnMouseExited on buttons and cards (hover effects)
  - setOnMouseClicked on ListView rows (dashboard navigation)
  - focusedProperty().addListener on TextFields (green focus border)
  - cbFilter.setOnAction on ComboBox (reload list on filter change)
  - Delegation model: AdminShell passes this::navigateTo as Consumer<String>
    to dashboard pages so clicking cards calls shell navigation

### JavaFX Part 3 — Graphics
  - Circle: avatar icons in AdminShell header, UserShell header, both logos
  - Color.web(): sets fill colours for circles
  - StackPane overlays text Label on top of Circle

### JavaFX Part 4 — UI Controls
  - Button: all action buttons (makeBtn, makeSecBtn, makeDangerBtn)
  - Label: all text display, stat numbers, section titles
  - TextField: username, search, resident fields
  - PasswordField: login and registration password fields
  - CheckBox: waste type multi-selection in registration
  - RadioButton + ToggleGroup: waste category in BookPickupPage,
    role selection in LoginScreen
  - ComboBox: filter status, date (day/month/year), time slot, collection point
  - ListView<String>: replaces TableView for all record displays
    (residents, bookings, leaderboard, history)
  - ScrollPane: wraps page content in both shells

---

## Exception Handling Summary

  Location                  | Exception Caught         | Scenario
  --------------------------|--------------------------|---------------------------
  LoginScreen               | AppException             | Empty fields, wrong password
  RegisterScreen            | AppException, Exception  | Duplicate ID, bad phone, etc
  RegisterScreen            | finally                  | Always shows error label
  ManageResidentsPage       | AppException             | Empty search, no selection
  ManageBookingsAdminPage   | AppException, Exception  | No selection, wrong status
  ManageBookingsAdminPage   | finally                  | Always clears selection
  BookPickupPage            | AppException             | Missing fields, duplicate slot
  BookPickupPage            | DateTimeException        | Invalid date (e.g. Feb 30)
  BookPickupPage            | NumberFormatException    | parseInt failure
  BookPickupPage            | finally                  | Always shows feedback label
  MyBookingsPage            | AppException, Exception  | No selection, wrong status
  MyBookingsPage            | finally                  | Always clears selection

---

## Java 8 Compatibility Notes
  - No switch expressions (Java 14+) — uses if-else
  - No var keyword (Java 10+)
  - No List.of() (Java 9+) — uses new ArrayList<>()
  - No module-info.java (Java 9+ JPMS)
  - Lambdas used throughout (Java 8+)
  - java.time.LocalDate and DateTimeFormatter (Java 8+)
  - java.util.function.Consumer (Java 8+)
