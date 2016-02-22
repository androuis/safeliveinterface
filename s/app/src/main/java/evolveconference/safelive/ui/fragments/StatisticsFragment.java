package evolveconference.safelive.ui.fragments;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import evolveconference.safelive.R;
import evolveconference.safelive.ui.view.Circle;
import evolveconference.safelive.ui.view.Circle.OnSeriesClickedListener;
import evolveconference.safelive.ui.view.Circle.Series;
import evolveconference.safelive.ui.view.Circle.Series.SeriesBuilder;
import evolveconference.safelive.ui.view.CircleAngleAnimation;
import evolveconference.safelive.ui.view.VisualizerView;

public class StatisticsFragment extends Fragment {

    public static final String EXTRA_DATE = "date";

    @Bind(R.id.time) TextView time;
    @Bind(R.id.date) TextView date;
    @Bind(R.id.wave_form) VisualizerView waveForm;
    @Bind(R.id.decibelSelector) TextView decibelView;
    @Bind(R.id.gap) ImageView gap;
    @Bind(R.id.circle) Circle circle;

    private RecordingThread mRecordingThread;
    private byte[] audioBuffer;
    private String decibelFormat;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
    private Date timeStamp = new Date();

    public static Fragment newInstance(Date date) {
        Fragment f = new StatisticsFragment();
        Bundle b = new Bundle();
        b.putLong(EXTRA_DATE, date.getTime());
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioBuffer = new byte[24 * 60 * 2];
        decibelFormat = getResources().getString(R.string.decibel_format);
        Bundle arg = getArguments();
        if (arg != null) {
            timeStamp = new Date(arg.getLong(EXTRA_DATE, System.currentTimeMillis()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statistics, container, false);
        ButterKnife.bind(this, v);

        time.setText(timeFormat.format(timeStamp));
        date.setText(dateFormat.format(timeStamp).toUpperCase());

        initSlider();

        initSeries();

        return v;
    }

    private void initSlider() {
        final Random r = new Random();

        // TODO gap and decibelView must be implemented as a custom view.
        gap.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    float x = event.getRawX() - v.getWidth() / 2;
                    v.setX(x);

                    decibelView.setX(x);

                    decibelView.post(new Runnable() {
                        @Override
                        public void run() {
                            final double db = r.nextInt(120);
                            decibelView.setText(String.format(decibelFormat, db));
                        }
                    });
                }
                return true;
            }
        });

        decibelView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float x = event.getRawX() - v.getWidth() / 2.0f;
                    v.setX(x);

                    gap.setX(x);
                    decibelView.post(new Runnable() {
                        @Override
                        public void run() {
                            final double db = r.nextInt(120);
                            decibelView.setText(String.format(decibelFormat, db));
                        }
                    });
                }

                return true;
            }

        });
    }

    private void initSeries() {
        circle.addSeries(new SeriesBuilder().setValue(50).setLabel(getString(R.string.in_train))
                .setColor(getResources().getColor(R.color.series_1)).build());
        circle.addSeries(new SeriesBuilder().setValue(80).setLabel(getString(R.string.in_car))
                .setColor(getResources().getColor(R.color.series_2)).build());
        circle.addSeries(new SeriesBuilder().setValue(55).setLabel(getString(R.string.jumping))
                .setColor(getResources().getColor(R.color.series_3)).build());
        circle.addSeries(new SeriesBuilder().setValue(100).setLabel(getString(R.string.running))
                .setColor(getResources().getColor(R.color.series_4)).build());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.statistics_title);

        CircleAngleAnimation animation = new CircleAngleAnimation(circle);
        animation.setDuration(1500);
        animation.setInterpolator(new DecelerateInterpolator());
        circle.startAnimation(animation);

        circle.setOnSeriesClickedListener(new OnSeriesClickedListener() {
            @Override
            public void onClick(Series series) {
                Toast.makeText(getActivity(), String.format("Series %s selected", series.getLabel()), Toast.LENGTH_SHORT).show();
            }
        });

        boolean started = startRecording();
        // If recording from Phone's mic doesn't work, simulating with random values.
        if (!started) {
            // Mock recording
            mRecordingThread = new RecordingThread();
            mRecordingThread.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRecording();
    }

    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    boolean isRecording = true;
    private Thread recordingThread = null;

    private int buffersizebytes = 2;

    private boolean startRecording() {

        try {
            buffersizebytes = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

            recorder = new AudioRecord(AudioSource.MIC, RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, buffersizebytes);
            recorder.startRecording();
        } catch (Exception e) {
            recorder = null;
            return false;
        }

        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                // Write the output audio in byte
                audioBuffer = new byte[buffersizebytes];
                while (isRecording) {
                    // gets the voice output from microphone to byte format
                    recorder.read(audioBuffer, 0, buffersizebytes);
                    waveForm.post(new Runnable() {
                        @Override
                        public void run() {
                            waveForm.updateVisualizer(audioBuffer);
                            updateDecibelLevel();
                        }
                    });
                }
            }
        }, "AudioRecorder Thread");

        recordingThread.start();

        return true;
    }

    private void stopRecording() {
        // stops the recording activity
        isRecording = false;
        if (null != recorder) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        recordingThread = null;
    }

    /**
     * A background thread that receives audio from the microphone and sends it to the waveform
     * visualizing view.
     */
    private class RecordingThread extends Thread {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
            Random r = new Random();


            while (isRecording) {

                try {
                    // give some idle time.
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                r.nextBytes(audioBuffer);

                waveForm.post(new Runnable() {
                    @Override
                    public void run() {
                        waveForm.updateVisualizer(audioBuffer);
                        updateDecibelLevel();
                    }
                });
            }
        }
    }

    /**
     * Computes the decibel level of the current sound buffer and updates the appropriate title
     * view.
     */

    private void updateDecibelLevel() {
        // Compute the root-mean-squared of the sound buffer and then apply the formula for
        // computing the decibel level, 20 * log_10(rms). This is an uncalibrated calculation
        // that assumes no noise in the samples; with 8-bit recording, it can range from
        // -90 dB to 0 dB.
        double sum = 0;

        for (byte rawSample : audioBuffer) {
            double sample = rawSample / Byte.MAX_VALUE;
            sum += sample * sample;
        }

        double rms = Math.sqrt(sum / audioBuffer.length);
        final double db = 20 * Math.log10(rms);

        // Update the title view on the main thread.
        decibelView.post(new Runnable() {
            @Override
            public void run() {
                decibelView.setText(String.format(decibelFormat, db));
            }
        });
    }

}
