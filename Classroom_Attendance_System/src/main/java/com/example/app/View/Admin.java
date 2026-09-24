package com.example.app.View;

import com.example.app.Controller.AdminController;
import com.example.app.Model.LoginComponents.Role;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class Admin {
    private final AdminController controller;

    public Admin() {
        this(new AdminController());
    }

    public Admin(AdminController controller) {
        this.controller = controller;
    }

    public BorderPane getView() {

        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(15));
        sidebar.setPrefWidth(240);
        sidebar.getStyleClass().add("sidebar");

        Label logo = new Label("LO");
        logo.getStyleClass().add("logo");

        Label appName = new Label("Läsnäolo");
        appName.getStyleClass().add("app-name");

        HBox header = new HBox(8, logo, appName);
        header.setAlignment(Pos.CENTER_LEFT);

        Button coursesButton = new Button("•  Kurssini");
        coursesButton.getStyleClass().add("sidebar-button");
        coursesButton.setMaxWidth(Double.MAX_VALUE);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label userName = new Label("Etunimi Sukunimi");
        Label userRole = new Label("Admin");

        VBox userInfo = new VBox(2, userName, userRole);
        userInfo.getStyleClass().add("user-info");

        sidebar.getChildren().addAll(
                header,
                coursesButton,
                spacer,
                userInfo
        );


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
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);

        TextField name = new TextField(user == null ? "" : user.getName());
        TextField email = new TextField(user == null ? "" : user.getEmail());
        ComboBox<String> role = new ComboBox<>(FXCollections.observableArrayList("Opiskelija", "Opettaja", "Admin"));
        role.setValue(user == null ? "Opiskelija" : roleText(user.getRole()));
        TextField courses = new TextField(user == null ? "" : user.getCourses());
        GridPane fields = new GridPane();
        fields.getStyleClass().add("admin-dialog-fields");
        fields.setHgap(10);
        fields.setVgap(10);
        fields.addRow(0, new Label("Nimi"), name);
        fields.addRow(1, new Label("Sähköposti"), email);
        fields.addRow(2, new Label("Rooli"), role);
        fields.addRow(3, new Label("Kurssit"), courses);
        dialog.getDialogPane().setContent(fields);
        dialog.getDialogPane().lookupButton(save).getStyleClass().add("dialog-save-button");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL)
                .getStyleClass().add("dialog-cancel-button");

        dialog.setResultConverter(button -> {
            if (button != save) return null;
            try {
                Role selectedRole = roleFromText(role.getValue());
                String selectedCourses = courses.getText().isBlank() ? "—" : courses.getText();
                if (user == null) {
                    controller.addUser(name.getText(), email.getText(), selectedRole, selectedCourses);
                } else {
                    controller.updateUser(user, name.getText(), email.getText(), selectedRole, selectedCourses);
                }
                table.refresh();
            } catch (IllegalArgumentException exception) {
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
}