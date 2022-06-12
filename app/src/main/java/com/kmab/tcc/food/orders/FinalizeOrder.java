package com.kmab.tcc.food.orders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FinalizeOrder extends AppCompatActivity {

    Context context;
    XConversions xConversions;
    TextView tvSubTotal, tvServiceCharge, tvTotalAmount, tvPay2;
    EditText etName, etSurname, etAddress, etNotes, etMessage;
    ProgressBar progressBar;
    CardView cardOrder;

    SetterOrders setterOrders = DeliveryArea.setterOrders;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    double itemProcessing = 0.01;
    static boolean isStillSearching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalize_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        xConversions = new XConversions(context);
        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvServiceCharge = findViewById(R.id.tvServiceCharge);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvPay2 = findViewById(R.id.tvPay2);
        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etAddress = findViewById(R.id.etAddress);
        etNotes = findViewById(R.id.etNotes);
        etMessage = findViewById(R.id.etMessage);
        progressBar = findViewById(R.id.progressBar);
        cardOrder = findViewById(R.id.cardOrder);

        double serviceCharge = 0;
        itemProcessing =  prefs.getFloat(AppInfo.FLO_ITEM_PROCESSING, (float) 0.01);
        if (setterOrders.getOrderCharge() < 5)
            serviceCharge = setterOrders.getOrderCharge() * 0.01;
        else if (setterOrders.getOrderCharge() < 40)
            serviceCharge = prefs.getFloat(AppInfo.FLO_LESS_THAN_40, (float) 0.5);
        else
            serviceCharge = setterOrders.getOrderCharge() * prefs.getFloat(AppInfo.FLO_PERCENT_FOR_ABOVE_40, (float) 0.05);

        final double total = serviceCharge + setterOrders.getOrderCharge() + setterOrders.getDeliveryCharge()
                + setterOrders.getTakeawayCharge() + MainActivity.listMealsOrdered.size() * itemProcessing;

        double subTotal = setterOrders.getOrderCharge() + setterOrders.getDeliveryCharge() + xConversions.getTakeAwayCharge();
        double service = serviceCharge + (MainActivity.listMealsOrdered.size() * itemProcessing);
        tvSubTotal.setText(xConversions.getFullPrice(subTotal));
        tvServiceCharge.setText(xConversions.getFullPrice(service));
        tvTotalAmount.setText(xConversions.getFullPrice(total));
        String pay = "Pay " + xConversions.getFullPrice(total);
        tvPay2.setText(pay);
        putDeliveryDetails();

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isStillSearching)
                    return;
                String message = etMessage.getText().toString();
                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (message.equals(""))
                    return;

                String merchantName = prefs.getString(AppInfo.TCC_MERCHANT, "TRIANGLE COUNTRY CLUB FRONT OFFICE");
                String code = String.valueOf(prefs.getLong(AppInfo.TCC_MERCHANT_CODE, 136986));
                String amount = String.valueOf(xConversions.getPrice(total));

                if (message.contains("New wallet")) {
                    message = message.substring(0, message.indexOf("New wallet")).trim();
                    etMessage.setText(message);
                } else {
                    assert merchantName != null;
                    if (message.contains(merchantName) && message.contains(code) && message.contains(amount)) {
                        progressBar.setVisibility(View.VISIBLE);
                        isStillSearching = true;

                        final String finalMessage = message;
                        FirebaseDatabase.getInstance().getReference()
                                .child(StaticVals.childOrders)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        boolean isMatchFound = false;
                                        if (dataSnapshot.exists())
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                SetterOrders setterOrders = snapshot.getValue(SetterOrders.class);

                                                if (uid.equals(setterOrders.getUid()) && finalMessage.equals(setterOrders.getConfirmationCode())) {
                                                    xConversions.showToast("You have already used this code!!", true);
                                                    isMatchFound = true;
                                                    break;
                                                }
                                                if (finalMessage.equals(setterOrders.getConfirmationCode())) {
                                                    isMatchFound = true;
                                                    xConversions.showToast("This code has already been used!!", true);
                                                    break;
                                                }
                                            }

                                        if (!isMatchFound) {
                                            String name = etName.getText().toString().trim();
                                            String surname = etSurname.getText().toString().trim();
                                            String address = etAddress.getText().toString();
                                            String notes = etNotes.getText().toString().trim();

                                            DeliveryArea.setterOrders.setName(name);
                                            DeliveryArea.setterOrders.setSurname(surname);
                                            DeliveryArea.setterOrders.setAddress(address);
                                            DeliveryArea.setterOrders.setNotes(notes);

                                            xConversions.showToast("You can now place your order", true);
                                            cardOrder.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.GONE);
                                        }
                                        isStillSearching = false;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    } else {
                        //etMessage.setText("");
                        xConversions.showToast("Incorrect message", true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void putDeliveryDetails() {
        etName.setText(prefs.getString(AppInfo.NAME, ""));
        etSurname.setText(prefs.getString(AppInfo.SURNAME, ""));
        etAddress.setText(prefs.getString(AppInfo.ADDRESS, ""));
        //etNotes.setText(prefs.getString(AppInfo.NOTES, ""));
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor = prefs.edit();
        editor.putString(AppInfo.NAME, etName.getText().toString());
        editor.putString(AppInfo.SURNAME, etSurname.getText().toString());
        editor.putString(AppInfo.ADDRESS, etAddress.getText().toString());
        editor.putString(AppInfo.NOTES, etNotes.getText().toString());
        editor.apply();
    }

    public void pay(View view) {
        String code = String.valueOf(prefs.getLong(AppInfo.TCC_MERCHANT_CODE, 136986));
        xConversions.openEcocashDialer(code);
    }
}
