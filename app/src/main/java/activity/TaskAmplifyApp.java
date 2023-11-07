package activity;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.core.Amplify;

public class TaskAmplifyApp extends Application {
    public static final String TAG="TaskApp";
    @Override
    public void onCreate(){
        super.onCreate();

        try{
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
        }catch (AmplifyException ae){
            Log.e(TAG,"Error Initalizing Amplify" + ae.getMessage());
        }
    }
}
