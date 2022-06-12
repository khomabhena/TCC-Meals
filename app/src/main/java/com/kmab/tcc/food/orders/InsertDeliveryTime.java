package com.kmab.tcc.food.orders;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.List;

public class InsertDeliveryTime extends AsyncTask<SetterDeliveryTime, Void, Void> {

    private SQLiteDatabase db;
    DBOperations dbOperations;
    List listKeys;

    public InsertDeliveryTime(Context context, List listKeys) {
        dbOperations = new DBOperations(context);
        db = dbOperations.getWritableDatabase();
        this.listKeys = listKeys;
    }

    @Override
    protected Void doInBackground(SetterDeliveryTime... params) {
        SetterDeliveryTime setter = params[0];

        ContentValues values = new ContentValues();
        values.put(DBContract.DeliveryTime.KEY, setter.getKey());
        values.put(DBContract.DeliveryTime.NAME, setter.getName());
        values.put(DBContract.DeliveryTime.START_HOUR, setter.getStartHour());
        values.put(DBContract.DeliveryTime.END_HOUR, setter.getEndHour());
        values.put(DBContract.DeliveryTime.MINUTE, setter.getMinute());
        values.put(DBContract.DeliveryTime.EXTRA_CHARGE, setter.getExtraCharge());
        values.put(DBContract.DeliveryTime.IS_AVAILABLE, setter.isAvailable() ? "yes": "no");

        if (!listKeys.contains(setter.getKey()))
            db.insert(DBContract.DeliveryTime.TABLE_NAME, null, values);
        else
            db.update(DBContract.DeliveryTime.TABLE_NAME, values, DBContract.DeliveryTime.KEY + "='" + setter.getKey() + "'", null);

        listKeys = dbOperations.getDeliveryTimeKeys(db);

        return null;
    }

}
