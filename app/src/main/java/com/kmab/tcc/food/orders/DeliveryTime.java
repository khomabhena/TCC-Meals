package com.kmab.tcc.food.orders;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeliveryTime extends AppCompatActivity {

    Context context;
    XConversions xConversions;
    FloatingActionButton fabDone;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    DBOperations dbOperations;
    SQLiteDatabase db;
    List listKeys;
    Adapter adapter;
    static String selectedArea = "";
    double charge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        xConversions = new XConversions(context);
        dbOperations = new DBOperations(context);
        db = dbOperations.getWritableDatabase();
        listKeys = dbOperations.getCategoryKeys(db);

        fabDone = findViewById(R.id.fabDone);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        RecyclerView[] recyclerViewsMeal = {recyclerView};
        xConversions.initializeRecyclerviewLayouts(recyclerViewsMeal, LinearLayoutManager.VERTICAL);

        new BackTask().execute();
        loadData();

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryArea.setterOrders.setDeliveryCharge(charge);
                startActivity(new Intent(context, FinalizeOrder.class));
            }
        });
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference()
                .child(StaticVals.childDeliveryTime)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            progressBar.setVisibility(View.GONE);
                            return;
                        }

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            SetterDeliveryTime setter = snapshot.getValue(SetterDeliveryTime.class);

                            new InsertDeliveryTime(context, listKeys).execute(setter);
                        }

                        new BackTask().execute();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //
                    }
                });
    }

    private class BackTask extends AsyncTask<Void, SetterDeliveryTime, Integer> {

        List listInternal;

        @Override
        protected Integer doInBackground(Void... params) {

            Cursor cursor = dbOperations.getDeliveryTime(db);
            int count = cursor.getCount();

            String key, name;
            int startHour, endHour, minute;
            double extraCharge;
            boolean isAvailable;

            listInternal = new ArrayList();
            while (cursor.moveToNext()) {

                key = cursor.getString(cursor.getColumnIndex(DBContract.DeliveryTime.KEY));
                name = cursor.getString(cursor.getColumnIndex(DBContract.DeliveryTime.NAME));
                startHour = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBContract.DeliveryTime.START_HOUR)));
                endHour = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBContract.DeliveryTime.END_HOUR)));
                minute = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBContract.DeliveryTime.MINUTE)));
                extraCharge = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBContract.DeliveryTime.EXTRA_CHARGE)));
                isAvailable = cursor.getString(cursor.getColumnIndex(DBContract.DeliveryTime.IS_AVAILABLE)).equals("yes");

                SetterDeliveryTime setter = new SetterDeliveryTime(key, name, startHour, endHour, minute, extraCharge, isAvailable);

                publishProgress(setter);
            }

            return count;
        }

        @Override
        protected void onProgressUpdate(SetterDeliveryTime... values) {
            listInternal.add(values[0]);
        }

        @Override
        protected void onPostExecute(Integer count) {
            adapter = new Adapter(listInternal);
            recyclerView.setAdapter(adapter);
        }
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private List listAdapter;

        public Adapter(List listAdapter) {
            this.listAdapter = listAdapter;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            int layoutIdForListItem = R.layout.row_delivery_time;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
            Holder holder = new Holder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.bind((SetterDeliveryTime) listAdapter.get(position));
        }

        @Override
        public int getItemCount() {
            return listAdapter.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            TextView tvName, tvCharge;
            ImageView ivCheck;

            public Holder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvName);
                tvCharge = itemView.findViewById(R.id.tvCharge);
                ivCheck = itemView.findViewById(R.id.ivCheck);
            }

            void bind(final SetterDeliveryTime setter) {
                tvName.setText(setter.getName());
                if (DeliveryArea.isClub)
                    tvCharge.setText(xConversions.getFullPrice(0));
                else {
                    charge = DeliveryArea.setterOrders.getDeliveryCharge() + setter.getExtraCharge();
                    tvCharge.setText(xConversions.getFullPrice(charge));
                }

                if (setter.getKey().equals(selectedArea)) {
                    ivCheck.setImageResource(R.drawable.ic_check);
                    setOrderArea(setter);
                } else
                    ivCheck.setImageResource(R.drawable.ic_check_gray);

                if (setter.isAvailable())
                    ivCheck.setVisibility(View.VISIBLE);
                else
                    ivCheck.setVisibility(View.GONE);

                ivCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setOrderArea(setter);
                        xConversions.doDeliveryTimeChecks(adapter, ivCheck, setter.getKey(), listAdapter);
                    }
                });
            }

            private void setOrderArea(SetterDeliveryTime setter) {
                fabDone.setVisibility(View.VISIBLE);
                selectedArea = setter.getKey();
                xConversions.calculateDeliveryTime(DeliveryArea.setterOrders, setter.getStartHour(), setter.getEndHour(), setter.getMinute());
            }
        }

    }

}
