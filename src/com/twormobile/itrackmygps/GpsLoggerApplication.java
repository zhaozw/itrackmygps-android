package com.twormobile.itrackmygps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.twormobile.itrackmygps.android.Log;
import android.widget.EditText;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.twormobile.itrackmygps.android.DialogBoxFactory;

public class GpsLoggerApplication extends Application {
    private static final String TAG = GpsLoggerApplication.class.getSimpleName();

    public static String LOCATION_NEW_URL;
    public static String REGISTER_URL;
    public static String LOGIN_URL;

    private DeviceUUIDFactory uuidFactory;
    private String deviceId;

    private GpsManager gpsManager;
    private boolean bServiceRun;
    private RequestQueue queue;

    private SharedPreferences prefs;

    private boolean bLoggedIn = false;
    private String username;

    @Override
    public void onCreate() {
        setURLs();

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        checkUser();

        Log.i(TAG, "onCreated");
        uuidFactory = new DeviceUUIDFactory(getApplicationContext());
        deviceId = uuidFactory.getDeviceUuid().toString();

        gpsManager = GpsManager.get(getApplicationContext());
        queue = Volley.newRequestQueue(this);
        bServiceRun = false;
    }

    @Override
    public void onTerminate(){
        Log.i(TAG, "onTerminated");
    }

    public GpsManager getGpsManager() {
        return gpsManager;
    }

    public RequestQueue getVolleyRequestQueue() {
        return queue;
    }

    public boolean isServiceRunning(){
        return bServiceRun;
    }

    public void setServiceRunning(boolean b){
        bServiceRun = b;
    }

    public String getUUID(){
        return deviceId;
    }

    public void setURLs(){
        LOCATION_NEW_URL = getResources().getString(R.string.NEW_LOCATION_URL);
        REGISTER_URL = getResources().getString(R.string.REGISTER_URL);
        LOGIN_URL =  getResources().getString(R.string.LOGIN_URL);
    }

    public boolean isLoggedIn() {
        return bLoggedIn;
    }

    public String getUsername() { return username; }

    private void checkUser() {
        username = prefs.getString("username", null);
        if(username == null) {
            bLoggedIn = false;
        }
        else {
            bLoggedIn = true;
        }
    }

    public void showDialog(String title, String message, Activity activity){
        AlertDialog dialog = DialogBoxFactory.setDialog(title, message, activity);
        dialog.show();
    }

    public void setLoggedIn(String u){
        username = u;
        bLoggedIn = true;
    }

    public boolean isUsernameValid(EditText etxt, Activity activity){
        boolean valid = false;
        String value = String.valueOf(etxt.getText());
        if(value != null && value.trim().length() > 0) {
            int length = value.trim().length();
            if(length > 0 && length <= 16) {
                valid = true;
            }
        }

        if(!valid){
            String message = getResources().getString(R.string.invalid_username);
            showDialog("Error", message, activity);
        }

        return valid;
    }

    public boolean isEmailValid(EditText etxt, Activity activity){
        boolean valid = false;
        String value = String.valueOf(etxt.getText());
        if(value != null && value.trim().length() > 0) {
            valid = true;
        }

        if(!valid){
            String message = getResources().getString(R.string.invalid_email);
            showDialog("Error", message, activity);
        }

        return valid;
    }

    public boolean isPasswordValid(EditText etxt, Activity activity){
        boolean valid = true;
        String password = String.valueOf(etxt.getText());

        if(password.isEmpty()){
            valid = false;
        }

        if(!valid){
            String message = getResources().getString(R.string.invalid_password);
            showDialog("Error", message, activity);
        }

        return valid;
    }

    public void saveLogin(String username, String email, String pin){
        final String uuid = getUUID();

        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putString("uuid", uuid);
        editor.putString("pin", pin);
        editor.commit();

        setLoggedIn(username);
    }


}
