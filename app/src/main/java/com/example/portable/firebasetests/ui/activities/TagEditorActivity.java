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

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.utils.KeyBoardUtils;
import com.example.portable.firebasetests.utils.ToastUtils;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;

public class TagEditorActivity extends AppCompatActivity implements ColorPickerDialogListener, View.OnClickListener {
    private static final String TAG = "tag", ALL_TAGS = "all_tags";
    private Tag tag;
    private EditText nameEdit;
    private View colorPreview;
    private ArrayList<Tag> allTags;

    public static void start(Context context, ArrayList<Tag> allTags, Tag tag) {
        Intent starter = new Intent(context, TagEditorActivity.class);
        starter.putExtra(TAG, tag);
        starter.putExtra(ALL_TAGS, allTags);
        context.startActivity(starter);
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
                if (nameEdit.getText().toString().trim().length() > 0) {
                    saveTag();
                } else {
                    ToastUtils.showToastNotChoosed("tag name");
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
        tag.setName(nameEdit.getText().toString().trim());
        if (tag.getId() == null) {
            tag.setId("tag" + System.currentTimeMillis());
        }

        if (!allTags.contains(tag)) {
            FirebaseUtils.getInstance().addTag(tag);
            finish();
        } else {
            ToastUtils.showToast("tag allready exists", false);
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

    @Override
    protected void onStop() {
        super.onStop();
        KeyBoardUtils.hideKeyBoard(this);
    }
}
