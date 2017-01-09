package kade_c.taskforge;


import android.app.Activity;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Handles the file management (list creation etc.)
 */
public class InternalFilesManager {

    private Context context;
//    private FragmentActivity fActivity;
    private Activity fActivity;

    private String fileToOpen;

//    public InternalFilesManager(Context ctx, FragmentActivity activity, String[] listNames) {
//        context = ctx;
//        fActivity = activity;
//
//        setUpFilesList(listNames);
//        createListFiles();
//    }

    /**
     * Constructor to create our initial list files
     */
    public InternalFilesManager(Context ctx, Activity activity, String[] listNames, String useremail) {
        context = ctx;
        fActivity = activity;

        createListFiles(listNames, useremail);
    }

    /**
     * Constructor to read and read to list files
     */
    public InternalFilesManager(Context ctx, Activity activity, String filename, String useremail) {
        context = ctx;
        fActivity = activity;
        fileToOpen = filename;
    }

    /**
     * Creates each list file
     */
    private void createListFiles(String[] listNames, String email) {
        try {
            // Name of the file containing our deck list.
            for (String file : listNames) {
                File fileCheck = context.getFileStreamPath(file);

                // If file has already been created, skip
                if (fileCheck.exists()) continue;

                FileOutputStream fos = fActivity.openFileOutput(file, Context.MODE_APPEND);
                fos.write(email.getBytes());
                fos.write('\n');
                fos.write(file.getBytes());
                fos.write('\n');
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns an ArrayList containing each line in the current List file.
     */
    public ArrayList<String> readListFile() {
        ArrayList<String> lines = new ArrayList<>();
        File file = context.getFileStreamPath(fileToOpen);
        String line = "";
        byte[] buffer = new byte[4096];
        char c;
        int ret;

        try {
            // Checks if file exists
            if (file != null && file.exists()) {
                FileInputStream fos = fActivity.openFileInput(fileToOpen);

                int i = 0;
                for (ret = fos.read(buffer); ret > 0; ret--) {
                    c = (char) buffer[i];
                    line += c;
                    i++;

                    // At every new line, add the previous one to our ArrayList.
                    if (c == '\n') {
                        lines.add(line);
                        line = "";
                    }
                }
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * Writes the given to do in the appropriate file
     */
    public void writeListFile(String title, String content, String date) {
        try {
            FileOutputStream fos = fActivity.openFileOutput(fileToOpen, Context.MODE_APPEND);

            fos.write(title.getBytes());
            fos.write(" | ".getBytes());
            fos.write(content.getBytes());
            fos.write(" | ".getBytes());
            fos.write(date.getBytes());
            fos.write('\n');
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteItem(int lineToDelete) {
        try {
            // Reads file and saves file without deck to be deleted in temporary file.
            File file = context.getFileStreamPath(fileToOpen);
            FileOutputStream tempFile = fActivity.openFileOutput("temp_file", Context.MODE_PRIVATE);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String currentLine;
            int i = -1;

            while ((currentLine = reader.readLine()) != null) {
                i++;
                if (i == lineToDelete) continue;
                tempFile.write(currentLine.getBytes());
                tempFile.write('\n');
            }
            reader.close();

            // Then rewrites the temp file in our deck file.
            File tempFile2 = context.getFileStreamPath("temp_file");
            FileOutputStream fileToUpdate = fActivity.openFileOutput(fileToOpen, Context.MODE_PRIVATE);
            BufferedReader tempFileReader = new BufferedReader(new FileReader(tempFile2));

            while ((currentLine = tempFileReader.readLine()) != null) {
                fileToUpdate.write(currentLine.getBytes());
                fileToUpdate.write('\n');
            }
            tempFileReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
