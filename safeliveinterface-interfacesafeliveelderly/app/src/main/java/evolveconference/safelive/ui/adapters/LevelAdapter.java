package evolveconference.safelive.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import evolveconference.safelive.R;
import evolveconference.safelive.model.Patient;
import evolveconference.safelive.ui.activities.MainActivity;
import evolveconference.safelive.ui.adapters.LevelAdapter.LevelViewHolder;
import evolveconference.safelive.ui.fragments.CircleDashboardFragment;
import evolveconference.safelive.ui.view.RoundImageView;

public class LevelAdapter extends RecyclerView.Adapter<LevelViewHolder> {
    private final List<Patient> dataset;
    private Activity activity;

    public class LevelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        RoundImageView photo;
        TextView name;


        LevelViewHolder(View itemView) {
            super(itemView);
            photo = (RoundImageView) itemView.findViewById(R.id.level_avatar);
            name = (TextView) itemView.findViewById(R.id.level_name);
        }

        @Override
        public void onClick(View v) {
            ((MainActivity) activity).showFragment(new CircleDashboardFragment());
        }
    }

    public LevelAdapter(List<Patient> dataset, Activity activity) {
        this.dataset = dataset;
        this.activity = activity;
    }

    @Override
    public LevelViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.level_contact_view, parent, false);
        LevelViewHolder vh = new LevelViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(LevelViewHolder holder, int position) {
        holder.name.setText(dataset.get(position).name);
        holder.photo.setImageResource(dataset.get(position).photoId);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
