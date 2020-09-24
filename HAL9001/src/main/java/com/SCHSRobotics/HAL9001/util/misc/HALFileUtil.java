package com.SCHSRobotics.HAL9001.util.misc;

import android.util.Log;

import com.SCHSRobotics.HAL9001.util.exceptions.DumpsterFireException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HALFileUtil {
    private HALFileUtil() {}

    public static String[] getAllFileNames(String directoryPath, String... excluding) {
        File configDirectory = new File(directoryPath);
        File[] allFiles = configDirectory.listFiles();
        List<String> filenames = new ArrayList<>();
        for(int i = 0; i < allFiles.length; i++) {
            String filename = allFiles[i].getName().replace(".txt","");

            boolean notInExcluded = true;
            for(String excludedFilename : excluding) {
                notInExcluded &= !filename.equals(excludedFilename);
            }

            if(notInExcluded) {
                filenames.add(filename);
            }
        }

        Collections.sort(filenames);
        String[] filenamesArray = new String[filenames.size()];
        filenames.toArray(filenamesArray);

        return filenamesArray;
    }

    public static void deleteFile(String filepath) {
        File fileToDelete = new File(filepath);
        if (!fileToDelete.delete()) {
            Log.e("File Issues", "Problem deleting file at " + filepath);
        }
    }

    public static void createFile(String filepath) {
        File fileToCreate = new File(filepath);

        if (!fileToCreate.exists()) {
            try {
                if (!fileToCreate.createNewFile()) {
                    Log.e("File Error", "Could not create file at " + filepath);
                }
            } catch (IOException e) {
                throw new DumpsterFireException("Error creating file at " + filepath);
            }
        }
    }

    public static void createDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            Log.i("File Creation", directory.mkdir() ? "Directory created! Path: " + directoryPath : "File error, couldn't create directory at " + directoryPath);
        }
    }

    public static boolean fileExists(String filepath) {
        File file = new File(filepath);
        return file.exists();
    }

    public static void saveObject(String filepath, Object data) {
        FileOutputStream fos;
        try {

            File file = new File(filepath);
            if(file.exists()) {
                deleteFile(filepath);
                createFile(filepath);
            }

            fos = new FileOutputStream(filepath, true);

            ObjectOutputStream fWriter;

            try {
                fWriter = new ObjectOutputStream(fos);

                fWriter.writeObject(data);

                fWriter.flush();
                fWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fos.getFD().sync();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save(String filepath, String data) {
        FileOutputStream fos;
        try {

            File file = new File(filepath);
            if(file.exists()) {
                deleteFile(filepath);
                createFile(filepath);
            }

            fos = new FileOutputStream(filepath, true);

            FileWriter fWriter;

            try {
                fWriter = new FileWriter(fos.getFD());

                fWriter.write(data);

                fWriter.flush();
                fWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fos.getFD().sync();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String filepath) {
        FileInputStream fis;
        StringBuilder output = new StringBuilder();

        try {
            fis = new FileInputStream(filepath);

            FileReader fReader;
            BufferedReader bufferedReader;

            try {
                fReader = new FileReader(fis.getFD());
                bufferedReader = new BufferedReader(fReader);

                String line;
                while((line = bufferedReader.readLine()) != null) {
                    output.append(line);
                    output.append('\n');
                }

                bufferedReader.close();
                fReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                fis.getFD().sync();
                fis.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.substring(0, output.length() - 1);
    }
}
