package com.kmab.tcc.food.orders;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddCategories extends AppCompatActivity {

    Context context;
    XConversions xConversions;
    DBOperations dbOperations;
    SQLiteDatabase db;
    Adapter adapter;

    ProgressBar progressBar;
    FloatingActionButton fabSave;
    EditText etName;

    List listKeys;
    RecyclerView recyclerView, recyclerViewArrange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        xConversions = new XConversions(context);
        dbOperations = new DBOperations(context);
        db = dbOperations.getWritableDatabase();
        listKeys = dbOperations.getCategoryKeys(db);

        etName = findViewById(R.id.etName);
        fabSave = findViewById(R.id.fabSave);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewArrange = findViewById(R.id.recyclerViewArrange);

        RecyclerView[] recyclerViews = {recyclerView};
        xConversions.initializeRecyclerviewLayouts(recyclerViews, LinearLayoutManager.VERTICAL);

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();

            }
        });
        loadData();
        new BackTask().execute();
    }

    private void loadData() {
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

                        new BackTask().execute();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void save() {
        String name = etName.getText().toString().trim();
        if (name.equals(""))
            xConversions.showToast("Enter type name!!", false);
        else {
            progressBar.setVisibility(View.VISIBLE);
            String key = FirebaseDatabase.getInstance().getReference().child(StaticVals.childCategories).push().getKey();
            SetterCategories setterCategories = new SetterCategories(key, name);
            FirebaseDatabase.getInstance().getReference()
                    .child(StaticVals.childCategories)
                    .child(key)
                    .setValue(setterCategories)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                xConversions.showToast("Saved", true);
                                progressBar.setVisibility(View.GONE);
                                etName.setText("");

                                new BackTask().execute();
                            }
                        }
                    });
        }
    }

    private class BackTask extends AsyncTask<Void, SetterCategories, Integer> {

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
                adapter = new Adapter(listInternal);
                recyclerView.setAdapter(adapter);
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
            int layoutIdForListItem = R.layout.row_categories;
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

            void bind(SetterCategories setter) {
                tvName.setText(setter.getName());
            }
        }

    }

}
