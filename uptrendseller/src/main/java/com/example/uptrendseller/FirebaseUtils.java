package com.example.uptrendseller;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class FirebaseUtils {

    public interface EmailCheckListener {
        void onEmailCheckResult(boolean emailExists);
    }

    public static void checkUserEmailInDatabase(DatabaseReference databaseReference, String email, EmailCheckListener listener) {
        databaseReference.orderByChild("adminEmail").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean emailExists = snapshot.exists();
                        listener.onEmailCheckResult(emailExists);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle onCancelled if needed
                        listener.onEmailCheckResult(false); // Assume email does not exist in case of error
                    }
                });
    }
}
