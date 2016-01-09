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

import de.upb.hip.mobile.activities.BaseActivity;
import de.upb.hip.mobile.activities.MainActivity;
import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.activities.RouteActivity;

/**
 * Created by Christian on 09.01.2016.
 */
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
        return mActivitys.length;
    }

    @Override
    public Object getItem(int position) {
        return mActivitys[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
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

                Intent intent = new Intent(mContext, mActivitys[v.getId()]);
                mDrawerLayout.closeDrawer(mNavigationDrawerList);
                mContext.startActivity(intent);

            }
        });

        return convertView;
    }

    private Integer[] mIcones =
            {
                    R.drawable.ic_action_filter,
                    R.drawable.ic_action_filter
            };

    private Class[] mActivitys =
            {
                    MainActivity.class,
                    RouteActivity.class
            };

    private String[] mDescriptions =
            {
                    "Main Activity",
                    "Route Activity"
            };
}
