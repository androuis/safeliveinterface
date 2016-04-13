package evolveconference.safelive.tasks;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import evolveconference.safelive.SafeLiveApplication;
import evolveconference.safelive.utils.LimitedQueue;

/**
 * Created by andrei on 26/03/16.
 */
public class RecordIntentService extends IntentService {

    public static final String ACTION_START_RECORDING = "action_start_recording";
    public static final String ACTION_STOP_RECORDING = "action_stop_recording";
    public static final String ACTION_GET_RECORDING = "action_get_recording";

    public static final String EXTRA_LIMITED_QUEUE = "extra_limited_queue";

    private boolean shouldRecord;

    int blockSize = 2048;// = 256;
    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2;

    public RecordIntentService() {
        super(RecordIntentService.class.getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            switch (intent.getAction()) {
                case ACTION_START_RECORDING:
                    shouldRecord = true;
                    record();
                    break;
                case ACTION_STOP_RECORDING:
                    shouldRecord = false;
                    break;
                case ACTION_GET_RECORDING:
                    break;
            }
        }
    }

    private void record() {
        try {
            final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                    RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
            if (audioRecord == null) {
                return;
            }

            final byte[] buffer = new byte[blockSize];
            final double[] toTransform = new double[blockSize];
            audioRecord.startRecording();
            int i = 0;
            while (shouldRecord) {
                Thread.sleep(1000);
                final int bufferReadResult = audioRecord.read(buffer, 0, blockSize);
                SafeLiveApplication.instance.addToLimitQueue(buffer);
            }
            audioRecord.stop();
            audioRecord.release();
        } catch (Throwable t) {
            Log.e("AudioRecord", "Recording Failed");
        }
    }

    private float calculate(int sampleRate, short[] audioData) {
        int numSamples = audioData.length;
        int numCrossing = 0;
        for (int p = 0; p < numSamples - 1; p++) {
            if ((audioData[p] > 0 && audioData[p + 1] <= 0) ||
                    (audioData[p] < 0 && audioData[p + 1] >= 0)) {
                numCrossing++;
            }
        }

        float numSecondsRecorded = (float) numSamples / (float) sampleRate;
        float numCycles = numCrossing / 2;


        return numCycles / numSecondsRecorded;
    }
}
