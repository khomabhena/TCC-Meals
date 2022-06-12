package com.kmab.tcc.food.orders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class OrderType extends AppCompatActivity {

    Context context;
    XConversions xConversions;
    FloatingActionButton fabDone;
    ImageView ivCheckEat, ivCheckTakeaway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar()  != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        xConversions = new XConversions(context);
        fabDone = findViewById(R.id.fabDone);
        ivCheckEat = findViewById(R.id.ivCheckEat);
        ivCheckTakeaway = findViewById(R.id.ivCheckTakeaway);

        ivCheckEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DeliveryArea.isClub) {
                    ivCheckEat.setImageResource(R.drawable.ic_check);
                    ivCheckTakeaway.setImageResource(R.drawable.ic_check_gray);
                    DeliveryArea.setterOrders.setTakeaway(false);

                    fabDone.setVisibility(View.VISIBLE);
                } else
                    xConversions.showToast("You selected external delivery", true);

            }
        });
        ivCheckTakeaway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivCheckTakeaway.setImageResource(R.drawable.ic_check);
                ivCheckEat.setImageResource(R.drawable.ic_check_gray);
                DeliveryArea.setterOrders.setTakeaway(true);

                fabDone.setVisibility(View.VISIBLE);
            }
        });

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DeliveryTime.class));
            }
        });
    }

}
