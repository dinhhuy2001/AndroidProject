package com.example.appcovid;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.hbb20.CountryCodePicker;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mtodaytotal, mtotal, mactive, mrecovered, mtodayrecovered, mdeaths, mtodaydeaths;

    String country;
    TextView mfilter;
    String[] types = {"cases", "active", "recovered", "deaths"};
    PieChart mPieChart;
    com.example.appcovid.Adapter adapter;

    private List<ModelClass> modelClassList;
    private List<ModelClass> modelClassList2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        countryCodePicker = findViewById(R.id.ccp);
        mtotal = findViewById(R.id.totalcase);
        mtodaytotal = findViewById(R.id.todaytotal);
        mactive = findViewById(R.id.activecase);
        mrecovered = findViewById(R.id.recoveredcase);
        mtodayrecovered = findViewById(R.id.todayrecovered);
        mdeaths = findViewById(R.id.totaldeath);
        mtodaydeaths = findViewById(R.id.todaydeath);
        mPieChart = findViewById(R.id.piechart);
        modelClassList = new ArrayList<>();
        modelClassList2 = new ArrayList<>();

        ApiUtilities.getAPIInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList2.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter = new Adapter(getApplicationContext(), modelClassList2);

        countryCodePicker.setAutoDetectedCountry(true);
        country = countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country = countryCodePicker.getSelectedCountryName();
                fetchdata();
            }
        });
        fetchdata();
    }


    private void fetchdata() {

        ApiUtilities.getAPIInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList.addAll(response.body());
                for (int i = 0; i < modelClassList.size(); i++){
                    if (modelClassList.get(i).getCountry().equals(country)){

                        mactive.setText(modelClassList.get(i).getActive());
                        mtodaydeaths.setText(modelClassList.get(i).getTodayDeaths());
                        mtodayrecovered.setText(modelClassList.get(i).getTodayRecovered());
                        mtodaytotal.setText(modelClassList.get(i).getTodayCases());
                        mtotal.setText(modelClassList.get(i).getCases());
                        mdeaths.setText(modelClassList.get(i).getDeaths());
                        mrecovered.setText(modelClassList.get(i).getRecovered());

                        int active, total, recovered, deaths;

                        active = Integer.parseInt(modelClassList.get(i).getActive());
                        total = Integer.parseInt(modelClassList.get(i).getCases());
                        recovered = Integer.parseInt(modelClassList.get(i).getRecovered());
                        deaths = Integer.parseInt(modelClassList.get(i).getDeaths());
                        updateGraph(total, active, recovered, deaths);

                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

    }

    private void updateGraph(int total, int active, int recovered, int deaths) {

        mPieChart.clearChart();
        mPieChart.addPieSlice(new PieModel("Total", total, Color.parseColor("#FFB701")));
        mPieChart.addPieSlice(new PieModel("Active", total, Color.parseColor("#FF4CAF50")));
        mPieChart.addPieSlice(new PieModel("Recovered", total, Color.parseColor("#38ACCD")));
        mPieChart.addPieSlice(new PieModel("Deaths", total, Color.parseColor("#F55c47")));
        mPieChart.startAnimation();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String item = types[i];
        mfilter.setText(item);
        adapter.filter(item);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}