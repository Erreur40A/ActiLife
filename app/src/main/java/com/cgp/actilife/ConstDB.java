package com.cgp.actilife;

public final class ConstDB {

    // ==================== TABLE: userdata ====================
    public static final String USERDATA = "userdata";
    public static final String USERDATA_PRENOM = "prenom";
    public static final String USERDATA_NOM = "nom";
    public static final String USERDATA_DATE_NAISSANCE = "date_naissance";
    public static final String USERDATA_TAILLE_CM = "taille_cm";
    public static final String USERDATA_POIDS_CIBLE = "poids_cible";
    public static final String USERDATA_OBJECTIF_PAS = "objectif_pas";
    public static final String USERDATA_RAPPEL_HYDRATATION_ACTIVE = "rappel_hydratation_active";
    public static final String USERDATA_DATE_CREATION = "date_creation";
    public static final String USERDATA_DATE_MISE_A_JOUR = "date_mise_a_jour";

    // ==================== TABLE: calories ====================
    public static final String CALORIES = "calories";
    public static final String CALORIES_NB_CALORIES_AUJOURDHUI = "nb_calories_aujourdhui";
    public static final String CALORIES_DERNIERE_MISE_A_JOUR = "derniere_mise_a_jour";
    public static final String CALORIES_CALORIES_NECESSAIRES_PAR_JOUR = "calories_necessaires_par_jour";

    // ==================== TABLE: pas ====================
    public static final String PAS = "pas";
    public static final String PAS_NB_PAS_AUJOURDHUI = "nb_pas_aujourdhui";
    public static final String PAS_DATE_DU_JOUR = "date_du_jour";
    public static final String PAS_OBJECTIF_PAS = "objectif_pas";

    // ==================== TABLE: poids ====================
    public static final String POIDS = "poids";
    public static final String POIDS_ID = "id";
    public static final String POIDS_POIDS_ACTUEL = "poids_actuel";
    public static final String POIDS_DATE_INSERTION = "date_insertion";

    // ==================== TABLE: repas ====================
    public static final String REPAS = "repas";
    public static final String REPAS_ID = "id";
    public static final String REPAS_NOM = "nom";
    public static final String REPAS_QUANTITE_G = "quantite_g";
    public static final String REPAS_CALORIES = "calories";

    // ==================== TABLE: activites_sportives ====================
    public static final String ACTIVITES_SPORTIVES = "activites_sportives";
    public static final String ACTIVITES_SPORTIVES_ID = "id";
    public static final String ACTIVITES_SPORTIVES_NOM = "nom";
    public static final String ACTIVITES_SPORTIVES_HEURE_DEBUT = "heure_debut";
    public static final String ACTIVITES_SPORTIVES_HEURE_FIN = "heure_fin";
    public static final String ACTIVITES_SPORTIVES_JOURS_ACTIFS = "jours_actifs";

    // ==================== TABLE: medicaments ====================
    public static final String MEDICAMENTS = "medicaments";
    public static final String MEDICAMENTS_ID = "id";
    public static final String MEDICAMENTS_NOM = "nom";
    public static final String MEDICAMENTS_HEURES_PRISE = "heures_prise";

    // ==================== TABLE: rappels_sommeil ====================
    public static final String RAPPELS_SOMMEIL = "rappels_sommeil";
    public static final String RAPPELS_SOMMEIL_ID = "id";
    public static final String RAPPELS_SOMMEIL_HEURE_REVEIL = "heure_reveil";
    public static final String RAPPELS_SOMMEIL_HEURE_COUCHER = "heure_coucher";

    private ConstDB() {
        // Classe utilitaire non instanciable
    }
}

