package com.iwitness.androidapp.libraries;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.iwitness.androidapp.controllers.adapters.KeyValuePairs;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class ConfigurationFactory {
    public ConfigurationFactory() {
        super();
    }

    public ConfigurationFactory(Activity activity) {
        init(activity);
    }

    private KeyValuePairs states;
    private KeyValuePairs heightConfigs;
    private AssetManager assetmanager;
    private Context applicationContext;
    private KeyValuePairs emergencyNumbers;

    public AssetManager assetManager() {
        return assetmanager;
    }

    public Context applicationContext() {
        return applicationContext;
    }

    public void init(Activity activity) {
        assetmanager = activity.getAssets();
        applicationContext = activity.getApplicationContext();
    }

    public KeyValuePairs getState() {
        if (states == null) {
            states = getXmlData("regions.xml", KeyValuePairs.class);
        }

        return states;
    }

    public KeyValuePairs getEmergencyNumbers() {
        if (emergencyNumbers == null) {
            emergencyNumbers = getXmlData("emergency_numbers.xml", KeyValuePairs.class);

        }

        return emergencyNumbers;
    }

    private KeyValuePairs _eyeColors;

    public KeyValuePairs getEyeColors() {
        if (_eyeColors == null) {
            _eyeColors = getXmlData("eye_colors.xml", KeyValuePairs.class);
        }

        return _eyeColors;
    }

    private KeyValuePairs _hairColors;

    public KeyValuePairs getHairColors() {
        if (_hairColors == null) {
            _hairColors = getXmlData("hair_colors.xml", KeyValuePairs.class);
        }

        return _hairColors;
    }

    public KeyValuePairs getTimeZones() {
        return getXmlData("timezones.xml", KeyValuePairs.class);
    }


    private KeyValuePairs _ethnicities;

    public KeyValuePairs getEthnicities() {
        if (_ethnicities == null) {
            _ethnicities = getXmlData("ethnicities.xml", KeyValuePairs.class);
        }

        return _ethnicities;
    }

    private KeyValuePairs _weights;

    public KeyValuePairs getWeights() {
        if (_weights == null) {
            _weights = generateIntList(1, 50, 10);
        }
        return _weights;
    }

    public KeyValuePairs generateIntList(int min, int max) {
        return generateIntList(min, max, 1);
    }

    public KeyValuePairs generateIntList(int min, int max, String key, String display) {
        return generateIntList(min, max, 1, key, display);
    }

    public KeyValuePairs generateIntList(int min, int max, int steps, String key, String display) {
        KeyValuePairs result = new KeyValuePairs(key, display);
        result.list = new ArrayList<KeyValuePairs>();
        Log.e("min","TEST"+min);
        for (int i = min; i <= max; i++) {
            String value = Integer.toString(i * steps);
            String key_weight = Integer.toString(i * steps);
            value = value+" "+"lb";
            Log.e("value","TEST"+value);
            result.list.add(new KeyValuePairs(key_weight, value));
        }
        return result;
    }

    public KeyValuePairs generateIntList(int min, int max, int steps) {
        return generateIntList(min, max, steps, "root", "root");
    }

    public <T> T getXmlData(String fileName, Class<T> input) {
        T result = null;

        try {
            AssetManager manager = assetManager();
            InputStream inputStream = manager.open(fileName);
            Serializer serializer = new Persister();
            result = serializer.read(input, inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String readFile(String path) {
        FileInputStream fis;
        String result = "";
        try {
            File file = new File(path);
            fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            result = new String(data, "UTF-8");
        } catch (Exception ignored) {

        }

        return result;
    }
}
