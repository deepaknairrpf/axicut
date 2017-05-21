package com.example.dingu.axicut.Inward;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.dingu.axicut.LoginActivity;
import com.example.dingu.axicut.R;
import com.example.dingu.axicut.SaleOrder;
import com.example.dingu.axicut.Utils.General.MyDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class InwardMainActivity extends AppCompatActivity{

    private DatabaseReference myDBRef;
    RecyclerView saleOrderRecyclerView;
    FirebaseAuth mAuth;
    protected FloatingActionButton fab;


    ArrayList <SaleOrder> saleOrderArrayList;
    InwardAdapter inwardAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inward_main);

        mAuth = FirebaseAuth.getInstance();

        myDBRef = MyDatabase.getDatabase().getInstance().getReference("Orders");
        myDBRef.keepSynced(true);
        setupFabButton();
        saleOrderRecyclerView = (RecyclerView)findViewById(R.id.InwardRecyclerList);
        saleOrderRecyclerView.setHasFixedSize(true);
        saleOrderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null)
                {
                    Intent intent = new Intent(InwardMainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        saleOrderArrayList = new ArrayList<>();
        inwardAdapter = new InwardAdapter(saleOrderArrayList);
        saleOrderRecyclerView.setAdapter(inwardAdapter);

        myDBRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if(dataSnapshot != null && dataSnapshot.getValue() != null)
                {
                    try{
                        SaleOrder saleOrder = dataSnapshot.getValue(SaleOrder.class);
                        saleOrderArrayList.add(0,saleOrder);
                        inwardAdapter.notifyDataSetChanged();
                    }catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(), "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inward_entry_work_orders, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);

        return super.onCreateOptionsMenu(menu);


    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                inwardAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.logout:
                mAuth.getInstance().signOut();


        }

        return super.onOptionsItemSelected(item);
    }


    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {

            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
    public void setupFabButton(){
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InwardMainActivity.this,InwardAddEditSaleOrder.class);
                intent.putExtra("InwardAction",InwardAction.CREATE_NEW_SALE_ORDER);
                startActivity(intent);
            }
        });

    }

}