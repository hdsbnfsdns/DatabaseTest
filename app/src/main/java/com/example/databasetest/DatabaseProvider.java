package com.example.databasetest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DatabaseProvider extends ContentProvider {
    public static final int CONTACT_DIR = 0;
    public static final int CONTACT_ITEM = 1;
    public static final String AUTHORITY = "com.example.databasetest.provider";
    public static UriMatcher uriMatcher;
    private MyDatabaseHelper dbHelper;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,"contact",CONTACT_DIR);
        uriMatcher.addURI(AUTHORITY,"contact/#",CONTACT_ITEM);
    }
    public DatabaseProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CONTACT_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.databasetest.provider.contact";
            case CONTACT_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.databasetest.provider.contact";
        }
        return null;
        }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //添加数据
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)){
            case CONTACT_DIR:
            case CONTACT_ITEM:
                long newContactId = db.insert("Contact",null,values);
                uriReturn = Uri.parse("content://" + AUTHORITY + "/book/" +newContactId);
                break;
            default:
                break;
        }
        return uriReturn;

    }

    @Override
    public boolean onCreate() {
        dbHelper = new MyDatabaseHelper(getContext(),"Contact.db",null,2);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        //查询数据
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor  cursor = null;
        switch (uriMatcher.match(uri)){
            case CONTACT_DIR:
                cursor = db.query("Contact",projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case CONTACT_ITEM:
                String contactId = uri.getPathSegments().get(1);
                cursor = db.query("Contact",projection,"id=?",new String[]{contactId},null,null,sortOrder);
            default:
                break;
        }
        return cursor;

    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        //更新数据
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int deletedRows = 0;
        switch (uriMatcher.match(uri)) {
            case CONTACT_DIR:
                deletedRows = db.delete("Contact",selection,selectionArgs);
                break;
            case CONTACT_ITEM:
                String contactId = uri.getPathSegments().get(1);
                deletedRows = db.delete("Contact","id = ?",new String[]{contactId});
                break;
            default:
                break;
        }
        return deletedRows;
        }

}