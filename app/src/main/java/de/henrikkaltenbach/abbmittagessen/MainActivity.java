package de.henrikkaltenbach.abbmittagessen;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
    private ProgressBar loadingCircle;
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
        loadingCircle = findViewById(R.id.progressBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDate();
        scrapeLunch();
    }

    private void getDate() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        tvWeekday.setText(calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.GERMANY));
        tvDate.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).format(today));
    }

    private void scrapeLunch() {
        loadingCircle.setVisibility(View.VISIBLE);
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
                Element meal = document.getElementById("Content_C1079_Col0" + dayOfWeek);
                if (meal != null) {
                    tvLunch.post(() -> tvLunch.setText(meal.text()));
                }

                Element vegetarianLunch = document.getElementById("Content_C1080_Col0" + dayOfWeek);
                if (vegetarianLunch != null) {
                    final String vegetarian = vegetarianLunch.text().substring(13);
                    tvVegetarianLunch.post(() -> tvVegetarianLunch.setText(vegetarian));
                }

                Element dessertElement = document.getElementById("Content_C1081_Col0" + dayOfWeek);
                if (dessertElement != null) {
                    final String dessert = dessertElement.text().substring(8);
                    tvDessert.post(() -> tvDessert.setText(dessert));
                }

                loadingCircle.post(() -> loadingCircle.setVisibility(View.GONE));
            }
        }).start();
    }
}