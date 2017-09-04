package com.example.cobsa.groceryhelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Test suite for MyContentProvider. Tests contain adding/modifying/deleting
 * ingredients/baskets/ingredients_in_basket. Doesn't test selection, selectionArgs
 * or sortOrder variables in MyContentProvider.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MyContentProviderTest {
    private MyContentProvider cp;
    // Mock data
    private String[] mBasketNames = new String[] {"Today", "Yesterday", "Last week", "Last month"};
    private String[] mIngredientNames = new String[] {"Milk", "Bread", "Butter", "Tomatoes", "Coffee", "Sugar"};


    @Before
    public void setUp() {
        // Get mock content provider
        cp = Robolectric.buildContentProvider(MyContentProvider.class).create().get();
    }

    @Test
    public void AddBasketTest() {
        for (String basketName: mBasketNames) {
            Uri uri = cp.AddBasket(basketName);
            if(BuildConfig.DEBUG && uri == null) {
                throw new AssertionError("Uri is null,");
            }
            Cursor cursor = cp.query(uri,new String[] {MyContentProvider.BASKET_NAME,MyContentProvider.BASKET_ID},
                    null,null,null);
            if(cursor != null) {
                cursor.moveToNext();
                if(BuildConfig.DEBUG && !basketName.equals(cursor.getString(0))) {
                    throw new AssertionError("Column data doesn't match. Expected: " + basketName + " Got: " + cursor.getString(0));
                }
                cursor.close();
            }

            if(BuildConfig.DEBUG && cursor == null) {
                throw new AssertionError("Cursor is null");
            }
        }
    }

    @Test
    public void RemoveBasketTest() {
        for(String basketName: mBasketNames) {
            Uri uri = cp.AddBasket(basketName);
            if(BuildConfig.DEBUG && uri == null) {
                throw new AssertionError("Uri is null.");
            }
            int count = cp.RemoveBasket(Long.parseLong(uri.getLastPathSegment()));
            if(BuildConfig.DEBUG &&count != 1) {
                throw new AssertionError("Wrong amount of rows deleted. Deleted rows: " + count);
            }

        }
    }
    @Test
    public void GetAllBasketsTest() {
        // Add All preset baskets to db
        for(String basketName:mBasketNames) {
            cp.AddBasket(basketName);
        }
        Cursor cursor = cp.GetAllBaskets();
        if(BuildConfig.DEBUG && cursor == null) {
            throw new AssertionError("Cursor is null.");
        }
        if(BuildConfig.DEBUG && cursor.getCount() != mBasketNames.length) {
            throw new AssertionError("Row counts don't match: Expected: " + mBasketNames.length + " Got: " + cursor.getCount());
        }
    }
    @Test
    public void UpdateBasket() {
        for(String basketName: mBasketNames) {
            Uri uri = cp.AddBasket(basketName);
            String updatedBasketName = basketName + " updated";
            ContentValues values = new ContentValues();
            values.put(MyContentProvider.BASKET_NAME,updatedBasketName);
            int count = cp.update(uri,values,null,null);
            if(BuildConfig.DEBUG && count != 1) {
                throw new AssertionError("Wrong amount of lines updated.");
            }
            Cursor cursor = cp.query(uri,new String[] { MyContentProvider.BASKET_NAME},null,null,null);
            if(BuildConfig.DEBUG && cursor == null ) {
                throw new AssertionError("Cursor is null.");
            }
            cursor.moveToNext();
            if(BuildConfig.DEBUG && !updatedBasketName.equals(cursor.getString(0))) {
                throw new AssertionError("Expected: " + updatedBasketName + " Got: " + cursor.getString(0));
            }
            cursor.close();

        }
    }
    @Test
    public void AddIngredientTest() {
        for (String ingredientName: mIngredientNames) {
            Uri uri = cp.AddIngredient(ingredientName);
            if(BuildConfig.DEBUG && uri == null) {
                throw new AssertionError("Uri is null,");
            }
            Cursor cursor = cp.query(uri,new String[] {MyContentProvider.INGREDIENTS_NAME,MyContentProvider.INGREDIENT_ID},
                    null,null,null);
            if(cursor != null) {
                cursor.moveToNext();
                if(BuildConfig.DEBUG && !ingredientName.equals(cursor.getString(0))) {
                    throw new AssertionError("Column data doesn't match. Expected: " + ingredientName + " Got: " + cursor.getString(0));
                }
                cursor.close();
            }

            if(BuildConfig.DEBUG && cursor == null) {
                throw new AssertionError("Cursor is null");
            }
        }
    }

    @Test
    public void RemoveIngredientTest() {
        for(String ingredientName: mIngredientNames) {
            Uri uri = cp.AddIngredient(ingredientName);
            if(BuildConfig.DEBUG && uri == null) {
                throw new AssertionError("Uri is null.");
            }
            int count = cp.RemoveIngredient(Long.parseLong(uri.getLastPathSegment()));
            if(BuildConfig.DEBUG &&count != 1) {
                throw new AssertionError("Wrong amount of rows deleted. Deleted rows: " + count);
            }

        }
    }
    @Test
    public void GetAllIngredientsTest() {
        // Add All preset baskets to db
        for(String ingredientName:mIngredientNames) {
            cp.AddIngredient(ingredientName);
        }
        Cursor cursor = cp.GetAllIngredients();
        if(BuildConfig.DEBUG && cursor == null) {
            throw new AssertionError("Cursor is null.");
        }
        if(BuildConfig.DEBUG && cursor.getCount() != mIngredientNames.length) {
            throw new AssertionError("Row counts don't match: Expected: " + mIngredientNames.length + " Got: " + cursor.getCount());
        }
    }
    @Test
    public void UpdateIngredient() {
        for(String ingredientName: mIngredientNames) {
            Uri uri = cp.AddIngredient(ingredientName);
            String updatedIngredientName = ingredientName + " updated";
            ContentValues values = new ContentValues();
            values.put(MyContentProvider.INGREDIENTS_NAME,updatedIngredientName);
            int count = cp.update(uri,values,null,null);
            if(BuildConfig.DEBUG && count != 1) {
                throw new AssertionError("Wrong amount of lines updated.");
            }
            Cursor cursor = cp.query(uri,new String[] { MyContentProvider.INGREDIENTS_NAME},null,null,null);
            if(BuildConfig.DEBUG && cursor == null ) {
                throw new AssertionError("Cursor is null.");
            }
            cursor.moveToNext();
            if(BuildConfig.DEBUG && !updatedIngredientName.equals(cursor.getString(0))) {
                throw new AssertionError("Expected: " + updatedIngredientName + " Got: " + cursor.getString(0));
            }
            cursor.close();
        }
    }
    @Test
    public void IngredientsBasket() {
        ArrayList<String> basketIDs = new ArrayList<>();
        ArrayList<String> ingredientsIDs = new ArrayList<>();
        for(String basketName: mBasketNames) {
            Uri uri = cp.AddBasket(basketName);
            basketIDs.add(uri.getLastPathSegment());
        }
        for(String ingredientName: mIngredientNames) {
            Uri uri = cp.AddIngredient(ingredientName);
            ingredientsIDs.add(uri.getLastPathSegment());
        }
        // Add all ingredients to all baskets
        for(String basketID: basketIDs) {
            for (String ingredientID: ingredientsIDs) {
                cp.AddIngredientToBasket(Long.parseLong(ingredientID),Long.parseLong(basketID));

            }
        }
        // Check that all baskets contain correct amount of ingredients
        for(String basketId: basketIDs) {
            Cursor cursor =cp.GetAllIngredientsInBasket(Long.parseLong(basketId));
            if(BuildConfig.DEBUG && cursor == null) {
                throw new AssertionError("Cursor is null.");
            }
            if(BuildConfig.DEBUG && cursor.getCount() != mIngredientNames.length) {
                throw new AssertionError("Count's don't match. Expected: " + mIngredientNames.length + " Got: " + cursor.getCount());
            }
        }
        // Update info on basket contents

        for(String basketID: basketIDs)
        {
            for (String ingredientID: ingredientsIDs)
            {
                ContentValues values = new ContentValues();
                values.put(MyContentProvider.BASKET_ITEM_CHECKED,1);
                values.put(MyContentProvider.INGREDIENT_AMOUNT,5);
                cp.update(Uri.withAppendedPath(MyContentProvider.BASKETS_URI,
                        Long.parseLong(ingredientID) + "/ingredient/" + Long.parseLong(basketID)),values,null,null);

            }
        }

        // Check all values are updated
        for(String basketID: basketIDs)
        {
            Cursor cursor = cp.query(Uri.withAppendedPath(MyContentProvider.BASKETS_URI,basketID + "/ingredient"),new String[]{
                    MyContentProvider.BASKET_ID,
                    MyContentProvider.INGREDIENT_ID_WITH_TABLE },
                    MyContentProvider.BASKET_ITEM_CHECKED + "=" + "1",null,null
            );

            if(cursor != null) {
                if(cursor.getCount() != mIngredientNames.length) {
                    throw new AssertionError("Not all rows where updated. Expected: " +
                            mIngredientNames.length + " Got: " + cursor.getCount());
                }
            } else {
                throw new AssertionError("Cursor is null");
            }
            cursor.close();

        }
        //Remove ingredients from baskets
        int count;
        for(String basketId: basketIDs) {
            for(String ingredientId: ingredientsIDs) {
                count = cp.RemoveIngredientFromBasket(Long.parseLong(ingredientId),Long.parseLong(basketId));
                if (BuildConfig.DEBUG && count != 1) {
                    throw new AssertionError("Deleted wrong amount of rows. Deleted rows: " + count);
                }
            }
        }
        // Check that all baskets contain 0 ingredients
        for(String basketId: basketIDs) {
            Cursor cursor =cp.GetAllIngredientsInBasket(Long.parseLong(basketId));
            if(BuildConfig.DEBUG && cursor == null) {
                throw new AssertionError("Cursor is null.");
            }
            if(BuildConfig.DEBUG && cursor.getCount() != 0) {
                throw new AssertionError("Count's don't match. Expected: " + mIngredientNames.length + " Got: " + cursor.getCount());
            }
        }
    }
}