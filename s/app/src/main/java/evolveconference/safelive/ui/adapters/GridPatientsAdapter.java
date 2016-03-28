package evolveconference.safelive.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.ui.activities.MainActivity;
import evolveconference.safelive.ui.fragments.DetailPatientFragment;
import evolveconference.safelive.ui.fragments.PatientHomepageFragment;

/**
 * Adapter fot Patients screen
 */
public class GridPatientsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_PATIENT_CARD = TYPE_HEADER + 1;

    private Map<String, List<Item>> mHashMap;
    private List<AdapterItem> mItems;
    private Context mContext;
    private Picasso mImageLoader;

    public GridPatientsAdapter(Map<String, List<Item>> hashMap, Context context) {
        this.mHashMap = hashMap;
        this.mContext = context;
        mImageLoader = Picasso.with(context);
        initItemsArray();
    }

    /**
     * Updates data in adapter
     *
     * @param hashMap
     */
    public void swapData(Map<String, List<Item>> hashMap) {
        this.mHashMap = hashMap;
        initItemsArray();
        notifyDataSetChanged();
    }

    private static final class AdapterItem {

        private Object mValue;
        private int mType;

        public AdapterItem(Object mValue, int mType) {
            this.mValue = mValue;
            this.mType = mType;
        }

        public Object getValue() {
            return mValue;
        }

        public int getType() {
            return mType;
        }

    }

    private void initItemsArray() {

        if (mItems == null) {
            mItems = new ArrayList<>();
        } else {
            mItems.clear();
        }

        for (String key : mHashMap.keySet()) {

            mItems.add(new AdapterItem(key, TYPE_HEADER));
            List<Item> values = mHashMap.get(key);

            for (Item value : values) {

                mItems.add(new AdapterItem(value, TYPE_PATIENT_CARD));
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view;
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (type) {
            case TYPE_HEADER:
                view = inflater.inflate(R.layout.patients_header_layout, parent, false);
                return new ListHeaderViewHolder(view);
            case TYPE_PATIENT_CARD:
                view = inflater.inflate(R.layout.patient_card_view, parent, false);
                return new PatientCardViewHolder(view);
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //final Item item = chart.get(position);
        final int itemtype = mItems.get(position).getType();
        switch (itemtype) {
            case TYPE_HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                itemController.mHeaderTitle.setText((String) mItems.get(position).getValue());
                break;
            case TYPE_PATIENT_CARD:
                Item item = (Item) mItems.get(position).getValue();
                final PatientCardViewHolder patient = (PatientCardViewHolder) holder;
                patient.mName.setText(mContext.getString(R.string.first_and_last_names, item.firstName, item.lastName));
                if (!TextUtils.isEmpty(item.avatar)) {
                    mImageLoader.load(item.avatar)
                            .resizeDimen(R.dimen.picture_size, R.dimen.picture_size)
                            .centerCrop()
                            .placeholder(R.drawable.user_empty_placeholder)
                            .into(patient.mAvatar);
                }
                patient.mAlertCount.setVisibility(item.alertCount > 0 ? View.VISIBLE : View.GONE);
                patient.mAlertCount.setText(String.valueOf(item.alertCount));
                patient.mWarningCount.setVisibility(item.warningCount > 0 ? View.VISIBLE : View.GONE);
                patient.mWarningCount.setText(String.valueOf(item.warningCount));
                break;

        }
    }


    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.header_text)
        TextView mHeaderTitle;

        public ListHeaderViewHolder(View headerView) {
            super(headerView);
            ButterKnife.bind(this, headerView);
        }
    }

    class PatientCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.patient_name)
        TextView mName;
        @Bind(R.id.patient_avatar)
        ImageView mAvatar;
        @Bind(R.id.patient_alert_count)
        TextView mAlertCount;
        @Bind(R.id.patient_warning_count)
        TextView mWarningCount;

        public PatientCardViewHolder(View profileView) {
            super(profileView);
            ButterKnife.bind(this, profileView);
            mAvatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Item item = (Item) mItems.get(getAdapterPosition()).getValue();
            ((MainActivity) mContext).showFragment(PatientHomepageFragment.newInstance(item.id), true);
        }
    }

    public static class Item {
        private int id;
        public String firstName;
        public String lastName;
        public String avatar;
        public int alertCount;
        public int warningCount;

        public Item(int id, String firstName, String lastName, String avatar, int alertCount, int warningCount) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.avatar = avatar;

            this.alertCount = alertCount;
            this.warningCount = warningCount;
        }

    }
}
