package com.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.hardware.camera2.CameraManager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.myapplication.models.ConversionModel;
import com.myapplication.networks.HTTPAsyncTask;
import com.myapplication.networks.TextToMorseAsyncTask;

import java.security.Policy;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button toMorseButton;
    private Button toTextButton;
    private EditText inputToConvert;
    private TextView convertedText;

    ConversionModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        /************************************************/
        /** Retrieve text_to_morse JSON with HTTP call **/
        HTTPAsyncTask task = new HTTPAsyncTask();
        task.setHTTPListener(new HTTPAsyncTask.HTTPListener() {
            @Override
            public void onHTTPCallback(ConversionModel response) {
                model = response;
            }
        });
        task.execute(getString(R.string.textToMorseAPI), getString(R.string.morseToTextAPI));
        /************************************************/

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        /*********************************************************************************/
        /**                          TextToMorse Conversion Process                     **/
        toMorseButton = findViewById(R.id.to_morse_button);
        inputToConvert = findViewById(R.id.input_editText);
        convertedText = findViewById(R.id.converted_text);
        toTextButton = findViewById(R.id.to_text_button);

        toMorseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextToMorseAsyncTask task = new TextToMorseAsyncTask();
                task.setConversionListener(new TextToMorseAsyncTask.ConversionListener() {
                    @Override
                    public void onConversionCallback(String response) {
                        model.setOutput(response);
                        convertedText.setText(model.getOutput());
                    }
                });
                model.setInput(inputToConvert.getText().toString());
                task.execute(model.getInput(), model.getTextToMorseURL());
            }
        });

        toTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextToMorseAsyncTask task = new TextToMorseAsyncTask();
                task.setConversionListener(new TextToMorseAsyncTask.ConversionListener() {
                    @Override
                    public void onConversionCallback(String response) {
                        model.setOutput(response);
                        convertedText.setText(model.getOutput());
                    }
                });
                model.setInput(inputToConvert.getText().toString());
                task.execute(model.getInput(), model.getMorseToTextURL());
            }
        });

        /************************************************************************************/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /*********************************************************************************/
    /**                          Flashlight                                         **/

    //turn on
    camera = Camera.open()
    Policy.Parameters p = camera.getParameters();
	p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH)
	camera.setParameters(p)
	camera.startPreview()

    //turn off
    camera = Camera.open()
    Policy.Parameters p = camera.getParameters();
	p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF)
	camera.setParameters(p)
	camera.stopPreview()

    public class FlashLightActivity extends Activity {

        //flag to detect flash is on or off
        private boolean isLighOn = false;

        private Camera camera;

        private Button button;

        @Override
        protected void onStop() {
            super.onStop();

            if (camera != null) {
                camera.release();
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);

            button = (Button) findViewById(R.id.buttonFlashlight);

            Context context = this;
            PackageManager pm = context.getPackageManager();

            // if device support camera?
            if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Log.e("err", "Device has no camera!");
                return;
            }

            camera = Camera.open();
            final Policy.Parameters p = camera.getParameters();

            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    if (isLighOn) {

                        Log.i("info", "torch is turn off!");

                        p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(p);
                        camera.stopPreview();
                        isLighOn = false;

                    } else {

                        Log.i("info", "torch is turn on!");

                        p.setFlashMode(android.hardware.Camera.Parameters.FLASH_MODE_TORCH);

                        camera.setParameters(p);
                        camera.startPreview();
                        isLighOn = true;

                    }

                }
            });

        }
    }


    /************************************************************************************/


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
