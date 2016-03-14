package de.upb.hip.mobile.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.upb.hip.mobile.activities.R;
import de.upb.hip.mobile.models.Route;
import de.upb.hip.mobile.models.RouteSet;
import de.upb.hip.mobile.models.RouteTag;

/**
 * Adapter for the Recycler View in the RouteActivity
 */
public class RouteRecyclerAdapter
        extends RecyclerView.Adapter<RouteRecyclerAdapter.ViewHolder> implements Filterable {

    private final Set<String> mActiveTags;
    private RouteSet mRouteSet;
    private Context mContext;
    private List<RouteSelectedListener> mRouteSelectedListeners = new LinkedList<>();

    /**
     * Constructor for the adapter
     *
     * @param routeSet   RouteSet
     * @param context    Context Activity
     * @param activeTags activeTags
     */
    public RouteRecyclerAdapter(RouteSet routeSet, Context context, Set<String> activeTags) {
        this.mRouteSet = routeSet;
        this.mContext = context;
        this.mActiveTags = activeTags;
    }

    /**
     * Create a new Filter and return it
     *
     * @return
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };
    }

    /**
     * Create new views (invoked by the layout manager)
     *
     * @param parent   ViewGroup
     * @param viewType
     * @return
     */
    @Override
    public RouteRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_route_row_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     *
     * @param holder   View which is replaced
     * @param position Position in the dataset (routeSet)
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Route route = getFilteredRoutes().getRouteByPosition(position);
        holder.mTitle.setText(route.getTitle());

        String description;
        description = route.getDescription();
        holder.mDescription.setText(description);

        int durationInMinutes = route.getDuration() / 60;
        holder.mDuration
                .setText(mContext.getResources()
                                .getQuantityString(
                                        R.plurals.route_activity_duration_minutes,
                                        durationInMinutes,
                                        durationInMinutes)
                );

        holder.mDistance.setText(String.format(mContext.getResources().
                getString(R.string.route_activity_distance_kilometer), route.getDistance()));

        // Check if there are actually tags for this route
        if (route.getTags() != null) {
            holder.mTagsLayout.removeAllViews();
            for (RouteTag tag : route.getTags()) {
                ImageView tagImageView = new ImageView(mContext);
                tagImageView.setImageDrawable(tag.getImage(route.getId(), mContext));
                holder.mTagsLayout.addView(tagImageView);
            }
        }

        Attachment att = DBAdapter.getAttachment(route.getId(), route.getImageName());
        try {
            Bitmap b = BitmapFactory.decodeStream(att.getContent());
            Drawable image = new BitmapDrawable(mContext.getResources(), b);
            holder.mImage.setImageDrawable(image);
        } catch (CouchbaseLiteException e) {
            Log.e("routes", e.toString());
        }
        holder.mView.setId(route.getId());
    }

    /**
     * Return the size of the dataset (invoked by the layout manager)
     *
     * @return size of the dataset
     */
    @Override
    public int getItemCount() {
        return getFilteredRoutes().getSize();
    }

    /**
     * Filters rouutes for activeTags
     *
     * @return routes which contains activeTags
     */
    public RouteSet getFilteredRoutes() {
        List<Route> result = new LinkedList<>();

        routeLoop:
        for (Route route : this.mRouteSet.routes) {
            for (RouteTag tag : route.getTags()) {
                if (mActiveTags.contains(tag.getTag())) {
                    result.add(route);
                    continue routeLoop;
                }
            }
        }

        RouteSet set = new RouteSet();
        set.setRoutes(result);

        return set;
    }

    /**
     * Register the listener for routeSelection
     *
     * @param listener
     */
    public void registerRouteSelectedListener(RouteSelectedListener listener) {
        mRouteSelectedListeners.add(listener);
    }

    /**
     * Notifys listeners when a route was selected
     *
     * @param route
     */
    private void notifyRouteSelectedListeners(Route route) {
        for (RouteSelectedListener listener : mRouteSelectedListeners) {
            listener.onRouteSelected(route);
        }
    }

    /**
     * Interface for the routeSelectedListener
     */
    public interface RouteSelectedListener {
        void onRouteSelected(Route route);
    }

    /**
     * Provide a reference to the views for each data item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ImageView mImage;
        public TextView mTitle;
        public TextView mDescription;
        public TextView mDuration;
        public TextView mDistance;
        public LinearLayout mTagsLayout;

        public ViewHolder(View v) {
            super(v);
            this.mView = v;
            this.mImage = (ImageView) v.findViewById(R.id.routeRowItemImage);
            this.mTitle = (TextView) v.findViewById(R.id.routeRowItemTitle);
            this.mDescription = (TextView) v.findViewById(R.id.routeRowItemDescription);
            this.mDuration = (TextView) v.findViewById(R.id.routeRowItemDuration);
            this.mDistance = (TextView) v.findViewById(R.id.routeRowItemDistance);
            this.mTagsLayout = (LinearLayout) v.findViewById(R.id.routeRowItemTagsLayout);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyRouteSelectedListeners(mRouteSet.getRouteById(v.getId()));
                }
            });
        }
    }
}