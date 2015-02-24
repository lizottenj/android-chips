package com.android.ex.chips;

import com.android.ex.chips.Queries.Query;
import com.android.ex.chips.transform.CircleTransform;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A class that inflates and binds the views in the dropdown list from
 * RecipientEditTextView.
 */
public class DropdownChipLayouter {
    /**
     * The type of adapter that is requesting a chip layout.
     */
    public enum AdapterType {
        BASE_RECIPIENT,
        RECIPIENT_ALTERNATES,
        SINGLE_RECIPIENT
    }

    private final LayoutInflater mInflater;
    private final Context mContext;
    private Query mQuery;

    public DropdownChipLayouter(LayoutInflater inflater, Context context) {
        mInflater = inflater;
        mContext = context;
    }

    public void setQuery(Query query) {
        mQuery = query;
    }


    /**
     * Layouts and binds recipient information to the view. If convertView is null, inflates a new
     * view with getItemLaytout().
     *
     * @param convertView The view to bind information to.
     * @param parent The parent to bind the view to if we inflate a new view.
     * @param entry The recipient entry to get information from.
     * @param position The position in the list.
     * @param type The adapter type that is requesting the bind.
     * @param constraint The constraint typed in the auto complete view.
     *
     * @return A view ready to be shown in the drop down list.
     */
    public View bindView(View convertView, ViewGroup parent, RecipientEntry entry, int position,
            AdapterType type, String constraint) {
        return bindView(convertView, parent, entry, position, type, constraint, false);
    }

    /**
     * Layouts and binds recipient information to the view. If convertView is null, inflates a new
     * view with getItemLaytout().
     *
     * @param convertView The view to bind information to.
     * @param parent The parent to bind the view to if we inflate a new view.
     * @param entry The recipient entry to get information from.
     * @param position The position in the list.
     * @param type The adapter type that is requesting the bind.
     * @param constraint The constraint typed in the auto complete view.
     *
     * @param sectionDivider
     * @return A view ready to be shown in the drop down list.
     */
    public View bindView(View convertView, ViewGroup parent, RecipientEntry entry, int position,
            AdapterType type, String constraint, boolean sectionDivider) {
        // Default to show all the information
        String displayName = entry.getDisplayName();
        boolean showImage = true;

        final View itemView = reuseOrInflateView(convertView, parent, type);

        final ViewHolder viewHolder = new ViewHolder(itemView);
        bindTextToView(displayName, viewHolder.displayNameView);

        if(sectionDivider) {
            if(entry.isWalleUser()) {
                bindTextToView("Walle Friends", viewHolder.sectionDivider);
            } else {
                bindTextToView("Other Friends", viewHolder.sectionDivider);
            }
        } else {
            bindTextToView("", viewHolder.sectionDivider);
        }

        if(showImage)
        {
            Picasso.with(parent.getContext()).load(entry.getPhotoThumbnailUri()).error(R.drawable.ic_contact_picture).transform(
                    new CircleTransform()).into(viewHolder.imageView);
        }

        return itemView;
    }

    /**
     * Returns a new view with {@link #getItemLayoutResId()}.
     */
    public View newView() {
        return mInflater.inflate(getItemLayoutResId(), null);
    }

    /**
     * Returns the same view, or inflates a new one if the given view was null.
     */
    protected View reuseOrInflateView(View convertView, ViewGroup parent, AdapterType type) {
        int itemLayout = getItemLayoutResId();
        switch (type) {
            case BASE_RECIPIENT:
            case RECIPIENT_ALTERNATES:
                break;
            case SINGLE_RECIPIENT:
                itemLayout = getAlternateItemLayoutResId();
                break;
        }
        return convertView != null ? convertView : mInflater.inflate(itemLayout, parent, false);
    }

    /**
     * Binds the text to the given text view. If the text was null, hides the text view.
     */
    protected void bindTextToView(CharSequence text, TextView view) {
        if (view == null) {
            return;
        }

        if (text != null && text.length() > 0) {
            view.setText(text);
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    protected CharSequence getDestinationType(RecipientEntry entry) {
        return mQuery.getTypeLabel(mContext.getResources(), entry.getDestinationType(),
            entry.getDestinationLabel()).toString().toUpperCase();
    }

    /**
     * Returns a layout id for each item inside auto-complete list.
     *
     * Each View must contain two TextViews (for display name and destination) and one ImageView
     * (for photo). Ids for those should be available via {@link #getDisplayNameResId()},
     * {@link #getDestinationResId()}, and {@link #getPhotoResId()}.
     */
    protected int getItemLayoutResId() {
        return R.layout.chips_recipient_dropdown_item;
    }

    /**
     * Returns a layout id for each item inside alternate auto-complete list.
     *
     * Each View must contain two TextViews (for display name and destination) and one ImageView
     * (for photo). Ids for those should be available via {@link #getDisplayNameResId()},
     * {@link #getDestinationResId()}, and {@link #getPhotoResId()}.
     */
    protected int getAlternateItemLayoutResId() {
        return R.layout.chips_alternate_item;
    }

    /**
     * Returns a resource ID representing an image which should be shown when ther's no relevant
     * photo is available.
     */
    protected int getDefaultPhotoResId() {
        return R.drawable.ic_contact_picture;
    }

    /**
     * Returns an id for TextView in an item View for showing a display name. By default
     * {@link android.R.id#title} is returned.
     */
    protected int getDisplayNameResId() {
        return android.R.id.title;
    }

    /**
     * Returns an id for ImageView in an item View for showing photo image for a person. In default
     * {@link android.R.id#icon} is returned.
     */
    protected int getPhotoResId() {
        return android.R.id.icon;
    }

    /**
     * A holder class the view. Uses the getters in DropdownChipLayouter to find the id of the
     * corresponding views.
     */
    protected class ViewHolder {
        public final TextView displayNameView;
        public final TextView entrySource;
        public final ImageView imageView;
        public final TextView sectionDivider;

        public ViewHolder(View view) {
            displayNameView = (TextView) view.findViewById(getDisplayNameResId());
            imageView = (ImageView) view.findViewById(getPhotoResId());
            entrySource = (TextView) view.findViewById(R.id.source);
            sectionDivider = (TextView) view.findViewById(R.id.typeLabel);
        }
    }
}
