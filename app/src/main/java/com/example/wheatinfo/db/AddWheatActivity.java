package com.example.wheatinfo.db;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.wheatinfo.R;

public class AddWheatActivity extends Activity implements OnClickListener {

    private Button addTodoBtn;
    private EditText yearEditText;
    private EditText productionEditText;
    private EditText yieldEditText;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Add Wheat");

        setContentView(R.layout.activity_add_record);

        yearEditText = (EditText) findViewById(R.id.subject_edit_year);
        productionEditText = (EditText) findViewById(R.id.subject_edit_production);
        yieldEditText = (EditText) findViewById(R.id.subject_edit_yield);

        addTodoBtn = (Button) findViewById(R.id.add_record);

        dbManager = new DBManager(this);
        dbManager.open();
        addTodoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_record:

                if(TextUtils.isEmpty(yearEditText.getText().toString())) {
                    yearEditText.setError("Empty year");
                    return;
                }
                if(TextUtils.isEmpty(productionEditText.getText().toString())) {
                    productionEditText.setError("Empty production");
                    return;
                }
                if(TextUtils.isEmpty(yieldEditText.getText().toString())) {
                    yieldEditText.setError("Empty yield");
                    return;
                }

                final long year = Long.parseLong(yearEditText.getText().toString());
                final long production = Long.parseLong(productionEditText.getText().toString());
                final long yield = Long.parseLong(yieldEditText.getText().toString());

                if(year>2020||year<1950) {
                    yearEditText.setError("Incorrect year");
                    return;
                }

                dbManager.insert(year, production, yield);

                Intent main = new Intent(AddWheatActivity.this, WheatListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(main);
                break;
        }
    }

}