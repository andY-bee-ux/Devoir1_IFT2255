package client.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import org.projet.model.*;
import client.service.ApiService;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Cette classe permet de gérer les interactions avec les utilisateurs sur la plateforme PickCourse.
 */

public class ClientController {

    // Composants
    private TextField champRecherche;
    private ListView<Cours> listeResultats;
    private ComboBox<String> typeRecherche; // "ID", "Nom" ou "Mot-clé"
    private Label messageLabel; // au niveau de la classe
    private VBox comparaisonBox; // pour la vue comparaison
    private ListView<String> listeCoursComparaison; // liste des cours à comparer
    private VBox criteresBox; // checkboxes critères
    private TextField sessionField;
    private Label messageResultatAcademique = new Label();

    private TableView<ObservableList<String>> tableComparaison;
    private final ApiService coursService = new ApiService();
    private final String[] CRITERES = {
            "id", "name", "description", "credits", "scheduledSemester",
            "prerequisite_courses", "equivalent_courses", "concomitant_courses",
            "mode", "available_terms", "available_periods", "udemWebsite",
            "popularité officielle", "difficulté officielle", "difficulté inofficielle", "charge de travail inofficielle"
    };


    /**
     * Cette méthode permet d'initialiser le ClientController() avec l'interface de recherche.
     */
    public ClientController() {
        champRecherche = new TextField();
        listeResultats = new ListView<>();
        typeRecherche = new ComboBox<>();
        typeRecherche.getItems().addAll("ID", "Nom", "Mot-clé");
        typeRecherche.setValue("Nom"); // valeur par défaut
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-style: italic;");

        initialize();
    }
    public ComboBox<String> getTypeRecherche() {
        return typeRecherche;
    }

    /**
     * Cette méthode permet d'initialiser le controller avec les résultats de la recherche de cours.
     */

    private void initialize() {
        listeResultats.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cours cours, boolean empty) {
                super.updateItem(cours, empty);
                if (empty || cours == null) {
                    setText(null);
                    setGraphic(null);
                } else {
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

                    // Boutons
                    Button btnHoraire = new Button("Voir horaire");
                    btnHoraire.setOnAction(e -> afficherHoraire(cours));

                    Button btnAvis = new Button("Voir avis");
                    btnAvis.setOnAction(e -> afficherAvis(cours));
                    Button btnEligibilite = new Button("Vérifier l'éligibilité");
                    btnEligibilite.setOnAction(e -> afficherEligibilite(cours));

                    HBox boutons = new HBox(10, btnHoraire, btnAvis, btnEligibilite);
                    container.getChildren().addAll(titre, description, credits, prereq, equiv, concom, boutons);
                    setGraphic(container);




                }
            }
        });
    }

    /**
     * Cette méthode permet de gérer l'action de recherche effectuée par l'utilisater.
     */
    public void rechercher() {
        String texte = champRecherche.getText().trim();
        listeResultats.getItems().clear();
        messageLabel.setText(""); // reset message

        if (texte.isEmpty()) {
            messageLabel.setText("Veuillez entrer un texte de recherche.");
            return;
        }

        String param;
        // La recherche vers notre API prend un paramètre et on a donc besoin que l'utilisateur
        // spécifie ce dernier.
        switch (typeRecherche.getValue()) {
            case "ID": param = "id"; break;
            case "Mot-clé": param = "description"; break;
            default: param = "name"; break;
        }

        List<Cours> resultats = coursService.rechercherCours(param, texte, "true", "A24");

        if (resultats.isEmpty()) {
            messageLabel.setText("Aucun cours trouvé pour : " + texte);
        } else {
            listeResultats.getItems().addAll(resultats);
        }
    }


    /**
     * Cette méthode permet d'afficher l'horaire d'un cours donné.
     * @param cours cours dont on veut afficher l'horaire.
     */

    public void afficherHoraire(Cours cours) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Horaire pour " + cours.getId() + " - " + cours.getName());

        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 10;");

        if (cours.getSchedules() == null || cours.getSchedules().isEmpty()) {
            root.getChildren().add(new Label("Aucun horaire disponible."));
        } else {
            // Récupérer les semestres disponibles
            List<String> semestres = cours.getSchedules().stream()
                    .map(Cours.Schedule::getSemester)
                    .toList();

            Label labelChoix = new Label("Sélectionnez un semestre:");
            ComboBox<String> comboSemestre = new ComboBox<>();
            comboSemestre.getItems().addAll(semestres);
            comboSemestre.setValue(semestres.get(0)); // valeur par défaut

            VBox horairesBox = new VBox(5); // contiendra les horaires filtrés
            Label messageErreur = new Label();
            messageErreur.setStyle("-fx-text-fill: red; -fx-font-style: italic;");

            Button btnAfficher = new Button("Afficher l'horaire");
            btnAfficher.setOnAction(e -> {
                horairesBox.getChildren().clear();
                messageErreur.setText(""); // reset message
                String semestreChoisi = comboSemestre.getValue();

                if (!semestres.contains(semestreChoisi)) {
                    messageErreur.setText("⚠️ Le semestre sélectionné n'existe pas pour ce cours.");
                    return;
                }

                // filtrer le bon semestre
                cours.getSchedules().stream()
                        .filter(s -> s.getSemester().equals(semestreChoisi))
                        .forEach(sched -> {
                            VBox schedBox = new VBox(5);
                            schedBox.setStyle("-fx-border-color: gray; -fx-border-radius: 5; -fx-padding: 5; -fx-background-color: #f9f9f9;");
                            Label semesterLabel = new Label("Semestre: " + sched.getSemester());
                            semesterLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
                            schedBox.getChildren().add(semesterLabel);

                            if (sched.getSections() != null) {
                                for (Cours.Section sec : sched.getSections()) {
                                    VBox secBox = new VBox(3);
                                    secBox.setStyle("-fx-padding: 3; -fx-border-color: lightgray; -fx-border-radius: 3;");
                                    Label secLabel = new Label("Section: " + sec.getName());
                                    secLabel.setStyle("-fx-font-weight: bold;");
                                    secBox.getChildren().add(secLabel);

                                    if (sec.getVolets() != null) {
                                        for (Cours.Volet volet : sec.getVolets()) {
                                            VBox voletBox = new VBox(2);
                                            Label voletLabel = new Label("Volet: " + volet.getName());
                                            voletBox.getChildren().add(voletLabel);

                                            if (volet.getActivities() != null) {
                                                for (Cours.Activity act : volet.getActivities()) {
                                                    String info = String.join("/", act.getDays()) + " "
                                                            + act.getStart_time() + "–" + act.getEnd_time()
                                                            + " (" + act.getMode() + ")";
                                                    voletBox.getChildren().add(new Label(info));
                                                }
                                            }
                                            secBox.getChildren().add(voletBox);
                                        }
                                    }
                                    schedBox.getChildren().add(secBox);
                                }
                            }
                            horairesBox.getChildren().add(schedBox);
                        });
            });

            root.getChildren().addAll(labelChoix, comboSemestre, btnAfficher, messageErreur, horairesBox);
        }

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        Scene scene = new Scene(scroll, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Cette méthode permet d'afficher les avis relatifs à un cours.
     * @param cours cours dont on veut afficher les avis.
     */
    public void afficherAvis(Cours cours) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Avis pour " + cours.getId() + " - " + cours.getName());

        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 10;");

        List<Avis> avisList = coursService.getAvisCours(cours.getId());

        if (avisList.isEmpty()) {
            root.getChildren().add(new Label("Aucun avis disponible pour ce cours."));
        } else {
            for (Avis avis : avisList) {
                VBox avisBox = new VBox(5);
                avisBox.setStyle("-fx-border-color: gray; -fx-padding: 5; -fx-background-color: #f9f9f9;");

                Label auteur = new Label("Professeur : " + avis.getNomProfesseur());
                auteur.setStyle("-fx-font-weight: bold;");

                Label texte = new Label(avis.getCommentaire());
                texte.setWrapText(true);

                Label note = new Label("Note Difficulté : " + avis.getNoteDifficulte());
                Label note2 = new Label("Note Charge de Travail : " + avis.getNoteChargeTravail());

                avisBox.getChildren().addAll(auteur, texte, note,note2);
                root.getChildren().add(avisBox);
            }
        }

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        Scene scene = new Scene(scroll, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Cette méthode permet de traiter la vérification d'éligibilité à un cours.
     * @param cours cours auquel on veut vérifier l'éligibilité.
     */
    private void afficherEligibilite(Cours cours) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Vérifier votre éligibilité à " + cours.getId());

        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 10;");

        Label instruction = new Label("Vérifiez si vous êtes éligible à ce cours : " + cours.getId());
        instruction.setStyle("-fx-font-weight: bold;");

        TextField champCycle = new TextField();
        champCycle.setPromptText("Entrez votre cycle (1-4)");

        TextField champCoursFaits = new TextField();
        champCoursFaits.setPromptText("Ajoutez les cours déjà complétés (séparés par des virgules)");

        Button btnVerifier = new Button("Vérifier l'éligibilité");
        Label resultat = new Label();

        btnVerifier.setOnAction(e -> {
            String cycleStr = champCycle.getText().trim();
            String[] coursFaitsArray = champCoursFaits.getText().split(",");
            List<String> coursFaits = List.of(coursFaitsArray).stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            try {
                int cycle = Integer.parseInt(cycleStr);
                String message = new ApiService().checkEligibility(cours.getId(), coursFaits, cycle);
                resultat.setText(message);
            } catch (NumberFormatException ex) {
                resultat.setText("Le cycle doit être un nombre entre 1 et 4");
            }
        });

        root.getChildren().addAll(instruction, champCycle, champCoursFaits, btnVerifier, resultat);

        Scene scene = new Scene(root, 500, 250);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Cette méthode permet de gérer la logique derrière la requête pour voir les résultats académiques.
     * @return
     */
    public VBox afficherResultatsAcademiques() {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20;");

        Label title = new Label("Résultats académiques");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField champSigle = new TextField();
        champSigle.setPromptText("Entrez le sigle du cours (ex: IFT2255)");

        Button btnVoir = new Button("Voir les résultats");
        btnVoir.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        TextArea zoneResultats = new TextArea();
        zoneResultats.setEditable(false);
        zoneResultats.setWrapText(true);
        zoneResultats.setPrefHeight(300);

        btnVoir.setOnAction(e -> {
            String sigle = champSigle.getText().trim();
            if (sigle.isEmpty()) {
                zoneResultats.setText("Veuillez entrer un sigle de cours.");
                return;
            }

            String resultat = coursService.afficherResultatAcademiques(sigle);
            zoneResultats.setText(resultat);
        });

        root.getChildren().addAll(title, champSigle, btnVoir, zoneResultats);
        return root;
    }

    public VBox getVueComparaison() {
        if (comparaisonBox != null) return comparaisonBox;
       // pour éviter que ça shrink
        VBox innerBox = new VBox(10);
        innerBox.setStyle("-fx-padding: 20;");

        ScrollPane scrollPane = new ScrollPane(innerBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        comparaisonBox = new VBox();
        comparaisonBox.getChildren().add(scrollPane);


        Label title = new Label("Comparaison de cours");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // ------------------- Mode comparaison -------------------
        ToggleGroup modeGroup = new ToggleGroup();
        RadioButton rbDeuxCours = new RadioButton("Comparer 2 cours");
        rbDeuxCours.setToggleGroup(modeGroup);
        rbDeuxCours.setSelected(true);

        RadioButton rbEnsemblesCours = new RadioButton("Comparer des ensembles de cours");
        rbEnsemblesCours.setToggleGroup(modeGroup);

        HBox modeBox = new HBox(10, rbDeuxCours, rbEnsemblesCours);

        // ------------------- Liste de cours -------------------
        listeCoursComparaison = new ListView<>();
        listeCoursComparaison.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listeCoursComparaison.setPrefHeight(120);
        listeCoursComparaison.setEditable(true);

        TextField champAjouterCours = new TextField();
        champAjouterCours.setPromptText("Ajouter sigle de cours");

        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setOnAction(e -> {
            String sigle = champAjouterCours.getText().trim();
            if (!sigle.isEmpty() && !listeCoursComparaison.getItems().contains(sigle)) {
                listeCoursComparaison.getItems().add(sigle);
                champAjouterCours.clear();
            }
        });

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.setDisable(true);
        btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        listeCoursComparaison.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    btnSupprimer.setDisable(
                            listeCoursComparaison.getSelectionModel().getSelectedItems().isEmpty()
                    );
                });

        btnSupprimer.setOnAction(e -> {
            ObservableList<String> selected = FXCollections.observableArrayList(
                    listeCoursComparaison.getSelectionModel().getSelectedItems()
            );
            if (!selected.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Supprimer des cours");
                alert.setHeaderText(null);
                alert.setContentText(
                        selected.size() == 1
                                ? "Supprimer le cours " + selected.get(0) + " ?"
                                : "Supprimer les " + selected.size() + " cours sélectionnés ?"
                );
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        listeCoursComparaison.getItems().removeAll(selected);
                    }
                });
            }
        });

        HBox ajoutCoursBox = new HBox(10, champAjouterCours, btnAjouter, btnSupprimer);

        Label infoSuppression = new Label("Sélectionnez un cours et cliquez sur Supprimer pour l’enlever.");
        infoSuppression.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");

        criteresBox = new VBox(5);
        for (String crit : CRITERES) {
            CheckBox cb = new CheckBox(crit);
            criteresBox.getChildren().add(cb);
        }
        ScrollPane scrollCriteres = new ScrollPane(criteresBox);
        scrollCriteres.setPrefHeight(150);

        sessionField = new TextField();
        sessionField.setPromptText("Session (optionnel)");

        Button btnComparer = new Button("Comparer");
        btnComparer.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        btnComparer.setOnAction(e -> {
            if (rbDeuxCours.isSelected()) {
                lancerComparaison();
            } else {
                lancerComparaisonCombinaisons();
            }
        });

        // table qui contiendra le résultat
        tableComparaison = new TableView<>();
        tableComparaison.setPrefHeight(600);  // plus haute
        tableComparaison.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); // permet de ne pas shrinker les colonnes ( normalement)


        messageResultatAcademique.setWrapText(true);
        messageResultatAcademique.setStyle(
                "-fx-padding: 8;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-font-style: italic;"
        );

        innerBox.getChildren().addAll(
                title,
                modeBox,
                ajoutCoursBox,
                infoSuppression,
                listeCoursComparaison,
                new Label("Critères à comparer:"),
                scrollCriteres,
                new Label("Session:"),
                sessionField,
                btnComparer,
                tableComparaison,
                messageResultatAcademique
        );

        return comparaisonBox;
    }

    /**
     * Cette méthode permet de gérer la comparaison des ensembles de cours.
     */

    private void lancerComparaisonCombinaisons() {

        List<List<String>> ensemblesCours = obtenirEnsemblesSelectionnes(); // méthode pour créer listes
        if (ensemblesCours.isEmpty()) {
            messageLabel.setText("Veuillez sélectionner au moins un ensemble de cours.");
            return;
        }

        String session = sessionField.getText().trim().isEmpty() ? null : sessionField.getText().trim();

        List<List<String>> resultat;
        try {
            resultat = coursService.comparerCombinaisonCoursApi(ensemblesCours, session);
        } catch (Exception e) {
            messageLabel.setText("Erreur lors de la comparaison des ensembles : " + e.getMessage());
            return;
        }

        if (resultat == null || resultat.isEmpty()) {
            messageLabel.setText("Aucun résultat de comparaison pour ces ensembles.");
            return;
        }

        afficherTableResultats(resultat);
    }

    /**
     * Cette méthode permet de convertir les listes en tableviews.
     * @param resultat liste de cours
     */
    private void afficherTableResultats(List<List<String>> resultat) {
        tableComparaison.getColumns().clear();
        tableComparaison.getItems().clear();

        if (resultat.isEmpty()) return;

        int colonnes = resultat.get(0).size();

        for (int i = 0; i < colonnes; i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>("Col " + (i+1));
            column.setCellValueFactory(data -> {
                ObservableList<String> row = data.getValue();
                return new SimpleStringProperty(idx < row.size() ? row.get(idx) : "");
            });
            tableComparaison.getColumns().add(column);
        }

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (List<String> row : resultat) {
            data.add(FXCollections.observableArrayList(row));
        }
        tableComparaison.setItems(data);
    }

    // ------------------- Construire ensembles -------------------
    private List<List<String>> obtenirEnsemblesSelectionnes() {
        List<List<String>> ensembles = new ArrayList<>();
        for (String sigle : listeCoursComparaison.getItems()) {
            ensembles.add(List.of(sigle)); // chaque cours devient un ensemble d’un seul cours
        }
        return ensembles;
    }

    private void lancerComparaison() {

        List<String> cours = listeCoursComparaison.getItems();

        List<String> criteresSelectionnes = criteresBox.getChildren().stream()
                .filter(node -> node instanceof CheckBox cb && cb.isSelected())
                .map(node -> ((CheckBox) node).getText())
                .toList();

        // Reset messages
        messageLabel.setText("");
        messageResultatAcademique.setText("");

        if (cours.isEmpty() || criteresSelectionnes.isEmpty()) {
            messageLabel.setText("Veuillez sélectionner au moins un cours et un critère.");
            return;
        }

        // critères officiels
        if (cours.size() == 2) { // comparerCoursParResultats ne gère que 2 cours
            try {
                String json = coursService.comparerCoursParResultats(cours.get(0), cours.get(1));
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(json);

                StringBuilder resultText = new StringBuilder();

                if (criteresSelectionnes.contains("popularité officielle")) {
                    String popularite = root.has("popularite")
                            ? root.get("popularite").asText()
                            : "Information non disponible";
                    resultText.append("Popularité officielle : ").append(popularite).append("\n");
                }

                if (criteresSelectionnes.contains("difficulté officielle")) {
                    String difficulte = root.has("difficulte")
                            ? root.get("difficulte").asText()
                            : "Information non disponible";
                    resultText.append("Difficulté officielle : ").append(difficulte).append("\n");
                }

                messageResultatAcademique.setText(resultText.toString());

            } catch (Exception e) {
                messageResultatAcademique.setText("Impossible d’obtenir la comparaison officielle.");
            }
        } else if (criteresSelectionnes.contains("popularité officielle") || criteresSelectionnes.contains("difficulté officielle")) {
            messageResultatAcademique.setText("Les critères officiels nécessitent exactement deux cours.");
        }

       // critères non officiels ( basés sur les avis)
        boolean difficulteInoff = criteresSelectionnes.contains("difficulté inofficielle");
        boolean chargeInoff = criteresSelectionnes.contains("charge de travail inofficielle");

        if (difficulteInoff || chargeInoff) {
            String[] ids = cours.toArray(new String[0]);

            if (difficulteInoff) {
                List<List<String>> diffAvis = coursService.getDifficulteAvis(ids);
                StringBuilder sb = new StringBuilder(" Difficulté inofficielle moyenne :\n");
                for (int i = 0; i < diffAvis.size(); i++) {
                    sb
                            //append(ids[i]).append(" : ")
                            .append(String.join(": ", diffAvis.get(i)))
                            .append("\n");
                }
                messageResultatAcademique.setText(messageResultatAcademique.getText() + sb.toString());
            }

            if (chargeInoff) {
                List<List<String>> chargeAvis = coursService.getChargeDeTravailAvis(ids);
                StringBuilder sb = new StringBuilder(" Charge de travail inofficielle moyenne :\n");
                for (int i = 0; i < chargeAvis.size(); i++) {
                    sb.append(ids[i]).append(" : ")
                            .append(String.join(", ", chargeAvis.get(i)))
                            .append("\n");
                }
                messageResultatAcademique.setText(messageResultatAcademique.getText() + sb.toString());
            }
        }

        // critères basés sur le "catalogue"
        List<String> criteresTable = criteresSelectionnes.stream()
                .filter(c -> !c.equals("popularité officielle")
                        && !c.equals("difficulté officielle")
                        && !c.equals("difficulté inofficielle")
                        && !c.equals("charge de travail inofficielle"))
                .toList();

        if (criteresTable.isEmpty()) {
            tableComparaison.getItems().clear();
            tableComparaison.getColumns().clear();
            return;
        }

        List<List<String>> resultat = coursService.comparerCours(
                cours.toArray(new String[0]),
                criteresTable.toArray(new String[0]),
                sessionField.getText().trim().isEmpty() ? null : sessionField.getText().trim()
        );

        if (resultat == null || resultat.isEmpty()) {
            messageLabel.setText("Aucun résultat de comparaison.");
            return;
        }

        tableComparaison.getColumns().clear();
        for (int i = 0; i < criteresTable.size(); i++) {
            final int idx = i;
            TableColumn<ObservableList<String>, String> column =
                    new TableColumn<>(criteresTable.get(i));
            column.setCellValueFactory(data -> {
                ObservableList<String> row = data.getValue();
                return new SimpleStringProperty(idx < row.size() ? row.get(idx) : "");
            });
            tableComparaison.getColumns().add(column);
        }

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (List<String> row : resultat) {
            data.add(FXCollections.observableArrayList(row.stream().limit(criteresTable.size()).toList()));
        }
        tableComparaison.setItems(data);
    }


    /**
     * Cette méthode permet d'afficher les horaires d'un ensemble de cours.
     * @param idsCours
     * @param session
     * @param choix
     */


    public void afficherHorairesEnsemble(List<String> idsCours, String session, Map<String, Map<String, String>> choix) {
        if (idsCours == null || idsCours.isEmpty()) {
            messageLabel.setText("Veuillez sélectionner au moins un cours.");
            return;
        }
        if (session == null || session.isBlank()) {
            messageLabel.setText("Veuillez entrer une session.");
            return;
        }

        if (idsCours.size() > 6) {
            messageLabel.setText("La limite maximale de cours a été atteinte.");
            return;
        }
        // Appel au service API
        ApiService.ResultatHoraire resultat;
        try {
            resultat = coursService.genererHoraire(idsCours, session, true, choix);
        } catch (Exception e) {
            messageLabel.setText("Erreur lors de la récupération des horaires : " + e.getMessage());
            return;
        }

        if (resultat == null || resultat.horaire == null || resultat.horaire.isEmpty()) {
            messageLabel.setText("Aucun horaire disponible pour les cours sélectionnés.");
            return;
        }

        // Création de la fenêtre
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Horaires des cours sélectionnés");

        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 10;");

        // Affichage des horaires
        for (String coursId : resultat.horaire.keySet()) {
            VBox coursBox = new VBox(5);
            coursBox.setStyle("-fx-border-color: gray; -fx-padding: 5; -fx-background-color: #f9f9f9;");
            coursBox.getChildren().add(new Label("Cours : " + coursId));

            List<List<String>> blocs = resultat.horaire.get(coursId);
            if (blocs == null || blocs.isEmpty()) {
                coursBox.getChildren().add(new Label("Aucun horaire disponible."));
            } else {
                for (List<String> bloc : blocs) {
                    String jours = bloc.get(0);
                    String heures = bloc.get(1);
                    coursBox.getChildren().add(new Label(jours + " " + heures));
                }
            }

            root.getChildren().add(coursBox);
        }

        // Affichage des conflits ( s'il y en a)
        if (resultat.conflits != null && !resultat.conflits.isEmpty()) {
            VBox conflitBox = new VBox(5);
            conflitBox.setStyle("-fx-border-color: red; -fx-padding: 5; -fx-background-color: #ffe6e6;");
            conflitBox.getChildren().add(new Label("⚠️ Conflits détectés :"));
            for (ApiService.ResultatHoraire.ConflitHoraireDTO conflit : resultat.conflits) {
                conflitBox.getChildren().add(new Label(conflit.toString()));
            }
            root.getChildren().add(conflitBox);
        }

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        Scene scene = new Scene(scroll, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    // ---------------- Getters pour le Main ----------------
    public TextField getChampRecherche() { return champRecherche; }
    public ListView<Cours> getListeResultats() { return listeResultats; }
}
