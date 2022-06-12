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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeliveryArea extends AppCompatActivity {

    Context context;
    XConversions xConversions;
    FloatingActionButton fabDone;
    RecyclerView recyclerView;
    ProgressBar progressBar;

    DBOperations dbOperations;
    SQLiteDatabase db;
    List listKeys;
    static SetterOrders setterOrders;
    Adapter adapter;
    static boolean isClub = true;
    static String selectedArea = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_area);
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

        initializeSetter();
        new BackTask().execute();
        loadData();

        fabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, OrderType.class));
            }
        });
    }

    private void initializeSetter() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String key = FirebaseDatabase.getInstance().getReference().child(StaticVals.childOrders).push().getKey();

        setterOrders = new SetterOrders(uid, key, xConversions.getOrderNumber(), null, null, null, null, null, null,
                0, 0, 0, 0, 0,0, 0, 0, 0, false, false, false, false, false, false);
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference()
                .child(StaticVals.childDeliveryArea)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            progressBar.setVisibility(View.GONE);
                            return;
                        }

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            SetterDeliveryArea setter = snapshot.getValue(SetterDeliveryArea.class);

                            new InsertDeliveryArea(context, listKeys).execute(setter);
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

    private class BackTask extends AsyncTask<Void, SetterDeliveryArea, Integer> {

        List listInternal;

        @Override
        protected Integer doInBackground(Void... params) {

            Cursor cursor = dbOperations.getDeliveryArea(db);
            int count = cursor.getCount();

            String key, area, description;
            double charge;
            boolean isAvailable, isClub;

            listInternal = new ArrayList();
            while (cursor.moveToNext()) {

                key = cursor.getString(cursor.getColumnIndex(DBContract.DeliveryArea.KEY));
                area = cursor.getString(cursor.getColumnIndex(DBContract.DeliveryArea.AREA));
                description = cursor.getString(cursor.getColumnIndex(DBContract.DeliveryArea.DESCRIPTION));
                charge = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBContract.DeliveryArea.CHARGE)));
                isAvailable = cursor.getString(cursor.getColumnIndex(DBContract.DeliveryArea.IS_AVAILABLE)).equals("yes");
                isClub = cursor.getString(cursor.getColumnIndex(DBContract.DeliveryArea.IS_CLUB)).equals("yes");

                SetterDeliveryArea setter = new SetterDeliveryArea(key, area, description, charge, isAvailable, isClub);

                //if (isAvailable)
                publishProgress(setter);
            }

            return count;
        }

        @Override
        protected void onProgressUpdate(SetterDeliveryArea... values) {
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
            int layoutIdForListItem = R.layout.row_delivery_areas;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
            Holder holder = new Holder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.bind((SetterDeliveryArea) listAdapter.get(position));
        }

        @Override
        public int getItemCount() {
            return listAdapter.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            TextView tvArea, tvDesc;
            ImageView ivCheck;

            public Holder(View itemView) {
                super(itemView);
                tvArea = itemView.findViewById(R.id.tvArea);
                ivCheck = itemView.findViewById(R.id.ivCheck);
                tvDesc = itemView.findViewById(R.id.tvDesc);
            }

            void bind(final SetterDeliveryArea setter) {
                tvArea.setText(setter.getArea());
                tvDesc.setText(setter.getDescription());

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
                        xConversions.doDeliveryChecks(adapter, ivCheck, setter.getKey(), listAdapter);
                    }
                });
            }

            private void setOrderArea(SetterDeliveryArea setter) {
                setterOrders.setDeliveryCharge(setter.getCharge());
                setterOrders.setOrderCharge(xConversions.getOrderCharge());
                setterOrders.setDeliveryAreaKey(setter.getKey());
                isClub = setter.isClub();
                fabDone.setVisibility(View.VISIBLE);
                selectedArea = setter.getKey();
            }
        }

    }

}
