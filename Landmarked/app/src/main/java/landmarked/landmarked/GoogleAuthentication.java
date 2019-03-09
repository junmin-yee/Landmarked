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
import com.google.android.gms.signin.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.widget.TextView;
import java.util.concurrent.TimeUnit ;
import android.support.annotation.NonNull;

public class GoogleAuthentication extends AppCompatActivity implements View.OnClickListener {
    static GoogleSignInClient m_GoogleSignInClient;
    GoogleSignInAccount m_account;
    final int RC_SIGN_ON = 9001;
    public String Name;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.sign_in_page);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        m_GoogleSignInClient = GoogleSignIn.getClient(this, gso);
        m_account = GoogleSignIn.getLastSignedInAccount(this);
    }




    // sign_in_button is default name for google sign in button in our log in page's xml file
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;

            case R.id.sign_out_button:
                signOut();
                break;

        }
    }
    private void signIn() {
        Intent signInIntent = m_GoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_ON);
        onActivityResult(RC_SIGN_ON, RC_SIGN_ON, signInIntent);
        //finish();
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
            m_account = completedTask.getResult(ApiException.class);
            Name = m_account.getEmail();
            // Signed in successfully, show authenticated UI.
            updateAuth(m_account);
        } catch (ApiException e) {

            updateAuth(null);
        }
    }
    private void updateAuth(GoogleSignInAccount acct)
    {
        if(acct == null)
        {

        }
        else
        {

        }
    }


    protected GoogleSignInAccount getUser()//returns true only if m_account is null
    {
        return m_account;
    }



    @Override protected void onStart()
    {
        super.onStart();
        setContentView(R.layout.sign_in_page);//set to our sign in page, our existing user check method has some logic that will either hide or make visible buttons depending on the case

        //  findViewById(R.id.sign_in_button).setOnClickListener(this);
      //  View ContinueButton = findViewById(R.id.continue_button);
      //  ContinueButton.setVisibility(View.INVISIBLE);//make the continue button visible and
        //CheckForExistingSignIn(m_account);
      //  if(m_account == null)
     //   {
      //      Intent ii = m_GoogleSignInClient.getSignInIntent();
      //      startActivityForResult(ii, RC_SIGN_ON);
      //  }
      //  else
      //  {
       //     finish();
      //  }
    }


    protected void signOut()
    {
        m_GoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //finish();
                        setContentView(R.layout.sign_in_page);
                    }
                });

    }
    @Override protected void onStop()
    {
        super.onStop();
        //I commented out this line because i don't think our users are going to want to have to log in everytime. The little function I wrote to signout can easily be
        //used if the user selects an option that indicates they DO want to be logged out at each app close.
       // signOut();

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
