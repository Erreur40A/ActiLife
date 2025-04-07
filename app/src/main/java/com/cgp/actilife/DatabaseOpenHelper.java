package com.cgp.actilife;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import java.util.ArrayList;
import java.util.Map;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "ActiLife.db";
    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void updateTableWithId(String tableName, Map<String, Object> fields, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            StringBuilder updateQuery = new StringBuilder("UPDATE " + tableName + " SET ");
            ArrayList<String> params = new ArrayList<>();

            // Construction de la requête dynamique
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                updateQuery.append(entry.getKey()).append(" = ?, ");
                params.add(entry.getValue().toString());
            }

            updateQuery.delete(updateQuery.length() - 2, updateQuery.length());  // Supprimer la dernière virgule
            updateQuery.append(" WHERE id = ?");

            params.add(String.valueOf(id)); // Ajout de l'ID en paramètre

            // Exécution de la requête
            db.execSQL(updateQuery.toString(), params.toArray(new String[0]));
        } catch (Exception e) {
            Log.e("DatabaseError", "Error updating table " + tableName + " with id " + id, e);
        } finally {
            db.close();
        }
    }

    // Méthode générique pour les tables sans `id` (mise à jour sans condition WHERE)
    public void updateTableWithoutId(String tableName, Map<String, Object> fields) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            StringBuilder updateQuery = new StringBuilder("UPDATE " + tableName + " SET ");
            ArrayList<String> params = new ArrayList<>();

            // Construction de la requête dynamique
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                updateQuery.append(entry.getKey()).append(" = ?, ");
                params.add(entry.getValue().toString());
            }

            updateQuery.delete(updateQuery.length() - 2, updateQuery.length());  // Supprimer la dernière virgule

            // Exécution de la requête
            db.execSQL(updateQuery.toString(), params.toArray(new String[0]));
        } catch (Exception e) {
            Log.e("DatabaseError", "Error updating table " + tableName, e);
        } finally {
            db.close();
        }
    }

    // Méthode générique pour insérer des données dans n'importe quelle table
    public void insertData(String tableName, Map<String, Object> fields) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // Construction de la requête d'insertion
            StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
            StringBuilder values = new StringBuilder(" VALUES (");
            ArrayList<String> params = new ArrayList<>();

            // Parcours des champs et construction des parties "champ1, champ2, ..." et "?, ?, ..."
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                query.append(entry.getKey()).append(", ");
                values.append("?, ");
                params.add(entry.getValue().toString());
            }

            // Suppression de la dernière virgule
            query.delete(query.length() - 2, query.length());
            values.delete(values.length() - 2, values.length());

            // Fermeture de la parenthèse et fin de la requête
            query.append(") ").append(values).append(")");

            // Exécution de la requête d'insertion
            db.execSQL(query.toString(), params.toArray(new String[0]));
        } catch (Exception e) {
            Log.e("DatabaseError", "Error inserting data into table " + tableName, e);
        } finally {
            db.close();
        }
    }

    // Méthode pour vider une table
    public void viderTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // Construction de la requête pour vider la table
            String query = "DELETE FROM " + tableName;
            db.execSQL(query);
        } catch (Exception e) {
            Log.e("DatabaseError", "Error clearing table " + tableName, e);
        } finally {
            db.close();
        }
    }

    // Méthode pour effacer un enregistrement dans une table en utilisant un ID
    public void effacerEnregistrement(String tableName, long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // Construction de la requête pour effacer un enregistrement par ID
            String query = "DELETE FROM " + tableName + " WHERE id = ?";
            db.execSQL(query, new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e("DatabaseError", "Error deleting record with ID " + id + " from table " + tableName, e);
        } finally {
            db.close();
        }
    }
}
