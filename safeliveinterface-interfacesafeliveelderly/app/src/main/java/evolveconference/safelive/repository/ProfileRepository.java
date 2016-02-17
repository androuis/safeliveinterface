package evolveconference.safelive.repository;

import evolveconference.safelive.model.Profile;

public class ProfileRepository {

    Profile profile;

    public ProfileRepository() {
        profile = new Profile();
        profile.name = "Emilia";
        profile.location = "New Residence, Brussels";
        profile.role = "Nurse";
    }

    public Profile getProfile() {
        return profile;
    }
}
