package com.example.app.View;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class Admin {

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
        courseFilter.setPromptText("Kaikki kurssit");
        courseFilter.setPrefWidth(140);

        courseFilter.getItems().addAll(
                "Ohjelmoinnin perusteet",
                "Tietokannat",
                "Java",
                "Web-ohjelmointi"
        );

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

        TableView<String[]> table = new TableView<>();

        TableColumn<String[], String> nameColumn =
                new TableColumn<>("Nimi");

        TableColumn<String[], String> emailColumn =
                new TableColumn<>("Sähköposti");

        TableColumn<String[], String> roleColumn =
                new TableColumn<>("Rooli");

        TableColumn<String[], String> courseColumn =
                new TableColumn<>("Kurssit");

        TableColumn<String[], String> editColumn =
                new TableColumn<>("");


        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue()[0])
        );

        emailColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue()[1])
        );

        roleColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue()[2])
        );

        courseColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue()[3])
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

                    String[] user = getTableView()
                            .getItems()
                            .get(getIndex());

                    System.out.println(
                            "Muokataan käyttäjää: " + user[0]
                    );
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
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


        table.setItems(FXCollections.observableArrayList(

                new String[]{
                        "Maija Meikäläinen",
                        "maija.meikalainen@metropolia.fi",
                        "Opiskelija",
                        "Ohjelmoinnin perusteet, Tietokannat"
                },

                new String[]{
                        "Matti Mallikas",
                        "matti.mallikas@metropolia.fi",
                        "Opiskelija",
                        "Ohjelmoinnin perusteet"
                },

                new String[]{
                        "Laura Opettaja",
                        "laura.opettaja@metropolia.fi",
                        "Opettaja",
                        "Java, Tietokannat"
                },

                new String[]{
                        "Antti Admin",
                        "antti.admin@metropolia.fi",
                        "Admin",
                        "—"
                },

                new String[]{
                        "Ville Virtanen",
                        "ville.virtanen@metropolia.fi",
                        "Opiskelija",
                        "Web-ohjelmointi"
                },

                new String[]{
                        "Sara Salminen",
                        "sara.salminen@metropolia.fi",
                        "Opiskelija",
                        "Java"
                }
        ));

        table.setPlaceholder(
                new Label("Ei käyttäjiä")
        );


        VBox content = new VBox(25);
        content.setPadding(new Insets(30));
        content.getStyleClass().add("content");

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
}