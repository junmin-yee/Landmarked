package landmarked.landmarked;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.common.SignInButton;
import android.view.View.OnClickListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleAuthentication extends AppCompatActivity implements View.OnClickListener {
    GoogleSignInClient mGoogleSignInClient;
    final int RC_SIGN_ON = 9001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.login_screen);

        //GoogleSignInOptions are where we add requests for different types of permissions. As per the google sign in docs, i've used
        //minimal permission requests, only the requestEmail(). However, if necesarry, this is where we would add them. more information here:
        //https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInOptions#DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_ON);
    }


    // sign_in_button is default name for google sign in button in our log in page's xml file
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;

        }
    }


    //BY DEFAULT: our google sign in button is going to be invisible. Based on whether or not a user has previously signed in to our app,
    //we will either hide the button or display the button to sign in. This is a manager function that i chose to make modular in case we
    //end up making changes to our XML or home screen. It's called when GoogleSignIn.getLastSignedInAccount(...) is called.

    protected void CheckForExistingSignIn(GoogleSignInAccount acct)
    {
        if(acct == null)
        {
            //user has not previouisly signed in, display sign in button
            View LoginButton = findViewById(R.id.sign_in_button);
            LoginButton.setVisibility(View.VISIBLE);
        }
        else
        {
            //user has already logged in previously. No need to display log in button
            View LoginButton = findViewById(R.id.sign_in_button);
            LoginButton.setVisibility(View.GONE);
            setContentView(R.layout.activity_get_sensor_data);

        }
    }


    //It's entirely likely that this code will need to go in our main file so we can check first thing if a user is already logged in. Our home screen will
    //probably either need to be a sign in form, or if the user is already signed in some kind of "hello ____! welcome back!"
    @Override protected void onStart()
    {

        super.onStart();
        //setContentView(R.layout.login_screen);
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null. We'll test this in the manager function CheckForExistingSignIn by sending account as an arg
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        CheckForExistingSignIn(account);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_ON)
        {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            CheckForExistingSignIn(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

            CheckForExistingSignIn(null);
        }
    }
}

