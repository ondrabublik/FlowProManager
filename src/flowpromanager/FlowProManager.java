/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowpromanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author obublik
 */
public class FlowProManager {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        deleteFileInZip();
        createManifest();
        updateManifest();
    }

    public static void deleteFileInZip() throws IOException {
        /* Define ZIP File System Properies in HashMap */
        Map<String, String> zip_properties = new HashMap<>();
        /* We want to read an existing ZIP File, so we set this to False */
        zip_properties.put("create", "false");

        /* Specify the path to the ZIP File that you want to read as a File System */
        System.out.println("Working Directory = "
                + System.getProperty("user.dir") + "\\FlowPro.jar");
        URI zip_disk = URI.create("jar:file:/" + correctChars(System.getProperty("user.dir")) + "/FlowPro.jar");
        /* Create ZIP file System */
        try (FileSystem zipfs = FileSystems.newFileSystem(zip_disk, zip_properties)) {
            /* Get the Path inside ZIP File to delete the ZIP Entry */
            Path pathInZipfile = zipfs.getPath("META-INF/MANIFEST.MF");
            System.out.println("Deleting " + pathInZipfile.toUri());
            /* Execute Delete */
            Files.delete(pathInZipfile);
            System.out.println("Manifest was removed!");
        } catch (java.nio.file.NoSuchFileException e) {
            System.out.println("Manifest not found!");
        }
    }

    public static String correctChars(String in) {
        StringBuilder out = new StringBuilder(in);
        for(int i = 0; i < out.length(); i++){
            if(out.charAt(i) == '\\')
                out.setCharAt(i, '/');
        }
        return out.toString();
    }
    
    public static void createManifest(){
        try {
            FileWriter fw = new FileWriter("MANIFEST.MF");
            try (BufferedWriter out = new BufferedWriter(fw)) {
                String radka = "Manifest-Version: 1.0";
                out.write(radka);
                out.newLine();
                radka = "Main-Class: flowpro.core.FlowProMain";
                out.write(radka);
                out.newLine();
                
                radka = "Class-Path:";
                File dir = new File("./lib");
                File[] filesList = dir.listFiles();
                for (File file : filesList) {
                    if (file.isFile()) {
                        radka = radka + " lib/" +file.getName();
                    }
                }
                dir = new File("./modules/equations");
                filesList = dir.listFiles();
                for (File file : filesList) {
                    if (file.isFile()) {
                        radka = radka + " modules/equations/" +file.getName();
                    }
                }
                dir = new File("./modules/dynamics");
                filesList = dir.listFiles();
                for (File file : filesList) {
                    if (file.isFile()) {
                        radka = radka + " modules/dynamics/" +file.getName();
                    }
                }
                out.write(radka);
                out.newLine();
                out.close();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public static void updateManifest() throws IOException{
        File zipFile = new File("FlowPro.jar");;
        File[] files = new File[1];
        files[0] = new File("MANIFEST.MF");
    
        // get a temp file
        File tempFile = File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();
        boolean renameOk = zipFile.renameTo(tempFile);
        if (!renameOk) {
            throw new RuntimeException(
                    "could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        }
        byte[] buf = new byte[1024];
        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            boolean notInFiles = true;
            for (File f : files) {
                if (f.getName().equals(name)) {
                    notInFiles = false;
                    break;
                }
            }
            if (notInFiles) { // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(name)); // Transfer bytes from the ZIP file to the output file
                int len;
                while ((len = zin.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            entry = zin.getNextEntry();
        } // Close the streams
        zin.close(); // Compress the files
        for (int i = 0; i < files.length; i++) {
            InputStream in = new FileInputStream(files[i]); // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry("META-INF/" + files[i].getName())); // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            } // Complete the entry
            out.closeEntry();
            in.close();
        } // Complete the ZIP file
        out.close();
        tempFile.delete();
    }
}
