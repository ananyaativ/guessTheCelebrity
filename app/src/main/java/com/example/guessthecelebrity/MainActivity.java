package com.example.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    int num;
    String[] names = new String[100];
    String[] Images = new String[105];
    int pos;
    Button button0;
    Button button1;
    Button button2;
    Button button3;


    public void guess(View view) {
        if (view.getTag().toString().equals(Integer.toString(pos))) {
            Toast.makeText(this, "CORRECT!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "INCORRECT! It is " + names[num], Toast.LENGTH_SHORT).show();
        }
        newQuestion();

    }

    public void newQuestion() {
        try {
            Random random = new Random();
            int incorrectNames;
            String[] buttonNames = new String[4];
            num = random.nextInt(100);
            pos = random.nextInt(4);
            downloadImage dtask = new downloadImage();
            Bitmap myImage = dtask.execute(Images[num + 5]).get();
            imageView.setImageBitmap(myImage);

            for (int i = 0; i < 4; i++) {
                if (i == pos)
                    buttonNames[i] = names[num];
                else {
                    incorrectNames = random.nextInt(100);
                    while (incorrectNames == num) {
                        incorrectNames = random.nextInt(100);
                    }
                    buttonNames[i] = names[incorrectNames];
                }
            }

            button0.setText(buttonNames[0]);
            button1.setText(buttonNames[1]);
            button2.setText(buttonNames[2]);
            button3.setText(buttonNames[3]);
        } catch (Exception e) {
            e.fillInStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);


        downloadContent task = new downloadContent();
        String result = null;

        int i = 0;
        int j = 0;
        try {
            result = task.execute("https://www.imdb.com/list/ls052283250/").get();
            String[] split = result.split("<div class=\"footer filmosearch\">");
            Pattern p = Pattern.compile("<img alt=\"(.*?)\"");
            Matcher m = p.matcher(split[0]);
            while (m.find()) {
                names[i] = m.group(1);
                i++;
            }

            p = Pattern.compile("src=\"(.*?)\"");
            m = p.matcher(split[0]);
            while (m.find()) {
                Images[j] = m.group(1);
                j++;
                //Log.i("Images",m.group(1));
            }

        } catch (Exception e) {
            e.fillInStackTrace();
        }
        newQuestion();


    }

    public static class downloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                return BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class downloadContent extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            StringBuilder result = new StringBuilder();
            URL url;
            HttpURLConnection connection;
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result.append(current);
                    data = reader.read();
                }
                return result.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "failed";
            }
        }
    }
}


