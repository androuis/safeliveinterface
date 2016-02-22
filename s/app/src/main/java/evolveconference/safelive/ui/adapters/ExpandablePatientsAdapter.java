package evolveconference.safelive.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.ui.activities.MainActivity;
import evolveconference.safelive.ui.fragments.DetailPatientFragment;
import evolveconference.safelive.ui.fragments.LocationFragment;
import evolveconference.safelive.ui.view.RoundImageView;

public class ExpandablePatientsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int PATIENT_CARD = 1;

    private List<Item> chart;
    private Activity activity;

    public ExpandablePatientsAdapter(List<Item> data, Activity activity) {
        this.chart = data;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (type) {
            case HEADER:
                view = inflater.inflate(R.layout.list_header, parent, false);
                return new ListHeaderViewHolder(view);
            case PATIENT_CARD:
                view = inflater.inflate(R.layout.patient_card_view, parent, false);
                return new PatientCardViewHolder(view);
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = chart.get(position);
        switch (item.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                itemController.refferalItem = item;
                itemController.header_title.setText(item.title);
                itemController.header_subtitle.setText(item.subtitle);
                if (item.childrens == null) {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.ic_less);
                } else {
                    itemController.btn_expand_toggle.setImageResource(R.drawable.ic_more);
                }
                itemController.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.childrens == null) {
                            item.childrens = new ArrayList<>();
                            int count = 0;
                            int pos = chart.indexOf(itemController.refferalItem);
                            while (chart.size() > pos + 1 && chart.get(pos + 1).type == PATIENT_CARD) {
                                item.childrens.add(chart.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemController.btn_expand_toggle.setImageResource(R.drawable.ic_more);
                        } else {
                            int pos = chart.indexOf(itemController.refferalItem);
                            int index = pos + 1;
                            for (Item i : item.childrens) {
                                chart.add(index, i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
                            itemController.btn_expand_toggle.setImageResource(R.drawable.ic_less);
                            item.childrens = null;
                        }
                    }
                });
                break;
            case PATIENT_CARD:
                final PatientCardViewHolder patient = (PatientCardViewHolder) holder;
                patient.name.setText(item.title);
                patient.avatar.setImageResource(item.avatar);
                patient.avatar.setCircleColor(item.colorRisk);
                patient.alertLeft.setVisibility(item.leftWarningEnabled ? View.VISIBLE : View.GONE);
                patient.alertRight.setVisibility(item.rightWarningEnabled ? View.VISIBLE : View.GONE);
                break;

        }
    }


    @Override
    public int getItemViewType(int position) {
        return chart.get(position).type;
    }

    @Override
    public int getItemCount() {
        return chart.size();
    }

    class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.header_text) TextView header_title;
        @Bind(R.id.header_subtext) TextView header_subtitle;
        @Bind(R.id.btn_expand_toggle) ImageView btn_expand_toggle;
        public Item refferalItem;

        public ListHeaderViewHolder(View headerView) {
            super(headerView);
            ButterKnife.bind(this, headerView);
        }
    }

    class PatientCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.patient_name) TextView name;
        @Bind(R.id.patient_avatar) RoundImageView avatar;
        @Bind(R.id.patient_warning_left) ImageView alertLeft;
        @Bind(R.id.patient_warning_right) ImageView alertRight;

        public PatientCardViewHolder(View profileView) {
            super(profileView);
            ButterKnife.bind(this, profileView);
            avatar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ((MainActivity) activity).showFragment(LocationFragment.newInstance(1));
        }
    }

    public static class Item {
        private int id;
        public int type;
        public String title;
        public String subtitle;
        public List<Item> childrens;
        public int avatar;
        public int colorRisk;
        public boolean leftWarningEnabled;
        public boolean rightWarningEnabled;

        public Item(int id, int type, String title, String subtitle, int colorRisk) {
            this(id, type, title, subtitle, colorRisk, -1, false, false);
        }

        public Item(int id, int type, String title, String subtitle, int colorRisk, int avatar, boolean leftWarningEnabled, boolean rightWarningEnabled) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.subtitle = subtitle;
            this.colorRisk = colorRisk;
            this.avatar = avatar;

            this.leftWarningEnabled = leftWarningEnabled;
            this.rightWarningEnabled = rightWarningEnabled;
        }

    }
}
