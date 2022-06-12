package com.kmab.tcc.food.orders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Settings extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
    }

    public void launchActivity(View view) {
        int id = view.getId();
        if (view.getId() == R.id.tvMeals)
            startActivity(new Intent(context, AddMeals.class));
        if (id == R.id.tvAddDelivery)
            startActivity(new Intent(context, AddDeliveryArea.class));
        /*if (id == R.id.tvAddDeliveryTime)
            startActivity(new Intent(context, AddDeliveryTime.class));*/
        if (id == R.id.tvAddCategories)
            startActivity(new Intent(context, AddCategories.class));
    }

}
