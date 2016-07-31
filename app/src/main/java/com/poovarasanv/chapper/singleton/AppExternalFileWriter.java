package com.poovarasanv.chapper.singleton;

/**
 * Created by poovarasan on 30/07/16.
 */


import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Prasham Trivedi
 * @version 2.5
 *          <p>
 *          This class will create a directory having same name as your
 *          application. With all the states handled and reported back to
 *          developer.
 *          </p>
 */
public class AppExternalFileWriter {

    private static final String canNotWriteFile = "Can not write file: ";
    private static final String canNotCreateDirectory = "Can not create directory: ";
    private final File externalStorageDirectory;
    private final File externalCacheDirectory;
    private Context context;
    private File appDirectory;
    private File appCacheDirectory;

    /**
     * Creates external file writer
     *
     * @param context : Context
     */
    public AppExternalFileWriter(Context context) {
        this.context = context;
        externalStorageDirectory = Environment.getExternalStorageDirectory();
        externalCacheDirectory = context.getExternalCacheDir();

    }

    private File createFile(String fileName, boolean inCache) throws ExternalFileWriterException {
        return createFile(fileName, getAppDirectory(inCache));
    }

    /**
     * Create a file in the app directory with given file name.
     *
     * @param fileName : Desired name of the file
     * @param parent   parent of the file
     * @return : File with desired name
     */
    private File createFile(String fileName, File parent) throws ExternalFileWriterException {
        if (isExternalStorageAvailable(true)) {
            try {

                if (parent.isDirectory()) {

                    File detailFile = new File(parent, fileName);
                    if (!detailFile.exists())
                        detailFile.createNewFile();
                    else {
                        String messege = "File already there ";
                        throwException(messege);
                    }
                    return detailFile;
                } else {
                    throwException(parent + " should be a directory");
                }
            } catch (IOException e) {
                e.printStackTrace();
                String errorMessege = "IOException " + e;
                throwException(errorMessege);
            } catch (Exception e) {
                e.printStackTrace();
                String errorMessege = "Exception " + e;
                throwException(errorMessege);
            }
        }
        return null;
    }

    /**
     * Creates app directory
     */
    private void createAppDirectory() throws ExternalFileWriterException {
        String directoryName = context.getString(context.getApplicationInfo().labelRes);

        if (isExternalStorageAvailable(false)) {

            appDirectory = new File(Environment.getExternalStorageDirectory().toString(),
                    directoryName);
            createDirectory(appDirectory);

            appCacheDirectory = new File(externalCacheDirectory, directoryName);
            createDirectory(appCacheDirectory);

        }

    }

    private double getAvailableSpace() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdAvailSize = (double) stat.getAvailableBlocks() * (double) stat.getBlockSize();
        return sdAvailSize;
    }

    private boolean isExternalStorageAvailable(boolean isForFile)
            throws ExternalFileWriterException {
        String errorStarter = (isForFile) ? canNotWriteFile : canNotCreateDirectory;

        String storageState = Environment.getExternalStorageState();

        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else if (storageState.equals(Environment.MEDIA_BAD_REMOVAL)) {
            throwException(errorStarter + "Media was removed before it was unmounted.");
        } else if (storageState.equals(Environment.MEDIA_CHECKING)) {
            throwException(errorStarter + "Media is present and being disk-checked, "
                    + "Please wait and try after some time");
        } else if (storageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            throwException(errorStarter + "Presented Media is read only");
        } else if (storageState.equals(Environment.MEDIA_NOFS)) {
            throwException(errorStarter + "Blank or unsupported file media");
        } else if (storageState.equals(Environment.MEDIA_SHARED)) {
            throwException(errorStarter + "Media is shared with USB mass storage");
        } else if (storageState.equals(Environment.MEDIA_REMOVED)) {
            throwException(errorStarter + "Media is not present");
        } else if (storageState.equals(Environment.MEDIA_UNMOUNTABLE)) {
            throwException(errorStarter + "Media is present but cannot be mounted");
        } else if (storageState.equals(Environment.MEDIA_UNMOUNTED)) {
            throwException(errorStarter + "Media is present but not mounted");
        }

        return false;
    }

    private void throwException(String errorMessege) throws ExternalFileWriterException {
        throw new ExternalFileWriterException(errorMessege);
    }

    private File createDirectory(File directory) throws ExternalFileWriterException {
        if (!directory.exists() || !directory.isDirectory()) {
            if (directory.mkdirs()) {
                String messege = "directory " + directory + " created : Path "
                        + directory.getPath();

            } else {
                if (directory.exists()) {
                    if (directory.isDirectory()) {
                        String messege = "directory " + directory + " Already exists : Path "
                                + directory.getPath();

                    } else {
                        String messege = directory
                                + "should be a directory but found a file : Path "
                                + directory.getPath();
                        throwException(messege);
                    }

                }
            }
        }
        return directory;
    }

    /**
     * Write byte array to file. Will show error if given file is a directory.
     *
     * @param file : File where data is to be written.
     * @param data String which you want to write a file. If size of this is
     *             greater than size available, it will show error.
     */
    private void writeDataToFile(File file, String data) throws ExternalFileWriterException {
        byte[] stringBuffer = data.getBytes();
        writeDataToFile(file, stringBuffer);
    }

    /**
     * Write byte array to file. Will show error if given file is a directory.
     *
     * @param file : File where data is to be written.
     * @param data byte array which you want to write a file. If size of this is
     *             greater than size available, it will show error.
     */
    private void writeDataToFile(File file, byte[] data) throws ExternalFileWriterException {
        if (isExternalStorageAvailable(true)) {
            if (file.isDirectory()) {
                throwException(file + " is not a file, can not write data in it");
            } else {
                if (file != null && data != null) {
                    double dataSize = data.length;
                    double remainingSize = getAvailableSpace();
                    if (dataSize >= remainingSize) {
                        throwException("Not enough size available");

                    } else {
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            out.write(data);
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }

    private File getAppDirectory(boolean inCache) {
        return (inCache) ? this.appCacheDirectory : this.appDirectory;
    }

    /**
     * Creates subdirectory in application directory
     *
     * @param directoryName name of subdirectory
     * @return File object of created subdirectory
     * @throws ExternalFileWriterException if external storage is not available
     */
    public File createSubDirectory(String directoryName, boolean inCache)
            throws ExternalFileWriterException {
        if (isExternalStorageAvailable(false)) {

            getAppDirectory();

            File subDirectory = new File(getAppDirectory(inCache), directoryName);

            return createDirectory(subDirectory);
        } else
            return null;
    }

    /**
     * Checks whether directory with given name exists in AppDirectory
     *
     * @param directoryName : Name of the directory to check.
     * @return true if a directory with "directoryName" exists, false otherwise
     */
    public boolean isDirectoryExists(String directoryName, boolean checkInCache) {
        File parentDirectory = (checkInCache) ? appCacheDirectory : appDirectory;
        return isDirectoryExists(directoryName, parentDirectory);
    }

    /**
     * Check whether file with given name exists in parentDirectory or not.
     *
     * @param fileName        : Name of the file to check.
     * @param parentDirectory : Parent directory where directory with "fileName" should be present
     * @return true if a file  with "fileName" exists, false otherwise
     */
    public boolean isFileExists(String fileName, File parentDirectory) {
        File directoryToCheck = new File(parentDirectory, fileName);
        return directoryToCheck.exists() && directoryToCheck.isFile();
    }

    /**
     * Checks whether file with given name exists in AppDirectory
     *
     * @param fileName : Name of the file to check.
     * @return true if a file with "directoryName" exists, false otherwise
     */
    public boolean isFileExists(String fileName, boolean checkInCache) {
        File parentDirectory = (checkInCache) ? appCacheDirectory : appDirectory;
        return isFileExists(fileName, parentDirectory);
    }

    /**
     * Check whether directory with given name exists in parentDirectory or not.
     *
     * @param directoryName   : Name of the directory to check.
     * @param parentDirectory : Parent directory where directory with "directoryName" should be present
     * @return true if a directory with "directoryName" exists, false otherwise
     */
    public boolean isDirectoryExists(String directoryName, File parentDirectory) {
        File directoryToCheck = new File(parentDirectory, directoryName);
        return directoryToCheck.exists() && directoryToCheck.isDirectory();
    }

    /**
     * Creates subdirectory in parent directory
     *
     * @param parent        : Parent directory where directory with "directoryName" should be created
     * @param directoryName name of subdirectory
     * @return File object of created subdirectory
     * @throws ExternalFileWriterException if external storage is not available
     */
    public File createSubDirectory(File parent, String directoryName)
            throws ExternalFileWriterException {
        if (isExternalStorageAvailable(false)) {

            getAppDirectory();

            if (!parent.isDirectory())
                throwException(parent.getName() + " Must be a directory ");

            File subDirectory = new File(parent, directoryName);

            return createDirectory(subDirectory);
        } else
            return null;
    }

    /**
     * Deletes given directory with all its subdirectories and its files.
     *
     * @param directory : Directory to delete
     */
    public void deleteDirectory(File directory) {
        if (directory != null) {
            if (directory.isDirectory())
                for (File child : directory.listFiles()) {

                    if (child != null) {
                        if (child.isDirectory())
                            deleteDirectory(child);
                        else
                            child.delete();
                    }
                }

            directory.delete();
        }
//		return false;
    }

    /**
     * Get created app directory
     *
     * @return File object of created AppDirectory
     */
    public File getAppDirectory() throws ExternalFileWriterException {
        if (appDirectory == null) {
            createAppDirectory();
        }
        return appDirectory;
    }

    /**
     * get External Cache directory
     *
     * @return File object of External Cache directory
     */
    public File getExternalCacheDirectory() {
        return externalCacheDirectory;
    }

    /**
     * Get external storage directory
     *
     * @return File object of external storage directory
     */
    public File getExternalStorageDirectory() {
        return externalStorageDirectory;
    }

    /**
     * Write data in file of a parent directory
     *
     * @param parent   parent directory
     * @param fileName desired filename
     * @param data     data
     * @throws ExternalFileWriterException if external storage is not available or free space is
     *                                     less than size of the data
     */
    public void writeDataToFile(File parent, String fileName, byte[] data)
            throws ExternalFileWriterException {
        if (isExternalStorageAvailable(true)) {
            getAppDirectory();

            File file = createFile(fileName, parent);

            writeDataToFile(file, data);
        }
    }

    /**
     * Writes data to the file. The file will be created in the directory name
     * same as app.
     *
     * @param fileName name of the file
     * @param data     data to write
     * @throws ExternalFileWriterException if external storage is not available or free space is
     *                                     less than size of the data
     */
    public void writeDataToFile(String fileName, String data, boolean inCache)
            throws ExternalFileWriterException {
        if (isExternalStorageAvailable(true)) {
            getAppDirectory();

            File file = createFile(fileName, inCache);

            writeDataToFile(file, data);
        }
    }

    /**
     * Writes data to the file. The file will be created in the directory name
     * same as app.
     *
     * @param fileName name of the file
     * @param data     data to write
     * @throws ExternalFileWriterException if external storage is not available or free space is
     *                                     less than size of the data
     */
    public void writeDataToFile(String fileName, byte[] data, boolean inCache)
            throws ExternalFileWriterException {
        if (isExternalStorageAvailable(true)) {
            getAppDirectory();

            File file = createFile(fileName, inCache);

            writeDataToFile(file, data);
        }
    }

    /**
     * Write data in file of a parent directory
     *
     * @param parent   parent directory
     * @param fileName desired filename
     * @param data     data
     * @throws ExternalFileWriterException if external storage is not available or free space is
     *                                     less than size of the data
     */
    public void writeDataToFile(File parent, String fileName, String data)
            throws ExternalFileWriterException {
        if (isExternalStorageAvailable(true)) {
            getAppDirectory();

            File file = createFile(fileName, parent);

            writeDataToFile(file, data);
        }
    }

    /**
     * Writes data to the file. The file will be created in the directory name
     * same as app.
     * <p>
     * Name of the file will be the timestamp.extension
     * </p>
     *
     * @param extension extension of the file, pass null if you don't want to have
     *                  extension.
     * @param data      data to write
     * @param inCache   Pass true if you want to write data in External Cache. false if you want to write data in external directory.
     * @throws ExternalFileWriterException if external storage is not available or free space is
     *                                     less than size of the data
     */
    public void writeDataToTimeStampedFile(String extension, String data, boolean inCache)
            throws ExternalFileWriterException {
        if (isExternalStorageAvailable(true)) {
            getAppDirectory();

            String fileExtension = (TextUtils.isEmpty(extension)) ? "" : "." + extension;
            String fileName = System.currentTimeMillis() + fileExtension;

            File file = createFile(fileName, getAppDirectory(inCache));

            writeDataToFile(file, data);
        }
    }

    /**
     * Writes data to the file. The file will be created in the directory name
     * same as app.
     * <p>
     * Name of the file will be the timestamp.extension
     * </p>
     *
     * @param parent    parent directory path
     * @param extension extension of the file, pass null if you don't want to have
     *                  extension.
     * @param data      data to write
     * @throws ExternalFileWriterException if external storage is not available or free space is
     *                                     less than size of the data
     */
    public void writeDataToTimeStampedFile(File parent, String extension, String data)
            throws ExternalFileWriterException {
        if (isExternalStorageAvailable(true)) {
            getAppDirectory();

            String fileExtension = (TextUtils.isEmpty(extension)) ? "" : "." + extension;
            String fileName = System.currentTimeMillis() + fileExtension;

            File file = createFile(fileName, parent);

            writeDataToFile(file, data);
        }
    }

    /**
     * Writes data to the file. The file will be created in the directory name
     * same as app.
     * <p>
     * Name of the file will be the timestamp.extension
     * </p>
     *
     * @param extension extension of the file, pass null if you don't want to have
     *                  extension.
     * @param data      data to write
     * @throws ExternalFileWriterException if external storage is not available or free space is
     *                                     less than size of the data
     */
    public void writeDataToTimeStampedFile(String extension, byte[] data, boolean inCache)
            throws ExternalFileWriterException {
        if (isExternalStorageAvailable(true)) {
            getAppDirectory();

            String fileExtension = (TextUtils.isEmpty(extension)) ? "" : "." + extension;
            String fileName = System.currentTimeMillis() + fileExtension;

            File file = createFile(fileName, getAppDirectory(inCache));

            writeDataToFile(file, data);
        }
    }

    /**
     * Writes data to the file. The file will be created in the directory name
     * same as app.
     * <p>
     * Name of the file will be the timestamp.extension
     * </p>
     *
     * @param parent    parent directory path
     * @param extension extension of the file, pass null if you don't want to have
     *                  extension.
     * @param data      data to write
     * @throws ExternalFileWriterException if external storage is not available or free space is
     *                                     less than size of the data
     */
    public void writeDataToTimeStampedFile(File parent, String extension, byte[] data)
            throws ExternalFileWriterException {
        if (isExternalStorageAvailable(true)) {
            getAppDirectory();

            String fileExtension = (TextUtils.isEmpty(extension)) ? "" : "." + extension;
            String fileName = System.currentTimeMillis() + fileExtension;

            File file = createFile(fileName, parent);

            writeDataToFile(file, data);
        }
    }

    /**
     * Exception to report back developer about media state or storage state if
     * writing is not
     * possible
     */
    public class ExternalFileWriterException
            extends Exception {

        public ExternalFileWriterException(String messege) {
            super(messege);
        }

    }
}