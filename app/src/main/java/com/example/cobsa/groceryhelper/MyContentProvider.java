package com.example.cobsa.groceryhelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;


import java.util.List;

/**
 * ContentProvider implementation for the application. Used to offer support for RecyclerViewer
 * and Async updates. Uses SQLiteOpenHelper for database access.
 */

public class MyContentProvider extends ContentProvider {

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String PROVIDER_NAME = "com.example.cobsa.groceryhelper.MyContentProvider";

    // Assign unique int to different uri cases

    private static final int INGREDIENTS = 1;
    private static final int INGREDIENT = 2;
    private static final int BASKETS = 3;
    private static final int BASKET = 4;
    private static final int INGREDIENTS_BASKET = 5;
    private static final int INGREDIENT_BASKET = 6;

    private SQLiteDatabase db;

    // Match url to int to help switch clauses

    static {
        mUriMatcher.addURI(PROVIDER_NAME,"ingredient",INGREDIENTS);
        mUriMatcher.addURI(PROVIDER_NAME,"ingredient/#",INGREDIENT);
        mUriMatcher.addURI(PROVIDER_NAME,"basket", BASKETS);
        mUriMatcher.addURI(PROVIDER_NAME,"basket/#",BASKET);
        mUriMatcher.addURI(PROVIDER_NAME,"basket/#/ingredient", INGREDIENTS_BASKET);
        mUriMatcher.addURI(PROVIDER_NAME,"basket/#/ingredient/#", INGREDIENT_BASKET);
    }

    //URLS
    public static final Uri INGREDIENTS_URI = Uri.parse("content://" + PROVIDER_NAME + "/ingredient");
    public static final Uri BASKETS_URI = Uri.parse("content://" + PROVIDER_NAME + "/basket");

    // Database variables
    private static String DATABASE_NAME = "Grocery_helper";
    private static int DATABASE_VERSION = 1;
    //Ingredient table
    private static String INGREDIENTS_TABLE_NAME = "ingredients";
    public static String INGREDIENTS_NAME = "ingredient_name";
    public static String INGREDIENT_ID = "ingredient_id";
    public static String INGREDIENT_ID_WTTH_TABLE = INGREDIENTS_TABLE_NAME + "." + INGREDIENT_ID;
    // Basket Table
    private static String BASKET_TABLE_NAME = "baskets";
    public static String BASKET_NAME = "basket_name";
    public static String BASKET_ID = "basket_id";
    // Basket/Ingredient Table
    private static String BASKET_INGREDIENT_TABLE_NAME = "basket_ingredient";
    public static String INGREDIENT_AMOUNT = "ingredient_amount";
    public static String BASKET_ITEM_CHECKED = "basket_item_checked";

    // JOIN TABLE QUERIES
    private static final String BASKET_INGEDIENT_TABLE_QUERY = INGREDIENTS_TABLE_NAME + " JOIN " + BASKET_INGREDIENT_TABLE_NAME
            + " ON " + INGREDIENTS_TABLE_NAME + "." + INGREDIENT_ID + "="
            + BASKET_INGREDIENT_TABLE_NAME + "." + INGREDIENT_ID + "";



    // Create DatabaseHelper class to access db
    private static class MySqLiteHelper extends SQLiteOpenHelper {

        // TABLE QUERIES

        private static String INGREDIENTS_TABLE = "CREATE TABLE " + INGREDIENTS_TABLE_NAME +
                " (" + INGREDIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                INGREDIENTS_NAME + " VARCHAR(100) NOT NULL );";
        private static String BASKET_TABLE = "CREATE TABLE " + BASKET_TABLE_NAME +
                " (" + BASKET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BASKET_NAME + " VARCHAR(100) NOT NULL);";
        private static String BASKET_INGREDIENT_TABLE = "CREATE TABLE " + BASKET_INGREDIENT_TABLE_NAME + "("
                + BASKET_ID + " INTEGER,"
                + INGREDIENT_ID + " INTEGER, "
                + INGREDIENT_AMOUNT + " INTEGER, "
                + BASKET_ITEM_CHECKED + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + INGREDIENT_ID + ") REFERENCES " + INGREDIENTS_TABLE_NAME +"("
                + INGREDIENT_ID + ") ON DELETE CASCADE, "
                + "FOREIGN KEY(" + BASKET_ID + ") REFERENCES " +  BASKET_TABLE_NAME + "("
                + BASKET_ID + ") ON DELETE CASCADE);";


        MySqLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(INGREDIENTS_TABLE);
            db.execSQL(BASKET_TABLE);
            db.execSQL(BASKET_INGREDIENT_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + INGREDIENTS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + BASKET_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + BASKET_INGREDIENT_TABLE_NAME);

            onCreate(db);
        }
    }



    @Override
    public boolean onCreate() {
        //Return true if writable db was successfully obtained.
        Context mContext = getContext();
        MySqLiteHelper mMySqLiteHelper = new MySqLiteHelper(mContext);

        db = mMySqLiteHelper.getWritableDatabase();

        return db!=null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Return cursor based on query parameters.
        String id;
        Cursor cursor;
        switch (mUriMatcher.match(uri)) {
            case INGREDIENT:
                id = uri.getLastPathSegment();
                cursor = db.query(INGREDIENTS_TABLE_NAME,projection,
                        INGREDIENT_ID + "=" + id +(!TextUtils.isEmpty(selection)? " AND (" +selection+ ")":""),
                        selectionArgs,null,null,sortOrder);
                break;
            case INGREDIENTS:
                cursor = db.query(INGREDIENTS_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;

            case BASKET:
                id = uri.getLastPathSegment();
                cursor = db.query(BASKET_TABLE_NAME,projection,
                        BASKET_ID + "=" + id +(!TextUtils.isEmpty(selection)? " AND (" +selection+ ")":""),
                        selectionArgs,null,null,sortOrder);
                break;
            case BASKETS:
                cursor = db.query(BASKET_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case INGREDIENTS_BASKET:
                List<String> pathSegments = uri.getPathSegments();
                id = pathSegments.get(1);
                /* Query needs to join INGREDIENT_TABLE with BASKET_INGREDIENT_TABLE_NAME
                so query contains INGREDIENT_NAME etc fields.
                */
                cursor = db.query(BASKET_INGEDIENT_TABLE_QUERY,projection,BASKET_ID + "=" + id,selectionArgs,null,null,sortOrder);
                break;
            default:
                return null;
        }

        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        // Add @param values to appropriate table. Table is decided by
        // @param uri. Return uri to newly added resource
        long rowID;
        Uri _uri;
        String BasketID;
        switch (mUriMatcher.match(uri)) {
            case INGREDIENTS:
                rowID = db.insert(INGREDIENTS_TABLE_NAME,null,values);
                if (rowID > 0) {
                    // Add reference to content provider
                    _uri = ContentUris.withAppendedId(INGREDIENTS_URI,rowID);
                    getContext().getContentResolver().notifyChange(_uri,null);
                    break;
                }
                throw new SQLException("Failed to add Ingredient. URI: " + uri + " Values: " + (values != null? values.toString():""));
            case BASKETS:
                rowID = db.insert(BASKET_TABLE_NAME,null,values);
                if (rowID > 0) {
                    _uri = ContentUris.withAppendedId(BASKETS_URI,rowID);
                    getContext().getContentResolver().notifyChange(_uri,null);
                    break;
                }
            case INGREDIENTS_BASKET:
                BasketID = uri.getPathSegments().get(1);
                if(values == null) {
                    throw new SQLException("Please provide values in method parameters");
                }
                values.put(BASKET_ID, Long.parseLong(BasketID));
                rowID = db.insert(BASKET_INGREDIENT_TABLE_NAME,null,values);
                if (rowID > 0 ) {
                    _uri = ContentUris.withAppendedId(Uri.withAppendedPath(BASKETS_URI, BasketID + "/ingredient/"),rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    break;
                }
            default:
                throw new SQLException("Unknown data type: " + uri);
        }
        return _uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        /*
        Deletes rows from database based on @param uri and @param selection, selection args.
        Return number of deleted rows
         */

        //Helper variables
        int count;
        String basketID;
        String ingredientID;

        switch (mUriMatcher.match(uri)) {
            case INGREDIENT:
                ingredientID = uri.getLastPathSegment();
                count = db.delete(INGREDIENTS_TABLE_NAME,INGREDIENT_ID + "=" + ingredientID
                        +(!TextUtils.isEmpty(selection)? " AND (" +selection + ")" : ""),selectionArgs);
                break;

            case BASKET:
                basketID = uri.getLastPathSegment();
                count = db.delete(BASKET_TABLE_NAME,BASKET_ID + "=" + basketID
                        +(!TextUtils.isEmpty(selection)? " AND (" +selection + ")" : ""),selectionArgs);
                break;
            case INGREDIENT_BASKET:
                basketID = uri.getPathSegments().get(1); // PROVIDER/basket/#/ingredient/#
                ingredientID = uri.getPathSegments().get(3);
                count = db.delete(BASKET_INGREDIENT_TABLE_NAME, BASKET_ID + "=" + basketID + " AND " +
                        INGREDIENT_ID + "=" + ingredientID + (!TextUtils.isEmpty(selection)? " AND (" +selection + ")" : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        /*
        * Updates rows based on uri, selection, selectionArgs. Modified values are specified in @param values.
        * @param values must contain all necessary columns
         */
        int count; // Helper variable
        String id;
        switch (mUriMatcher.match(uri)) {
            case INGREDIENT:
                id = uri.getLastPathSegment();
                count = db.update(INGREDIENTS_TABLE_NAME,values,INGREDIENT_ID + "=" + id +(!TextUtils.isEmpty(selection)? " AND (" +selection + ")" : ""),selectionArgs);
                break;
            case BASKET:
                id = uri.getLastPathSegment();
                count = db.update(BASKET_TABLE_NAME,values,BASKET_ID + "=" + id +(!TextUtils.isEmpty(selection)? " AND (" +selection + ")" : ""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        return count;
    }

    // Helper functions


    public Uri AddBasket(String basketName) {
        ContentValues values = new ContentValues();
        values.put(BASKET_NAME,basketName);
        return this.insert(BASKETS_URI,values);

    }
    public int RemoveBasket(long basketID) {
        return this.delete(Uri.withAppendedPath(BASKETS_URI,Long.toString(basketID)),null,null);

    }
    public Cursor GetAllBaskets() {
        return this.query(MyContentProvider.BASKETS_URI,new String[] {
                BASKET_NAME,BASKET_ID},null,null,null);

    }
    public Uri AddIngredient(String ingredientName) {
        ContentValues values = new ContentValues();
        values.put(INGREDIENTS_NAME,ingredientName);
        return this.insert(INGREDIENTS_URI,values);

    }
    public int RemoveIngredient(long ingredientID){
        return this.delete(Uri.withAppendedPath(INGREDIENTS_URI,Long.toString(ingredientID)),null,null);

    }
    public Cursor GetAllIngredients() {
        return this.query(MyContentProvider.INGREDIENTS_URI,new String[] {
                INGREDIENTS_NAME,INGREDIENT_ID},null,null,null);
    }
    public Uri AddIngredientToBasket(long ingredientId, long basketID) {
        ContentValues values = new ContentValues();
        values.put(INGREDIENT_ID,ingredientId);
        return this.insert(Uri.withAppendedPath(BASKETS_URI,Long.toString(basketID)+"/ingredient"),values);
    }
    public Cursor GetAllIngredientsInBasket(long basketID) {
        return this.query(Uri.withAppendedPath(BASKETS_URI,Long.toString(basketID)+"/ingredient"),
                new String[] {INGREDIENT_ID_WTTH_TABLE,INGREDIENTS_NAME,BASKET_ITEM_CHECKED,INGREDIENT_AMOUNT},
                null,null,null);
    }
    public int RemoveIngredientFromBasket(long ingredientID, long basketID) {
        return this.delete(Uri.withAppendedPath(BASKETS_URI,basketID+"/ingredient/" + ingredientID),null,null);
    }
}
