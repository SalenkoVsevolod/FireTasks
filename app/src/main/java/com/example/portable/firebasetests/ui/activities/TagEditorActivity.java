package com.example.portable.firebasetests.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Tag;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;

public class TagEditorActivity extends AppCompatActivity implements ColorPickerDialogListener, View.OnClickListener {
    public static final String TAG = "tag", ALL_TAGS = "all_tags";
    public static final int UPDATE = 1, CREATE = 2;
    private Tag tag;
    private EditText nameEdit;
    private View colorPreview;
    private ArrayList<Tag> allTags;

    public static Intent getStarterIntent(Context context, ArrayList<Tag> allTags, Tag tag) {
        Intent starter = new Intent(context, TagEditorActivity.class);
        starter.putExtra(TAG, tag);
        starter.putExtra(ALL_TAGS, allTags);
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_editor);
        nameEdit = (EditText) findViewById(R.id.tag_name_edit);
        findViewById(R.id.dialog_cancel).setOnClickListener(this);
        findViewById(R.id.dialog_ok).setOnClickListener(this);
        colorPreview = findViewById(R.id.color_preview);
        colorPreview.setOnClickListener(this);
        tag = (Tag) getIntent().getSerializableExtra(TAG);
        allTags = (ArrayList<Tag>) getIntent().getSerializableExtra(ALL_TAGS);
        if (allTags == null) {
            allTags = new ArrayList<>();
        }
        if (tag == null) {
            tag = new Tag();
            tag.setColor(Color.GREEN);
            colorPreview.setBackgroundColor(Color.GREEN);
        } else {
            nameEdit.setText(tag.getName());
            nameEdit.setTextColor((int) tag.getColor());
            colorPreview.setBackgroundColor((int) tag.getColor());
        }
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        tag.setColor(color);
        colorPreview.setBackgroundColor(color);
        nameEdit.setTextColor(color);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.color_preview:
                showColorPicker();
                break;
            case R.id.dialog_ok:
                if (nameEdit.getText().toString().length() > 0) {
                    saveTag();
                }
                break;
            case R.id.dialog_cancel:
                finish();
                break;
        }
    }

    private void showColorPicker() {
        ColorPickerDialog.Builder builder = ColorPickerDialog.newBuilder();
        builder.setColor((int) tag.getColor());
        builder.show(this);
    }

    private void saveTag() {
        int result;
        tag.setName(nameEdit.getText().toString());
        if (tag.getId() == null) {
            tag.setId("tag" + System.currentTimeMillis());
            result = CREATE;
        } else {
            result = UPDATE;
        }

        if (!allTags.contains(tag)) {
            Intent intent = new Intent();
            intent.putExtra(TAG, tag);
            setResult(result, intent);
            finish();
        } else {
            Toast.makeText(this, "tag allready exists", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
