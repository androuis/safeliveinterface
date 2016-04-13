package evolveconference.safelive;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import evolveconference.safelive.model.Patient;
import evolveconference.safelive.model.Profile;
import evolveconference.safelive.repository.PatientRepository;
import evolveconference.safelive.repository.ProfileRepository;
import evolveconference.safelive.tasks.RecordIntentService;
import evolveconference.safelive.utils.LimitedQueue;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class SafeLiveApplication extends Application {

    PatientRepository patientRepository;
    ProfileRepository profileRepository;
    public static SafeLiveApplication instance;
    public static DateFormat anomalyTimestampFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public static final String SHARED_PREFS = "shared_prefs";
    public static final String SHARED_PREFS_KEY_SIZE = "key_size";
    public static final String SHARED_PREFS_KEY_VALUE = "key_value";
    private static final String SOUND_FILE = ".sound";
    private static final int LIMIT_QUEUE = 15 * 60;
    private LimitedQueue limitedQueue = new LimitedQueue(LIMIT_QUEUE);

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        instance = this;
        initCalligraphy();
        patientRepository = new PatientRepository();
        profileRepository = new ProfileRepository();
        saveSound();
    }

    private void saveSound() {
        try {
            FileOutputStream fileOutputStream = openFileOutput(SOUND_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(limitedQueue);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        readSound();
    }

    private void readSound() {
        try {
            FileInputStream fileInputStream = openFileInput(SOUND_FILE);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            limitedQueue = (LimitedQueue) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(getString(R.string.roboto_regular))
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    public LimitedQueue getLimitedQueue() {
        return limitedQueue;
    }

    public void addToLimitQueue(Object element) {
        limitedQueue.add(element);
    }

    public List<Patient> getPatients(){
        return patientRepository.getPatients();
    }

    public Profile getProfile() {
        return profileRepository.getProfile();
    }

    public List<Patient> getContactLevel1() {
        return patientRepository.getContactLevel1();
    }

    public List<Patient> getContactLevel2() {
        return patientRepository.getContactLevel2();
    }

    public Patient findPatientById(int id) {
        return patientRepository.findPatientById(id);
    }
}
