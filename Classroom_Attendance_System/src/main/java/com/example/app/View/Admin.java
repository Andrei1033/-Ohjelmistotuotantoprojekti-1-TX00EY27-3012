package com.example.app.View;

import com.example.app.Controller.AdminController;
import com.example.app.Model.LoginComponents.Role;
import com.example.app.Model.LoginComponents.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Cursor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class Admin {
    private final AdminController controller;
    private final User currentUser;
    private final Runnable onLogout;
    private boolean refreshingCourseOptions;

    public Admin() {
        this(new AdminController(), null, null);
    }

    public Admin(AdminController controller) {
        this(controller, null, null);
    }

    public Admin(User currentUser) {
        this(new AdminController(), currentUser, null);
    }

    public Admin(User currentUser, Runnable onLogout) {
        this(new AdminController(), currentUser, onLogout);
    }

    public Admin(AdminController controller, User currentUser) {
        this(controller, currentUser, null);
    }

    public Admin(AdminController controller, User currentUser, Runnable onLogout) {
        this.controller = controller;
        this.currentUser = currentUser;
        this.onLogout = onLogout;
    }

    public BorderPane getView() {

        VBox sidebar = new VBox();
        sidebar.setPrefWidth(158);
        sidebar.setPadding(new Insets(12, 14, 10, 10));
        sidebar.setStyle("-fx-background-color: #202F49;");

        HBox header = new HBox(9);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 20, 0));

        Circle logoCircle = new Circle(13, Color.web("#536FA4"));
        Label logoText = sidebarText("LO", 12, FontWeight.BOLD, "#FFFFFF");
        StackPane logo = new StackPane(logoCircle, logoText);
        logo.setPrefSize(20, 20);

        Label appName = sidebarText("Läsnäolo", 13, FontWeight.BOLD, "#FFFFFF");
        header.getChildren().addAll(logo, appName);

        Button coursesButton = new Button("<   Kurssini");
        coursesButton.setPrefHeight(22);
        coursesButton.setMaxWidth(Double.MAX_VALUE);
        coursesButton.setAlignment(Pos.CENTER_LEFT);
        coursesButton.setFocusTraversable(false);
        coursesButton.setStyle("-fx-background-color: #344A70; -fx-text-fill: white; "
                + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 4;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        com.example.app.Model.Admin currentAdmin = controller.getUsers().stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .findFirst()
                .orElse(null);
        String userName = currentUser == null
                ? (currentAdmin == null ? "Etunimi Sukunimi" : currentAdmin.getName())
                : currentUser.getFullName();
        String userRole = currentUser == null
                ? "Ylläpitäjä"
                : roleLabel(currentUser);

        HBox user = new HBox(7);
        user.setAlignment(Pos.CENTER_LEFT);
        user.setCursor(Cursor.HAND);
        Circle avatarCircle = new Circle(15, Color.web("#536FA4"));
        Label initials = sidebarText(initialsOf(userName), 12, FontWeight.BOLD, "#FFFFFF");
        StackPane avatar = new StackPane(avatarCircle, initials);
        avatar.setPrefSize(20, 20);
        avatar.setCursor(Cursor.HAND);

        Label userNameLabel = sidebarText(userName, 12, FontWeight.BOLD, "#FFFFFF");
        Label userRoleLabel = sidebarText(userRole, 11, FontWeight.NORMAL, "#A9B0BD");
        VBox userInfo = new VBox(0, userNameLabel, userRoleLabel);
        user.getChildren().addAll(avatar, userInfo);

        Button logout = new Button("Kirjaudu ulos");
        logout.setMaxWidth(Double.MAX_VALUE);
        logout.setStyle("-fx-background-color: transparent; -fx-text-fill: #A9B0BD; "
                + "-fx-font-size: 9px; -fx-alignment: CENTER-LEFT; -fx-cursor: hand;");
        logout.setOnAction(event -> {
            if (onLogout != null) {
                onLogout.run();
            }
        });

        user.setOnMouseClicked(event -> {
            if (currentUser != null && user.getScene() != null) {
                ProfileEditWindow.show(
                        user.getScene().getWindow(),
                        currentUser,
                        () -> {
                            userNameLabel.setText(currentUser.getFullName());
                            initials.setText(initialsOf(currentUser.getFullName()));
                        }
                );
                event.consume();
            }
        });

        sidebar.getChildren().addAll(header, new Region(), coursesButton, spacer, logout, user);


        HBox filters = new HBox(10);
        filters.setAlignment(Pos.CENTER_LEFT);

        Button allButton = new Button("kaikki");
        Button adminButton = new Button("Admin");
        Button teacherButton = new Button("Opettaja");
        Button studentButton = new Button("Opiskelija");

        allButton.getStyleClass().add("filter-button-active");
        adminButton.getStyleClass().add("filter-button");
        teacherButton.getStyleClass().add("filter-button");
        studentButton.getStyleClass().add("filter-button");

        TextField searchField = new TextField();
        searchField.setPromptText("Hae nimellä tai s-postilla");
        searchField.setPrefWidth(180);

        ComboBox<String> courseFilter = new ComboBox<>();
        courseFilter.setPromptText("Kaikki");
        courseFilter.setPrefWidth(140);

        courseFilter.getItems().add("Kaikki");
        courseFilter.getItems().addAll(controller.getCourses());
        courseFilter.setValue("Kaikki");

        Button addUserButton = new Button("+ Lisää uusi käyttäjä");
        addUserButton.getStyleClass().add("add-user-button");

        filters.getChildren().addAll(
                allButton,
                adminButton,
                teacherButton,
                studentButton,
                searchField,
                courseFilter,
                addUserButton
        );

        TableView<com.example.app.Model.Admin> table = new TableView<>();

        TableColumn<com.example.app.Model.Admin, String> nameColumn =
                new TableColumn<>("Nimi");

        TableColumn<com.example.app.Model.Admin, String> emailColumn =
                new TableColumn<>("Sähköposti");

        TableColumn<com.example.app.Model.Admin, String> roleColumn =
                new TableColumn<>("Rooli");

        TableColumn<com.example.app.Model.Admin, String> courseColumn =
                new TableColumn<>("Kurssit");

        TableColumn<com.example.app.Model.Admin, String> editColumn =
                new TableColumn<>("");


        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName())
        );

        emailColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmail())
        );

        roleColumn.setCellValueFactory(data ->
                new SimpleStringProperty(roleText(data.getValue().getRole()))
        );

        courseColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCourses())
        );

        editColumn.setCellValueFactory(data ->
                new SimpleStringProperty("")
        );


        nameColumn.setCellFactory(column -> new TableCell<>() {

            private final HBox container = new HBox(10);
            private final Label avatar = new Label();
            private final Label name = new Label();

            {
                container.setAlignment(Pos.CENTER_LEFT);

                avatar.getStyleClass().add("profile-avatar");
                name.getStyleClass().add("user-name");

                container.getChildren().addAll(
                        avatar,
                        name
                );
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {

                    name.setText(item);

                    String[] parts = item.split(" ");

                    if (parts.length >= 2) {
                        avatar.setText(
                                parts[0].substring(0, 1).toUpperCase()
                                        +
                                        parts[1].substring(0, 1).toUpperCase()
                        );
                    } else {
                        avatar.setText(
                                item.substring(0, Math.min(2, item.length()))
                                        .toUpperCase()
                        );
                    }

                    setGraphic(container);
                }
            }
        });



        roleColumn.setCellFactory(column -> new TableCell<>() {

            private final Label roleLabel = new Label();

            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);

                if (empty || role == null) {
                    setGraphic(null);
                } else {

                    roleLabel.setText(role);

                    roleLabel.getStyleClass().removeAll(
                            "role-student",
                            "role-teacher",
                            "role-admin"
                    );

                    switch (role) {
                        case "Opiskelija":
                            roleLabel.getStyleClass().add("role-student");
                            break;

                        case "Opettaja":
                            roleLabel.getStyleClass().add("role-teacher");
                            break;

                        case "Admin":
                            roleLabel.getStyleClass().add("role-admin");
                            break;
                    }

                    setGraphic(roleLabel);
                }
            }
        });



        editColumn.setCellFactory(column -> new TableCell<>() {

            private final Button editButton = new Button("✎");

            {
                editButton.getStyleClass().add("edit-button");

                editButton.setOnAction(event -> {
                    com.example.app.Model.Admin user = getTableRow().getItem();
                    if (user != null) {
                        showUserDialog(user, table);
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                } else {
                    setGraphic(editButton);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }
        });



        table.getColumns().addAll(
                nameColumn,
                emailColumn,
                roleColumn,
                courseColumn,
                editColumn
        );


        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        nameColumn.setMinWidth(180);
        emailColumn.setMinWidth(180);
        roleColumn.setMinWidth(120);
        courseColumn.setMinWidth(180);
        editColumn.setMinWidth(55);

        nameColumn.setPrefWidth(220);
        emailColumn.setPrefWidth(220);
        roleColumn.setPrefWidth(130);
        courseColumn.setPrefWidth(250);
        editColumn.setPrefWidth(60);


        table.setItems(controller.getUsers());

        table.setPlaceholder(
                new Label("Ei käyttäjiä")
        );

        Runnable refresh = () -> table.setItems(controller.filterUsers(
                selectedRole(allButton, adminButton, teacherButton, studentButton),
                searchField.getText(),
                courseFilter.getValue()));
        allButton.setOnAction(event -> {
            setActiveFilter(allButton, adminButton, teacherButton, studentButton);
            refresh.run();
        });
        adminButton.setOnAction(event -> {
            setActiveFilter(adminButton, allButton, teacherButton, studentButton);
            refresh.run();
        });
        teacherButton.setOnAction(event -> {
            setActiveFilter(teacherButton, allButton, adminButton, studentButton);
            refresh.run();
        });
        studentButton.setOnAction(event -> {
            setActiveFilter(studentButton, allButton, adminButton, teacherButton);
            refresh.run();
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> refresh.run());
        courseFilter.valueProperty().addListener((observable, oldValue, newValue) -> refresh.run());
        addUserButton.setOnAction(event -> showUserDialog(null, table));


        VBox content = new VBox(25);
        content.setPadding(new Insets(30));
        content.getStyleClass().add("content");

        if (controller.getDatabaseError() != null) {
            Label error = new Label(
                    "Käyttäjätietojen haku tietokannasta epäonnistui: "
                            + controller.getDatabaseError());
            error.setWrapText(true);
            error.setStyle("-fx-text-fill: #b00020;");
            content.getChildren().add(error);
        }

        content.getChildren().addAll(
                filters,
                table
        );

        VBox.setVgrow(table, Priority.ALWAYS);

        BorderPane root = new BorderPane();

        root.setLeft(sidebar);
        root.setCenter(content);

        root.getStyleClass().add("root");

        root.getStylesheets().add(
                getClass()
                        .getResource("/style.css")
                        .toExternalForm()
        );

        return root;
    }

    private Role selectedRole(Button all, Button admin, Button teacher, Button student) {
        if (admin.getStyleClass().contains("filter-button-active")) return Role.ADMIN;
        if (teacher.getStyleClass().contains("filter-button-active")) return Role.TEACHER;
        if (student.getStyleClass().contains("filter-button-active")) return Role.STUDENT;
        return null;
    }

    private void setActiveFilter(Button active, Button... inactive) {
        active.getStyleClass().removeAll("filter-button", "filter-button-active");
        active.getStyleClass().add("filter-button-active");
        for (Button button : inactive) {
            button.getStyleClass().removeAll("filter-button", "filter-button-active");
            button.getStyleClass().add("filter-button");
        }
    }

    private String roleText(Role role) {
        return switch (role) {
            case ADMIN -> "Admin";
            case TEACHER -> "Opettaja";
            case STUDENT -> "Opiskelija";
        };
    }

    private String roleLabel(User user) {
        return switch (user.getRole()) {
            case ADMIN -> "Ylläpitäjä";
            case TEACHER -> "Opettaja";
            case STUDENT -> "Opiskelija";
        };
    }

    private void addCourseSelector(VBox courseFields,
                                   List<ComboBox<String>> courseSelectors,
                                   List<String> availableCourses,
                                   String selectedCourse) {
        ComboBox<String> course = new ComboBox<>(
                FXCollections.observableArrayList("Ei kursseja")
        );
        course.getItems().addAll(availableCourses);
        course.setValue(selectedCourse);
        course.setPrefWidth(220);
        courseSelectors.add(course);
        course.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (refreshingCourseOptions) {
                return;
            }
            if (newValue != null
                    && !newValue.equals("Ei kursseja")
                    && courseSelectors.stream()
                    .anyMatch(other -> other != course && newValue.equals(other.getValue()))) {
                refreshingCourseOptions = true;
                course.setValue(oldValue == null ? "Ei kursseja" : oldValue);
                refreshingCourseOptions = false;
                return;
            }
            refreshCourseOptions(courseSelectors, availableCourses);
        });

        int addButtonIndex = courseFields.getChildren().size();
        if (addButtonIndex > 0
                && courseFields.getChildren().get(addButtonIndex - 1) instanceof Button) {
            addButtonIndex--;
        }
        courseFields.getChildren().add(addButtonIndex, course);
    }

    private void refreshCourseOptions(List<ComboBox<String>> courseSelectors,
                                      List<String> availableCourses) {
        refreshingCourseOptions = true;
        try {
            List<String> selectedCourses = courseSelectors.stream()
                    .map(ComboBox::getValue)
                    .filter(course -> course != null && !course.equals("Ei kursseja"))
                    .toList();

            for (ComboBox<String> selector : courseSelectors) {
                String currentCourse = selector.getValue();
                List<String> options = new ArrayList<>();
                options.add("Ei kursseja");
                for (String course : availableCourses) {
                    if (!selectedCourses.contains(course) || course.equals(currentCourse)) {
                        options.add(course);
                    }
                }
                selector.setItems(FXCollections.observableArrayList(options));
                selector.setValue(options.contains(currentCourse) ? currentCourse : "Ei kursseja");
            }
        } finally {
            refreshingCourseOptions = false;
        }
    }

    private void showUserDialog(com.example.app.Model.Admin user,
                                TableView<com.example.app.Model.Admin> table) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(user == null ? "Lisää käyttäjä" : "Muokkaa käyttäjää");
        dialog.setHeaderText(user == null
                ? "Luo uusi käyttäjä"
                : "Muokkaa käyttäjän tietoja");
        dialog.getDialogPane().getStyleClass().add("admin-dialog");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );
        ButtonType save = new ButtonType("Tallenna", ButtonBar.ButtonData.OK_DONE);
        ButtonType delete = new ButtonType("Poista", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        if (user != null) {
            dialog.getDialogPane().getButtonTypes().add(delete);
        }

        TextField name = new TextField(user == null ? "" : user.getName());
        TextField email = new TextField(user == null ? "" : user.getEmail());
        ComboBox<String> role = new ComboBox<>(FXCollections.observableArrayList("Opiskelija", "Opettaja", "Admin"));
        role.setValue(user == null ? "Opiskelija" : roleText(user.getRole()));
        List<ComboBox<String>> courseSelectors = new ArrayList<>();
        VBox courseFields = new VBox(5);
        List<String> existingCourses = user == null || user.getCourses().equals("—")
                ? List.of()
                : List.of(user.getCourses().split(",\\s*"));
        List<String> availableCourses = new ArrayList<>(controller.getCourses());
        if (existingCourses.isEmpty()) {
            addCourseSelector(courseFields, courseSelectors, availableCourses, "Ei kursseja");
        } else {
            for (String course : existingCourses) {
                if (!availableCourses.contains(course)) {
                    availableCourses.add(course);
                }
                addCourseSelector(courseFields, courseSelectors, availableCourses, course);
            }
        }
        refreshCourseOptions(courseSelectors, availableCourses);
        Button addCourse = new Button("+");
        addCourse.setFocusTraversable(false);
        addCourse.setOnAction(event -> {
            addCourseSelector(courseFields, courseSelectors, availableCourses, "Ei kursseja");
            refreshCourseOptions(courseSelectors, availableCourses);
        });
        courseFields.getChildren().add(addCourse);
        PasswordField password = new PasswordField();
        password.setPromptText(user == null ? "Pakollinen" : "Jätä tyhjäksi, jos ei vaihdeta");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Vahvista salasana");
        GridPane fields = new GridPane();
        fields.getStyleClass().add("admin-dialog-fields");
        fields.setHgap(10);
        fields.setVgap(10);
        fields.addRow(0, new Label("Nimi"), name);
        fields.addRow(1, new Label("Sähköposti"), email);
        fields.addRow(2, new Label("Rooli"), role);
        fields.addRow(3, new Label("Kurssit"), courseFields);
        fields.addRow(4, new Label("Salasana"), password);
        fields.addRow(5, new Label("Vahvista"), confirmPassword);
        ScrollPane scrollPane = new ScrollPane(fields);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(360);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().lookupButton(save).getStyleClass().add("dialog-save-button");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL)
                .getStyleClass().add("dialog-cancel-button");

        dialog.setResultConverter(button -> {
            if (button == delete) {
                Alert confirmation = new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "Haluatko varmasti poistaa käyttäjän " + user.getName() + "?",
                        ButtonType.YES,
                        ButtonType.NO
                );
                confirmation.setHeaderText("Vahvista käyttäjän poisto");
                if (confirmation.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    try {
                        controller.deleteUser(user);
                        table.refresh();
                    } catch (RuntimeException exception) {
                        Alert alert = new Alert(
                                Alert.AlertType.ERROR,
                                exception.getMessage(),
                                ButtonType.OK
                        );
                        alert.setHeaderText("Käyttäjän poisto epäonnistui");
                        alert.showAndWait();
                    }
                }
                return null;
            }
            if (button != save) return null;
            try {
                Role selectedRole = roleFromText(role.getValue());
                String selectedCourses = courseSelectors.stream()
                        .map(ComboBox::getValue)
                        .filter(course -> course != null && !course.equals("Ei kursseja"))
                        .distinct()
                        .reduce((first, second) -> first + ", " + second)
                        .orElse("—");
                String selectedPassword = password.getText();
                if (user == null && selectedPassword.isBlank()) {
                    throw new IllegalArgumentException("Salasana on pakollinen uudelle käyttäjälle");
                }
                if (!selectedPassword.isBlank()
                        && !selectedPassword.equals(confirmPassword.getText())) {
                    throw new IllegalArgumentException("Salasanat eivät täsmää");
                }
                if (user == null) {
                    controller.addUser(name.getText(), email.getText(), selectedRole, selectedCourses,
                            selectedPassword);
                } else {
                    controller.updateUser(user, name.getText(), email.getText(), selectedRole,
                            selectedCourses, selectedPassword.isBlank() ? null : selectedPassword);
                }
                table.refresh();
            } catch (RuntimeException exception) {
                Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage(), ButtonType.OK);
                alert.setHeaderText("Virheelliset käyttäjätiedot");
                alert.showAndWait();
            }
            return null;
        });
        dialog.showAndWait();
    }

    private Role roleFromText(String value) {
        return switch (value) {
            case "Admin" -> Role.ADMIN;
            case "Opettaja" -> Role.TEACHER;
            default -> Role.STUDENT;
        };
    }

    private static String initialsOf(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        String[] parts = trimmed.split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private static Label sidebarText(String value, double size, FontWeight weight, String color) {
        Label label = new Label(value);
        label.setFont(Font.font("System", weight, size));
        label.setTextFill(Color.web(color));
        return label;
    }
}