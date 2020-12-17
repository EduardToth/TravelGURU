package com.example.travelmantics.utilities;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class PersistenceSetter {

    private static int nrOfCalls = 0;

    public static void setupPersistence() {
        if(nrOfCalls == 0) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    //.setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .setPersistenceEnabled(true)
                    .build();

            FirebaseFirestore.getInstance().setFirestoreSettings(settings);
            nrOfCalls++;
        }
    }
}
