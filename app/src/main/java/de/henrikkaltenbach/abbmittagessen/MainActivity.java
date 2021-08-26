package de.henrikkaltenbach.abbmittagessen;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvWeekday;
    private TextView tvDate;
    private TextView tvLunch;
    private TextView tvVegetarianLunch;
    private TextView tvDessert;
    private ProgressBar progressBar;
    private int dayOfWeek;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvWeekday = findViewById(R.id.tvWeekday);
        tvDate = findViewById(R.id.tvDate);
        tvLunch = findViewById(R.id.tvLunch);
        tvVegetarianLunch = findViewById(R.id.tvVegetarianLunch);
        tvDessert = findViewById(R.id.tvDessert);
        progressBar = findViewById(R.id.progressBar);
        getDate();
        scrape();
    }

    private void getDate() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        tvWeekday.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.GERMANY));
        tvDate.setText(sdf.format(today));
    }

    private void scrape() {
        new Thread(() -> {
            Document document = null;
            try {
                document = Jsoup.connect(
                        "https://new.abb.com/de/ueber-uns/gesellschaften/abb-wirtschaftsbetriebe"
                ).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (document != null) {
                /*Elements weekElements = document.select("h1.tile-headline");
                String[] weeks = {
                        weekElements.get(1).text().substring(15),
                        weekElements.get(2).text().substring(15)
                };

                TextView textView = findViewById(R.id.textView);
                textView.post(() -> {
                    textView.setText(weeks[0] + "\n" + weeks[1]);
                });*/

                Element meal = document.getElementById("Content_C1079_Col0" + (dayOfWeek - 2));
                //Element meal = document.getElementById("Content_C1139_Col0" + (dayOfWeek - 2));
                if (meal != null) {
                    tvLunch.post(() -> {
                        tvLunch.setText(meal.text());
                    });
                }

                Element vegetarianLunch = document.getElementById("Content_C1080_Col0" + (dayOfWeek - 2));
                //Element vegetarianLunch = document.getElementById("Content_C1140_Col0" + (dayOfWeek - 2));
                if (vegetarianLunch != null) {
                    final String vegetarian = vegetarianLunch.text().substring(13);
                    tvVegetarianLunch.post(() -> {
                        tvVegetarianLunch.setText(vegetarian);
                    });
                }

                Element dessertElement = document.getElementById("Content_C1081_Col0" + (dayOfWeek - 2));
                //Element dessertElement = document.getElementById("Content_C1141_Col0" + (dayOfWeek - 2));
                if (dessertElement != null) {
                    final String dessert = dessertElement.text().substring(8);
                    tvDessert.post(() -> {
                        tvDessert.setText(dessert);
                    });
                }

                progressBar.post(() -> {
                    progressBar.setVisibility(View.GONE);
                });
            }
        }).start();
    }
}