package com.kmab.tcc.food.orders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    CardView cardOrders;
    XConversions xConversions;
    RecyclerView recyclerView, recyclerViewMeals;
    TextView tvOrderItems, tvOrderAmount;
    ProgressBar progressBar;
    CardView cardCart;

    DBOperations dbOperations;
    SQLiteDatabase db;
    AdapterCategories adapterCategories;
    List listKeys, listKeysMeals;
    static String selectedCategory = "";
    Adapter adapter;

    static List listMealsOrdered;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        prefs = getSharedPreferences(AppInfo.USER_INFO, Context.MODE_PRIVATE);
        xConversions = new XConversions(context);
        dbOperations = new DBOperations(context);
        db = dbOperations.getWritableDatabase();
        listKeys = dbOperations.getCategoryKeys(db);
        listKeysMeals = dbOperations.getMealKeys(db);

        if (listMealsOrdered == null)
            listMealsOrdered = new ArrayList();
        if (selectedCategory.equals(""))
            selectedCategory = dbOperations.getFirstCategory(db);

        progressBar = findViewById(R.id.progressBar);
        cardOrders = findViewById(R.id.cardOrders);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewMeals = findViewById(R.id.recyclerViewMeals);
        tvOrderItems = findViewById(R.id.tvOrderItems);
        tvOrderAmount = findViewById(R.id.tvOrderAmount);
        cardCart = findViewById(R.id.cardCart);

        RecyclerView[] recyclerViews = {recyclerView};
        xConversions.initializeRecyclerviewLayouts(recyclerViews, LinearLayoutManager.HORIZONTAL);
        RecyclerView[] recyclerViewsMeal = {recyclerViewMeals};
        xConversions.initializeRecyclerviewLayouts(recyclerViewsMeal, LinearLayoutManager.VERTICAL);

        cardOrders.setOnClickListener(this);
        cardCart.setOnClickListener(this);
        loadCategories();

        new BackTaskCat().execute();
        new BackTask().execute();


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                        loadCategories();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

        if (listMealsOrdered != null)
            xConversions.calculateOrderAmount(listMealsOrdered, tvOrderItems, tvOrderAmount);

        getVitals();
    }

    private void getVitals() {
        FirebaseDatabase.getInstance().getReference()
                .child(StaticVals.childAdmin)
                .child(StaticVals.childMerchant)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                            return;

                        SetterMerchant setterMerchant = dataSnapshot.getValue(SetterMerchant.class);
                        editor = prefs.edit();
                        editor.putLong(AppInfo.TCC_MERCHANT_CODE, setterMerchant.getCode());
                        editor.putString(AppInfo.TCC_MERCHANT, setterMerchant.getName());
                        editor.apply();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child(StaticVals.childCharges)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                            return;

                        SetterCharges setterCharges = dataSnapshot.getValue(SetterCharges.class);
                        editor = prefs.edit();
                        editor.putFloat(AppInfo.FLO_SERVICE_CHARGE, (float) setterCharges.getServiceCharge());
                        editor.putFloat(AppInfo.FLO_ITEM_PROCESSING, (float) setterCharges.getItemProcessing());
                        editor.putFloat(AppInfo.FLO_LESS_THAN_40, (float) setterCharges.getLessThan40());
                        editor.putFloat(AppInfo.FLO_PERCENT_FOR_ABOVE_40, (float) setterCharges.getPercentForAbove40());
                        editor.apply();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference()
                .child(StaticVals.childCategories)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        progressBar.setVisibility(View.GONE);
                        if (!dataSnapshot.exists())
                            return;

                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            SetterCategories setter = snapshot.getValue(SetterCategories.class);

                            new InsertCategories(context, listKeys).execute(setter);
                        }

                        new BackTaskCat().execute();
                        loadData();
                        if (selectedCategory.equals(""))
                            selectedCategory = dbOperations.getFirstCategory(db);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardOrders:
                startActivity(new Intent(context, Settings.class));
                break;
            case R.id.cardCart:
                startActivity(new Intent(context, Cart.class));
                break;
        }
    }


    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference()
                .child(StaticVals.childMeals)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            progressBar.setVisibility(View.GONE);
                            return;
                        }

                        for (DataSnapshot snap: dataSnapshot.getChildren()) {
                            for (DataSnapshot snapshot: snap.getChildren()) {
                                SetterMeals setter = snapshot.getValue(SetterMeals.class);

                                new InsertMeals(context, listKeysMeals).execute(setter);
                            }
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

    private class BackTask extends AsyncTask<Void, SetterMeals, Integer> {

        List listInternal;

        @Override
        protected Integer doInBackground(Void... params) {

            Cursor cursor = dbOperations.getMeals(db, selectedCategory);
            int count = cursor.getCount();

            String key, type, name, size, description, link;
            int limit;
            double price, takeawayCharge;
            boolean isAvailable, isHomeDelivery;
            long timestamp;

            listInternal = new ArrayList();
            while (cursor.moveToNext()) {

                key = cursor.getString(cursor.getColumnIndex(DBContract.Meals.KEY));
                type = cursor.getString(cursor.getColumnIndex(DBContract.Meals.CATEGORY_KEY));
                name = cursor.getString(cursor.getColumnIndex(DBContract.Meals.NAME));
                size = cursor.getString(cursor.getColumnIndex(DBContract.Meals.SIZE));
                description = cursor.getString(cursor.getColumnIndex(DBContract.Meals.DESCRIPTION));
                link = cursor.getString(cursor.getColumnIndex(DBContract.Meals.LINK));
                limit = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBContract.Meals.LIMIT)));
                price = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBContract.Meals.PRICE)));
                takeawayCharge = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBContract.Meals.TAKEAWAY_CHARGE)));
                isAvailable = cursor.getString(cursor.getColumnIndex(DBContract.Meals.IS_AVAILABLE)).equals("yes");
                isHomeDelivery = cursor.getString(cursor.getColumnIndex(DBContract.Meals.IS_HOME_DELIVERY)).equals("yes");
                timestamp = Long.parseLong(cursor.getString(cursor.getColumnIndex(DBContract.Meals.TIMESTAMP)));

                if (!isAvailable)
                    continue;
                SetterMeals setter = new SetterMeals(key, type, name, size, description, link, limit, price,
                        takeawayCharge, isAvailable, isHomeDelivery, timestamp);

                publishProgress(setter);
            }

            return count;
        }

        @Override
        protected void onProgressUpdate(SetterMeals... values) {
            listInternal.add(values[0]);
        }

        @Override
        protected void onPostExecute(Integer count) {
            adapter = new Adapter(listInternal);
            recyclerViewMeals.setAdapter(adapter);
        }
    }

    private class BackTaskCat extends AsyncTask<Void, SetterCategories, Integer> {

        List listInternal;

        @Override
        protected Integer doInBackground(Void... params) {
            Cursor cursor = dbOperations.getCategory(db);
            int count = cursor.getCount();

            String key, name;

            listInternal = new ArrayList();
            while (cursor.moveToNext()) {

                key = cursor.getString(cursor.getColumnIndex(DBContract.Categories.KEY));
                name = cursor.getString(cursor.getColumnIndex(DBContract.Categories.NAME));

                SetterCategories setter = new SetterCategories(key, name);

                publishProgress(setter);
            }

            return count;
        }

        @Override
        protected void onProgressUpdate(SetterCategories... values) {
            listInternal.add(values[0]);
        }

        @Override
        protected void onPostExecute(Integer count) {
            if (count != 0) {
                adapterCategories = new AdapterCategories(listInternal);
                recyclerView.setAdapter(adapterCategories);
            }
        }
    }

    class AdapterCategories extends RecyclerView.Adapter<AdapterCategories.Holder> {

        private List listAdapter;

        public AdapterCategories(List listAdapter) {
            this.listAdapter = listAdapter;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            int layoutIdForListItem = R.layout.row_categories_2;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
            Holder holder = new Holder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.bind((SetterCategories) listAdapter.get(position));
        }

        @Override
        public int getItemCount() {
            return listAdapter.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            TextView tvName;

            public Holder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvName);
            }

            void bind(final SetterCategories setter) {
                tvName.setText(setter.getName());

                if (selectedCategory.equals("")) {
                    if (getAdapterPosition() == 0)
                        xConversions.setTextColor(tvName, R.color.green, true);
                    else
                        xConversions.setTextColor(tvName, R.color.black, false);
                } else {
                    if (setter.getKey().equals(selectedCategory))
                        xConversions.setTextColor(tvName, R.color.green, true);
                    else
                        xConversions.setTextColor(tvName, R.color.black, false);
                }

                tvName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selectedCategory = setter.getKey();
                        new BackTask().execute();
                        new BackTaskCat().execute();
                    }
                });
            }
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
            int layoutIdForListItem = R.layout.row_meals;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
            Holder holder = new Holder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.bind((SetterMeals) listAdapter.get(position));
        }

        @Override
        public int getItemCount() {
            return listAdapter.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            TextView tvName, tvCount, tvPrice, tvSize;
            ImageView ivImage, ivRemove, ivAdd;
            CardView cardAdd;

            public Holder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvName);
                ivImage = itemView.findViewById(R.id.ivImage);
                ivRemove = itemView.findViewById(R.id.ivRemove);
                tvCount = itemView.findViewById(R.id.tvCount);
                ivAdd = itemView.findViewById(R.id.ivAdd);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                cardAdd = itemView.findViewById(R.id.cardAdd);
                tvSize = itemView.findViewById(R.id.tvSize);
            }

            void bind(final SetterMeals setter) {
                tvName.setText(setter.getName());
                tvSize.setText(setter.getSize());
                tvPrice.setText(xConversions.getFullPrice(setter.getPrice()));
                xConversions.insertGlideImage(setter.getLink(), ivImage);
                boolean isOrdered = xConversions.isOrderAvailable(listMealsOrdered, setter, ivRemove, tvCount, cardAdd);


                ivRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        xConversions.removeOrder(adapter, setter, listMealsOrdered);
                        xConversions.calculateOrderAmount(listMealsOrdered, tvOrderItems, tvOrderAmount);
                        xConversions.isOrderAvailable(listMealsOrdered, setter, ivRemove, tvCount, cardAdd);
                    }
                });
                ivAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listMealsOrdered.add(setter);
                        adapter.notifyDataSetChanged();
                        xConversions.calculateOrderAmount(listMealsOrdered, tvOrderItems, tvOrderAmount);
                        xConversions.isOrderAvailable(listMealsOrdered, setter, ivRemove, tvCount, cardAdd);
                    }
                });
                cardAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listMealsOrdered.add(setter);
                        adapter.notifyDataSetChanged();
                        xConversions.calculateOrderAmount(listMealsOrdered, tvOrderItems, tvOrderAmount);
                        xConversions.isOrderAvailable(listMealsOrdered, setter, ivRemove, tvCount, cardAdd);
                    }
                });
            }
        }

    }
}
