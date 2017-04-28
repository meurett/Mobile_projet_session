package ca.ulaval.ima.projet_session;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "depense_table";
    private static final String COL_0 = "date";
    // "date" sert de primary_key (improbable d'avoir deux temps égaux à
    // la même milliseconde à l'échelle d'un utilisateur unique !
    private static final String COL_1 = "price";
    private static final String COL_2 = "categorie";
    private static final String COL_3 = "description";
    private static final String COL_4 = "image";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_0 + " INTEGER PRIMARY KEY, " +
                COL_1 + " REAL, " +
                COL_2 + " VARCHAR, " +
                COL_3 + " VARCHAR, " +
                COL_4 + " BLOB);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void createTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_0 + " INTEGER PRIMARY KEY, " +
                COL_1 + " REAL, " +
                COL_2 + " VARCHAR, " +
                COL_3 + " VARCHAR, " +
                COL_4 + " BLOB);";
        db.execSQL(createTable);
    }

    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteTable = "DROP TABLE " + TABLE_NAME;
        db.execSQL(deleteTable);
    }

    public Cursor getSumsByCategorie(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query =
                "SELECT " + COL_2 + " , SUM(" + COL_1 + ")" +
                " FROM " + TABLE_NAME +
                " GROUP BY " + COL_2 + " ;";
        return db.rawQuery(query, null);
    }

    public boolean addData(String date, String categorie, String prix, String description, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_0, date);
        contentValues.put(COL_1, prix);
        contentValues.put(COL_2, categorie);
        contentValues.put(COL_3, description);
        contentValues.put(COL_4, image);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return (result != -1);
    }

    public boolean addDataWithoutImage(String date, String categorie, String prix, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_0, date);
        contentValues.put(COL_1, prix);
        contentValues.put(COL_2, categorie);
        contentValues.put(COL_3, description);
        contentValues.putNull(COL_4);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return (result != -1);
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public void updateDepense(String date, String prix, String description, String categorie, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_0, date);
        contentValues.put(COL_1, prix);
        contentValues.put(COL_2, categorie);
        if (!(description.equals(""))) {
            contentValues.put(COL_3, description);
        }
        contentValues.put(COL_4, image);

        db.update(TABLE_NAME, contentValues, COL_0+"= ?", new String[]{date});
    }

    public void updateDepenseWithoutImage(String date, String prix, String description, String categorie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_0, date);
        contentValues.put(COL_1, prix);
        contentValues.put(COL_2, categorie);
        if (!(description.equals(""))) {
            contentValues.put(COL_3, description);
        }
        contentValues.putNull(COL_4);

        db.update(TABLE_NAME, contentValues, COL_0+"= ?", new String[]{date});
    }

    public Bitmap getImage(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT image FROM " + TABLE_NAME +
                " WHERE " + COL_0 + " = '" + date + "';";
        Cursor data = db.rawQuery(query, null);
        data.moveToNext();
        if (!(data.getBlob(0) == null)){
            return BitmapHelper.getBitmap(data.getBlob(0));
        } else {
            return null;
        }
    }

    public String exportDB(Activity a) {
        String s = "";
        DatabaseHelper dbhelper = this;
        File exportDir = new File(a.getExternalFilesDir(null) + "/depenses/");
        if (!exportDir.exists()){
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "depenses.csv");

        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT " + COL_0 + ", " + COL_1 + ", " + COL_2 + ", " + COL_3 + " FROM " + TABLE_NAME, null);
            String[] columns = curCSV.getColumnNames();
            String[] columns_extend = new String[columns.length + 1];
            Integer i = 0;
            while (i<columns.length){
                columns_extend[i] = columns[i];
                i++;
            }
            columns_extend[i] = "Date humaine";
            csvWrite.writeNext(columns_extend);
            while (curCSV.moveToNext()) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(curCSV.getString(0)));
                String mMonth = getMonthFromInt(calendar.get(Calendar.MONTH));
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                int mYear = calendar.get(Calendar.YEAR);
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), mDay + " " + mMonth + " " + mYear};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            return file.toString();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
            return sqlEx.getMessage();
        }
    }

    private String getMonthFromInt(int month){
        String result = "";
        switch(month){
            case 0:
                result = "Janvier";
                break;
            case 1:
                result = "Février";
                break;
            case 2:
                result = "Mars";
                break;
            case 3:
                result = "Avril";
                break;
            case 4:
                result = "Mai";
                break;
            case 5:
                result = "Juin";
                break;
            case 6:
                result = "Juillet";
                break;
            case 7:
                result = "Aout";
                break;
            case 8:
                result = "Septembre";
                break;
            case 9:
                result = "Octobre";
                break;
            case 10:
                result = "Novembre";
                break;
            case 11:
                result = "Décembre";
                break;
        }
        return result;
    }

    public void deleteDepense(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "DELETE FROM " + TABLE_NAME +
                " WHERE " + COL_0 + " = '" + date + "';";
        db.execSQL(query);
    }
}
