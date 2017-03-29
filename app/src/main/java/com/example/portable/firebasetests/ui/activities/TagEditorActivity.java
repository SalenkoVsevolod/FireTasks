package com.example.portable.firebasetests.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.network.FirebaseManager;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;

import java.util.ArrayList;

public class TagEditorActivity extends AppCompatActivity implements ColorPickerDialogListener, View.OnClickListener {
    private static final String TAG = "tag", ALL_TAGS = "all_tags";
    private Tag tag;
    private EditText nameEdit;
    private View colorPreview;
    private Button colorPick;
    private CardView tagPreviewCardView;
    private TextView tagPreviewTextView;
    private View previewContainer;
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
        nameEdit = (EditText) findViewById(R.id.tag_name_editx);
        colorPick = (Button) findViewById(R.id.pick_color_button);
        colorPreview = findViewById(R.id.color_preview);
        colorPreview.setOnClickListener(this);
        tagPreviewCardView = (CardView) findViewById(R.id.tag_cardview);
        tagPreviewTextView = (TextView) findViewById(R.id.tagTextView);

        previewContainer = findViewById(R.id.preview_container);
        findViewById(R.id.save_button).setOnClickListener(this);
        tag = (Tag) getIntent().getSerializableExtra(TAG);
        allTags = (ArrayList<Tag>) getIntent().getSerializableExtra(ALL_TAGS);
        if (tag == null) {
            tag = new Tag();
        } else {
            nameEdit.setText(tag.getName());
            colorPreview.setBackgroundColor((int) tag.getColor());
            tagPreviewCardView.setCardBackgroundColor((int) tag.getColor());
            tagPreviewTextView.setText(tag.getName());
            previewContainer.setVisibility(View.VISIBLE);
            hideButton();
        }
        colorPick.setOnClickListener(this);
        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                previewContainer.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                tagPreviewTextView.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        tag.setColor(color);
        tagPreviewCardView.setCardBackgroundColor(color);
        colorPreview.setBackgroundColor(color);
        hideButton();
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    private void hideButton() {
        colorPick.setVisibility(View.GONE);
        colorPreview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                saveTag();
                break;
            case R.id.color_preview:
            case R.id.pick_color_button:
                showColorPicker();
                break;
        }
    }

    private void showColorPicker() {
        ColorPickerDialog.Builder builder = ColorPickerDialog.newBuilder();
        builder.show(this);
    }

    private void saveTag() {
        tag.setName(nameEdit.getText().toString());
        if (tag.getId() == null) {
            tag.setId("tag" + System.currentTimeMillis());
        }
        if (!allTags.contains(tag)) {
            FirebaseManager.getInstance().addTag(tag);
            finish();
        } else {
            Toast.makeText(this, "tag allready exists", Toast.LENGTH_SHORT).show();
        }
    }
}
