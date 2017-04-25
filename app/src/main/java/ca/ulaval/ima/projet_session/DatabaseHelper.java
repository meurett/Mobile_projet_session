package ca.ulaval.ima.projet_session;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper{

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
        /*
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_0 + " INTEGER PRIMARY KEY, " +
                COL_1 + " VARCHAR, " +
                COL_2 + " VARCHAR, " +
                COL_3 + " VARCHAR, " +
                COL_4 + " XXX);";
        */
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_0 + " INTEGER PRIMARY KEY, " +
                COL_1 + " VARCHAR, " +
                COL_2 + " VARCHAR, " +
                COL_3 + " VARCHAR);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //public boolean addData(String date, String categorie, String prix, String description, XXX image){
    public boolean addData(String date, String categorie, String prix, String description){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_0, date);
        contentValues.put(COL_1, prix);
        contentValues.put(COL_2, categorie);
        contentValues.put(COL_3, description);
        //contentValues.put(COL_4, image);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return (result != -1);
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public void updateDepense(String date, String prix, String description, String categorie){
        SQLiteDatabase db = this.getWritableDatabase();
        String descriptionString = "";
        if (!(description.equals(""))){
            descriptionString = "," + COL_3 + " = '" + description + "' ";
        }
        String query = "UPDATE " + TABLE_NAME + " SET " +
                COL_1 + " = '" + prix + "' ," +
                COL_2 + " = '" + categorie + "'" +
                descriptionString +
                "WHERE " + COL_0 + " = '" + date + "'";
        db.execSQL(query);
    }
}
