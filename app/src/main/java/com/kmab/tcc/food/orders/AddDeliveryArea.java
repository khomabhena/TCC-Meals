package com.kmab.tcc.food.orders;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

public class AddDeliveryArea extends AppCompatActivity {

    Context context;
    XConversions xConversions;
    FloatingActionButton fabDone;
    EditText etArea, etDesc, etIsClub, etCharge;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_area);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        xConversions = new XConversions(context);
        etArea = findViewById(R.id.etArea);
        etDesc = findViewById(R.id.etDesc);
        etIsClub = findViewById(R.id.etIsClub);
        etCharge = findViewById(R.id.etCharge);
        fabDone = findViewById(R.id.fabDone);
        progressBar = findViewById(R.id.progressBar);

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String area = etArea.getText().toString().trim();
                String desc = etDesc.getText().toString().trim();
                String isClub = etIsClub.getText().toString().trim();
                String charge = etCharge.getText().toString().trim();

                if (area.equals("")) xConversions.showToast("Specify area", true);
                else if (desc.equals("")) xConversions.showToast("Specify description", true);
                else if (isClub.equals("")) xConversions.showToast("Is it within the country club area", true);
                else if (charge.equals("")) xConversions.showToast("Specify extra charge", true);
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    String key = FirebaseDatabase.getInstance().getReference().child(StaticVals.childDeliveryArea).push().getKey();
                    SetterDeliveryArea setter = new SetterDeliveryArea(key, area, desc,
                            Double.parseDouble(charge), true, isClub.equals("yes"));

                    FirebaseDatabase.getInstance().getReference()
                            .child(StaticVals.childDeliveryArea)
                            .child(key)
                            .setValue(setter)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        etArea.setText("");
                                        etDesc.setText("");
                                        etIsClub.setText("");
                                        etCharge.setText("");

                                        xConversions.showToast("Saved", true);
                                    }
                                }
                            });
                }
            }
        });
    }


}
