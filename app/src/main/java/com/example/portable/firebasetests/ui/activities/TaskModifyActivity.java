package com.example.portable.firebasetests.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.Notifier;
import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.model.Remind;
import com.example.portable.firebasetests.model.SubTask;
import com.example.portable.firebasetests.model.Tag;
import com.example.portable.firebasetests.model.Task;
import com.example.portable.firebasetests.network.FirebaseListenersManager;
import com.example.portable.firebasetests.network.FirebaseUtils;
import com.example.portable.firebasetests.network.listeners.AllTagsFirebaseListener;
import com.example.portable.firebasetests.ui.adapters.ReminderAdapter;
import com.example.portable.firebasetests.ui.adapters.SubtaskClickableAdapter;
import com.example.portable.firebasetests.ui.adapters.TagAdapter;
import com.example.portable.firebasetests.utils.TimeUtils;

import java.util.ArrayList;

import static com.example.portable.firebasetests.model.SubTask.PRIORITIES;

public class TaskModifyActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TASK_ARG = "task";
    private EditText descriptionEdit, nameEdit;
    private RecyclerView tagsRecycler;
    private RecyclerView subTasksRecycleView, remindersRecyclerView;
    private Task task;
    private ArrayList<Tag> tags;
    private Tag lastSelectedTag;
    private View tagRoot;
    private TextView tagTextView;
    private ImageView tagEditImageView;
 /*   private ImageView editTag;
    private ArrayList<Tag> tags;*/


    public static void start(Context context, Task task) {
        Intent starter = new Intent(context, TaskModifyActivity.class);
        starter.putExtra(TASK_ARG, task);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_modify);
        tagRoot = findViewById(R.id.tag_root);
        tagTextView = (TextView) findViewById(R.id.tag_text);
        tagEditImageView = (ImageView) findViewById(R.id.tag_edit_iv);
        task = (Task) getIntent().getSerializableExtra(TASK_ARG);
        nameEdit = (EditText) findViewById(R.id.nameET);
        descriptionEdit = (EditText) findViewById(R.id.taskDescriptionEdit);
        tagsRecycler = (RecyclerView) findViewById(R.id.tags_recycler);
        tags = new ArrayList<>();
        TagAdapter.OnTagInteractionListener onTagInteractionListener = new TagAdapter.OnTagInteractionListener() {
            @Override
            public void clickOnTag(Tag tag) {
                selectTag(tag);
            }

            @Override
            public void clickOnEdit(Tag tag) {
                TagEditorActivity.start(TaskModifyActivity.this, tags, tag);
            }
        };
        tagsRecycler.setAdapter(new TagAdapter(tags, onTagInteractionListener));
        tagsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        subTasksRecycleView = (RecyclerView) findViewById(R.id.subTasksRecyclerView);
        Button addSubTaskButton = (Button) findViewById(R.id.addSubTaskButton);
        remindersRecyclerView = (RecyclerView) findViewById(R.id.reminder_recycler);
    /*    editTag = (ImageView) findViewById(R.id.edit_tag_imv);
        editTag.setOnClickListener(this);*/
        findViewById(R.id.add_tag_imv).setOnClickListener(this);
        findViewById(R.id.add_reminder_imv).setOnClickListener(this);
        configViewsForClosingKeyBord(findViewById(R.id.task_modify_root));
        if (task.getId() != null) {
            descriptionEdit.setText(task.getDescription());
            nameEdit.setText(task.getName());
        }

        initSubtasksRecyclerView();

        addSubTaskButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                editSubtask(null);
            }
        });
        remindersRecyclerView.setAdapter(new ReminderAdapter(task.getReminds(), new ReminderAdapter.OnRemindClickListener() {
            @Override
            public void onClick(Remind remind) {
                startActivityForResult(ReminderEditorActivity
                        .getStarterIntent(TaskModifyActivity.this, remind, task.getTimeStamp()), 36);
            }
        }));
        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.task_modify_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (TimeUtils.isInPast(task.getCalendar().getTimeInMillis())) {
            findViewById(R.id.reminders_container).setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseListenersManager.getInstance().setAllTagsListener(new AllTagsFirebaseListener.OnTagsSyncListener() {
            @Override
            public void onSync(ArrayList<Tag> tagsArray) {
                tags.clear();
                tags.addAll(tagsArray);
                tagsRecycler.getAdapter().notifyDataSetChanged();
                selectTag(tags.get(0));
                /*if (tagsArray.size() == 0) {
                    tagSpinner.setVisibility(View.GONE);
                    editTag.setVisibility(View.GONE);
                    task.setTagId(null);
                } else {
                    tagSpinner.setVisibility(View.VISIBLE);
                    editTag.setVisibility(View.VISIBLE);
                    tags.clear();
                    tags.addAll(tagsArray);
                    tagSortingAdapter.notifyDataSetChanged();
                    if (task.getTagId() != null) {
                        int pos = -1;
                        for (int i = 0; i < tags.size(); i++) {
                            if (tags.get(i).getId().equals(task.getTagId())) {
                                pos = i;
                                break;
                            }
                        }
                        tagSpinner.setSelection(pos);
                    } else {
                        tagSpinner.setSelection(0);
                    }
                }*/

            }
        });
    }

    private void selectTag(Tag tag) {
        int pos = tags.indexOf(tag);
        tags.remove(pos);
        tagsRecycler.getAdapter().notifyItemRemoved(pos);
        task.setTagId(tag.getId());
        if (lastSelectedTag != null) {
            tags.add(lastSelectedTag);
            tagsRecycler.getAdapter().notifyItemInserted(tags.size());
        }
        drawNewTag(tag);
        lastSelectedTag = tag;
    }

    private void drawNewTag(Tag tag) {
        tagTextView.setText(tag.getName());
        tagTextView.setTextColor((int) tag.getColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tagEditImageView.setImageTintList(ColorStateList.valueOf((int) tag.getColor()));
        }
        GradientDrawable drawable = new GradientDrawable();
        drawable.setStroke(5, (int) tag.getColor());
        drawable.setCornerRadius(270f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tagRoot.setBackground(drawable);
        } else {
            tagRoot.setBackgroundDrawable(drawable);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseListenersManager.getInstance().removeAllTagsListener();
    }

    private void configViewsForClosingKeyBord(View rootView) {
        if (!(rootView instanceof EditText)) {
            rootView.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard();
                    return false;
                }
            });
        }
        if (rootView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) rootView).getChildCount(); i++) {
                View innerView = ((ViewGroup) rootView).getChildAt(i);
                configViewsForClosingKeyBord(innerView);
            }
        }
    }


    private void hideKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void showErrorToast(String cause) {
        Toast toast = Toast.makeText(this, "You should choose " + cause, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.RED);
        toast.show();
    }

    private boolean canComplete() {
        if (task.getName().length() == 0) {
            showErrorToast("name");
            return false;
        }
        if (task.getDescription().length() == 0) {
            showErrorToast("description");
            return false;
        }
        if (task.getTagId() == null) {
            showErrorToast("tag");
            return false;
        }
        if (task.getSubTasks().size() == 0) {
            showErrorToast("subtasks");
            return false;
        }
        return true;

    }

    private void initSubtasksRecyclerView() {
        subTasksRecycleView.setAdapter(new SubtaskClickableAdapter(task.getSubTasks(), new SubtaskClickableAdapter.OnSubtaskClickListener() {
            @Override
            public void onClick(SubTask subTask) {
                editSubtask(subTask);
            }
        }));
        subTasksRecycleView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_reminder_imv:
                startActivityForResult(ReminderEditorActivity.getStarterIntent(this, null, task.getTimeStamp()), 36);
                break;
        /*    case R.id.add_tag_imv:
                TagEditorActivity.start(this, tags, null);
                break;
            case R.id.edit_tag_imv:

                break;*/
        }
    }


    private void editSubtask(final SubTask subTask) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_subtask_editor, null);
        final EditText editText = (EditText) view.findViewById(R.id.subtask_name);
        final Spinner spinner = (Spinner) view.findViewById(R.id.priority_spinner);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, PRIORITIES));
        Button button = (Button) view.findViewById(R.id.delete_subtask);
        if (subTask != null) {
            button.setVisibility(View.VISIBLE);
            editText.setText(subTask.getDescription());
            spinner.setSelection((int) subTask.getPriority());
        }
        builder.setView(view);
        builder.setCancelable(true);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText.getText().toString().length() > 0) {
                    if (subTask == null) {
                        SubTask s = new SubTask();
                        s.setDescription(editText.getText().toString());
                        s.setId("" + System.currentTimeMillis());
                        s.setPriority(spinner.getSelectedItemId());
                        task.getSubTasks().add(s);
                        subTasksRecycleView.getAdapter().notifyItemInserted(task.getSubTasks().indexOf(s));
                    } else {
                        int index = task.getSubTasks().indexOf(subTask);
                        subTask.setDescription(editText.getText().toString());
                        subTask.setPriority(spinner.getSelectedItemId());
                        task.getSubTasks().set(index, subTask);
                        subTasksRecycleView.getAdapter().notifyItemChanged(index);
                    }
                }
            }
        });
        final AlertDialog dialog = builder.show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = task.getSubTasks().indexOf(subTask);
                task.getSubTasks().remove(subTask);
                subTasksRecycleView.getAdapter().notifyItemRemoved(pos);
                dialog.dismiss();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.modify_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void saveTask() {
        if (task.getId() == null) {
            task.setId(Preferences.getInstance().readUserId() + "_task_" + System.currentTimeMillis());
        }
        for (Remind r : task.getReminds()) {
            Log.i("remind", r.getId() + ":" + r);
        }
        for (int i = 0; i < task.getReminds().size(); i++) {
            Notifier.removeAlarm((int) task.getReminds().get(i).getTimeStamp());
        }
        Notifier.setAlarms(task);
        FirebaseUtils.getInstance().saveTask(task);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 36:
                handleRemindResult(resultCode, data == null ? null : (Remind) data.getSerializableExtra(ReminderEditorActivity.REMINDER));
                break;
        }
    }

    private void handleRemindResult(int result, Remind remind) {
        switch (result) {
            case ReminderEditorActivity.CREATE:
                if (remind.getTimeStamp() > System.currentTimeMillis()) {
                    task.getReminds().add(remind);
                    remindersRecyclerView.getAdapter().notifyItemInserted(task.getReminds().size());
                } else {
                    showErrorToast("time in future");
                }
                break;
            case ReminderEditorActivity.UPDATE:
                if (remind.getTimeStamp() > System.currentTimeMillis()) {
                    final int index = task.getReminds().indexOf(remind);
                    task.getReminds().set(index, remind);
                    remindersRecyclerView.getAdapter().notifyItemChanged(index);
                } else {
                    showErrorToast("time in future");
                }
                break;
            case ReminderEditorActivity.DELETE:
                int i = task.getReminds().indexOf(remind);
                task.getReminds().remove(i);
                remindersRecyclerView.getAdapter().notifyItemRemoved(i);
                Notifier.removeAlarm((int) remind.getTimeStamp());
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.saveTask:
                assembleTaskAndSave();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void assembleTaskAndSave() {
        task.setName(nameEdit.getText().toString());
        task.setDescription(descriptionEdit.getText().toString());
       /* if (tagSpinner.getVisibility() == View.VISIBLE) {
            task.setTagId(((Tag) tagSpinner.getSelectedItem()).getId());
        } else {
            task.setTagId(null);
        }

        if (canComplete()) {
            saveTask();
            finish();
        }*/
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("All unsaved data will be removed");
        builder.setPositiveButton("quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("cancel", null);
        builder.show();
    }
}