package evolveconference.safelive.utils;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class RecordIntentService extends IntentService {
    public static final String ACTION_START_RECORD = "action_start_record";
    public static final String ACTION_STOP_RECORD = "action_stop_record";

    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private static final int SLEEP_TIME = 1000;
    private static final int LAST_FREQUENCIES_SIZE = 15 * 60;

    int blockSize = 2048;// = 256;
    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2;

    boolean shouldRecord;
    LimitSizeQueue lastFrequencies = new LimitSizeQueue(LAST_FREQUENCIES_SIZE);

    public RecordIntentService() {
        super("RecordIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_RECORD.equals(action)) {
                shouldRecord = true;
                record();
            } else if (ACTION_STOP_RECORD.equals(action)) {
                shouldRecord = false;
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

            final short[] buffer = new short[blockSize];
            final double[] toTransform = new double[blockSize];
            audioRecord.startRecording();
            while (shouldRecord) {
                Thread.sleep(SLEEP_TIME);
                final int bufferReadResult = audioRecord.read(buffer, 0, blockSize);
                lastFrequencies.add(calculate(RECORDER_SAMPLERATE, buffer));
            }
            audioRecord.stop();
            audioRecord.release();
        } catch (Throwable t) {
            Log.e("AudioRecord", "Recording Failed");
        }
    }

    public static float calculate(int sampleRate, short[] audioData) {
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
