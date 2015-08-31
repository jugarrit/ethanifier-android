package io.supportkit.ethanifier;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.supportkit.core.User;
import io.supportkit.ethanifier.fragments.LoggedInFragment;
import io.supportkit.ethanifier.fragments.OnboardingFragment;

public class MainActivity extends AppCompatActivity implements OnboardingFragment.OnUserLoginListener {
    private static SharedPreferences settings;
    private FragmentManager fragmentManager;
    private OnboardingFragment onboardingFragment;
    private LoggedInFragment loggedInFragment;
    private boolean userLoggedIn;

    private final User user = User.getCurrentUser();
    private final AsyncTask asyncAPICaller = new AsyncSlackCaller();
    private final String[] slackMethods = new String[] {"slackPresence", "slackUserInfo"};
    private final String apiEndpoint = "ethanifier.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        settings = getPreferences(Context.MODE_PRIVATE);

        fragmentManager = getSupportFragmentManager();
        onboardingFragment = new OnboardingFragment();
        loggedInFragment = new LoggedInFragment();
        userLoggedIn = settings.getBoolean("userLoggedIn", false);

        asyncAPICaller.execute((Object[]) slackMethods);

        if(userLoggedIn) {
            addSomeProperties(user);
            showLoggedInFragment();
        }
        else {
            showOnboardingFragment();
        }
    }

    private void addSomeProperties(final User user) {
        final Map<String, Object> customProperties = new HashMap<>();

        user.setFirstName(settings.getString("firstName", "Anonymous"));
        user.setLastName(settings.getString("lastName", "Anonymous"));
        user.setEmail(settings.getString("email", "anonymous@anon.co"));
        user.addProperties(customProperties);
    }

    @Override
    public void onUserLoggedIn(String firstName, String lastName, String email) {
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("firstName", firstName);
        editor.putString("lastName", lastName);
        editor.putString("email", email);
        editor.putBoolean("userLoggedIn", true);

        editor.commit();
        addSomeProperties(user);
        showLoggedInFragment();
    }

    private void showLoggedInFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, loggedInFragment)
                .commit();
    }

    private void showOnboardingFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, onboardingFragment)
                .commit();
    }

    private void onSlackSuccess(JSONObject[] slackResponses) throws JSONException {
        String presence, name, avatarUrl;

        presence = slackResponses[0].getString("presence");
        name = slackResponses[1].getJSONObject("user").getString("real_name");
        avatarUrl = slackResponses[1].getJSONObject("user").getJSONObject("profile").getString("image_192");

        loggedInFragment.onSlackResponse(presence, name, avatarUrl);
    }

    private class AsyncSlackCaller extends AsyncTask<String, Void, JSONObject[]> {
        private OkHttpClient client;

        @Override
        protected JSONObject[] doInBackground(String... methods) {
            client = new OkHttpClient();

            try {
                int count = methods.length;
                JSONObject[] response = new JSONObject[methods.length];

                for (int i = 0; i < count; i++) {
                    response[i] = get(apiEndpoint + "/" + methods[i]);
                }

                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute (JSONObject[] result) {
            try {
                onSlackSuccess(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject get(String url) throws IOException, JSONException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();

            return new JSONObject(response.body().string());
        }
    }
}
