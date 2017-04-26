package com.example.portable.firebasetests.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.network.FirebaseObserver;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.utils.KeyBoardUtils;
import com.example.portable.firebasetests.utils.ToastUtils;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

public class TagEditorActivity extends AppCompatActivity implements ColorPickerDialogListener, View.OnClickListener {

    private static final String TAG = "tag";
    private Tag tag;
    private EditText nameEdit;
    private View colorPreview;

    public static void start(Context context, Tag tag) {
        Intent starter = new Intent(context, TagEditorActivity.class);
        starter.putExtra(TAG, tag);
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

    private void showTagModifyingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Tag will be changed in all tasks. Proceed?");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUtils.getInstance().addTag(tag);
                finish();
            }
        });
        builder.setNegativeButton("cancel", null);
        builder.setCancelable(true);
        builder.show();
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
        tag.setName(nameEdit.getText().toString());
        if (tag.getId() == null) {
            tag.setId("tag" + System.currentTimeMillis());
        }

        for (Tag t : FirebaseObserver.getInstance().getTags()) {
            if (t.isIdentical(tag)) {
                ToastUtils.showToast("tag already exists", false);
                return;
            }
        }
        showTagModifyingDialog();

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
