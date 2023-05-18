package com.example.firebase_auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.firebase_auth.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth mAuth;
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;
    String mVerificationId;
    SignInClient oneTapClient;
    BeginSignInRequest signInRequest;
    SignInCredential credential;
    SignInCredential googleCredential;
    String idToken;
    Intent data;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();


        binding.btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        binding.btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin();
            }
        });
        binding.btnotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

                        Log.d("eee", "onVerificationCompleted:" + credential);

                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        Log.w("eee", "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {

                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                        } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                            // reCAPTCHA verification attempted with null Activity
                        }

                        // Show a message and update the UI
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {

                        Log.d("eee", "onCodeSent:" + verificationId);
                        Toast.makeText(MainActivity.this, "code sent", Toast.LENGTH_SHORT).show();

                        // Save verification ID and resending token so we can use them later
                         mVerificationId = verificationId;
                         mResendToken = token;
                    }
                };
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(binding.phone.getText().toString())       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(MainActivity.this)                 // (optional) Activity for callback binding
                                // If no activity is passed, reCAPTCHA verification can not be used.
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);

            }
        });
        binding.phBtnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,binding.otp.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }
        });
        binding.gsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("641835188654-jlphu87nfeajpv7fglbcs8tct2frld1g.apps.googleusercontent.com")
                        .requestEmail()
                        .build();

                // Initialize sign in client
                googleSignInClient = GoogleSignIn.getClient(MainActivity.this, googleSignInOptions);
                Intent intent = googleSignInClient.getSignInIntent();
                // Start activity for result
                startActivityForResult(intent, 100);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            // When request code is equal to 100 initialize task
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            // check condition
            if (signInAccountTask.isSuccessful()) {
                // When google sign in successful initialize string
                String s = "Google sign in successful";
                // Display Toast
               displayToast(s);
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null initialize auth credential
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        // Check credential
                        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Check condition
                                if (task.isSuccessful()) {
                                    // When task is successful redirect to profile activity display Toast
                                    //startActivity(new Intent(MainActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    displayToast("Firebase authentication successful");
                                } else {
                                    // When task is unsuccessful display Toast
                                    displayToast("Authentication Failed :" + task.getException().getMessage());
                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }

            }
        }

    }
    private void displayToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("fff", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("fff", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }



    private void signin() {
        mAuth.signInWithEmailAndPassword(binding.email.getText().toString(),binding.password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("ddd", "signInWithEmail:success");
                            Toast.makeText(MainActivity.this, "Authentication Success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("ddd", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }
                    }
                });
    }

    private void registerUser() {
        mAuth.createUserWithEmailAndPassword(binding.email.getText().toString(),binding.password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d("ccc", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            Log.w("ccc", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }
}