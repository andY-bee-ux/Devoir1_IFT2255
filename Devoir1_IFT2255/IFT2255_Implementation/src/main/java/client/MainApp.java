package client;

import client.controller.ClientController;
import client.service.ApiService;
import javafx.application.Application;
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

        // -------- Top Bar (pleine largeur, responsive) --------
        GridPane topBar = new GridPane();
        topBar.setPadding(new Insets(10, 0, 10, 0));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setPrefHeight(55);

        String[] labels = {
                "Accueil",
                "Rechercher",
                "Programmes",
                "Avis",
                "Comparaison",
                "Résultats Académiques",
                "Horaire"
        };

        Label[] menuItems = new Label[labels.length];

        for (int i = 0; i < labels.length; i++) {
            Label lbl = new Label(labels[i]);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
            lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

            lbl.setOnMouseEntered(e ->
                    lbl.setStyle("-fx-text-fill: #1abc9c; -fx-font-size: 14px; -fx-underline: true;")
            );
            lbl.setOnMouseExited(e ->
                    lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;")
            );

            menuItems[i] = lbl;

            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / labels.length);
            cc.setHgrow(Priority.ALWAYS);
            topBar.getColumnConstraints().add(cc);

            topBar.add(lbl, i, 0);
        }

        // Actions
        menuItems[0].setOnMouseClicked(e -> afficherAccueil());
        menuItems[1].setOnMouseClicked(e -> afficherRecherche());
        menuItems[2].setOnMouseClicked(e -> afficherCoursProgramme());
        menuItems[3].setOnMouseClicked(e -> afficherMessage("Avis non implémentés"));
        menuItems[4].setOnMouseClicked(e -> {
            if (rechercheController == null) rechercheController = new ClientController();
            root.setCenter(rechercheController.getVueComparaison());
        });

        menuItems[5].setOnMouseClicked(e -> afficherResultatsAcademiques());
        menuItems[6].setOnMouseClicked(e -> {
            if (rechercheController == null) rechercheController = new ClientController();

            // --- Étape 1 : récupérer les sigles des cours ---
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Afficher horaire");
            dialog.setHeaderText("Entrez les sigles des cours séparés par des virgules");
            dialog.setContentText("Cours : ");
            dialog.showAndWait().ifPresent(input -> {

                List<String> idsCours = List.of(input.split(",")).stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

                if (idsCours.isEmpty()) {
                    rechercheController.getChampRecherche().setText("Aucun cours saisi.");
                    return;
                }

                // --- Étape 2 : récupérer la session ---
                TextInputDialog sessionDialog = new TextInputDialog("A24");
                sessionDialog.setTitle("Session");
                sessionDialog.setHeaderText("Entrez la session");
                sessionDialog.setContentText("Session : ");
                sessionDialog.showAndWait().ifPresent(session -> {

                    // --- Étape 3 : création d'une fenêtre pour choisir TH/TP ---
                    Stage choixStage = new Stage();
                    choixStage.setTitle("Choisir les sections");

                    VBox layout = new VBox(10);
                    layout.setPadding(new Insets(15));

                    Map<String, ComboBox<String>> thComboBoxMap = new HashMap<>();
                    Map<String, ComboBox<String>> tpComboBoxMap = new HashMap<>();

                    for (String coursId : idsCours) {
                        HBox coursBox = new HBox(10);
                        coursBox.setAlignment(Pos.CENTER_LEFT);

                        Label lblCours = new Label(coursId);
                        lblCours.setPrefWidth(100);

                        ComboBox<String> thCombo = new ComboBox<>();
                        thCombo.getItems().addAll("A", "B", "C"); // À adapter selon les sections réelles
                        thCombo.setValue("A"); // valeur par défaut
                        thComboBoxMap.put(coursId, thCombo);

                        ComboBox<String> tpCombo = new ComboBox<>();
                        tpCombo.getItems().addAll("A101", "A102", "B101"); // À adapter selon TP disponibles
                        tpCombo.setValue("A101"); // valeur par défaut
                        tpComboBoxMap.put(coursId, tpCombo);

                        coursBox.getChildren().addAll(lblCours, new Label("TH:"), thCombo, new Label("TP:"), tpCombo);
                        layout.getChildren().add(coursBox);
                    }

                    Button btnValider = new Button("Valider");
                    btnValider.setOnAction(ev -> {
                        Map<String, Map<String, String>> choix = new HashMap<>();

                        for (String coursId : idsCours) {
                            Map<String, String> sectionsChoisies = new HashMap<>();
                            sectionsChoisies.put("TH", thComboBoxMap.get(coursId).getValue());
                            sectionsChoisies.put("TP", tpComboBoxMap.get(coursId).getValue());
                            choix.put(coursId, sectionsChoisies);
                        }

                        // --- Étape 4 : appel du controller avec les choix ---
                        rechercheController.afficherHorairesEnsemble(idsCours, session, choix);
                        choixStage.close();
                    });

                    layout.getChildren().add(btnValider);
                    Scene scene = new Scene(new ScrollPane(layout), 400, 300);
                    choixStage.setScene(scene);
                    choixStage.show();

                });
            });
        });



        root.setTop(topBar);

        // Contenu principal
        afficherAccueil();

        Scene scene = new Scene(root,1200,800); // pas de largeur/hauteur fixe
        stage.setScene(scene);
        stage.setMinWidth(1800);  // largeur minimale
        stage.setMinHeight(1000);  // hauteur minimale
        stage.setTitle("PickCourse");

// Toujours après avoir fixé la scène
        stage.setMaximized(true);
        stage.setResizable(true);

        stage.show();

    }


    // -------- Méthodes pour changer le centre --------
    private void afficherAccueil() {
        VBox accueil = new VBox(25);
        accueil.setAlignment(Pos.CENTER);
        accueil.setStyle("-fx-padding: 40;");

        // Logo central...
        Image logo = new Image(getClass().getResourceAsStream("/PickCourse-logo.png"));
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(220);
        logoView.setPreserveRatio(true);

        Label welcome = new Label("Bienvenue sur PickCourse");
        welcome.setStyle("""
        -fx-font-size: 28px;
        -fx-font-weight: bold;
        -fx-text-fill: #2c3e50;
    """);

        Label subtitle = new Label(
                "Comparez, explorez et choisissez vos cours en un clic!"
        );
        subtitle.setStyle("""
        -fx-font-size: 16px;
        -fx-text-fill: #555;
    """);
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(600);
        subtitle.setAlignment(Pos.CENTER);

        // ---- Actions rapides ----
        HBox actions = new HBox(20);
        actions.setAlignment(Pos.CENTER);

        Button btnRecherche = new Button("Rechercher un cours");
        Button btnProgramme = new Button("Voir les programmes");

        btnRecherche.setStyle("""
        -fx-background-color: #3498db;
        -fx-text-fill: white;
        -fx-font-size: 14px;
        -fx-padding: 10 18;
    """);

        btnProgramme.setStyle("""
        -fx-background-color: #1abc9c;
        -fx-text-fill: white;
        -fx-font-size: 14px;
        -fx-padding: 10 18;
    """);

        btnRecherche.setOnAction(e -> afficherRecherche());
        btnProgramme.setOnAction(e -> afficherCoursProgramme());

        actions.getChildren().addAll(btnRecherche, btnProgramme);

        accueil.getChildren().addAll(
                logoView,
                welcome,
                subtitle,
                actions
        );

        root.setCenter(accueil);
    }

    /**
     * Cette méthode gère l'affichage de l'interface de Programme.
     */


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
                List<Cours> cours = new ApiService().getCoursesForAProgram(programmeId);
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
            }
        });

        layout.getChildren().addAll(title, champProgramme, btnLancer, scrollPane);
        root.setCenter(layout);
    }

    /**
     * Cette méthode gère l'affichage de l'interface des détails de Cours.
     * @param cours
     */

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
        btnHoraire.setOnAction(e -> new ClientController().afficherHoraire(cours));

        Button btnAvis = new Button("Voir avis");
        btnAvis.setOnAction(e -> new ClientController().afficherAvis(cours));

        HBox boutons = new HBox(10, btnHoraire, btnAvis);
        container.getChildren().addAll(titre, description, credits, prereq, equiv, concom, boutons);

        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
    }

    /**
     * Cette méthode gère l'affichage des résultats de recherche.
     */

    private void afficherRecherche() {
        if (rechercheController == null) {
            rechercheController = new ClientController();
        }

        VBox rechercheLayout = new VBox(10);
        rechercheLayout.setStyle("-fx-padding: 20;");

        Label title = new Label("Recherche de cours");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button btnLancerRecherche = new Button("Rechercher");
        btnLancerRecherche.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnLancerRecherche.setOnAction(e -> rechercheController.rechercher());
        rechercheLayout.getChildren().addAll(
                title,
                new Label("Type de recherche :"),
                rechercheController.getTypeRecherche(), // <-- ComboBox
                rechercheController.getChampRecherche(),
                btnLancerRecherche,
                rechercheController.getListeResultats()
        );

        root.setCenter(rechercheLayout);
    }

    /**
     * Cette méthode permet d'afficher des messages ( exemple messages d'erreur).
     * @param texte contenu du message.
     */
    private void afficherMessage(String texte) {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(new Label(texte));
        root.setCenter(box);
    }

    /**
     * Cette méthode permet de gérer l'affichage de l'interface des résltats académiques.
     */
    private void afficherResultatsAcademiques() {
        if (rechercheController == null) {
            rechercheController = new ClientController();
        }
        root.setCenter(rechercheController.afficherResultatsAcademiques());
    }


    public static void main(String[] args) {
        launch();
    }
}
