package com.example.myapplication;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ClubRepository {
    private static final List<Club> clubs = new ArrayList<>();
    private static final String TAG = "ClubRepository";

    public static void addClub(Club club) {
        // keep in-memory
        clubs.add(club);

        // persist to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clubs")
                .add(club)
                .addOnSuccessListener(docRef -> {
                    Log.d(TAG, "Club saved to Firestore: " + docRef.getId());
                    // update local object id (best-effort)
                    club.setId(docRef.getId());
                })
                .addOnFailureListener(e -> Log.w(TAG, "Failed to save club to Firestore", e));
    }

    public static List<Club> getAllClubs() {
        return new ArrayList<>(clubs);
    }

    public static Club getClubById(String id) {
        if (id == null) return null;
        for (Club c : clubs) {
            if (id.equals(c.getId()) || id.equals(c.getName())) return c;
        }
        return null;
    }
}
