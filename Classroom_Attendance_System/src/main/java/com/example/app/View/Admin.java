package com.example.app.View;

import com.example.app.Controller.AdminController;
import com.example.app.Localisation.FontSupport;
import com.example.app.Localisation.i18n;
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
import javafx.util.StringConverter;

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
        Label logoText = sidebarText(i18n.t("app.logo"), 12, FontWeight.BOLD, "#FFFFFF");
        StackPane logo = new StackPane(logoCircle, logoText);
        logo.setPrefSize(20, 20);

        Label appName = sidebarText(i18n.t("app.attendance"), 13, FontWeight.BOLD, "#FFFFFF");
        header.getChildren().addAll(logo, appName);

        Button coursesButton = new Button("<   " + i18n.t("teacher.myCourses"));
        coursesButton.setPrefHeight(22);
        coursesButton.setMaxWidth(Double.MAX_VALUE);
        coursesButton.setAlignment(Pos.CENTER_LEFT);
        coursesButton.setFocusTraversable(false);
        coursesButton.setStyle("-fx-background-color: #344A70; -fx-text-fill: white; "
                + "-fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 4;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        ComboBox<i18n.Lang> languageCombo = new ComboBox<>();
        languageCombo.getItems().setAll(i18n.SUPPORTED);
        languageCombo.setValue(i18n.getCurrentLang());
        languageCombo.setMaxWidth(Double.MAX_VALUE);
        languageCombo.setFocusTraversable(false);
        languageCombo.setCursor(Cursor.HAND);
        languageCombo.setStyle(
                "-fx-background-color: #344A70; "
                        + "-fx-background-radius: 4; "
                        + "-fx-font-family: '" + FontSupport.pickFamily() + "'; "
                        + "-fx-font-size: 11px; "
                        + "-fx-text-fill: white;"
        );
        languageCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(i18n.Lang language) {
                return language == null ? "" : language.label();
            }

            @Override
            public i18n.Lang fromString(String value) {
                return null;
            }
        });
        languageCombo.setOnAction(event -> {
            i18n.Lang selected = languageCombo.getValue();
            if (selected != null && !selected.equals(i18n.getCurrentLang())) {
                i18n.setLanguage(selected);
                if (languageCombo.getScene() != null) {
                    languageCombo.getScene().setRoot(getView());
                }
            }
        });

        com.example.app.Model.Admin currentAdmin = controller.getUsers().stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .findFirst()
                .orElse(null);
        String userName = currentUser == null
                ? (currentAdmin == null ? i18n.t("admin.defaultName") : currentAdmin.getName())
                : currentUser.getFullName();
        String userRole = currentUser == null
                ? i18n.t("app.admin.label")
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

        Button logout = new Button(i18n.t("app.logout"));
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

        sidebar.getChildren().addAll(
                header, new Region(), coursesButton, spacer, languageCombo, logout, user);


        HBox filters = new HBox(10);
        filters.setAlignment(Pos.CENTER_LEFT);

        Button allButton = new Button(i18n.t("admin.filterAll"));
        Button adminButton = new Button(i18n.t("admin.userRoleAdmin"));
        Button teacherButton = new Button(i18n.t("admin.userRoleTeacher"));
        Button studentButton = new Button(i18n.t("admin.userRoleStudent"));

        allButton.getStyleClass().add("filter-button-active");
        adminButton.getStyleClass().add("filter-button");
        teacherButton.getStyleClass().add("filter-button");
        studentButton.getStyleClass().add("filter-button");

        TextField searchField = new TextField();
        searchField.setPromptText(i18n.t("app.searchByName"));
        searchField.setPrefWidth(180);

        ComboBox<String> courseFilter = new ComboBox<>();
        courseFilter.setPromptText(i18n.t("app.all"));
        courseFilter.setPrefWidth(140);

        courseFilter.getItems().add(i18n.t("app.all"));
        courseFilter.getItems().addAll(controller.getCourses());
        courseFilter.setValue(i18n.t("app.all"));

        Button addUserButton = new Button(i18n.t("admin.addUser"));
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
                new TableColumn<>(i18n.t("admin.userName"));

        TableColumn<com.example.app.Model.Admin, String> emailColumn =
                new TableColumn<>(i18n.t("admin.userEmail"));

        TableColumn<com.example.app.Model.Admin, String> roleColumn =
                new TableColumn<>(i18n.t("admin.userRole"));

        TableColumn<com.example.app.Model.Admin, String> courseColumn =
                new TableColumn<>(i18n.t("admin.userCourses"));

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

                    if (role.equals(i18n.t("admin.userRoleStudent"))) {
                        roleLabel.getStyleClass().add("role-student");
                    } else if (role.equals(i18n.t("admin.userRoleTeacher"))) {
                        roleLabel.getStyleClass().add("role-teacher");
                    } else if (role.equals(i18n.t("admin.userRoleAdmin"))) {
                        roleLabel.getStyleClass().add("role-admin");
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
                new Label(i18n.t("admin.emptyUserList"))
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
                    i18n.t("admin.loadFailed", controller.getDatabaseError()));
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
        root.setNodeOrientation(FontSupport.isRtl()
                ? javafx.geometry.NodeOrientation.RIGHT_TO_LEFT
                : javafx.geometry.NodeOrientation.LEFT_TO_RIGHT);

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
            case ADMIN -> i18n.t("admin.userRoleAdmin");
            case TEACHER -> i18n.t("admin.userRoleTeacher");
            case STUDENT -> i18n.t("admin.userRoleStudent");
        };
    }

    private String roleLabel(User user) {
        return switch (user.getRole()) {
            case ADMIN -> i18n.t("app.admin.label");
            case TEACHER -> i18n.t("app.teacher");
            case STUDENT -> i18n.t("app.student");
        };
    }

    private void addCourseSelector(VBox courseFields,
                                   List<ComboBox<String>> courseSelectors,
                                   List<String> availableCourses,
                                   String selectedCourse) {
        ComboBox<String> course = new ComboBox<>(
                FXCollections.observableArrayList(i18n.t("app.emptyCourses"))
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
                    && !newValue.equals(i18n.t("app.emptyCourses"))
                    && courseSelectors.stream()
                    .anyMatch(other -> other != course && newValue.equals(other.getValue()))) {
                refreshingCourseOptions = true;
                course.setValue(oldValue == null ? i18n.t("app.emptyCourses") : oldValue);
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
                    .filter(course -> course != null
                            && !course.equals(i18n.t("app.emptyCourses")))
                    .toList();

            for (ComboBox<String> selector : courseSelectors) {
                String currentCourse = selector.getValue();
                List<String> options = new ArrayList<>();
                options.add(i18n.t("app.emptyCourses"));
                for (String course : availableCourses) {
                    if (!selectedCourses.contains(course) || course.equals(currentCourse)) {
                        options.add(course);
                    }
                }
                selector.setItems(FXCollections.observableArrayList(options));
                selector.setValue(options.contains(currentCourse)
                        ? currentCourse : i18n.t("app.emptyCourses"));
            }
        } finally {
            refreshingCourseOptions = false;
        }
    }

    private void showUserDialog(com.example.app.Model.Admin user,
                                TableView<com.example.app.Model.Admin> table) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(user == null
                ? i18n.t("admin.addUserTitle") : i18n.t("admin.editUserTitle"));
        dialog.setHeaderText(user == null
                ? i18n.t("admin.createUserHeader")
                : i18n.t("admin.editUserHeader"));
        dialog.getDialogPane().getStyleClass().add("admin-dialog");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/style.css").toExternalForm()
        );
        dialog.getDialogPane().setNodeOrientation(FontSupport.isRtl()
                ? javafx.geometry.NodeOrientation.RIGHT_TO_LEFT
                : javafx.geometry.NodeOrientation.LEFT_TO_RIGHT);
        ButtonType save = new ButtonType(i18n.t("app.save"), ButtonBar.ButtonData.OK_DONE);
        ButtonType delete = new ButtonType(i18n.t("admin.delete"), ButtonBar.ButtonData.OTHER);
        ButtonType cancel = new ButtonType(
                i18n.t("app.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(save, cancel);
        if (user != null) {
            dialog.getDialogPane().getButtonTypes().add(delete);
        }

        TextField name = new TextField(user == null ? "" : user.getName());
        TextField email = new TextField(user == null ? "" : user.getEmail());
        ComboBox<String> role = new ComboBox<>(FXCollections.observableArrayList(
                i18n.t("admin.userRoleStudent"),
                i18n.t("admin.userRoleTeacher"),
                i18n.t("admin.userRoleAdmin")));
        role.setValue(user == null ? i18n.t("admin.userRoleStudent") : roleText(user.getRole()));
        List<ComboBox<String>> courseSelectors = new ArrayList<>();
        VBox courseFields = new VBox(5);
        List<String> existingCourses = user == null || user.getCourses().equals("—")
                ? List.of()
                : List.of(user.getCourses().split(",\\s*"));
        List<String> availableCourses = new ArrayList<>(controller.getCourses());
        if (existingCourses.isEmpty()) {
            addCourseSelector(courseFields, courseSelectors, availableCourses,
                    i18n.t("app.emptyCourses"));
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
            addCourseSelector(courseFields, courseSelectors, availableCourses,
                    i18n.t("app.emptyCourses"));
            refreshCourseOptions(courseSelectors, availableCourses);
        });
        courseFields.getChildren().add(addCourse);
        PasswordField password = new PasswordField();
        password.setPromptText(user == null
                ? i18n.t("admin.passwordPrompt") : i18n.t("profile.optionalPassword"));
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText(i18n.t("admin.passwordConfirmPrompt"));
        GridPane fields = new GridPane();
        fields.getStyleClass().add("admin-dialog-fields");
        fields.setHgap(10);
        fields.setVgap(10);
        fields.addRow(0, new Label(i18n.t("admin.userName")), name);
        fields.addRow(1, new Label(i18n.t("admin.userEmail")), email);
        fields.addRow(2, new Label(i18n.t("admin.userRole")), role);
        fields.addRow(3, new Label(i18n.t("admin.userCourses")), courseFields);
        fields.addRow(4, new Label(i18n.t("login.password")), password);
        fields.addRow(5, new Label(i18n.t("app.confirm")), confirmPassword);
        ScrollPane scrollPane = new ScrollPane(fields);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(360);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setNodeOrientation(FontSupport.isRtl()
                ? javafx.geometry.NodeOrientation.RIGHT_TO_LEFT
                : javafx.geometry.NodeOrientation.LEFT_TO_RIGHT);
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().lookupButton(save).getStyleClass().add("dialog-save-button");
        dialog.getDialogPane().lookupButton(cancel)
                .getStyleClass().add("dialog-cancel-button");

        dialog.setResultConverter(button -> {
            if (button == delete) {
                Alert confirmation = new Alert(
                        Alert.AlertType.CONFIRMATION,
                        i18n.t("admin.userDeletePrompt", user.getName()),
                        new ButtonType(i18n.t("app.confirm"), ButtonBar.ButtonData.YES),
                        new ButtonType(i18n.t("app.cancel"), ButtonBar.ButtonData.NO)
                );
                confirmation.getDialogPane().setNodeOrientation(FontSupport.isRtl()
                        ? javafx.geometry.NodeOrientation.RIGHT_TO_LEFT
                        : javafx.geometry.NodeOrientation.LEFT_TO_RIGHT);
                confirmation.setHeaderText(i18n.t("admin.userDeleteTitle"));
                ButtonType confirmButton = confirmation.getDialogPane().getButtonTypes().get(0);
                if (confirmation.showAndWait().orElse(null) == confirmButton) {
                    try {
                        controller.deleteUser(user);
                        table.refresh();
                    } catch (RuntimeException exception) {
                        Alert alert = new Alert(
                                Alert.AlertType.ERROR,
                                exception.getMessage(),
                                new ButtonType(i18n.t("app.confirm"), ButtonBar.ButtonData.OK_DONE)
                        );
                        alert.getDialogPane().setNodeOrientation(FontSupport.isRtl()
                                ? javafx.geometry.NodeOrientation.RIGHT_TO_LEFT
                                : javafx.geometry.NodeOrientation.LEFT_TO_RIGHT);
                        alert.setHeaderText(i18n.t("admin.deleteUserFailed"));
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
                        .filter(course -> course != null
                                && !course.equals(i18n.t("app.emptyCourses")))
                        .distinct()
                        .reduce((first, second) -> first + ", " + second)
                        .orElse("—");
                String selectedPassword = password.getText();
                if (user == null && selectedPassword.isBlank()) {
                    throw new IllegalArgumentException(i18n.t("admin.passwordRequired"));
                }
                if (!selectedPassword.isBlank()
                        && !selectedPassword.equals(confirmPassword.getText())) {
                    throw new IllegalArgumentException(i18n.t("admin.confirmPasswordMismatch"));
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
                Alert alert = new Alert(
                        Alert.AlertType.ERROR,
                        exception.getMessage(),
                        new ButtonType(i18n.t("app.confirm"), ButtonBar.ButtonData.OK_DONE));
                alert.getDialogPane().setNodeOrientation(FontSupport.isRtl()
                        ? javafx.geometry.NodeOrientation.RIGHT_TO_LEFT
                        : javafx.geometry.NodeOrientation.LEFT_TO_RIGHT);
                alert.setHeaderText(i18n.t("admin.invalidUserData"));
                alert.showAndWait();
            }
            return null;
        });
        dialog.showAndWait();
    }

    private Role roleFromText(String value) {
        if (i18n.t("admin.userRoleAdmin").equals(value)) return Role.ADMIN;
        if (i18n.t("admin.userRoleTeacher").equals(value)) return Role.TEACHER;
        return Role.STUDENT;
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