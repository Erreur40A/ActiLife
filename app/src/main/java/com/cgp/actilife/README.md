# Application Android ActiLife

## Aide à la Base de Données
La classe `DatabaseOpenHelper` étend `SQLiteAssetHelper` pour gérer la base de données avec diverses opérations telles que l'insertion, la mise à jour et la suppression. Cette classe prend en charge les opérations dynamiques sur les tables, facilitant ainsi l'interaction avec la base de données.

### Opérations Supportées :
- **Insertion de Données** : Insérer de nouveaux enregistrements dans n'importe quelle table.
    - Utilisez la méthode `insertData` pour insérer des enregistrements dans n'importe quelle table. Exemple d'utilisation :
    ```java
    Map<String, Object> fields = new HashMap<>();
    fields.put(ConstDB.USERDATA_PRENOM, "John");
    fields.put(ConstDB.USERDATA_NOM, "Doe");
    fields.put(ConstDB.USERDATA_DATE_NAISSANCE, "1990-01-01");
    dbHelper.insertData(ConstDB.USERDATA, fields);
    ```

- **Mise à Jour des Données** : Mettre à jour les enregistrements soit par ID, soit sans ID.
  - Pour mettre à jour un enregistrement par ID, utilisez la méthode `updateTableWithId` :
    ```java
    Map<String, Object> fields = new HashMap<>();
    fields.put(ConstDB.MEDICAMENTS_NOM, "nouveau_nom_med");
    dbHelper.updateTableWithId(ConstDB.MEDICAMENTS, fields, 2);
    ```
  - Pour mettre à jour des enregistrements sans spécifier d'ID :
    ```java
    Map<String, Object> fields = new HashMap<>();
    fields.put(ConstDB.CALORIES_NB_CALORIES_AUJOURDHUI, 2000);
    dbHelper.updateTableWithoutId(ConstDB.CALORIES, fields);
    ```
- **Suppression de Données** : Supprimer des enregistrements par ID ou vider complètement une table.
  - Pour supprimer un enregistrement par ID :
    ```java
    dbHelper.effacerEnregistrement(ConstDB.POIDS, 1);
    ```
  - Pour vider tous les enregistrements d'une table :
    ```java
    dbHelper.viderTable(ConstDB.REPAS);
    ```
- **Récupération de Données** : Récupérer tous les enregistrements ou des enregistrements spécifiques par ID.
  - Pour récupérer tous les enregistrements d'une table :
    ```java
    List<Map<String, String>> records = dbHelper.getAll(ConstDB.ACTIVITES_SPORTIVES);
    ```
  - Pour récupérer un enregistrement spécifique par ID :
    ```java
    Map<String, String> record = dbHelper.getOneWithId(ConstDB.MEDICAMENTS, 1);
    ```
  - Il existe également des méthodes pour récupérer des attributs spécifiques :
    ```java
    public String getAttributeWithId(String tableName, String attribute, long id);
    public String getAttributeWithoutId(String tableName, String attribute);
    public String getMotivation(String type); ```Elle renvoie une motivation aleatoire en fonction du type```
    On a 4 types de motivation:  
    public static final String MOTIVATIONS_TYPE_SPORT = "sport";
    public static final String MOTIVATIONS_TYPE_SOMMEIL = "sommeil";
    public static final String MOTIVATIONS_TYPE_PAS = "pas";
    public static final String MOTIVATIONS_TYPE_USER_CALORIES = "user_calories";
    ```

## Constantes
La classe `ConstDB` contient des valeurs constantes pour les noms de tables et les noms de colonnes utilisés dans l'application. Cela réduit le risque d'erreurs et simplifie les interactions avec la base de données.

### Tables :
1. **userdata** : Stocke les données utilisateur telles que le nom, la date de naissance, la taille, le poids et les rappels d'hydratation.
2. **calories** : Stocke les données quotidiennes sur les calories.
3. **pas** : Suit le nombre de pas effectués chaque jour.
4. **poids** : Stocke le poids actuel et la date d'insertion.
5. **repas** : Stocke les données des repas, y compris le nom, la quantité et les calories.
6. **activites_sportives** : Stocke les données relatives aux activités physiques, y compris les heures de début et de fin.
7. **medicaments** : Stocke les détails des médicaments, y compris le nom et les heures de prise.
8. **rappels_sommeil** : Stocke les rappels liés au sommeil, y compris les heures de réveil et de coucher.
9. **motivations** : Stocke les messages de motivation pour l'utilisateur.


### Exemple d'utilisation dans une activity

DatabaseOpenHelper db = new DatabaseOpenHelper(this);
-- pour obtenir toutes les donnees de la table userdata
Map<String, String> userdata = db.getOneWithoutId(ConstDB.USERDATA);
if (userdata.containsKey(ConstDB.USERDATA_NOM) && userdata.get(ConstDB.USERDATA_NOM) != null) {
    etNom.setText(userdata.get(ConstDB.USERDATA_NOM));
} else {
    // do something
}

-- pour une obtenir une motivation aleatoire de sport
String motivation_sport = db.getMotivation(ConstDB.MOTIVATIONS_TYPE_SPORT);
