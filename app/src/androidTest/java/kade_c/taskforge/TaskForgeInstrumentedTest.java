package kade_c.taskforge;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import kade_c.taskforge.activities.SplashActivity;
import kade_c.taskforge.activities.TaskForgeActivity;
import kade_c.taskforge.utils.Tutorial;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TaskForgeInstrumentedTest {

    private Context appContext;

    @Before
    public void initTests() {
        appContext = InstrumentationRegistry.getTargetContext();
    }

    /**
     * Checks if our apps resources are correctly stored / used
     */
    @Test
    public void checkRessourceValidity() throws Exception {
        String resString = appContext.getResources().getString(R.string.app_name);

        assertEquals("Task Forge", resString);
    }

    /**
     * Tests launch of TaskForgeActivity
     */
    @Test
    public void SplashActivityLaunch() throws Exception {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {}
            @Override
            public void onServiceDisconnected(ComponentName componentName) {}
        };
        Intent intent = new Intent(appContext, SplashActivity.class);
        appContext.bindService(intent, serviceConnection, 0);
    }


    /**
     * Tests launch of TaskForgeActivity
     */
    @Test
    public void TFActivityLaunch() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {}
            @Override
            public void onServiceDisconnected(ComponentName componentName) {}
        };
        Intent intent = new Intent(appContext, TaskForgeActivity.class);
        appContext.bindService(intent, serviceConnection, 0);
    }

    @Test
    public void useAppContext() throws Exception {
        assertEquals("kade_c.taskforge", appContext.getPackageName());
    }
}
