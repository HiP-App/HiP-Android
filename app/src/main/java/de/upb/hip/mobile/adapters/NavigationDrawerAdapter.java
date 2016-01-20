package de.upb.hip.mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import de.upb.hip.mobile.activities.*;

public class NavigationDrawerAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private DrawerLayout mDrawerLayout;
    private ListView mNavigationDrawerList;

    public NavigationDrawerAdapter(Context c, DrawerLayout mDrawerLayout, ListView mNavigationDrawerList) {
        mContext = c;
        mInflater = LayoutInflater.from(c);
        this.mDrawerLayout = mDrawerLayout;
        this.mNavigationDrawerList = mNavigationDrawerList;
    }

    @Override
    public int getCount() {
        return mActivities.length;
    }

    @Override
    public Object getItem(int position) {
        return mActivities[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getId(Class activityClass){
        for (int id = 0; id < mActivities.length; id++ ){
            if(mActivities[id].equals(activityClass)){
                return id;
            }
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.navigation_drawer_list_item, null);
        TextView textView = (TextView)convertView.findViewById(R.id.navigation_drawer_item_text);
        textView.setText(mDescriptions[position]);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.navigation_drawer_item_icon);
        imageView.setImageResource(mIcones[position]);

        convertView.setId(position);
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, mActivities[v.getId()]);
                mDrawerLayout.closeDrawer(mNavigationDrawerList);

                mContext.startActivity(intent);

            }
        });

        return convertView;
    }

    private Integer[] mIcones =
            {
                    R.drawable.ic_launcher,
                    R.drawable.ic_launcher
            };

    private Class[] mActivities =
            {
                    MainActivity.class,
                    RouteActivity.class
            };

    private String[] mDescriptions =
            {
                    "Übersicht",
                    "Routen Navigation"
            };
}
