package com.kmab.tcc.food.orders;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import java.util.List;

public class Cart extends AppCompatActivity {

    Context context;
    XConversions xConversions;
    TextView tvOrderItems, tvOrderAmount;
    RecyclerView recyclerViewMeals;
    FloatingActionButton fabSave;

    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        xConversions = new XConversions(context);
        fabSave = findViewById(R.id.fabSave);
        tvOrderItems = findViewById(R.id.tvOrderItems);
        tvOrderAmount = findViewById(R.id.tvOrderAmount);
        recyclerViewMeals = findViewById(R.id.recyclerViewMeals);

        RecyclerView[] recyclerViewsMeal = {recyclerViewMeals};
        xConversions.initializeRecyclerviewLayouts(recyclerViewsMeal, LinearLayoutManager.VERTICAL);

        xConversions.calculateOrderAmount(MainActivity.listMealsOrdered, tvOrderItems, tvOrderAmount);
        adapter = new Adapter(xConversions.getUniqueList(MainActivity.listMealsOrdered));
        recyclerViewMeals.setAdapter(adapter);

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DeliveryArea.class));
            }
        });
    }

    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private List listAdapter;

        public Adapter(List listAdapter) {
            this.listAdapter = listAdapter;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            int layoutIdForListItem = R.layout.row_cart;
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

            public Holder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvName);
                ivImage = itemView.findViewById(R.id.ivImage);
                ivRemove = itemView.findViewById(R.id.ivRemove);
                tvCount = itemView.findViewById(R.id.tvCount);
                ivAdd = itemView.findViewById(R.id.ivAdd);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvSize = itemView.findViewById(R.id.tvSize);
            }

            void bind(final SetterMeals setter) {
                tvName.setText(setter.getName());
                tvSize.setText(setter.getSize());
                tvPrice.setText(xConversions.getFullPrice(setter.getPrice()));
                xConversions.insertGlideImage(setter.getLink(), ivImage);
                xConversions.isOrderAvailable(MainActivity.listMealsOrdered, setter, ivRemove, tvCount, null);


                ivRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        xConversions.removeOrder(adapter, setter, MainActivity.listMealsOrdered);
                        xConversions.calculateOrderAmount(MainActivity.listMealsOrdered, tvOrderItems, tvOrderAmount);
                        xConversions.isOrderAvailable(MainActivity.listMealsOrdered, setter, ivRemove, tvCount, null);
                    }
                });
                ivAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.listMealsOrdered.add(setter);
                        adapter.notifyDataSetChanged();
                        xConversions.calculateOrderAmount(MainActivity.listMealsOrdered, tvOrderItems, tvOrderAmount);
                        xConversions.isOrderAvailable(MainActivity.listMealsOrdered, setter, ivRemove, tvCount, null);
                    }
                });
            }
        }

    }

}
