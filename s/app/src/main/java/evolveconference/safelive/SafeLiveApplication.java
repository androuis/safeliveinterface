package evolveconference.safelive;

import android.app.Application;
import android.support.multidex.MultiDex;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import evolveconference.safelive.model.Patient;
import evolveconference.safelive.model.Profile;
import evolveconference.safelive.repository.PatientRepository;
import evolveconference.safelive.repository.ProfileRepository;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class SafeLiveApplication extends Application {

    PatientRepository patientRepository;
    ProfileRepository profileRepository;
    public static SafeLiveApplication instance;
    public  static DateFormat anomalyTimestampFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);


    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        instance = this;
        initCalligraphy();
        patientRepository = new PatientRepository();
        profileRepository = new ProfileRepository();
    }

    private void initCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(getString(R.string.roboto_regular))
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
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
