package com.debugcc.myliferpg.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.debugcc.myliferpg.Models.User;
import com.debugcc.myliferpg.R;
import com.debugcc.myliferpg.Utils.FirebaseTasks;
import com.debugcc.myliferpg.Utils.UserPreferences;
import com.debugcc.myliferpg.databinding.ActivitySignInBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Activity_SignIn";

    private ProgressDialog mProgressDialog;
    private ActivitySignInBinding binding;

    /// GOOGLE
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    /// FACEBOOK
    private CallbackManager mCallbackManager;
    private FacebookCallback<LoginResult> mCallBack;

    /// FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);

        /// FIREBASE
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in: FIREBASE");
                    /*Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getPhotoUrl());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getEmail());*/
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        }; /// END FIREBASE

        if (UserPreferences.getCurrentUser(SignInActivity.this) != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            SignInActivity.this.startActivity(intent);
            SignInActivity.this.finish();
        }

        /// GOOGLE
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build(); /// END GOOGLE

        /// FACEBOOK
        mCallbackManager = CallbackManager.Factory.create();
        chargeFacebookCallBack();
        binding.facebookButtonLogin.setReadPermissions("public_profile", "email", "user_friends", "contact_email"); /// END FACEBOOK
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fake_google_button_login:
                signInGoogle();
                break;
            case R.id.fake_facebook_button_login:
                signInFacebook();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /// FACEBOOK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        /// GOOGLE
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }


    /// GOOGLE
    private void signInGoogle() {
        //showProgressDialog("Cargando...");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed:" + connectionResult);
        //hideProgressDialog();
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        //Log.d(TAG, "handleSignInResult: " + result.isSuccess());
        //hideProgressDialog();
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount(); /// google account
            if (acct!=null) {
                firebaseAuthWithGoogle(acct);

                /*User user = new User();
                user.setId( acct.getId() );
                user.setEmail( acct.getEmail() );
                user.setName( acct.getDisplayName() );
                if( acct.getPhotoUrl() != null)
                    user.setUrlProfilePicture( acct.getPhotoUrl().toString() );
                user.setProvider( User.GOOGLE_PROVIDER );

                saveUserAndRedirect(user);*/
            }
        } else {
            // Signed out, show unauthenticated UI.
            Log.d(TAG, "handleSignInResult: FAIL " + result.getStatus().toString());
        }
    } /// END GOOGLE


    /// FACEBOOK
    private void signInFacebook() {
        //showProgressDialog("Cargando...");
        binding.facebookButtonLogin.performClick();
        binding.facebookButtonLogin.setPressed(true);
        binding.facebookButtonLogin.invalidate();
        binding.facebookButtonLogin.registerCallback(mCallbackManager, mCallBack);
        binding.facebookButtonLogin.setPressed(false);
        binding.facebookButtonLogin.invalidate();
    }

    private void chargeFacebookCallBack() {
        mCallBack = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                //hideProgressDialog();
                firebaseAuthWithFacebook(loginResult.getAccessToken());

                // App code
                /*GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted (JSONObject object, GraphResponse response) {
                                //Log.e("response: ", response + "");
                                Log.e("response OBJECT: ", object.toString() + "");
                                hideProgressDialog();

                                /*User user = new User();
                                try {
                                    if (object.has("id")) {
                                        user.setId( object.getString("id") );
                                        user.setUrlProfilePicture( "https://graph.facebook.com/" + user.getId() + "/picture?type=large" );
                                    }
                                    if (object.has("email"))
                                        user.setEmail( object.getString("email") );
                                    if (object.has("name"))
                                        user.setName( object.getString("name") );

                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                user.setProvider( User.FACEBOOK_PROVIDER );

                                saveUserAndRedirect(user);**
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();*/

                /*GraphRequest graphRequest = GraphRequest.newMyFriendsRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONArrayCallback() {
                            @Override
                            public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {

                                Log.e(TAG, "onCompleted: " + graphResponse.toString());
                                try {
                                    Log.e(TAG, "onCompleted: ARRAY" + jsonArray.getJSONObject(0).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle param = new Bundle();
                param.putString("fields", "friendlist, members");
                graphRequest.setParameters(param);
                graphRequest.executeAsync();*/
            }

            @Override
            public void onCancel() {
                //hideProgressDialog();
                Log.e(TAG, "onCancel: " );
            }

            @Override
            public void onError(FacebookException error) {
                //hideProgressDialog();
                Log.e(TAG, "onError: " + error.toString() );
            }
        };
    } /// END FACEBOOK


    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, "onResult: SALIO CERRO SESION");
                    }
                });

        FirebaseAuth.getInstance().signOut();
    }

    /// FIREBASE
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        //showProgressDialog("Cargando...");

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "signInWithCredential:onComplete: LOGUEADO CON GOOGLE" + task.isSuccessful());
                        //hideProgressDialog();

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser user = task.getResult().getUser();
                            User my_user = new User();
                            my_user.setId(user.getUid());
                            my_user.setProvider(User.GOOGLE_PROVIDER);
                            my_user.setName(user.getDisplayName());
                            my_user.setEmail(user.getEmail());
                            my_user.setUrlProfilePicture(user.getPhotoUrl().toString());
                            saveUserAndRedirect(my_user);
                        }
                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        //showProgressDialog("Cargando...");

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e(TAG, "signInWithCredential:onComplete: LOGUEADO CON FACEBOOK" + task.isSuccessful());
                        //hideProgressDialog();

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser user = task.getResult().getUser();
                            User my_user = new User();
                            my_user.setId(user.getUid());
                            my_user.setProvider(User.FACEBOOK_PROVIDER);
                            my_user.setName(user.getDisplayName());
                            my_user.setEmail(user.getEmail());
                            my_user.setUrlProfilePicture(user.getPhotoUrl().toString());
                            saveUserAndRedirect(my_user);
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    } /// END FIREBASE


    private void saveUserAndRedirect(User user) {

        UserPreferences.setCurrentUser(user, SignInActivity.this);
        FirebaseTasks.setUser(user);

        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        SignInActivity.this.startActivity(intent);
        SignInActivity.this.finish();
    }

    private void showProgressDialog(String text) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(text);

        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
        }
    }
}
