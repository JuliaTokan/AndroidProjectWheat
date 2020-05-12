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

public class ModifyWheatActivity extends Activity implements OnClickListener {

    private EditText yearEditText;
    private EditText productionEditText;
    private EditText yieldEditText;
    private Button updateBtn, deleteBtn;

    private long _id;

    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Modify Wheat");

        setContentView(R.layout.activity_modify_record);

        dbManager = new DBManager(this);
        dbManager.open();

        yearEditText = (EditText) findViewById(R.id.mod_edit_year);
        productionEditText = (EditText) findViewById(R.id.mod_edit_production);
        yieldEditText = (EditText) findViewById(R.id.mod_edit_yield);

        updateBtn = (Button) findViewById(R.id.btn_update);
        deleteBtn = (Button) findViewById(R.id.btn_delete);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String year = intent.getStringExtra("year");
        String production = intent.getStringExtra("production");
        String yield = intent.getStringExtra("yield");

        _id = Long.parseLong(id);

        yearEditText.setText(year);
        productionEditText.setText(production);
        yieldEditText.setText(yield);

        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:

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

                long year = Long.parseLong(yearEditText.getText().toString());
                long production = Long.parseLong(productionEditText.getText().toString());
                long yield = Long.parseLong(yieldEditText.getText().toString());

                if(year>2020||year<1950) {
                    yearEditText.setError("Incorrect year");
                    return;
                }

                dbManager.update(_id, year, production, yield);
                this.returnHome();
                break;

            case R.id.btn_delete:
                dbManager.delete(_id);
                this.returnHome();
                break;
        }
    }

    public void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(), WheatListActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home_intent);
    }
}
