package evolveconference.safelive.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import evolveconference.safelive.R;
import evolveconference.safelive.model.BloodPressureMeasure;
import evolveconference.safelive.model.HeartMeasure;
import evolveconference.safelive.model.Measure;
import evolveconference.safelive.model.OxymetryMeasure;
import evolveconference.safelive.model.Patient;
import evolveconference.safelive.model.TemperatureMeasure;
import evolveconference.safelive.utils.Generator;

public class PatientRepository {

    List<Patient> patients;
    List<Patient> level1;
    List<Patient> level2;

    public PatientRepository() {
        patients = randomPatients();
        level1 = randomLevel();
        level2 = randomLevel();
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public List<Patient> getContactLevel1() {
        return level1;
    }

    public List<Patient> getContactLevel2() {
        return level2;
    }

    String[] names = { "Laura Jacobs", "Thomas Peeters", "Marie Mertens", "Nicolas Janssens", "Maxime Maes", "Sarah Willems" };

    int[] avatars = { R.drawable.old1, R.drawable.old2, R.drawable.old3, R.drawable.old4, R.drawable.old5, R.drawable.old6 };

    private List<Patient> randomPatients() {
        List<Patient> ps = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            Patient p = new Patient(i, names[i], avatars[i],  Generator.randomAlertWarningRisk());
            p.addMeasure(randomOximetry());
            p.addMeasure(randomPressure());
            p.addMeasure(randomTemperature());
            ps.add(p);
        }

        return ps;
    }

    private List<Patient> randomLevel() {
        List<Patient> ps = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            int p = r.nextInt(names.length);
            Patient pa = new Patient(i, names[p], avatars[p], R.color.white_smoke);
            ps.add(pa);
        }

        return ps;
    }

    Random r = new Random();

    private Measure randomOximetry() {
        return new OxymetryMeasure(r.nextInt(100));
    }

    private Measure randomPressure() {
        int min = 70 + r.nextInt(30);
        int max = min + r.nextInt(40);
        return new BloodPressureMeasure(min, max);
    }

    private Measure randomHeart() {
        return new HeartMeasure(50 + r.nextInt(50));
    }

    private Measure randomTemperature() {
        return new TemperatureMeasure(30 + r.nextInt(10));
    }

    public Patient findPatientById(int id) {
        for (Patient p : patients) {
            if (p.id == id) {
                return p;
            }
        }
        return null;
    }
}
