package com.example.williamstest;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserList extends ListActivity implements AppCompatCallback {

    /**
     * The current user who has logged in.
     */
    private String userLogged;

    /**
     * The list with all the tests.
     */
    private String[] user = new String[0];

    /**
     * The alert dialog.
     */
    private AlertDialog alertDialog;

    /**
     * A support object to implement the toolbar since I can't extend the
     * AppCompactActivity class.
     */
    private AppCompatDelegate delegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);
        try { user = loadUser(); } catch (IOException e) { e.printStackTrace(); }
        delegate = AppCompatDelegate.create(this, this);
        delegate.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        delegate.setSupportActionBar(myToolbar);
        final List<String> user_list = new ArrayList<String>(Arrays.asList(user));
        if (user_list.contains("Test ")) user_list.remove(user_list.indexOf("Test "));
        if (user_list.size() == 0) showCustomDialog(); else {
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list,R.id.textList, user_list);
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
                            String[] currencies = user_list.get(position).split("\\s+");
                            myIntent.putExtra("cartella", currencies[1]);
                            myIntent.putExtra("protocollo", findProtocol(currencies[1]));
                            myIntent.putExtra("userLogged", userLogged);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            UserList.this.startActivity(myIntent);
                        }
                    });
                    btn_negative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                            String[] currencies = user_list.get(position).split("\\s+");
                            File dir = new File("/data/user/0/com.example.williamstest/app_draw" + currencies[1]);
                            if (dir.isDirectory()) {
                                String[] children = dir.list();
                                for (int i = 0; i < children.length; i++) new File(dir, children[i]).delete();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ordina_genere_uomo) {
            try { user = loadUser(); } catch (IOException e) { e.printStackTrace(); }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            List<String> u2 = new ArrayList<String>();
            for (int i=0; i<user_list.size(); i++)
                if (user_list.get(i).contains("Maschio")) u2.add(user_list.get(i));
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list,R.id.textList, u2);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.ordina_genere_donna) {
            try { user = loadUser(); } catch (IOException e) { e.printStackTrace(); }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            List<String> u2 = new ArrayList<String>();
            for (int i=0; i<user_list.size(); i++)
                if (user_list.get(i).contains("Femmina")) u2.add(user_list.get(i));
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list,R.id.textList, u2);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.ordina_prot_a) {
            try { user = loadUser(); } catch (IOException e) { e.printStackTrace(); }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            List<String> u2 = new ArrayList<String>();
            for (int i=0; i<user_list.size(); i++)
                if (user_list.get(i).contains(" a ")) u2.add(user_list.get(i));
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list,R.id.textList, u2);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.ordina_prot_b) {
            try { user = loadUser(); } catch (IOException e) { e.printStackTrace(); }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            List<String> u2 = new ArrayList<String>();
            for (int i = 0; i < user_list.size(); i++)
                if (user_list.get(i).contains(" b ")) u2.add(user_list.get(i));
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, u2);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.ordina_eta_c) {
            try { user = loadUser(); } catch (IOException e) { e.printStackTrace(); }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            List<String> u2 = new ArrayList<String>();
            for (int i = 0; i < user_list.size(); i++)
                if (!user_list.get(i).contains("Eta: / ")) u2.add(user_list.get(i));
            Collections.sort(u2, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return Comparator.comparingInt(this::extractInt)
                            .thenComparing(Comparator.reverseOrder())
                            .compare(o1, o2);
                }
                private int extractInt(String s) {
                    try {
                        return Integer.parseInt(s.split(":")[1].trim());
                    } catch (NumberFormatException exception) {
                        return -1;
                    }
                }
            });
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, u2);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.ordina_eta_d) {
            try { user = loadUser(); } catch (IOException e) { e.printStackTrace(); }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            List<String> u2 = new ArrayList<String>();
            for (int i = 0; i < user_list.size(); i++)
                if (!user_list.get(i).contains(" / ")) u2.add(user_list.get(i));
            Collections.sort(u2, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return Comparator.comparingInt(this::extractInt)
                            .thenComparing(Comparator.naturalOrder())
                            .compare(o1, o2);
                }
                private int extractInt(String s) {
                    try {
                        return Integer.parseInt(s.split(":")[1].trim());
                    } catch (NumberFormatException exception) {
                        return -1;
                    }
                }
            });
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, u2);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.ordina_data_d) {
            try { user = loadUser(); } catch (IOException e) { e.printStackTrace(); }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            user_list.sort(Comparator.comparing(s -> {
                String stringDate = s.substring(s.lastIndexOf(':') + 1).trim();
                try {
                    return new SimpleDateFormat("dd-mm-yyyy").parse(stringDate);
                } catch (ParseException | java.text.ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }));
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, user_list);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.ordina_data_c) {
            try { user = loadUser(); } catch (IOException e) { e.printStackTrace(); }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            user_list.sort(Comparator.comparing(s -> {
                String stringDate = s.substring(s.lastIndexOf(':') + 1).trim();
                try {
                    return new SimpleDateFormat("dd-mm-yyyy").parse(stringDate);
                } catch (ParseException | java.text.ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }));
            Collections.reverse(user_list);
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, user_list);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.remove_filter) {
            try { user = loadUser(); } catch (IOException e) { e.printStackTrace(); }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, user_list);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method retrives the list of test done by the users.
     *
     * @return the list of tests
     */
    public String[] loadUser() throws IOException{
        Bundle extras = getIntent().getExtras();
        userLogged = extras.getString("userLogged");
        String user[] = null;
        int numb = 0, arrayString = 0;
        File dir = new File("/data/user/0/com.example.williamstest/");
        if (!dir.isDirectory()) throw new IllegalStateException();
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("app_draw")) {
                if (new File(file.getAbsolutePath() + "/infotest.txt").exists()) {
                    FileReader f = new FileReader(file.getAbsolutePath() + "/infotest.txt");
                    LineNumberReader reader = new LineNumberReader(f);
                    String line;
                    line = reader.readLine();
                    if (line.equals(userLogged)) numb++;
                    f.close();
                }
            }
        }
        user = new String[numb];
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("app_draw")) {
                String typeTest = file.getName().replaceAll("[^\\d.]", "");
                if (new File(file.getAbsolutePath() + "/infotest.txt").exists()) {
                    FileReader f = new FileReader(file.getAbsolutePath() + "/infotest.txt");
                    LineNumberReader reader = new LineNumberReader(f);
                    String line;
                    line = reader.readLine();
                    if (line.equals(userLogged)) {
                        user[arrayString] = "Test: " + typeTest+"   ";
                        line = reader.readLine();
                        user[arrayString] += "    Genere: " + line;
                        line = reader.readLine();
                        if (line.equals("0")) user[arrayString] += "    Eta: /   ";
                        else user[arrayString] += "    Eta: " + line+"   ";
                        line = reader.readLine();
                        user[arrayString] += "    Protocollo: " + line+"   ";
                        line = reader.readLine();
                        user[arrayString++] += "    Data: " + line+"   ";
                    }
                    f.close();
                }
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
        System.out.println (new File("/data/user/0/com.example.williamstest/app_draw8/a1_score.txt").exists());
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
        myIntent.putExtra("userLogged", userLogged);
        UserList.this.startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(UserList.this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        myIntent.putExtra("userLogged", userLogged);
        UserList.this.startActivity(myIntent);
    }

    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
    }

}