package com.sch.fakecontacts.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sch.fakecontacts.R;
import com.sch.fakecontacts.model.generator.ContactGenerator;
import com.sch.fakecontacts.model.generator.GenerationOptions;
import com.sch.fakecontacts.ui.dialogs.ProgressDialogFragment;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_SELECT_GROUP = 0;

    private static final String PREF_CONTACT_COUNT = "contact_count";
    private static final String PREF_ERASE_EXISTING = "erase_existing";
    private static final String PREF_WITH_EMAILS = "with_emails";
    private static final String PREF_WITH_PHONES = "with_phones";

    private SwitchCompat withEmailsView;
    private SwitchCompat withPhonesView;
    private SwitchCompat eraseExistingView;
    private EditText countView;
    private Button selectGroupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countView = (EditText) findViewById(R.id.edit_count);
        withEmailsView = (SwitchCompat) findViewById(R.id.switch_with_emails);
        withPhonesView = (SwitchCompat) findViewById(R.id.switch_with_phones);
        eraseExistingView = (SwitchCompat) findViewById(R.id.switch_erase_existing);

        final Button generateButton = (Button) findViewById(R.id.button_generate);
        generateButton.setOnClickListener(this);

        selectGroupButton = (Button) findViewById(R.id.button_select_group);
        selectGroupButton.setOnClickListener(this);

        restorePersistentState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePersistentState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_contacts:
                openContacts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SELECT_GROUP:
                if (resultCode == RESULT_OK) {
                    final long groupId = data.getLongExtra(GroupsActivity.EXTRA_GROUP_ID, 0);
                    final String groupName = data.getStringExtra(GroupsActivity.EXTRA_GROUP_NAME);
                    selectGroupButton.setText(groupName);
                    selectGroupButton.setTag(groupId);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_select_group:
                MainActivityPermissionsDispatcher.selectGroupWithCheck(this);
                break;
            case R.id.button_generate:
                if (validateParams()) {
                    MainActivityPermissionsDispatcher.generateContactsWithCheck(this);
                }
                break;
        }
    }

    private void openContacts() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private int getContactCount() {
        return countView.length() > 0 ? Integer.parseInt(countView.getText().toString()) : 0;
    }

    private boolean validateParams() {
        return countView.length() > 0;
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    void selectGroup() {
        GroupsActivity.startForResult(this, REQUEST_CODE_SELECT_GROUP);
    }

    @NeedsPermission(Manifest.permission.WRITE_CONTACTS)
    void generateContacts() {
        final Long groupId = (Long) selectGroupButton.getTag();
        final GenerationOptions.Builder builder = new GenerationOptions.Builder()
                .setContactCount(getContactCount())
                .setEraseExisting(eraseExistingView.isChecked());

        if (groupId != null) {
            builder.setGroupId(groupId);
        }
        if (withEmailsView.isChecked()) {
            builder.withEmails();
        }
        if (withPhonesView.isChecked()) {
            builder.withPhones();
        }
        new GenerateContactsTask(this, builder.build()).execute();
    }

    private void restorePersistentState() {
        final int contactCount = getPreferences().getInt(PREF_CONTACT_COUNT, 100);
        countView.setText(String.valueOf(contactCount));
        eraseExistingView.setChecked(getPreferences().getBoolean(PREF_ERASE_EXISTING, true));
    }

    private void savePersistentState() {
        getPreferences().edit()
                .putInt(PREF_CONTACT_COUNT, getContactCount())
                .putBoolean(PREF_ERASE_EXISTING, eraseExistingView.isChecked())
                .putBoolean(PREF_WITH_EMAILS, withEmailsView.isChecked())
                .putBoolean(PREF_WITH_PHONES, withPhonesView.isChecked())
                .apply();
    }

    private SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private static class GenerateContactsTask extends AsyncTask<Void, Void, Void> {
        private final FragmentActivity activity;
        private final GenerationOptions options;
        private final ProgressDialogFragment progressDialogFragment;

        GenerateContactsTask(FragmentActivity activity, GenerationOptions options) {
            this.activity = activity;
            this.options = options;
            progressDialogFragment = ProgressDialogFragment.newInstance(activity, R.string.message_please_wait);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogFragment.show(activity.getSupportFragmentManager(), null);
        }

        @Override
        protected Void doInBackground(Void... params) {
            new ContactGenerator(activity).generate(options);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialogFragment.dismissAllowingStateLoss();
            Toast.makeText(activity, R.string.message_done, Toast.LENGTH_SHORT).show();
        }
    }
}
