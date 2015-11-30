package de.upb.hip.mobile.activities;

import de.upb.hip.mobile.activities.*;
import de.upb.hip.mobile.adapters.*;
import de.upb.hip.mobile.helpers.*;
import de.upb.hip.mobile.listeners.*;
import de.upb.hip.mobile.models.*;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timo on 26.01.2015.
 */
public class FilterActivity extends Activity {


    // Recycler View: Filter
    private RecyclerView mFilterRecyclerView;
    private RecyclerView.Adapter mFilterAdapter;
    private RecyclerView.LayoutManager mFilterLayoutManager;

    private ExhibitSet exhibitSet;
    private List<String> activeFilter = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        mFilterRecyclerView = (RecyclerView) findViewById(R.id.filter_recycler_view);
        mFilterRecyclerView.setHasFixedSize(true);
        mFilterLayoutManager = new LinearLayoutManager(this);
        mFilterRecyclerView.setLayoutManager(mFilterLayoutManager);
        List<String> categories = new ArrayList<>();
        categories.add("Test");
        for(String item: categories) this.activeFilter.add(item);
        mFilterAdapter = new FilterRecyclerAdapter(categories, this.activeFilter);
        mFilterRecyclerView.setAdapter(mFilterAdapter);
//        mFilterRecyclerView.addOnItemTouchListener(new FilterRecyclerClickListener(this));
    }

    public void activityBack(View view) {
        this.finish();
    }
}
