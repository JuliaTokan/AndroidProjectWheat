package com.example.wheatinfo.db;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;

import com.example.wheatinfo.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphWheatActivity extends Activity {
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Graph Wheat");

        setContentView(R.layout.activity_graph_record);

        dbManager = new DBManager(this);
        dbManager.open();

        Cursor cursor = dbManager.fetch();
        DataPoint[] dataPoints = new DataPoint[cursor.getCount()];
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int i = 0;
                do {
                    dataPoints[i] = new DataPoint(
                            cursor.getInt(cursor.getColumnIndex("year")),
                            cursor.getInt(cursor.getColumnIndex("yield")));
                    i++;
                } while (cursor.moveToNext());
            }
        }
        final GraphView graph = findViewById(R.id.myGraph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        graph.addSeries(series);
    }
}
