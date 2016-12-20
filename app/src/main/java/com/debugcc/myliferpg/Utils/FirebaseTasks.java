package com.debugcc.myliferpg.Utils;


import com.debugcc.myliferpg.Models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseTasks {
    private static final String TAG = "FIREBASE TASKS";
    private static FirebaseDatabase mDatabaseInstance;

    public static DatabaseReference getDatabase() {
        if (mDatabaseInstance == null) {
            mDatabaseInstance = FirebaseDatabase.getInstance();
            mDatabaseInstance.setPersistenceEnabled(true);
        }
        return mDatabaseInstance.getReference();
    }

    public static void setUser(User user) {
        DatabaseReference reference = getDatabase().child("users/"+user.getId());

        DatabaseReference userData = reference.child("name");
        userData.setValue(user.getName());
        userData = reference.child("email");
        userData.setValue(user.getEmail());
        userData = reference.child("provider");
        userData.setValue(user.getProvider());
        userData = reference.child("picture");
        userData.setValue(user.getUrlProfilePicture());
    }
}
