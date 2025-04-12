package com.cgp.actilife;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "ActiLife.db";
    private static final Random RANDOM = new Random();
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
            Log.i("Update - "+tableName, "Maj reussie" );
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
            Log.i("Update - "+tableName, "Maj reussie" );
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
            Log.i("Insertion - "+tableName, "Insertion reussie" );
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


    public List<Map<String, String>> getAll(String tableName) {
        List<Map<String, String>> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);

            while (cursor.moveToNext()) {
                Map<String, String> record = new HashMap<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    record.put(cursor.getColumnName(i), cursor.getString(i));
                }
                records.add(record);
            }

            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseError", "Error retrieving all from " + tableName, e);
        } finally {
            db.close();
        }

        return records;
    }

    public Map<String, String> getOneWithId(String tableName, long id) {
        Map<String, String> record = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE id = ?", new String[]{String.valueOf(id)});

            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    record.put(cursor.getColumnName(i), cursor.getString(i));
                }
            }

            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseError", "Error retrieving one from " + tableName + " with ID " + id, e);
        } finally {
            db.close();
        }

        return record;
    }

    public Map<String, String> getOneWithoutId(String tableName) {
        Map<String, String> record = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);

            if (cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    record.put(cursor.getColumnName(i), cursor.getString(i));
                }
            }

            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseError", "Error retrieving one from " + tableName, e);
        } finally {
            db.close();
        }

        return record;
    }

    public String getAttributeWithId(String tableName, String attribute, long id) {
        String value = null;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT " + attribute + " FROM " + tableName + " WHERE id = ?", new String[]{String.valueOf(id)});

            if (cursor.moveToFirst()) {
                value = cursor.getString(0);
            }

            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseError", "Error retrieving attribute " + attribute + " from " + tableName + " with ID " + id, e);
        } finally {
            db.close();
        }

        return value;
    }

    public String getAttributeWithoutId(String tableName, String attribute) {
        String value = null;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("SELECT " + attribute + " FROM " + tableName + " LIMIT 1", null);

            if (cursor.moveToFirst()) {
                value = cursor.getString(0);
            }

            cursor.close();
        } catch (Exception e) {
            Log.e("DatabaseError", "Error retrieving attribute " + attribute + " from " + tableName, e);
        } finally {
            db.close();
        }

        return value;
    }

    public String getMotivation(String type){
        long id;

        switch (type) {
            case ConstDB.MOTIVATIONS_TYPE_SPORT:
                id = generateRandomLong(1, 10);
                break;
            case ConstDB.MOTIVATIONS_TYPE_SOMMEIL:
                id = generateRandomLong(11, 20);
                break;
            case ConstDB.MOTIVATIONS_TYPE_PAS:
                id = generateRandomLong(21, 30);
                break;
            case ConstDB.MOTIVATIONS_TYPE_USER_CALORIES:
                id = generateRandomLong(31, 40);
                break;
            default:
                throw new IllegalArgumentException("Type de motivation invalide : " + type);
        }

        return getAttributeWithId(ConstDB.MOTIVATIONS, ConstDB.MOTIVATIONS_MESSAGE, id);

    }

    public static long generateRandomLong(long a, long b) {
        if (a > b) {
            throw new IllegalArgumentException("a doit être inférieur ou égal à b");
        }
        return a + (long) (RANDOM.nextDouble() * (b - a + 1));
    }

}
