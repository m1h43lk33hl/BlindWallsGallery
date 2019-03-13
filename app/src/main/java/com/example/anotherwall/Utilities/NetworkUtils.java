package com.example.anotherwall.Utilities;

import android.net.Uri;
import android.renderscript.ScriptGroup;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private final static String BLIND_WALLS_BASE_URL = "https://api.blindwalls.gallery";

    private final static String NASA_API_KEY = "uv5c0ib7TLHcZ6GfqcrA3TgM5QvM0tnuAZU9pTIe";



    public static URL buildUrl(String preferenceURL)
    {
        Uri builtUri = Uri.parse(preferenceURL);

        URL url = null;

        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL builtPictureUrl(String pictureFragment)
    {
        Uri builtUri = Uri.parse(BLIND_WALLS_BASE_URL).buildUpon()
                .appendPath(pictureFragment)
                .build();

        URL url = null;

        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

        try
        {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        }
        finally {
            urlConnection.disconnect();
        }
    }


}
