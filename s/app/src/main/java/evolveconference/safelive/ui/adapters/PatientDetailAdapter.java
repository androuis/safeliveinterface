package evolveconference.safelive.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.ui.activities.MainActivity;
import evolveconference.safelive.ui.fragments.StatisticsFragment;
import evolveconference.safelive.ui.view.CircleLayout;
import evolveconference.safelive.ui.view.CircleLayout.OnCenterClickListener;
import evolveconference.safelive.ui.view.CircleLayout.OnItemClickListener;
import evolveconference.safelive.ui.view.CircleLayout.OnItemSelectedListener;
import evolveconference.safelive.ui.view.CircleLayout.OnRotationFinishedListener;
import evolveconference.safelive.ui.view.RoundImageView;

public class PatientDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int ALERT = 1;
    public static final int WARNING = 2;
    public static final int PROFILE = 3;
    public static final int MENU = 4;

    private List<Item> chart;
    private Activity activity;

    public PatientDetailAdapter(List<Item> data, Activity activity) {
        this.chart = data;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (type) {
            case PROFILE:
                view = inflater.inflate(R.layout.profile_view, parent, false);
                return new ProfileViewHolder(view);
            case HEADER:
                view = inflater.inflate(R.layout.list_header, parent, false);
                return new ListHeaderViewHolder(view);
            case ALERT:
                view = inflater.inflate(R.layout.alert_row, parent, false);
                return new AlertItemViewHolder(view);
            case WARNING:
                view = inflater.inflate(R.layout.warning_row, parent, false);
                return new WarningItemViewHolder(view);
            case MENU:
                view = inflater.inflate(R.layout.fragment_circle_dashboard, parent, false);
                return new CircleMenuViewHolder(view);
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = chart.get(position);
        switch (item.type) {
            case PROFILE:
                final ProfileViewHolder profile = (ProfileViewHolder) holder;
                profile.name.setText(item.title);
                profile.location.setText(item.subtitle);
                profile.role.setText(item.role);
                profile.avatar.setImageResource(item.avatar);
                profile.avatar.setCircleColor(item.colorRisk);
                break;
            case HEADER:
                final ListHeaderViewHolder itemViewHolder = (ListHeaderViewHolder) holder;
                itemViewHolder.refferalItem = item;
                itemViewHolder.header_title.setText(item.title);
                itemViewHolder.header_subtitle.setText(item.subtitle);
                if (item.invisibleChildren == null) {
                    itemViewHolder.btn_expand_toggle.setImageResource(R.drawable.ic_less);
                } else {
                    itemViewHolder.btn_expand_toggle.setImageResource(R.drawable.ic_more);
                }
                itemViewHolder.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.invisibleChildren == null) {
                            item.invisibleChildren = new ArrayList<>();
                            int count = 0;
                            int pos = chart.indexOf(itemViewHolder.refferalItem);
                            while (chart.size() > pos + 1 && chart.get(pos + 1).type != HEADER) {
                                item.invisibleChildren.add(chart.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemViewHolder.btn_expand_toggle.setImageResource(R.drawable.ic_more);
                        } else {
                            int pos = chart.indexOf(itemViewHolder.refferalItem);
                            int index = pos + 1;
                            for (Item i : item.invisibleChildren) {
                                chart.add(index, i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
                            itemViewHolder.btn_expand_toggle.setImageResource(R.drawable.ic_less);
                            item.invisibleChildren = null;
                        }
                    }
                });
                break;
            case ALERT:
                final AlertItemViewHolder row = (AlertItemViewHolder) holder;
                row.name.setText(item.title);
                row.warning.setText(item.subtitle);
                row.avatar.setVisibility(View.GONE);
                row.avatar.setCircleColor(item.colorRisk);
                break;
            case WARNING:
                final WarningItemViewHolder warning = (WarningItemViewHolder) holder;
                warning.name.setText(item.title);
                warning.warning.setText(item.subtitle);
                warning.avatar.setImageResource(item.avatar);
                warning.avatar.setCircleColor(item.colorRisk);
                warning.avatar.setVisibility(View.GONE);
                warning.enabled.setVisibility(View.GONE);
                break;
            case MENU:
                final CircleMenuViewHolder circleMenu = (CircleMenuViewHolder) holder;
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

    static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.header_text) TextView header_title;
        @Bind(R.id.header_subtext) TextView header_subtitle;
        @Bind(R.id.btn_expand_toggle) ImageView btn_expand_toggle;
        public Item refferalItem;

        public ListHeaderViewHolder(View headerView) {
            super(headerView);
            ButterKnife.bind(this, headerView);
        }
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.profile_name) TextView name;
        @Bind(R.id.profile_avatar) RoundImageView avatar;
        @Bind(R.id.profile_location) TextView location;
        @Bind(R.id.profile_role) TextView role;

        public ProfileViewHolder(View profileView) {
            super(profileView);
            ButterKnife.bind(this, profileView);
        }
    }

    static class AlertItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.row_avatar) RoundImageView avatar;
        @Bind(R.id.row_text) TextView name;
        @Bind(R.id.row_subtext) TextView warning;

        public AlertItemViewHolder(View profileView) {
            super(profileView);
            ButterKnife.bind(this, profileView);
        }
    }

    static class WarningItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.row_avatar) RoundImageView avatar;
        @Bind(R.id.row_text) TextView name;
        @Bind(R.id.row_subtext) TextView warning;
        @Bind(R.id.enabled) Switch enabled;

        public WarningItemViewHolder(View profileView) {
            super(profileView);
            ButterKnife.bind(this, profileView);
        }
    }

    public class CircleMenuViewHolder extends RecyclerView.ViewHolder implements OnItemSelectedListener,
            OnItemClickListener, OnRotationFinishedListener, OnCenterClickListener {
        @Bind(R.id.main_circle_layout) CircleLayout circleMenu;

        public CircleMenuViewHolder(View profileView) {
            super(profileView);
            ButterKnife.bind(this, profileView);
            setListener();
        }

        private void setListener() {
            // Set listeners
            circleMenu.setOnItemSelectedListener(this);
            circleMenu.setOnItemClickListener(this);
            circleMenu.setOnRotationFinishedListener(this);
            circleMenu.setOnCenterClickListener(this);
            circleMenu.setSelect(0);
        }


        @Override
        public void onItemSelected(View view, int position, String name) {
//        selectedTextView.setText(name);
        }

        private int[] resource = new int[]{ R.drawable.blood_pressure_2, R.drawable.audio_2, R.drawable.respiratory_2,
                R.drawable.activity_2, R.drawable.oximetry_2, R.drawable.heart_status_2, R.drawable.temperature_2 };

        @Override
        public void onItemClick(View view, int position, String name) {
//        selectedTextView.setText(name);
            circleMenu.setCenterImage(resource[position], randomColor(position));
        }

        private int randomColor(int i) {
            switch (i % 3) {
                case 0:
                    return R.color.wheel_red;
                case 1:
                    return R.color.wheel_green;
                case 2:
                    return R.color.wheel_orange;
                default:
                    return R.color.wheel_green;
            }
        }

        @Override
        public void onRotationFinished(View view, String name) {
            Animation animation = new RotateAnimation(0, 360, view.getWidth() / 2,
                    view.getHeight() / 2);
            animation.setDuration(250);
            view.startAnimation(animation);
        }

        @Override
        public void onCenterClick() {
            ((MainActivity) activity).showFragment(new StatisticsFragment());
        }
    }

    public static class Item {
        public String role;
        public int type;
        public String title;
        public String subtitle;
        public List<Item> invisibleChildren;
        public int avatar;
        public int colorRisk;

        public Item(int type, String title, String subtitle) {
            this.type = type;
            this.title = title;
            this.subtitle = subtitle;
        }

        public Item(int type, String title, String subtitle, int avatar, int colorRisk) {
            this.type = type;
            this.title = title;
            this.subtitle = subtitle;
            this.avatar = avatar;
            this.colorRisk = colorRisk;
        }
    }
}