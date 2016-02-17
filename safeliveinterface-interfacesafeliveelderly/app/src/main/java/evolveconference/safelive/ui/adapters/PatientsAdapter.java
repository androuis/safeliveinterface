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
import evolveconference.safelive.ui.adapters.PatientsAdapter.PatientViewHolder;
import evolveconference.safelive.ui.fragments.DetailPatientFragment;
import evolveconference.safelive.ui.view.RoundImageView;

public class PatientsAdapter extends RecyclerView.Adapter<PatientViewHolder> {
    private final List<Patient> dataset;
    private Activity activity;

    public class PatientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        RoundImageView photo;
        TextView name;
        TextView temperture;
        TextView bloodPressure;
        TextView oximetry;


        PatientViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            photo = (RoundImageView) itemView.findViewById(R.id.person_photo);
            photo.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.patient_name);
            temperture = (TextView) itemView.findViewById(R.id.patient_temperature);
            bloodPressure = (TextView) itemView.findViewById(R.id.patient_blood_pressure);
            oximetry = (TextView) itemView.findViewById(R.id.patient_oximetry);
        }

        @Override
        public void onClick(View v) {
            ((MainActivity) activity).showFragment(DetailPatientFragment.newInstance(dataset.get(getAdapterPosition()).id));
        }
    }

    public PatientsAdapter(List<Patient> dataset, Activity activity) {
        this.dataset = dataset;
        this.activity = activity;
    }

    @Override
    public PatientViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.patient_view, parent, false);
        PatientViewHolder vh = new PatientViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(PatientViewHolder holder, int position) {
        holder.name.setText(dataset.get(position).name);

        holder.oximetry.setText(dataset.get(position).measures.get(0).getLabel());
        holder.oximetry.setBackgroundResource(dataset.get(position).measures.get(0).getRisk());

        holder.bloodPressure.setText(dataset.get(position).measures.get(1).getLabel());
        holder.bloodPressure.setBackgroundResource(dataset.get(position).measures.get(1).getRisk());

        holder.temperture.setText(dataset.get(position).measures.get(2).getLabel());
        holder.temperture.setBackgroundResource(dataset.get(position).measures.get(2).getRisk());

        holder.photo.setImageResource(dataset.get(position).photoId);
        holder.photo.setCircleColor(dataset.get(position).risk);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
