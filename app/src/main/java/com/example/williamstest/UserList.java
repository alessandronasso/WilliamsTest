package com.example.williamstest;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserList extends ListActivity {

    private String[] user = loadUser();
    private AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final List<String> user_list = new ArrayList<String>(Arrays.asList(user));
        if (user_list.contains("Test ")) user_list.remove(user_list.indexOf("Test "));
        if (user_list.size() == 0) {
            showCustomDialog();
        } else {
            final ArrayAdapter<String> arAd = new ArrayAdapter<String>(this, R.layout.user_list, user_list);
            setListAdapter(arAd);
            ListView listView = getListView();
            listView.setTextFilterEnabled(true);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserList.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.alert_dialog, null);
                    builder.setView(dialogView);
                    Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);
                    Button btn_negative = (Button) dialogView.findViewById(R.id.dialog_negative_btn);
                    Button btn_neutral = (Button) dialogView.findViewById(R.id.dialog_neutral_btn);

                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    btn_positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                            Intent myIntent = new Intent(UserList.this, Result.class);
                            myIntent.putExtra("cartella", user_list.get(position).replaceAll("[^\\d.]", ""));
                            myIntent.putExtra("protocollo", findProtocol(user_list.get(position).replaceAll("[^\\d.]", "")));
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            UserList.this.startActivity(myIntent);
                        }
                    });
                    btn_negative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                            File dir = new File("/data/user/0/com.example.williamstest/app_draw" + user_list.get(position).replaceAll("[^\\d.]", ""));
                            if (dir.isDirectory()) {
                                String[] children = dir.list();
                                for (int i = 0; i < children.length; i++)
                                    new File(dir, children[i]).delete();
                            }
                            dir.delete();
                            final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            final Uri contentUri = Uri.fromFile(dir);
                            scanIntent.setData(contentUri);
                            sendBroadcast(scanIntent);
                            user_list.remove(position);
                            arAd.notifyDataSetChanged();
                            if (user_list.size() == 0) showCustomDialog();
                        }
                    });
                    btn_neutral.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                }
            });
        }
    }

    /**
     * This method retrives the list of test done by the users.
     *
     * @return the list of tests
     */
    public String[] loadUser() {
        String user[] = null;
        int numb = 0, arrayString = 0;
        File dir = new File("/data/user/0/com.example.williamstest/");
        if (!dir.isDirectory()) throw new IllegalStateException();
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("app_draw"))
                numb++;
        }
        user = new String[numb];
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("app_draw")) {
                String typeTest = file.getName().replaceAll("[^\\d.]", "");
                user[arrayString++] = "Test " + typeTest;
            }
        }
        return user;
    }

    /**
     * This methods retrieves the protocol that the user has clicked on.
     *
     * @param n folder number where get the data
     * @return the protocol drawn by the user
     */
    public String findProtocol(String n) {
        File dir = new File("/data/user/0/com.example.williamstest/app_draw" + n);
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.getName().endsWith("1_score.txt")) {
                    return file.getName().substring(0, 1);
                }
            }
        }
        return null;
    }

    /**
     * This methods generates the alert dialog if the page has no elements
     * to show.
     */
    private void showCustomDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_negative, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This methods, directly connected to the positive button of the alert dialog
     * which appaers when pressing on an item of the list, connects the user
     * to the corresponding activity.
     *
     * @param view current view
     */
    public void goNext(View view) {
        alertDialog.dismiss();
        Intent myIntent = new Intent(UserList.this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        UserList.this.startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(UserList.this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        UserList.this.startActivity(myIntent);
    }

}