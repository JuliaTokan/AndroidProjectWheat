package com.example.wheatinfo.db;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wheatinfo.R;
import com.example.wheatinfo.contacts.ContactActivity;
import com.example.wheatinfo.files.AddNoteActivity;
import com.example.wheatinfo.map.MapActivity;


public class WheatListActivity extends AppCompatActivity implements View.OnClickListener{

    private DBManager dbManager;

    private ListView listView;

    private Button noteBtn;
    private Button graphBtn;
    private Button moreBtn;
    private Button avrBtn;

    private Button agronomsBtn;
    private Button mapBtn;

    private SimpleCursorAdapter adapter;

    final String[] from = new String[]{DatabaseHelper.ID,
            DatabaseHelper.YEAR, DatabaseHelper.PRODUCTION, DatabaseHelper.YIELD};

    final int[] to = new int[]{R.id.id, R.id.year, R.id.production, R.id.yield};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_emp_list);

        noteBtn = (Button) findViewById(R.id.btn_add_note);
        noteBtn.setOnClickListener(this);

        graphBtn = (Button) findViewById(R.id.btn_graph);
        graphBtn.setOnClickListener(this);

        moreBtn = (Button) findViewById(R.id.btn_more_info);
        moreBtn.setOnClickListener(this);

        avrBtn = (Button) findViewById(R.id.btn_average);
        avrBtn.setOnClickListener(this);

        agronomsBtn = (Button) findViewById(R.id.btnAgronoms);
        agronomsBtn.setOnClickListener(this);

        mapBtn = (Button) findViewById(R.id.btnMap);
        mapBtn.setOnClickListener(this);

        dbManager = new DBManager(this);
        dbManager.open();
        Cursor cursor = dbManager.fetch();

        if(cursor.getCount()==0){
            noteBtn.setX(50);
            noteBtn.setY(1700);
            //graphBtn.setVisibility(View.INVISIBLE);
            moreBtn.setVisibility(View.INVISIBLE);
            avrBtn.setVisibility(View.INVISIBLE);
        }

        listView = (ListView) findViewById(R.id.list_view);
        listView.setEmptyView(findViewById(R.id.empty));

        adapter = new SimpleCursorAdapter(this, R.layout.activity_view_record, cursor, from, to, 0);
        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);

        // OnCLickListiner For List Items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {
                TextView idTextView = (TextView) view.findViewById(R.id.id);
                TextView yearTextView = (TextView) view.findViewById(R.id.year);
                TextView productionTextView = (TextView) view.findViewById(R.id.production);
                TextView yieldTextView = (TextView) view.findViewById(R.id.yield);

                String id = idTextView.getText().toString();
                String year = yearTextView.getText().toString();
                String production = productionTextView.getText().toString();
                String yield = yieldTextView.getText().toString();

                Intent modify_intent = new Intent(getApplicationContext(), ModifyWheatActivity.class);
                modify_intent.putExtra("year", year);
                modify_intent.putExtra("production", production);
                modify_intent.putExtra("yield", yield);
                modify_intent.putExtra("id", id);

                startActivity(modify_intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add_record) {

            Intent add_mem = new Intent(this, AddWheatActivity.class);
            startActivity(add_mem);

        }
        if (id == R.id.help) {

            buttonHelpOnClick();

        }
        return super.onOptionsItemSelected(item);
    }

    private void buttonMore(){
        Cursor cursor = dbManager.findAllMoreThan22mln();

        String result = "";
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    result+=cursor.getString(cursor.getColumnIndex("year"));
                    result+="\n     production : "+cursor.getString(cursor.getColumnIndex("production"));
                    result+="\n     yield : "+cursor.getString(cursor.getColumnIndex("yield"));
                    result+="\n\n";
                } while (cursor.moveToNext());
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("More than 22mln tons:")
                .setMessage(result.equals("")?"No wheat found":result)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void buttonDeveloperOnClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View viewImg = factory.inflate(R.layout.developer_info, null);

        builder.setTitle("About developer")
                .setView(viewImg)
                .setMessage("Name: Yulia Tokan \nGroup: TTP-3 \nCourse: 3")
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void buttonHelpOnClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        builder.setTitle("Help wheat info")
                .setMessage("On this page you can see all information about wheat yield.\n" +
                        "If you want to add new info click + in the upper corner.\n" +
                        "If you want to modify or delete info click on the desired element.\n" +
                        "Click ADD NOTE if you want to save useful information to file;\n" +
                        "Click GRAPH if you want to view yield to production ratio;\n" +
                        "Click MORE @@ MLN TONS to watch statistics production more than 22 mln tons;\n" +
                        "Click AVERAGE INFO to watch average statistics.\n" +
                        "Use green buttons for navigation.")
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void buttonAverage(){
        Cursor cursor = dbManager.findAverageProduction();

        String result = "";
        if (cursor != null) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex("AVG(production)"));
        } else
            result = "Not found";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Average production:")
                .setMessage(result)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_note:
                Intent notes_mem = new Intent(this, AddNoteActivity.class);
                startActivity(notes_mem);
                break;
            case R.id.btn_graph:
                Intent graph_mem = new Intent(this, GraphWheatActivity.class);
                startActivity(graph_mem);
                break;
            case R.id.btn_more_info:
                buttonMore();
                break;
            case R.id.btn_average:
                buttonAverage();
                break;
            case R.id.btnAgronoms:
                Intent agro_mem = new Intent(this, ContactActivity.class);
                startActivity(agro_mem);
                break;
            case R.id.btnMap:
                Intent map_mem = new Intent(this, MapActivity.class);
                startActivity(map_mem);
                break;
        }
    }
}
