package kade_c.taskforge.utils;


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
 * Task Forge data is kept via internal files
 * Handles the file management (list creation etc.)
 */
public class InternalFilesManager {

    private Context context;
    private Activity activity;

    // Name of the current file we must open
    private String fileToOpen;

    public InternalFilesManager(Context ctx, Activity activity) {
        context = ctx;
        this.activity = activity;
    }

    /**
     * Constructor to read and read to list files
     */
    public InternalFilesManager(Context ctx, Activity activity, String filename) {
        context = ctx;
        this.activity = activity;
        fileToOpen = filename;

        fileToOpen = fileToOpen.replace("\n", "");
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
                FileInputStream fos = activity.openFileInput(fileToOpen);

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
     * Reads our file containing our existing tabs (lists)
     * @return an ArrayList<String> of our tabs
     */
    public ArrayList<String> readTabFile() {
        ArrayList<String> lines = new ArrayList<>();
        File file = context.getFileStreamPath("tabs");
        String line = "";
        byte[] buffer = new byte[4096];
        char c;
        int ret;

        try {
            // Checks if file exists
            if (file != null && file.exists()) {
                FileInputStream fos = activity.openFileInput("tabs");

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
     * Writes the TO DO in our file
     */
    public void writeListFile(String title, String content, String date, String time) {
        try {
            FileOutputStream fos = activity.openFileOutput(fileToOpen, Context.MODE_APPEND);
            String toWrite = title + " | " +
                    content + " | " +
                    date + " | " +
                    time + " | " +
                    "false\n";

            fos.write(toWrite.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the ArrayList passed as parameter has duplicate values
     */
    private static boolean hasDuplicates(ArrayList<String> list, String name)
    {
        int numCount = 0;
        String compared = name + "\n";

        for (String str : list) {
            if (str.equals(compared)) numCount++;
        }

        if (numCount >= 1) {
            return true;
        }
        return false;
    }

    /**
     * Writes the TO DO in our file
     */
    public boolean writeTabFile(String tab) {
        try {
            ArrayList<String> tabs = readTabFile();

            // Check for duplicates
            if (hasDuplicates(tabs, tab)) {
                return false;
            }

            FileOutputStream fos = activity.openFileOutput("tabs", Context.MODE_APPEND);

            // Sets title, content, date and if checked
            fos.write(tab.getBytes());
            fos.write('\n');
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Deletes file passed as parameter
     */
    public void deleteFile(String fileToDelete) {
        File dir = context.getFilesDir();
        File file = new File(dir, fileToDelete);
        boolean deleted = file.delete();
    }

    /**
     * Deletes a line in our list
     * @param lineToDelete line to be deleted
     */
    public void deleteItem(int lineToDelete) {
        try {
            // Reads file and saves file without deck to be deleted in temporary file.
            File file = context.getFileStreamPath(fileToOpen);
            FileOutputStream tempFile = activity.openFileOutput("temp_file_todo", Context.MODE_PRIVATE);
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
            File tempFile2 = context.getFileStreamPath("temp_file_todo");
            FileOutputStream fileToUpdate = activity.openFileOutput(fileToOpen, Context.MODE_PRIVATE);
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

    /**
     * Deletes a tab in our tab file at a given position
     */
    public void deleteTab(int lineToDelete) {
        try {
            // Reads file and saves file without deck to be deleted in temporary file.
            File file = context.getFileStreamPath("tabs");
            FileOutputStream tempFile = activity.openFileOutput("temp_file_list", Context.MODE_PRIVATE);
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
            File tempFile2 = context.getFileStreamPath("temp_file_list");
            FileOutputStream fileToUpdate = activity.openFileOutput("tabs", Context.MODE_PRIVATE);
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


    /**
     * Changes the value of the checkbox for the right line
     */
    public void changeCheckBoxState(int lineToChange, boolean state) {
        try {
            // Reads file and saves file without deck to be deleted in temporary file.
            File file = context.getFileStreamPath(fileToOpen);
            FileOutputStream tempFile = activity.openFileOutput("temp_file", Context.MODE_PRIVATE);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String currentLine;
            int i = -1;

            while ((currentLine = reader.readLine()) != null) {
                i++;
                if (i == lineToChange) {
                    if (state)
                        currentLine = currentLine.replace("false", "true");
                    else
                        currentLine = currentLine.replace("true", "false");
                }
                tempFile.write(currentLine.getBytes());
                tempFile.write('\n');
            }
            reader.close();

            // Then rewrites the temp file in our deck file.
            File tempFile2 = context.getFileStreamPath("temp_file");
            FileOutputStream fileToUpdate = activity.openFileOutput(fileToOpen, Context.MODE_PRIVATE);
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

    /**
     * Replaces a line in our list
     * @param lineToReplace line to be replaced
     * @param title title to be used
     * @param content content to be used
     * @param date date to be used
     */
    public void replaceItem(int lineToReplace, String title, String content, String date, String time) {
        try {
            // Reads file and saves file without deck to be deleted in temporary file.
            File file = context.getFileStreamPath(fileToOpen);
            FileOutputStream tempFile = activity.openFileOutput("temp_file", Context.MODE_PRIVATE);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String currentLine;
            int i = -1;

            while ((currentLine = reader.readLine()) != null) {
                i++;
                if (i == lineToReplace) {
                    String[] splitted = currentLine.split(" \\| ");
                    splitted[0] = title;
                    splitted[1] = content;
                    splitted[2] = date;
                    splitted[3] = time;

                    String updatedLine = splitted[0] + " | " + splitted[1] + " | " +
                            splitted[2] + " | " + splitted[3] + " | " + splitted[4];
                    currentLine = updatedLine;
                    // Replace here

                }
                tempFile.write(currentLine.getBytes());
                tempFile.write('\n');
            }
            reader.close();

            // Then rewrites the temp file in our deck file.
            File tempFile2 = context.getFileStreamPath("temp_file");
            FileOutputStream fileToUpdate = activity.openFileOutput(fileToOpen, Context.MODE_PRIVATE);
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

    /**
     * Moves item at given position at the end of the list
     * @param lineToMove
     */
    public void moveItemToEnd(int lineToMove) {
        String line = "";

        try {
            // Reads file and saves file without deck to be deleted in temporary file.
            File file = context.getFileStreamPath(fileToOpen);
            FileOutputStream tempFile = activity.openFileOutput("temp_file_todo", Context.MODE_PRIVATE);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String currentLine;
            int i = -1;

            while ((currentLine = reader.readLine()) != null) {
                i++;
                if (i == lineToMove) {
                    line = currentLine;
                } else {
                    tempFile.write(currentLine.getBytes());
                    tempFile.write('\n');
                }
            }
            tempFile.write(line.getBytes());
            tempFile.write('\n');
            reader.close();

            // Then rewrites the temp file in our deck file.
            File tempFile2 = context.getFileStreamPath("temp_file_todo");
            FileOutputStream fileToUpdate = activity.openFileOutput(fileToOpen, Context.MODE_PRIVATE);
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
