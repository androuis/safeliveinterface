package evolveconference.safelive.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.ui.view.RoundImageView;
import evolveconference.safelive.ui.view.ThresholdCombinedView;
import evolveconference.safelive.ui.view.ThresholdVisualizerView;
import evolveconference.safelive.utils.DateUtils;

public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int ALERT = 1;
    public static final int WARNING = 2;
    public static final int PROFILE = 3;
    public static final int CHART = 4;
    public static final int HISTORY = 5;

    private List<Item> chart;

    public ExpandableListAdapter(List<Item> data) {
        this.chart = data;
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
            case CHART:
                view = inflater.inflate(R.layout.chart_view, parent, false);
                return new LineChartViewHolder(view);
            case HISTORY:
                view = inflater.inflate(R.layout.history_row, parent, false);
                return new HistoryItemViewHolder(view);
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
                row.avatar.setImageResource(item.avatar);
                row.avatar.setCircleColor(item.colorRisk);
                break;
            case WARNING:
                final WarningItemViewHolder warning = (WarningItemViewHolder) holder;
                warning.name.setText(item.title);
                warning.warning.setText(item.subtitle);
                warning.avatar.setImageResource(item.avatar);
                warning.avatar.setCircleColor(item.colorRisk);
                warning.enabled.setChecked(item.enabled);
                break;
            case CHART:
                final LineChartViewHolder chart = (LineChartViewHolder) holder;
                setChartValues(chart.chart);
                break;
            case HISTORY:
                final HistoryItemViewHolder history = (HistoryItemViewHolder) holder;
                history.title.setText(item.title);
                history.subTitle.setText(item.subtitle);
                history.date.setText(DateUtils.getFormattedDate(item.date));
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
        @Bind(R.id.profile_avatar) ImageView avatar;
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

    static class LineChartViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.chart1) ThresholdVisualizerView chart;

        public LineChartViewHolder(View profileView) {
            super(profileView);
            ButterKnife.bind(this, profileView);
        }
    }

    static class HistoryItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.history_row_date) TextView date;
        @Bind(R.id.history_row_text) TextView title;
        @Bind(R.id.history_row_subtext) TextView subTitle;

        public HistoryItemViewHolder(View profileView) {
            super(profileView);
            ButterKnife.bind(this, profileView);
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
        public boolean enabled;
        public Date date;

        public Item(int type) {
            this(type, "", "");
        }

        public Item(int type, String title) {
            this(type, title, "");
        }

        public Item(int type, String title, String subTitle, Date date) {
            this(type, title, subTitle);
            this.date = date;
        }

        public Item(int type, String title, String subtitle) {
            this(type, title, subtitle, "", -1, false);
        }

        public Item(int type, String title, String subtitle, int avatar, int colorRisk) {
            this(type, title, subtitle, "", avatar, false);
            this.colorRisk = colorRisk;
        }

        public Item(int type, String title, String subtitle, int avatar, int colorRisk, boolean enabled) {
            this(type, title, subtitle, "", avatar, enabled);
            this.colorRisk = colorRisk;
        }

        public Item(int type, String title, String subtitle, String role, int avatar) {
            this(type, title, subtitle, role, avatar, false);

        }

        public Item(int type, String title, String subtitle, String role, int avatar, boolean enabled) {
            this.type = type;
            this.title = title;
            this.subtitle = subtitle;
            this.role = role;
            this.avatar = avatar;
            this.enabled = enabled;
        }
    }

    private void setChartValues(ThresholdVisualizerView chart) {
        byte[] audioBuffer = new byte[4000];
        Random r = new Random();

        r.nextBytes(audioBuffer);
        chart.updateVisualizer(audioBuffer);
    }

    private void setChartValues(ThresholdCombinedView chart) {

        // no description text
        chart.setDescription("");
        chart.setNoDataTextDescription("No data");

        // enable touch gestures
        chart.setTouchEnabled(true);

        chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // add data
        chart.setData(buildData(12));

        chart.animateX(400);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.GRAY);
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaxValue(40f);
        leftAxis.setAxisMaxValue(200f);
        leftAxis.setStartAtZero(true);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTextColor(Color.RED);
        rightAxis.setAxisMinValue(40);
        rightAxis.setAxisMaxValue(200);
        rightAxis.setStartAtZero(true);
        rightAxis.setDrawGridLines(false);
    }

    private LineDataSet buildMaxThreshold() {
        ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            float val = 150;
            yVals2.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet heartBeatDataSet = new LineDataSet(yVals2, "Heart beat");
        heartBeatDataSet.setAxisDependency(AxisDependency.RIGHT);
        heartBeatDataSet.setColor(Color.GRAY);
        heartBeatDataSet.setDrawValues(false);
        heartBeatDataSet.setDrawCircles(false);
        heartBeatDataSet.setLineWidth(2f);
        heartBeatDataSet.setFillAlpha(65);
        heartBeatDataSet.setHighLightColor(Color.rgb(244, 117, 117));

        return heartBeatDataSet;
    }


    private LineDataSet buildMinThreshold() {
        ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            float val = 50;
            yVals2.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet heartBeatDataSet = new LineDataSet(yVals2, "Heart beat");
        heartBeatDataSet.setAxisDependency(AxisDependency.RIGHT);
        heartBeatDataSet.setColor(Color.GRAY);
        heartBeatDataSet.setDrawValues(false);
        heartBeatDataSet.setDrawCircles(false);
        heartBeatDataSet.setLineWidth(2f);
        heartBeatDataSet.setFillAlpha(65);
        heartBeatDataSet.setHighLightColor(Color.rgb(244, 117, 117));

        return heartBeatDataSet;
    }


    protected String[] mMonths = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    private CombinedData buildData(int count) {

        CombinedData combinedData = new CombinedData();
        for (int i = 0; i < mMonths.length; i++) {
            combinedData.addXValue(mMonths[i]);
        }

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals1 = new ArrayList<>();

        Random r = new Random();

        for (int i = 0; i < count; i++) {
            float val = r.nextInt(60) + 50;
            yVals1.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet bloodPressureDataset = new LineDataSet(yVals1, "Blood pressure");
        bloodPressureDataset.setAxisDependency(AxisDependency.LEFT);
        bloodPressureDataset.setColor(ColorTemplate.getHoloBlue());
        bloodPressureDataset.setCircleColor(Color.BLUE);
        bloodPressureDataset.setLineWidth(2f);
        bloodPressureDataset.setCircleSize(3f);
        bloodPressureDataset.setFillAlpha(65);
        bloodPressureDataset.setFillColor(ColorTemplate.getHoloBlue());
        bloodPressureDataset.setHighLightColor(Color.rgb(244, 117, 117));
        bloodPressureDataset.setDrawCircleHole(true);

        ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float val = r.nextInt(30) + 70;
            yVals2.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet heartBeatDataSet = new LineDataSet(yVals2, "Heart beat");
        heartBeatDataSet.setAxisDependency(AxisDependency.RIGHT);
        heartBeatDataSet.setColor(Color.RED);
        heartBeatDataSet.setDrawValues(false);
        heartBeatDataSet.setCircleColor(Color.BLACK);
        heartBeatDataSet.setLineWidth(2f);
        heartBeatDataSet.setCircleSize(3f);
        heartBeatDataSet.setFillAlpha(65);
        heartBeatDataSet.setFillColor(Color.RED);
        heartBeatDataSet.setDrawCircleHole(true);
        heartBeatDataSet.setHighLightColor(Color.rgb(244, 117, 117));


        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(heartBeatDataSet);
        dataSets.add(bloodPressureDataset); // add the datasets

        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.DKGRAY);
        data.setValueTextSize(9f);

        data.addDataSet(heartBeatDataSet);
        data.addDataSet(bloodPressureDataset);
        data.addDataSet(buildMinThreshold());
        data.addDataSet(buildMaxThreshold());

        combinedData.setData(data);
        combinedData.setData(generateBarData());
        // create a data object with the datasets


        return combinedData;
    }

    private BarData generateBarData() {

        BarData d = new BarData();

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int index = 0; index < 12; index++)
            entries.add(new BarEntry(getRandom(15, 30), index));

        BarDataSet set = new BarDataSet(entries, "Bar DataSet");
        set.setColor(Color.rgb(60, 220, 78));
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(10f);
        d.addDataSet(set);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        return d;
    }

    private float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }

}