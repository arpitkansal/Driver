package com.example.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FetchActivity extends AppCompatActivity {
    ListView listViewAreas;
    double[] lats, longs;
    String[] area_names;
   public String driver_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch);
       // spinner_area = findViewById(R.id.spinner);
        Button fetch = findViewById(R.id.button_fetch);
        listViewAreas = findViewById(R.id.listViewAreas);
        Button toMaps = findViewById(R.id.button_tomap);


        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAreas();
            }
        });

        toMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FetchActivity.this, MapActivity.class);
                intent.putExtra("lats",lats);
                intent.putExtra("longs",longs);
                intent.putExtra("names",area_names);
                startActivity(intent);
            }
        });
    }

    private void getAreas() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(FetchPathApi.base_url).addConverterFactory(GsonConverterFactory.create()).build();

        FetchPathApi api = retrofit.create(FetchPathApi.class);
        Call<List<Areas>> call = api.getAreas();

        call.enqueue(new Callback<List<Areas>>() {
            @Override
            public void onResponse(Call<List<Areas>> call, Response<List<Areas>> response) {
                List<Areas> areasList = response.body();

                //Creating an String array for the ListView
                String[] areas = new String[areasList.size()];
                lats = new double[areasList.size()];
                longs = new double[areasList.size()];
                area_names = new String[areasList.size()];

                //looping through all the heroes and inserting the names inside the string array
                for (int i = 0; i < areasList.size(); i++) {
                    areas[i] = areasList.get(i).getName();
                    lats[i] = areasList.get(i).getLats();
                    longs[i] = areasList.get(i).getLongs();
                    area_names[i] = areasList.get(i).getName();
                }
                for(int j = 0;j<areasList.size();j++){
                    Log.d("area_j",area_names[j]);
                }
                //displaying the string array into listview
                listViewAreas.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, areas));
            }
            @Override
            public void onFailure(Call<List<Areas>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
