package com.poovarasanv.chapper.singleton;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.activity.HomeActivity;
import com.poovarasanv.chapper.models.Contact;
import com.poovarasanv.chapper.models.MessageContact;
import com.poovarasanv.chapper.pojo.Message;
import com.poovarasanv.chapper.pojo.MySettings;
import com.poovarasanv.chapper.receivers.NotificationDismissedReceiver;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import iBoxDB.LocalServer.DB;

/**
 * Created by poovarasanv on 14/7/16.
 */
public class ChapperSingleton {


    private static Socket socket;
    private static int lastContactId = 0;
    private static Context context;

    private static final String SETTING = "Settings";
    private static final String MESSAGE = "Message";
    private static final String CONTACT = "Contact";


    public static void init(Context context) {
        ChapperSingleton.context = context;
    }

    public static AppExternalFileWriter getWriter() {
        return new AppExternalFileWriter(context);
    }

    public static DB getDB() {

        DB db = new DB();
        db.getConfig().ensureTable(MySettings.class, SETTING, "ID");
        db.getConfig().ensureTable(Message.class, MESSAGE, "ID");
        db.getConfig().ensureTable(Contact.class, CONTACT, "ID");

        return db;
    }

    public static Socket getSocket(Context context) {
        if (socket == null) {
            try {
                socket = IO.socket("http://10.0.2.2:3000");

                Log.i("Request Socket", "Sending Request");
                if (socket.connected() == false) {
                    if (socket.connect() != null) {
                    }
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return socket;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap getBitmapClippedCircle(Bitmap bitmap) {

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Path path = new Path();
        path.addCircle(
                (float) (width / 2)
                , (float) (height / 2)
                , (float) Math.min(width, (height / 2))
                , Path.Direction.CCW);

        final Canvas canvas = new Canvas(outputBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return outputBitmap;
    }

    public static List<Contact> getAllContacts() {

        int contactFetchLimit = 50;
        String query = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC LIMIT " + lastContactId + "," + contactFetchLimit;

        List<Contact> contacts = new ArrayList<>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, query);
        while (phones.moveToNext()) {
            lastContactId++;
            long id = Long.parseLong(phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID)));
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String image_uri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));


            contacts.add(new Contact(id, name, phoneNumber, image_uri));
        }
        phones.close();

        Log.i("Contacts Fetch : ", lastContactId + "");
        return contacts;
    }

    public static void setOffline() {
        socket = getSocket(context);


        if (!getNumber().equals("")) {
            JSONObject object = new JSONObject();
            try {
                object.put("user", getNumber());
                object.put("status", false);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.emit("status", object.toString());
        }


        Prefs.with(context).writeBoolean("offline", true);
    }

    public static void setOnline() {

        socket = getSocket(context);


        if (!getNumber().equals("")) {
            JSONObject object = new JSONObject();
            try {
                object.put("user", getNumber());
                object.put("status", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            socket.emit("status", object.toString());
        }

        Prefs.with(context).writeBoolean("offline", false);
    }


    static Storage storage = null;

    public static Storage getStorage() {

        if (storage == null) {
            if (SimpleStorage.isExternalStorageWritable()) {
                storage = SimpleStorage.getExternalStorage();
                storage.createDirectory("Chapper/data/messages");
                storage.createDirectory("Chapper/data/db");

            } else {
                storage = SimpleStorage.getInternalStorage(context);
                storage.createDirectory("Chapper/data/messages");
                storage.createDirectory("Chapper/data/db");


            }
        }

        return storage;
    }

    public static void initAndroid() {
        String path = "";
        if (SimpleStorage.isExternalStorageWritable()) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Chapper/data/db";
        } else {
            path = Environment.getDataDirectory().getAbsolutePath() + "/Chapper/data/db";
        }
        DB.root(path);
    }

    public static void writeLogin(String phoneNumber) {

        DB.AutoBox dbBox = getDB().open();
        dbBox.insert(SETTING, new MySettings(1, "login", phoneNumber, new GregorianCalendar().getTime()));
        getDB().close();

    }

    public static boolean isLoggedIn() {
        DB.AutoBox dbBox = getDB().open();
        MySettings loginSettings = dbBox.get(MySettings.class, SETTING, Long.parseLong("1"));

        if (loginSettings == null)
            return false;

        if (loginSettings != null || loginSettings.getValue() != null || loginSettings.getValue() != "") {
            return true;
        } else {
            return false;
        }
    }

    public static Date getDate() {
        return new GregorianCalendar().getTime();
    }

    public static void saveOutgoingMessage(String to, String message) {
        JSONObject object = new JSONObject();
        try {
            DB.AutoBox dbBox = getDB().open();
            object.put("user", to);
            object.put("content", message);
            long msgId = dbBox.newId();

//
            //{"to":"9789356631","content":"this is a demo message","from":"9789356631","messageId":33,"date":"Thu Jul 21 05:49:28 GMT 2016","status":true}

            JSONObject outgoing = new JSONObject();
            outgoing.put("to", to);
            outgoing.put("from", getNumber());
            outgoing.put("content", message);
            outgoing.put("messageId", msgId);
            outgoing.put("date", getDate());
            outgoing.put("status", true);


            Message msg = new Message();
            msg.setID(msgId);
            msg.setCreatedAt(getDate());
            msg.setFromUser(getNumber());
            msg.setToUser(to);
            msg.setStatus(true);
            msg.setMessage(message);
            dbBox.insert(MESSAGE, msg);

            saveUser(to);

            getDB().close();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        getSocket(context).emit("message", object.toString());
    }

    public static void saveIncommingMessage(JSONObject jsonObject) {
        DB.AutoBox dbBox = getDB().open();
        long msgId = dbBox.newId();
        Message msg = new Message();
        msg.setID(msgId);
        msg.setStatus(true);
        msg.setFromUser(jsonObject.optString("from"));
        msg.setCreatedAt(getDate());
        msg.setMessage(jsonObject.optString("content"));
        msg.setToUser(getNumber());

        dbBox.insert(MESSAGE, msg);

        saveUser(jsonObject.optString("from"));

        getDB().close();

    }

    public static String getNumber() {
        DB.AutoBox dbBox = getDB().open();
        MySettings loginSetting = dbBox.get(MySettings.class, SETTING, 1L);

        getDB().close();
        if (loginSetting.getValue() != null || loginSetting.getValue() != "") {
            return loginSetting.getValue();
        } else {
            return null;
        }


    }

    public static List<Message> getAllMessages(String to) {
        List<Message> m = new ArrayList<>();
        DB.AutoBox dbBox = getDB().open();
        for (Message msg : dbBox.select(Message.class, "from ? where fromUser == ? and toUser == ? or fromUser == ? and toUser == ?", MESSAGE, getNumber(), to, to, getNumber())) {
            m.add(msg);
        }
        getDB().close();

        return m;
    }

    public static Notification getNotification(String title, String message) {
        int icon = R.drawable.logo;


        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        new Intent(context.getApplicationContext(), HomeActivity.class),
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context);
        Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDeleteIntent(createOnDismissedIntent(context, 9789))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
                .setContentText(message).build();

        return notification;
    }

    public static NotificationManager getNotificationManager() {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private static PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("notificationId", notificationId);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, 0);
        return pendingIntent;
    }

    public static String getContactName(String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public static boolean contactExists(String number) {

        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    public static String getContactImage(String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.PHOTO_URI}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public static List<MessageContact> getAllMessageUser() {
        List<MessageContact> messageUser = new ArrayList<>();

        DB.AutoBox dbBox = getDB().open();
        for (Contact c : dbBox.select(Contact.class, "from Contact")) {
            messageUser.add(new MessageContact(
                    c.getId(),
                    getContactName(c.getNumber()),
                    c.getNumber(),
                    getContactImage(c.getNumber()),
                    ""
            ));
        }

        return messageUser;
    }

    public static void saveUser(String number) {
        DB.AutoBox dbBox = getDB().open();
        int size = 0;
        for (Contact c : dbBox.select(Contact.class, "from Contact where number == ?", number)) {
            size++;
        }

        if (size == 0) {
            Contact contact = new Contact();
            contact.setId(dbBox.newId());
            contact.setName(number);
            contact.setNumber(number);
            dbBox.insert(CONTACT, contact);
        }
        getDB().close();
    }

    public static void saveActiveUsers(JSONArray array) {
        JSONArray contactString = new JSONArray();
        try {
            for (int i = 0; i < array.length(); i++) {
                String number = array.get(i).toString();
                if (contactExists(number)) {
                    contactString.put(number);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
