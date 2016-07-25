package com.poovarasanv.chapper.singleton;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.activity.HomeActivity;
import com.poovarasanv.chapper.models.Contact;
import com.poovarasanv.chapper.models.MessageContact;
import com.poovarasanv.chapper.pojo.Message;
import com.poovarasanv.chapper.receivers.NotificationDismissedReceiver;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.SimpleStorageConfiguration;
import com.sromku.simple.storage.Storage;
import com.sromku.simple.storage.helpers.OrderType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by poovarasanv on 14/7/16.
 */
public class ChapperSingleton {


    private static Socket socket;
    private static int lastContactId = 0;
    private static Context context;

    public static void init(Context context) {
        ChapperSingleton.context = context;
    }

    public static Socket getSocket(Context context) {
        if (socket == null) {
            try {
                socket = IO.socket("http://10.0.2.2:3001");

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

            } else {
                storage = SimpleStorage.getInternalStorage(context);
                storage.createDirectory("Chapper/data/messages");


            }
        }

        return storage;
    }


    public static void writeLogin(String phoneNumber) {

        if (getStorage().isFileExist("Chapper/data", "login.txt") == false) {
            getStorage().createFile("Chapper/data", "login.txt", phoneNumber);
        }
    }

    public static boolean isLoggedIn() {
        if (getStorage().isFileExist("Chapper/data", "login.txt")) {
            String content = getStorage().readTextFile("Chapper/data", "login.txt");
            if (content != "") {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static String getDate() {
        Date date = new Date();
        return date.getYear() + "-" + date.getMonth() + 1 + "-" + date.getDate() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();

    }

    public static Date parseDate(String date) {

        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static void saveOutgoingMessage(String to, String message) {
        JSONObject object = new JSONObject();
        try {
            object.put("user", to);
            object.put("content", message);

//
            //{"to":"9789356631","content":"this is a demo message","from":"9789356631","messageId":33,"date":"Thu Jul 21 05:49:28 GMT 2016","status":true}

            JSONObject outgoing = new JSONObject();
            outgoing.put("to", to);
            outgoing.put("from", getNumber());
            outgoing.put("content", message);
            outgoing.put("messageId", getMessageLength(to));
            outgoing.put("date", getDate());
            outgoing.put("status", true);


            if (getStorage().isFileExist("Chapper/data/messages", to + ".txt")) {

                JSONArray jsonArray = new JSONArray(getStorage().readTextFile("Chapper/data/messages", to + ".txt"));
                jsonArray.put(outgoing);

                getStorage().deleteFile("Chapper/data/messages", to + ".txt");
                getStorage().createFile("Chapper/data/messages", to + ".txt", jsonArray.toString());
            } else {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(outgoing);
                getStorage().createFile("Chapper/data/messages", to + ".txt", jsonArray.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        getSocket(context).emit("message", object.toString());
    }

    public static int getMessageLength(String to) {
        if (getStorage().isFileExist("Chapper/data/messages", to + ".txt")) {
            try {
                JSONArray jsonArray = new JSONArray(getStorage().readTextFile("Chapper/data/messages", to + ".txt"));
                return jsonArray.length();
            } catch (JSONException e) {
                e.printStackTrace();

                return 0;
            }
        } else {
            return 0;
        }
    }

    public static void saveIncommingMessage(JSONObject jsonObject) {
        if (getStorage().isFileExist("Chapper/data/messages", jsonObject.optString("from") + ".txt")) {
            try {

                String from = jsonObject.optString("from");

                Date date = new Date();
                jsonObject.put("date", getDate());
                jsonObject.put("status", true);

                JSONArray jsonArray = new JSONArray(getStorage().readTextFile("Chapper/data/messages", from + ".txt"));
                jsonArray.put(jsonObject);

                getStorage().deleteFile("Chapper/data/messages", from + ".txt");
                getStorage().createFile("Chapper/data/messages", from + ".txt", jsonArray.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            String from = jsonObject.optString("from");

            Date date = new Date();

            try {
                jsonObject.put("date", getDate());
                jsonObject.put("status", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            getStorage().createFile("Chapper/data/messages", from + ".txt", jsonArray.toString());
        }
    }

    public static String getNumber() {
        if (getStorage().isFileExist("Chapper/data", "login.txt")) {
            String content = getStorage().readTextFile("Chapper/data", "login.txt");
            if (content != "") {
                return content;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static List<Message> getAllMessages(String to) {
        if (getStorage().isFileExist("Chapper/data/messages", to + ".txt")) {
            List<Message> m = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(getStorage().readTextFile("Chapper/data/messages", to + ".txt"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.optJSONObject(i);
                    Message message = new Message();
                    message.setToUser(to);
                    message.setFromUser(obj.optString("from"));
                    message.setMessage(obj.optString("content"));
                    message.setMessageId(obj.optInt("messageId"));
                    message.setStatus(obj.optBoolean("status"));

                    message.setCreatedAt(parseDate(obj.optString("date")));

                    m.add(message);
                }

                return m;

            } catch (JSONException e) {
                e.printStackTrace();

                return null;
            }

        } else {
            return null;
        }
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
        List<File> allUser = storage.getFiles("Chapper/data/messages", OrderType.DATE);

        for (File f : allUser) {
            MessageContact c = new MessageContact();
            c.setName(getContactName(f.getName().replace(".txt", "")));
            c.setNumber(f.getName().replace(".txt", ""));
            c.setImage(getContactImage(f.getName()));
            List<Message> messages = getAllMessages(f.getName().replace(".txt", ""));
            c.setMessage(messages.get(messages.size() - 1).getMessage().length() > 50 ? messages.get(messages.size() - 1).getMessage().substring(0, 45) + "..." : messages.get(messages.size() - 1).getMessage());
            messageUser.add(c);
        }

        return messageUser;
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

            if (getStorage().isFileExist("Chapper/data", "users.txt")) {
                getStorage().deleteFile("Chapper/data", "users.txt");
            }
            getStorage().createFile("Chapper/data", "users.txt", contactString.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
