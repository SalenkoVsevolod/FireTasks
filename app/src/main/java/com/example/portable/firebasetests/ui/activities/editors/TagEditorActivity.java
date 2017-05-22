package com.example.portable.firebasetests.ui.activities.editors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FirebaseObserver;
import com.example.portable.firebasetests.model.Tag;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

public class TagEditorActivity extends EditorActivity<Tag> implements ColorPickerDialogListener {
    public static final int REQUEST_CODE = 746;
    private static final String TAG = "tag";
    private Tag tag;
    private EditText nameEdit;
    private View colorPreview;
    private int resultCode;

    public static Intent getStarterIntent(Context context, Tag tag) {
        Intent starter = new Intent(context, TagEditorActivity.class);
        starter.putExtra(TAG, tag);
        return starter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_editor);
        setListeners();
        nameEdit = (EditText) findViewById(R.id.tag_name_edit);
        colorPreview = findViewById(R.id.color_preview);
        colorPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPicker();
            }
        });
        tag = (Tag) getIntent().getSerializableExtra(TAG);
        if (tag == null) {
            resultCode = CREATE;
            tag = new Tag();
            tag.setColor(Color.GREEN);
            colorPreview.setBackgroundColor(Color.GREEN);
        } else {
            resultCode = UPDATE;
            nameEdit.setText(tag.getName());
            nameEdit.setTextColor((int) tag.getColor());
            colorPreview.setBackgroundColor((int) tag.getColor());
        }
    }

    @Override
    protected boolean assembleEntityAndProceed() {
        if (nameEdit.getText().toString().trim().length() == 0) {
            showToastNotChose(getString(R.string.tag_name));
            return false;
        }
        tag.setName(nameEdit.getText().toString().trim());
        if (tag.getId() == null) {
            tag.setId("tag" + System.currentTimeMillis());
        }

        for (Tag t : FirebaseObserver.getInstance().getTags()) {
            if (t.isIdentical(tag)) {
                showToast(getString(R.string.tag_exists), false);
                return false;
            }
        }

        return true;
    }

    protected int getmResultCode() {
        return resultCode;
    }

    @Override
    protected Tag getResultData() {
        return tag;
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

    private void showColorPicker() {
        ColorPickerDialog.Builder builder = ColorPickerDialog.newBuilder();
        builder.setColor((int) tag.getColor());
        builder.show(this);
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
