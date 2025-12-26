package client;

import client.controller.ClientController;
import client.service.ApiService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.projet.model.Cours;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainApp extends Application {

    private BorderPane root;
    private ClientController rechercheController;

    @Override
    public void start(Stage stage) {
        root = new BorderPane();

        // -------- Top Bar --------
        GridPane topBar = new GridPane();
        topBar.setPadding(new Insets(10, 0, 10, 0));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setPrefHeight(55);

        String[] labels = {
                "Accueil", "Rechercher", "Programmes", "Avis",
                "Comparaison", "Résultats Académiques", "Horaire"
        };

        Label[] menuItems = new Label[labels.length];
        for (int i = 0; i < labels.length; i++) {
            Label lbl = new Label(labels[i]);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
            lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            final int idx = i;

            lbl.setOnMouseEntered(e -> lbl.setStyle(
                    "-fx-text-fill: #1abc9c; -fx-font-size: 14px; -fx-underline: true;"));
            lbl.setOnMouseExited(e -> lbl.setStyle(
                    "-fx-text-fill: white; -fx-font-size: 14px;"));

            menuItems[i] = lbl;
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / labels.length);
            cc.setHgrow(Priority.ALWAYS);
            topBar.getColumnConstraints().add(cc);
            topBar.add(lbl, i, 0);
        }

        // -------- Actions --------
        menuItems[0].setOnMouseClicked(e -> afficherAccueil());
        menuItems[1].setOnMouseClicked(e -> afficherRecherche());
        menuItems[2].setOnMouseClicked(e -> afficherCoursProgramme());
         menuItems[3].setOnMouseClicked(e -> {
            if (rechercheController == null) rechercheController = new ClientController();
            rechercheController.afficherAllAvis(); // ouvre la fenêtre séparée
        });

        menuItems[4].setOnMouseClicked(e -> {
            if (rechercheController == null) rechercheController = new ClientController();
            root.setCenter(rechercheController.getVueComparaison());
        });
        menuItems[5].setOnMouseClicked(e -> {
            if (rechercheController == null) rechercheController = new ClientController();
            root.setCenter(rechercheController.afficherResultatsAcademiques());
        });
        menuItems[6].setOnMouseClicked(e -> afficherHoraireDialog());

        root.setTop(topBar);

        // Contenu principal
        afficherAccueil();

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setMinWidth(1800);
        stage.setMinHeight(1000);
        stage.setTitle("PickCourse");
        stage.setMaximized(true);
        stage.setResizable(true);
        stage.show();
    }

    private void afficherAccueil() {
        VBox accueil = new VBox(25);
        accueil.setAlignment(Pos.CENTER);
        accueil.setStyle("-fx-padding: 40;");

        Image logo = new Image(getClass().getResourceAsStream("/PickCourse-logo.png"));
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(220);
        logoView.setPreserveRatio(true);

        Label welcome = new Label("Bienvenue sur PickCourse");
        welcome.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label subtitle = new Label("Comparez, explorez et choisissez vos cours en un clic!");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(600);
        subtitle.setAlignment(Pos.CENTER);

        HBox actions = new HBox(20);
        actions.setAlignment(Pos.CENTER);

        Button btnRecherche = new Button("Rechercher un cours");
        btnRecherche.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 18;");
        btnRecherche.setOnAction(e -> afficherRecherche());

        Button btnProgramme = new Button("Voir les programmes");
        btnProgramme.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 18;");
        btnProgramme.setOnAction(e -> afficherCoursProgramme());

        actions.getChildren().addAll(btnRecherche, btnProgramme);
        accueil.getChildren().addAll(logoView, welcome, subtitle, actions);

        root.setCenter(accueil);
    }

    private void afficherCoursProgramme() {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        Label title = new Label("Voir les cours d'un programme");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField champProgramme = new TextField();
        champProgramme.setPromptText("Entrez l'ID/le nom du programme");

        ScrollPane scrollPane = new ScrollPane();
        VBox listeCoursBox = new VBox(5);
        scrollPane.setContent(listeCoursBox);
        scrollPane.setFitToWidth(true);

        Button btnLancer = new Button("Afficher les cours");
        btnLancer.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        btnLancer.setOnAction(e -> {
            String programmeId = champProgramme.getText().trim();
            if (!programmeId.isEmpty()) {
                // --- Thread séparé pour éviter de bloquer l'UI ---
                new Thread(() -> {
                    List<Cours> cours = new ApiService().getCoursesForAProgram(programmeId);
                    Platform.runLater(() -> {
                        listeCoursBox.getChildren().clear();
                        if (cours.isEmpty()) {
                            listeCoursBox.getChildren().add(new Label("Aucun cours trouvé pour ce programme."));
                        } else {
                            for (Cours c : cours) {
                                Label lblCours = new Label(c.getId() + " - " + c.getName());
                                lblCours.setStyle("-fx-font-size: 14px; -fx-text-fill: #3498db; -fx-cursor: hand;");
                                lblCours.setOnMouseEntered(ev -> lblCours.setStyle("-fx-font-size: 14px; -fx-text-fill: #1abc9c; -fx-underline: true; -fx-cursor: hand;"));
                                lblCours.setOnMouseExited(ev -> lblCours.setStyle("-fx-font-size: 14px; -fx-text-fill: #3498db; -fx-cursor: hand;"));
                                lblCours.setOnMouseClicked(ev -> afficherCoursDetail(c));
                                listeCoursBox.getChildren().add(lblCours);
                            }
                        }
                    });
                }).start();
            }
        });

        layout.getChildren().addAll(title, champProgramme, btnLancer, scrollPane);
        root.setCenter(layout);
    }

    private void afficherCoursDetail(Cours cours) {
        VBox container = new VBox(5);
        container.setStyle("-fx-padding: 10; -fx-border-color: gray; -fx-border-radius: 5; -fx-background-radius: 5; -fx-background-color: #f4f4f4;");

        Label titre = new Label(cours.getId() + " - " + cours.getName());
        titre.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        Label description = new Label(cours.getDescription());
        description.setWrapText(true);

        Label credits = new Label("Crédits: " + cours.getCredits());
        Label prereq = new Label("Prérequis: " + (cours.getPrerequisite_courses() != null ? String.join(", ", cours.getPrerequisite_courses()) : "Aucun"));
        Label equiv = new Label("Équivalents: " + (cours.getEquivalent_courses() != null ? String.join(", ", cours.getEquivalent_courses()) : "Aucun"));
        Label concom = new Label("Concomitants: " + (cours.getConcomitant_courses() != null ? String.join(", ", cours.getConcomitant_courses()) : "Aucun"));

        Button btnHoraire = new Button("Voir horaire");
        btnHoraire.setOnAction(e -> {
            if (rechercheController == null) rechercheController = new ClientController();
            rechercheController.afficherHoraire(cours);
        });

        Button btnAvis = new Button("Voir avis");
        btnAvis.setOnAction(e -> {
            if (rechercheController == null) rechercheController = new ClientController();
            rechercheController.afficherAvis(cours);
        });

        HBox boutons = new HBox(10, btnHoraire, btnAvis);
        container.getChildren().addAll(titre, description, credits, prereq, equiv, concom, boutons);

        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
    }

    private void afficherRecherche() {
        if (rechercheController == null) rechercheController = new ClientController();

        VBox rechercheLayout = new VBox(10);
        rechercheLayout.setStyle("-fx-padding: 20;");

        Label title = new Label("Recherche de cours");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button btnLancerRecherche = new Button("Rechercher");
        btnLancerRecherche.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

        btnLancerRecherche.setOnAction(e -> {
            //Lancer la recherche dans un thread séparé pour éviter que ça ne plante
            new Thread(() -> rechercheController.rechercher()).start();

        });

        rechercheLayout.getChildren().addAll(
                title,
                new Label("Type de recherche :"),
                rechercheController.getTypeRecherche(),
                new Label("Texte de recherche :"),
                rechercheController.getChampRecherche(),
                new Label("Session (optionnel) :"),
                rechercheController.getChampSession(), // nouveau champ
                btnLancerRecherche,
                rechercheController.getMessageLabel(),
                rechercheController.getListeResultats()
        );


        root.setCenter(rechercheLayout);
    }

    private void afficherMessage(String texte) {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(new Label(texte));
        root.setCenter(box);
    }

    private void afficherHoraireDialog() {
        if (rechercheController == null) rechercheController = new ClientController();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Afficher horaire");
        dialog.setHeaderText("Entrez les sigles des cours séparés par des virgules");
        dialog.setContentText("Cours : ");
        dialog.showAndWait().ifPresent(input -> {
            List<String> idsCours = List.of(input.split(",")).stream().map(String::trim).filter(s -> !s.isEmpty()).toList();
            if (idsCours.isEmpty()) {
                rechercheController.getChampRecherche().setText("Aucun cours saisi.");
                return;
            }

            TextInputDialog sessionDialog = new TextInputDialog("A24");
            sessionDialog.setTitle("Session");
            sessionDialog.setHeaderText("Entrez la session");
            sessionDialog.setContentText("Session : ");
            sessionDialog.showAndWait().ifPresent(session -> {
                // Création fenêtre choix TH/TP
                Stage choixStage = new Stage();
                choixStage.setTitle("Choisir les sections");
                VBox layout = new VBox(10);
                layout.setPadding(new Insets(15));

                Map<String, ComboBox<String>> thMap = new HashMap<>();
                Map<String, ComboBox<String>> tpMap = new HashMap<>();

                for (String coursId : idsCours) {
                    HBox coursBox = new HBox(10);
                    coursBox.setAlignment(Pos.CENTER_LEFT);

                    Label lblCours = new Label(coursId);
                    lblCours.setPrefWidth(100);

                    ComboBox<String> thCombo = new ComboBox<>();
                    thCombo.getItems().addAll("A", "B", "C");
                    thCombo.setValue("A");
                    thMap.put(coursId, thCombo);

                    ComboBox<String> tpCombo = new ComboBox<>();
                    tpCombo.getItems().addAll("A101", "A102", "B101");
                    tpCombo.setValue("A101");
                    tpMap.put(coursId, tpCombo);

                    coursBox.getChildren().addAll(lblCours, new Label("TH:"), thCombo, new Label("TP:"), tpCombo);
                    layout.getChildren().add(coursBox);
                }

                Button btnValider = new Button("Valider");
                btnValider.setOnAction(ev -> {
                    Map<String, Map<String, String>> choix = new HashMap<>();
                    for (String coursId : idsCours) {
                        Map<String, String> sections = new HashMap<>();
                        sections.put("TH", thMap.get(coursId).getValue());
                        sections.put("TP", tpMap.get(coursId).getValue());
                        choix.put(coursId, sections);
                    }

                    rechercheController.afficherHorairesEnsemble(idsCours, session, choix);
                    choixStage.close();
                });

                layout.getChildren().add(btnValider);
                Scene scene = new Scene(new ScrollPane(layout), 400, 300);
                choixStage.setScene(scene);
                choixStage.show();
            });
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
