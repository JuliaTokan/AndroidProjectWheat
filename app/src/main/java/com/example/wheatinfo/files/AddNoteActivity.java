package com.example.wheatinfo.files;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.wheatinfo.R;
import com.example.wheatinfo.db.WheatListActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AddNoteActivity extends Activity implements OnClickListener {

    private Button noteBtn;
    private EditText subjEditText;
    private EditText textEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Add Wheat");

        setContentView(R.layout.activity_add_note);

        subjEditText = (EditText) findViewById(R.id.note_subject);
        textEditText = (EditText) findViewById(R.id.note_text);

        noteBtn = (Button) findViewById(R.id.note);
        noteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.note:

                if(TextUtils.isEmpty(subjEditText.getText().toString())) {
                    subjEditText.setError("Empty subject");
                    return;
                }
                if(TextUtils.isEmpty(textEditText.getText().toString())) {
                    textEditText.setError("Empty text");
                    return;
                }

                final String subj = subjEditText.getText().toString();
                final String text = textEditText.getText().toString();

                try {
                    writeToFile(subj, text);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent main = new Intent(AddNoteActivity.this, WheatListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(main);
                break;
        }
    }

    private void writeToFile(String subject, String content) throws IOException {
        File testFile = new File(this.getExternalFilesDir(null), "notes.txt");
        if (!testFile.exists())
            testFile.createNewFile();

        BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, false /*append*/));
        writer.write(subject+"\n"+content);
        writer.close();
    }

    private String readFromFile() throws IOException {
        String textFromFile = "";
        File testFile = new File(this.getExternalFilesDir(null), "notes.txt");
        if (testFile != null) {
            StringBuilder stringBuilder = new StringBuilder();
            // Reads the data from the file
            BufferedReader reader = new BufferedReader(new FileReader(testFile));
            String line;

            while ((line = reader.readLine()) != null) {
                textFromFile += line.replaceAll(";", "   ");
                textFromFile += "\n";
            }
            reader.close();
        }
        return textFromFile;
    }

}