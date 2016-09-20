package com.sch.fakecontacts.model.generator;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

import com.sch.fakecontacts.model.group.GroupManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactGenerator {
    private static final String ACCOUNT_NAME_PREFIX = "fake_account_";
    private static final int AVATAR_SIZE = 256;

    private final Context context;
    private final GroupManager groupManager;

    public ContactGenerator(Context context) {
        this.context = context;
        groupManager = new GroupManager(context);
    }

    public void generate(GenerationOptions options) {
        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        if (options.eraseExisting()) {
            ContentProviderOperation op = ContentProviderOperation.newDelete(RawContacts.CONTENT_URI)
                    .withSelection(RawContacts.ACCOUNT_NAME + " LIKE ?", new String[]{ACCOUNT_NAME_PREFIX + "%"})
                    .build();
            ops.add(op);
        }

        final long groupId = options.getGroupId() != -1 ? options.getGroupId() : groupManager.getDefaultGroupId();

        for (int i = 0; i < options.getContactCount(); i++) {
            createAccount(ops, i, options.getAccountType(), groupId, options);
        }

        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    private void createAccount(List<ContentProviderOperation> ops, int id, String type, long groupId, GenerationOptions options) {
        final int index = ops.size();
        final RandomDataGenerator random = new RandomDataGenerator();

        ContentProviderOperation op = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_NAME, ACCOUNT_NAME_PREFIX + id)
                .withValue(RawContacts.ACCOUNT_TYPE, type)
                .build();
        ops.add(op);

        final String firstName = "Fake";
        final String lastName = "contact " + id;

        op = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, index)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.GIVEN_NAME, firstName)
                .withValue(StructuredName.FAMILY_NAME, lastName)
                .build();
        ops.add(op);

        if (options.withPhones()) {
            op = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, index)
                    .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, random.randomPhoneNumber())
                    .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                    .build();
            ops.add(op);
        }

        if (options.withEmails()) {
            op = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, index)
                    .withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                    .withValue(Email.ADDRESS, random.randomEmail())
                    .withValue(Email.TYPE, Email.TYPE_HOME)
                    .build();
            ops.add(op);
        }

        if (options.withAvatars()) {
            final Bitmap avatar = random.randomAvatar(AVATAR_SIZE, AVATAR_SIZE,
                    (firstName.charAt(0) + "" + lastName.charAt(0)).toUpperCase());
            op = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, index)
                    .withValue(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE)
                    .withValue(Photo.PHOTO, bitmapToByteArray(avatar))
                    .build();
            ops.add(op);
            avatar.recycle();
        }

        op = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, index)
                .withValue(Data.MIMETYPE, GroupMembership.CONTENT_ITEM_TYPE)
                .withValue(GroupMembership.GROUP_ROW_ID, groupId)
                .withYieldAllowed(true)
                .build();
        ops.add(op);
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, output);
        return output.toByteArray();
    }
}
