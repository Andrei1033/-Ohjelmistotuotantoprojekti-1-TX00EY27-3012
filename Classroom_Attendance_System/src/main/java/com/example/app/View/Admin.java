package com.example.app.View;

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
        coursesButton.setPrefWidth(210);

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
        searchField.setPromptText("Hae nimi tai s-postila");
        searchField.setPrefWidth(150);

        ComboBox<String> courseFilter = new ComboBox<>();
        courseFilter.setPromptText("Kaikki kurssit");
        courseFilter.setPrefWidth(120);

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

        nameColumn.setPrefWidth(180);
        emailColumn.setPrefWidth(160);
        roleColumn.setPrefWidth(110);
        courseColumn.setPrefWidth(180);
        editColumn.setPrefWidth(50);

        table.getColumns().addAll(
                nameColumn,
                emailColumn,
                roleColumn,
                courseColumn,
                editColumn
        );

        table.setPlaceholder(new Label("Ei käyttäjiä"));


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