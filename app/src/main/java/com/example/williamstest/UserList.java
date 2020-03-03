package com.example.williamstest;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatCallback;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UserList extends ListActivity implements AppCompatCallback {

    /**
     * The current user who has logged in.
     */
    private String userLogged;

    /**
     * The string representing if the palette has been enabled.
     */
    private String paletteSelected;

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
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
        setContentView(R.layout.user_list);
        try {
            user = loadUser();
        } catch (IOException e) {
            e.printStackTrace();
        }
        delegate = AppCompatDelegate.create(this, this);
        delegate.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        delegate.setSupportActionBar(myToolbar);
        final List<String> user_list = new ArrayList<String>(Arrays.asList(user));
        if (user_list.contains("Test ")) user_list.remove(user_list.indexOf("Test "));
        if (user_list.size() == 0) showCustomDialog();
        else {
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, user_list);
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
                            myIntent.putExtra("palette", paletteSelected);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ordina_genere_uomo) {
            try {
                user = loadUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            List<String> u2 = new ArrayList<String>();
            for (int i = 0; i < user_list.size(); i++)
                if (user_list.get(i).contains("Maschio")) u2.add(user_list.get(i));
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, u2);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.ordina_genere_donna) {
            try {
                user = loadUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            List<String> u2 = new ArrayList<String>();
            for (int i = 0; i < user_list.size(); i++)
                if (user_list.get(i).contains("Femmina")) u2.add(user_list.get(i));
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, u2);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.ordina_prot_a) {
            try {
                user = loadUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            List<String> u2 = new ArrayList<String>();
            for (int i = 0; i < user_list.size(); i++)
                if (user_list.get(i).contains(" a ")) u2.add(user_list.get(i));
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, u2);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.ordina_prot_b) {
            try {
                user = loadUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            List<String> u2 = new ArrayList<String>();
            for (int i = 0; i < user_list.size(); i++)
                if (user_list.get(i).contains(" b ")) u2.add(user_list.get(i));
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, u2);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.ordina_eta_c) {
            try {
                user = loadUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            List<String> u2 = new ArrayList<String>();
            for (int i = 0; i < user_list.size(); i++)
                if (!user_list.get(i).contains("Eta: /")) u2.add(user_list.get(i));
            u2.sort(Comparator.comparing(s -> {
                String stringDate = s.split(":")[3].trim();
                stringDate = stringDate.replaceAll("[^\\d-]", "");
                System.out.println(stringDate);
                try {
                    return new SimpleDateFormat("dd-mm-yyyy").parse(stringDate);
                } catch (ParseException | java.text.ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }));
            Collections.reverse(u2);
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, u2);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.ordina_eta_d) {
            try {
                user = loadUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            List<String> u2 = new ArrayList<String>();
            for (int i = 0; i < user_list.size(); i++)
                if (!user_list.get(i).contains("Eta: /")) u2.add(user_list.get(i));
            u2.sort(Comparator.comparing(s -> {
                String stringDate = s.split(":")[3].trim();
                stringDate = stringDate.replaceAll("[^\\d-]", "");
                try {
                    return new SimpleDateFormat("dd-mm-yyyy").parse(stringDate);
                } catch (ParseException | java.text.ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }));
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, u2);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.ordina_data_d) {
            try {
                user = loadUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            try {
                user = loadUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
            try {
                user = loadUser();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<String> user_list = new ArrayList<String>(Arrays.asList(user));
            final ArrayAdapter arAd = new ArrayAdapter<String>(this, R.layout.user_list, R.id.textList, user_list);
            setListAdapter(arAd);
            arAd.notifyDataSetChanged();
        } else if (id == R.id.import_excel) {
            try {
                isStoragePermissionGranted();
                importIntoExcel();
                generateImages();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method retrives the list of test done by the users.
     *
     * @return the list of tests
     */
    public String[] loadUser() throws IOException {
        Bundle extras = getIntent().getExtras();
        userLogged = extras.getString("userLogged");
        paletteSelected = extras.getString("palette");
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
                        user[arrayString] = "Test: " + typeTest + "   ";
                        line = reader.readLine();
                        user[arrayString] += "    Genere: " + line;
                        line = reader.readLine();
                        if (line.equals("0")) user[arrayString] += "    Eta: /   ";
                        else user[arrayString] += "    Eta: " + line + "   ";
                        line = reader.readLine();
                        user[arrayString] += "    Protocollo: " + line + "   ";
                        line = reader.readLine();
                        user[arrayString++] += "    Data: " + line + "   ";
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
        System.out.println(new File("/data/user/0/com.example.williamstest/app_draw8/a1_score.txt").exists());
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
     * This method is used to delete a list of files recursively.
     *
     * @param fileOrDirectory Directory containing the files to delete.
     */
    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    /**
     * This method generates a directory containing all the images drawn by the users.
     *
     * @throws IOException if it is not possible to create a folder
     */
    private void generateImages() throws IOException {
        File dir = new File("/data/user/0/com.example.williamstest/");
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "/Download/ImmaginiTest");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs())
                Log.d("App", "failed to create directory");
        } else {
                if (mediaStorageDir.isDirectory()) {
                    for (File child : mediaStorageDir.listFiles())
                        deleteRecursive(child);
                }
                mediaStorageDir.delete();
                mediaStorageDir.mkdirs();
            }
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("app_draw") && Character.isDigit(file.getName().charAt(file.getName().length() - 1))) {
                File makingDir = new File(Environment.getExternalStorageDirectory(), "/Download/ImmaginiTest/Test"+file.getName().substring(file.getName().length() - 1));
                makingDir.mkdirs();
                for (File fileS : file.listFiles()) {
                    if (fileS.getName().endsWith(".png")) {
                        Bitmap b = BitmapFactory.decodeStream(new FileInputStream(fileS));
                        File mypath=new File(makingDir, fileS.getName());
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(mypath);
                            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This method generates an xlsx file with all the tests recorded.
     */
    private void importIntoExcel() throws IOException {
        String[] columns = {"Numero Test", "Genere", "Data di nascita", "Protocollo", "Data del test", " ", "Cornice", "Fluidità", "Flessibilità",
                "Originalita'", "Elaborazione'", "Titolo", "Tempo Reazione", "Tempo Completamento", "Numero cancellature", "Numero Undo"};


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("RiepilogoTest");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER_SELECTION);

        // Create a Row
        Row headerRow = sheet.createRow(0);


        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);

        }

        // Create Other rows and cells with contacts data
        int rowNum = 1;

        //Inserting the data
        File dir = new File("/data/user/0/com.example.williamstest/");

        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("app_draw")) {
                String typeTest = file.getName().replaceAll("[^\\d.]", "");
                if (new File(file.getAbsolutePath() + "/infotest.txt").exists()) {
                    FileReader f = new FileReader(file.getAbsolutePath() + "/infotest.txt");
                    LineNumberReader reader = new LineNumberReader(f);
                    String line;
                    String protocollo = "";
                    line = reader.readLine();
                    Row row = null;
                    if (line.equals(userLogged)) {
                        row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue("Test: " + typeTest);
                        line = reader.readLine();
                        row.createCell(1).setCellValue(line);
                        line = reader.readLine();
                        if (line.equals("0")) row.createCell(2).setCellValue("/");
                        row.createCell(2).setCellValue(line);
                        line = reader.readLine();
                        protocollo = line;
                        row.createCell(3).setCellValue(line);
                        line = reader.readLine();
                        row.createCell(4).setCellValue(line);
                    }
                    for (int i=0; i<12; i++) {
                        String content = "";
                        reader = new LineNumberReader(new FileReader(file.getAbsolutePath() + "/" + protocollo + (i + 1) + "_score.txt"));
                        while ((line = reader.readLine()) != null) {
                            content+=line+"\n";
                        }

                        String[] values = content.split("\n");
                        row.createCell(5).setCellValue(" "); //Vuota
                        row.createCell(6).setCellValue(i+1); //Cornice
                        row.createCell(7).setCellValue(values[0]); //Fluidita
                        row.createCell(8).setCellValue(values[1]); //Flessibilita
                        row.createCell(9).setCellValue(values[2]); //Originalita'
                        row.createCell(10).setCellValue(values[3]); //Elaborazione
                        row.createCell(11).setCellValue(values[9]); //Titolo
                        row.createCell(12).setCellValue(values[5]); //Tempo reazione
                        row.createCell(13).setCellValue(values[6]); //Tempo Completamento
                        row.createCell(14).setCellValue(values[7]); //Numero cancellature
                        row.createCell(15).setCellValue(values[8]); //Numero undo

                        row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(" ");
                        row.createCell(1).setCellValue(" ");
                        row.createCell(2).setCellValue(" ");
                        row.createCell(3).setCellValue(" ");
                        row.createCell(4).setCellValue(" ");
                    }
                    f.close();
                }
            }
        }

        sheet.setDefaultColumnWidth(23);

        // Write the output to a file
        if (new File("/storage/emulated/0/Download/risultatiTest.xlsx").exists())
            new File("/storage/emulated/0/Download/risultatiTest.xlsx").delete();
        FileOutputStream fileOut = new FileOutputStream(new File("/storage/emulated/0/Download/risultatiTest.xlsx"));
        workbook.write(fileOut);
        fileOut.close();
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
        myIntent.putExtra("palette", paletteSelected);
        UserList.this.startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(UserList.this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        myIntent.putExtra("userLogged", userLogged);
        myIntent.putExtra("palette", paletteSelected);
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

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

}