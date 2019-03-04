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
import android.widget.TextView;
import java.util.concurrent.TimeUnit ;

public class GoogleAuthentication extends AppCompatActivity implements View.OnClickListener {
    GoogleSignInClient mGoogleSignInClient;
    final int RC_SIGN_ON = 9001;
    public String Name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page);//set to our sign in page, our existing user check method has some logic that will either hide or make visible buttons depending on the case
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        CheckForExistingSignIn(account);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_ON);
        onActivityResult(RC_SIGN_ON, RC_SIGN_ON, signInIntent);
        finish();
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


  //If there's no signed in user, the login button will appear. if there's a signed in user already, then there will be a prompt that offers such information
    //as welcome back user email / user name

    protected void CheckForExistingSignIn(GoogleSignInAccount acct)
    {

        if(acct == null) {
            //GoogleSignInOptions are where we add requests for different types of permissions. As per the google sign in docs, i've used
            //minimal permission requests, only the requestEmIail(). However, if necesarry, this is where we would add them. more information here:
            //https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInOptions#DEFAULT_SIGN_IN
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            findViewById(R.id.sign_in_button).setOnClickListener(this);
            View Button = findViewById(R.id.continue_button);
            Button.setVisibility(View.INVISIBLE);//if user account is null, make our continue button invisible
        }
        else
        {
            View LoginButton = findViewById(R.id.sign_in_button);
            LoginButton.setVisibility(View.INVISIBLE);//if we have a user already, hide the google login button and
            View ContinueButton = findViewById(R.id.continue_button);
            ContinueButton.setVisibility(View.VISIBLE);//make the continue button visible and
            TextView welcomeBanner = (TextView) findViewById(R.id.welcome);
            welcomeBanner.setVisibility(View.VISIBLE);//make our TextView visible so that we can fill it with a warm, friendly, pay us all your money welcome message for the user.
            welcomeBanner.setText("Welcome back " +" " + acct.getEmail() + System.lineSeparator() + acct.getGivenName() + " " + acct.getFamilyName() + System.lineSeparator());
            ContinueButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoogleAuthentication.this.finish();
                    Intent ii = new Intent(getApplicationContext(), LandmarkedMain.class);
                    startActivity(ii);
                }
            });
        }
    }

    @Override protected void onStart()
    {
        super.onStart();

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
            Name = account.getEmail();
            // Signed in successfully, show authenticated UI.
            CheckForExistingSignIn(account);
        } catch (ApiException e) {

            CheckForExistingSignIn(null);
        }
    }
    private void signOut() {
        mGoogleSignInClient.signOut();
    }
    @Override protected void onStop()
    {
        super.onStop();
        //I commented out this line because i don't think our users are going to want to have to log in everytime. The little function I wrote to signout can easily be
        //used if the user selects an option that indicates they DO want to be logged out at each app close.
        //signOut();

    }
    @Override
    public void onBackPressed()
    {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());//If the user tries to hit the back buton as sign in screen,
        if (acct != null)
        {
            setContentView(R.layout.activity_get_sensor_data);// let them if they are already signed in.
        }
        else
        {
            setContentView(R.layout.sign_in_page);//keep them at the sign in page if they aren't.
        }
    }
}
