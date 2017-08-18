package com.example.cobsa.groceryhelper;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Class handling dialog pop up for adding items in database. Item type is defined by argument
 * "SHOW_TAG". Tag defines title text and what function is called from ContentProviderHelper.
 */

public class AddItemDialog extends DialogFragment {

    public static String SHOW_TAG = "show";
    public static final int BASKET = 1;
    public static final int INGREDIENT = 2;

    private EditText mEditText;
    private TextView mTextView;
    private String mTitle;
    private int tagId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        tagId = getArguments().getInt(SHOW_TAG);




        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
        // Get Layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate layout and set it to builders view
        View inflatedView = inflater.inflate(R.layout.add_item_layout, null);
        // Get editText and change title
        mEditText = (EditText) inflatedView.findViewById(R.id.add_item_name);
        mTextView = (TextView) inflatedView.findViewById(R.id.add_item_title);

        // Get title from outside fragment
        switch (tagId) {
            case BASKET:
                mTitle = getString(R.string.add_basket);
                mEditText.setHint(getString(R.string.search_hint_basket_name));
                break;
            case INGREDIENT:
                mTitle = getString(R.string.add_ingredient);
                mEditText.setHint(getString(R.string.search_hint_ingredient_name));
                break;
            default:
                mTitle = "PlaceHolder";
        }

        mTextView.setText(mTitle);
        // Add action buttons
        builder.setView(inflatedView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Add item to database
                        String itemName = mEditText.getText().toString();
                        ContentValues values = new ContentValues();
                        // Correct function to call
                        switch (tagId) {
                            case BASKET:
                                values.put(MyContentProvider.BASKET_NAME,itemName);
                                getContext().getContentResolver().insert(MyContentProvider.BASKETS_URI,values);
                                break;
                            case INGREDIENT:
                                values.put(MyContentProvider.INGREDIENTS_NAME,itemName);
                                getContext().getContentResolver().insert(MyContentProvider.INGREDIENTS_URI,values);
                                break;
                            default:
                                throw new IllegalArgumentException("Item type basket/ingredient etc is not specified. TagId: " + tagId);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddItemDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

}
