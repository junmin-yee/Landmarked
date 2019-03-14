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
import java.sql.Connection;

import landmarked.landmarked.Database.AzureConnectionClass;

public class GoogleAuthentication extends AppCompatActivity implements View.OnClickListener {
    static GoogleSignInClient m_GoogleSignInClient;
    GoogleSignInAccount m_account;
    final int RC_SIGN_ON = 9001;
    public String Name;
    Connection m_conn_instance;
    AzureConnectionClass m_azure;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        m_GoogleSignInClient = GoogleSignIn.getClient(this, gso);
        m_conn_instance = m_azure.ConnectionGetInstance();

    }

    //onStart looks for an account already signed in. The flow goes like this:
    //if user is signed in already, skip login screen altogether
    //if user is not already signed in, load login screen
    //on click of login butotn login with google
    //continue to main sensor screen
    @Override protected void onStart()
    {
        super.onStart();
        m_account = GoogleSignIn.getLastSignedInAccount(this);
        updateAuth(m_account);
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
        finish();
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
            //HERE IS WHERE WE NEED TO PUT GETTERS THAT WILL GET ALL OUR USER INFORMATION
            // Signed in successfully, show authenticated UI.
            updateAuth(m_account);
        } catch (ApiException e) {

            updateAuth(null);
        }
    }

    //This function needs to remain very simple. The more logic we put here, the more "jank" we will get in our login screen.
    //If there's something else to be tested at startup, this probably isn't the place
    private void updateAuth(GoogleSignInAccount acct)
    {
        if(acct == null)
        {

            setContentView(R.layout.sign_in_page);
            findViewById(R.id.sign_in_button).setOnClickListener(this);
        }
        else
        {
            finish();
        }
    }


    protected GoogleSignInAccount getUser() {
        try {
            return m_account;
        } catch (NullPointerException e) {

            //some container in the gui to display a message?
            return null;
        }
    }


    protected void signOut()
    {
        m_GoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       //Honestly i have no idea what's supposed to fill this function

                    }
                });

        finish();

    }
    @Override protected void onStop()
    {
        super.onStop();

    }
    @Override
    public void onBackPressed()
    {
        //in the event that the user presses the back button from login screen without logging in, finish the main activity as well as the current activity.
        //in my opinion, there't not really much else to do if the user doesn't want to log in. Possible we could use an offline mode?
        LandmarkedMain.getInstance().finish();
        finish();

    }
}
