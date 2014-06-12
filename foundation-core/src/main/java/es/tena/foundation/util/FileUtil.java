package es.tena.foundation.util;

import es.tena.foundation.util.filter.SQLFilter;
import es.tena.foundation.util.filter.XLSFilter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.comparator.NameFileComparator;

public final class FileUtil {

    private static final String encoding = "UTF-8";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private FileUtil() {
    }

    /**
     * Write byte array to file. If file already exists, it will be overwritten.
     *
     * @param file The file where the given byte array have to be written to.
     * @param bytes The byte array which have to be written to the given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, byte[] bytes) throws IOException {
        write(file, new ByteArrayInputStream(bytes), false);
    }

    /**
     * Write byte array to file with option to append to file or not. If not,
     * then any existing file will be overwritten.
     *
     * @param file The file where the given byte array have to be written to.
     * @param bytes The byte array which have to be written to the given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, byte[] bytes, boolean append) throws IOException {
        write(file, new ByteArrayInputStream(bytes), append);
    }

    /**
     * Write byte inputstream to file. If file already exists, it will be
     * overwritten.It's highly recommended to feed the inputstream as
     * BufferedInputStream or ByteArrayInputStream as those are been
     * automatically buffered.
     *
     * @param file The file where the given byte inputstream have to be written
     * to.
     * @param input The byte inputstream which have to be written to the given
     * file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, InputStream input) throws IOException {
        write(file, input, false);
    }

    /**
     * Write byte inputstream to file with option to append to file or not. If
     * not, then any existing file will be overwritten. It's highly recommended
     * to feed the inputstream as BufferedInputStream or ByteArrayInputStream as
     * those are been automatically buffered.
     *
     * @param file The file where the given byte inputstream have to be written
     * to.
     * @param input The byte inputstream which have to be written to the given
     * file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, InputStream input, boolean append) throws IOException {
        mkdirs(file);
        BufferedOutputStream output = null;

        try {
            output = new BufferedOutputStream(new FileOutputStream(file, append));
            int data = -1;
            while ((data = input.read()) != -1) {
                output.write(data);
            }
        } finally {
            close(input, file);
            close(output, file);
        }
    }

    /**
     * Saves an inputStream in a file
     *
     * @param is
     * @param filename
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static File save2FileISO(InputStream is, String filename) throws FileNotFoundException, IOException {

        BufferedOutputStream fOut = null;
        File f = new File(filename);
        try {
            fOut = new BufferedOutputStream(new FileOutputStream(f));
            byte[] buffer = new byte[32 * 1024];
            int bytesRead = 0;
            while ((bytesRead = is.read(buffer)) != -1) {
                fOut.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new IOException(" ERROR : " + e.toString());
        }
        is.close();
        fOut.close();
        return f;
    }

    /**
     * Save a collection of strings (meant for SQL scripts) to a file name
     *
     * @param strs
     * @param filename
     * @param encoding
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void save2file(Collection<String> strs, String filename, String encoding) throws FileNotFoundException, IOException {
        String urlFile = filename;
        Logger.getLogger(FileUtil.class.getName()).log(Level.INFO, "Creando: {0}", filename);
        FileOutputStream fos = new FileOutputStream(new File(urlFile));
        String scriptSQL = "";
        for (String sql : strs) {
            scriptSQL += sql + "\r\n";
        }
        fos.write(scriptSQL.getBytes(encoding));
        File read = new File(urlFile);
        FileInputStream fis = new FileInputStream(read);
        BufferedReader rd2 = new BufferedReader(new InputStreamReader(fis));
        String line2;
        StringBuilder file = new StringBuilder();
        while ((line2 = rd2.readLine()) != null) {
            file.append(line2);
            file.append('\n');
        }
        Logger.getLogger(FileUtil.class.getName()).log(Level.INFO, "Creado con exito: {0}", filename);

    }

    /**
     * Append the string to a file
     *
     * @param str
     * @param filename
     * @param encoding
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void save2fileAppend(StringBuilder str, String filename, String encoding) throws FileNotFoundException, IOException {
        String urlFile = filename;
        Logger.getLogger(FileUtil.class.getName()).log(Level.INFO, "Creando: {0}", filename);
        FileOutputStream fos = new FileOutputStream(new File(urlFile), true);
        fos.write(str.toString().getBytes(encoding));
        File read = new File(urlFile);
        FileInputStream fis = new FileInputStream(read);
        BufferedReader rd2 = new BufferedReader(new InputStreamReader(fis));
        String line2;
        StringBuilder file = new StringBuilder();
        while ((line2 = rd2.readLine()) != null) {
            file.append(line2);
            file.append('\n');
        }
    }

    /**
     * Save to a file the string supplied
     *
     * @param str
     * @param filename
     * @param encoding
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void save2file(StringBuilder str, String filename, String encoding) throws FileNotFoundException, IOException {
        String urlFile = filename;
        Logger.getLogger(FileUtil.class.getName()).log(Level.INFO, "Creando: {0}", filename);
        FileOutputStream fos = new FileOutputStream(new File(urlFile));
        fos.write(str.toString().getBytes(encoding));
        File read = new File(urlFile);
        FileInputStream fis = new FileInputStream(read);
        BufferedReader rd2 = new BufferedReader(new InputStreamReader(fis));
        String line2;
        StringBuilder file = new StringBuilder();
        while ((line2 = rd2.readLine()) != null) {
            file.append(line2);
            file.append('\n');
        }
        Logger.getLogger(FileUtil.class.getName()).log(Level.INFO, "Creado con exito {0}", filename);
    }

    /**
     * Save a inputStream into a file
     *
     * @param is
     * @param filename
     * @param encoding
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void save2File(InputStream is, String filename, String encoding) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(new File(filename));
        InputStreamReader isrd = new InputStreamReader(is);
        BufferedReader rd = new BufferedReader(isrd);
        Writer writer = new OutputStreamWriter(fos, encoding);
        try (BufferedWriter fout = new BufferedWriter(writer)) {
            String s;
            while ((s = rd.readLine()) != null) {
                fout.write(s);
                fout.newLine();
            }
            is.close();
        }
    }

    /**
     * Write character array to file. If file already exists, it will be
     * overwritten.
     *
     * @param file The file where the given character array have to be written
     * to.
     * @param chars The character array which have to be written to the given
     * file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, char[] chars) throws IOException {
        write(file, new CharArrayReader(chars), false);
    }

    /**
     * Write character array to file with option to append to file or not. If
     * not, then any existing file will be overwritten.
     *
     * @param file The file where the given character array have to be written
     * to.
     * @param chars The character array which have to be written to the given
     * file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, char[] chars, boolean append) throws IOException {
        write(file, new CharArrayReader(chars), append);
    }

    /**
     * Write string value to file. If file already exists, it will be
     * overwritten.
     *
     * @param file The file where the given string value have to be written to.
     * @param string The string value which have to be written to the given
     * file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, String string) throws IOException {
        write(file, new CharArrayReader(string.toCharArray()), false);
    }

    /**
     * Write string value to file with option to append to file or not. If not,
     * then any existing file will be overwritten.
     *
     * @param file The file where the given string value have to be written to.
     * @param string The string value which have to be written to the given
     * file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, String string, boolean append) throws IOException {
        write(file, new CharArrayReader(string.toCharArray()), append);
    }

    /**
     * Write character reader to file. If file already exists, it will be
     * overwritten. It's highly recommended to feed the reader as BufferedReader
     * or CharArrayReader as those are been automatically buffered.
     *
     * @param file The file where the given character reader have to be written
     * to.
     * @param reader The character reader which have to be written to the given
     * file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, Reader reader) throws IOException {
        write(file, reader, false);
    }

    /**
     * Write character reader to file with option to append to file or not. If
     * not, then any existing file will be overwritten. It's highly recommended
     * to feed the reader as BufferedReader or CharArrayReader as those are been
     * automatically buffered.
     *
     * @param file The file where the given character reader have to be written
     * to.
     * @param reader The character reader which have to be written to the given
     * file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, Reader reader, boolean append) throws IOException {
        mkdirs(file);
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file, append));
            int data = -1;
            while ((data = reader.read()) != -1) {
                writer.write(data);
            }
        } finally {
            close(reader, file);
            close(writer, file);
        }
    }

    /**
     * Write list of String records to file. If file already exists, it will be
     * overwritten.
     *
     * @param file The file where the given character reader have to be written
     * to.
     * @param records The list of String records which have to be written to the
     * given file.
     * @throws IOException If writing file fails.
     */
    public static void write(File file, List<String> records) throws IOException {
        write(file, records, false);
    }

    /**
     * Write list of String records to file with option to append to file or
     * not. If not, then any existing file will be overwritten.
     *
     * @param file The file where the given character reader have to be written
     * to.
     * @param records The list of String records which have to be written to the
     * given file.
     * @param append Append to file?
     * @throws IOException If writing file fails.
     */
    public static void write(File file, List<String> records, boolean append) throws IOException {
        mkdirs(file);
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file, append));
            for (String record : records) {
                writer.write(record);
                writer.write(LINE_SEPARATOR);
            }
        } finally {
            close(writer, file);
        }
    }

    /**
     * Writes a string into a file in the specified charset
     *
     * @param file
     * @param charset
     * @param data
     * @throws IOException
     */
    public static void writeToFile(File file, Charset charset, String data)
            throws IOException {
        OutputStream out = new FileOutputStream(file);
        Closeable stream = out;
        try {
            Writer writer = new OutputStreamWriter(out, charset);
            stream = writer;
            writer.write(data);
        } finally {
            stream.close();
        }
    }

    /**
     * Reads a string from a file in the specified charset
     *
     * @param file
     * @param charset
     * @return
     * @throws IOException
     */
    public static String readFromFile(File file, Charset charset) throws IOException {
        InputStream in = new FileInputStream(file);
        Closeable stream = in;
        try {
            Reader reader = new InputStreamReader(in, charset);
            stream = reader;
            StringBuilder inputBuilder = new StringBuilder();
            char[] buffer = new char[1024];
            while (true) {
                int readCount = reader.read(buffer);
                if (readCount < 0) {
                    break;
                }
                inputBuilder.append(buffer, 0, readCount);
            }
            return inputBuilder.toString();
        } finally {
            stream.close();
        }
    }

    /**
     * Read byte array from file. Take care with big files, this would be memory
     * hogging, rather use readStream() instead.
     *
     * @param file The file to read the byte array from.
     * @return The byte array with the file contents.
     * @throws IOException If reading file fails.
     */
    public static byte[] readBytes(File file) throws IOException {
        BufferedInputStream stream = (BufferedInputStream) readStream(file);
        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        return bytes;
    }

    /**
     * Read byte stream from file.
     *
     * @param file The file to read the byte stream from.
     * @return The byte stream with the file contents (actually:
     * BufferedInputStream).
     * @throws IOException If reading file fails.
     */
    public static InputStream readStream(File file) throws IOException {
        return new BufferedInputStream(new FileInputStream(file));
    }

    /**
     * Read character array from file. Take care with big files, this would be
     * memory hogging, rather use readReader() instead.
     *
     * @param file The file to read the character array from.
     * @return The character array with the file contents.
     * @throws IOException If reading file fails.
     */
    public static char[] readChars(File file) throws IOException {
        BufferedReader reader = (BufferedReader) readReader(file);
        char[] chars = new char[(int) file.length()];
        reader.read(chars);
        return chars;
    }

    /**
     * Read string value from file. Take care with big files, this would be
     * memory hogging, rather use readReader() instead.
     *
     * @param file The file to read the string value from.
     * @return The string value with the file contents.
     * @throws IOException If reading file fails.
     */
    public static String readString(File file) throws IOException {
        return new String(readChars(file));
    }

    /**
     * Read character reader from file.
     *
     * @param file The file to read the character reader from.
     * @return The character reader with the file contents (actually:
     * BufferedReader).
     * @throws IOException If reading file fails.
     */
    public static Reader readReader(File file) throws IOException {
        return new BufferedReader(new FileReader(file));
    }

    /**
     * Read list of String records from file.
     *
     * @param file The file to read the character writer from.
     * @return A list of String records which represents lines of the file
     * contents.
     * @throws IOException If reading file fails.
     */
    public static List<String> readRecords(File file) throws IOException {
        BufferedReader reader = (BufferedReader) readReader(file);
        List<String> records = new ArrayList<>();
        String record = null;

        try {
            while ((record = reader.readLine()) != null) {
                records.add(record);
            }
        } finally {
            close(reader, file);
        }

        return records;
    }

    /**
     * Copy file. Any existing file at the destination will be overwritten.
     *
     * @param source The file to read the contents from.
     * @param destination The file to write the contents to.
     * @throws IOException If copying file fails.
     */
    public static void copy(File source, File destination) throws IOException {
        copy(source, destination, true);
    }

    /**
     * Copy file with the option to overwrite any existing file at the
     * destination.
     *
     * @param source The file to read the contents from.
     * @param destination The file to write the contents to.
     * @param overwrite Set whether to overwrite any existing file at the
     * destination.
     * @throws IOException If the destination file already exists while
     * <tt>overwrite</tt> is set to false, or if copying file fails.
     */
    public static void copy(File source, File destination, boolean overwrite) throws IOException {
        if (destination.exists() && !overwrite) {
            throw new IOException("Copying file " + source.getPath() + " to " + destination.getPath() + " failed."
                    + " The destination file already exists.");
        }

        mkdirs(destination);
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            input = new BufferedInputStream(new FileInputStream(source));
            output = new BufferedOutputStream(new FileOutputStream(destination));
            int data = -1;
            while ((data = input.read()) != -1) {
                output.write(data);
            }
        } finally {
            close(input, source);
            close(output, destination);
        }
    }

    /**
     * Move (rename) file. Any existing file at the destination will be
     * overwritten.
     *
     * @param source The file to be moved.
     * @param destination The new destination of the file.
     * @throws IOException If moving file fails.
     */
    public static void move(File source, File destination) throws IOException {
        move(source, destination, true);
    }

    /**
     * Move (rename) file with the option to overwrite any existing file at the
     * destination.
     *
     * @param source The file to be moved.
     * @param destination The new destination of the file.
     * @param overwrite Set whether to overwrite any existing file at the
     * destination.
     * @throws IOException If the destination file already exists while
     * <tt>overwrite</tt> is set to false, or if moving file fails.
     */
    public static void move(File source, File destination, boolean overwrite) throws IOException {
        if (destination.exists()) {
            if (overwrite) {
                destination.delete();
            } else {
                throw new IOException(
                        "Moving file " + source.getPath() + " to " + destination.getPath() + " failed."
                        + " The destination file already exists.");
            }
        }

        mkdirs(destination);

        if (!source.renameTo(destination)) {
            throw new IOException(
                    "Moving file " + source.getPath() + " to " + destination.getPath() + " failed.");
        }
    }

    /**
     * Trim the eventual file path from the given file name. Anything before the
     * last occurred "/" and "\" will be trimmed, including the slash.
     *
     * @param fileName The file name to trim the file path from.
     * @return The file name with the file path trimmed.
     */
    public static String trimFilePath(String fileName) {
        return fileName
                .substring(fileName.lastIndexOf(FILE_SEPARATOR) + 1)
                .substring(fileName.lastIndexOf("\\") + 1);
    }

    /**
     * Gets a file name from a string file path
     *
     * @param fileName The file name and path.
     * @return The file name.
     */
    public static String getFilePath(String fileName) {
        return fileName.substring(fileName.lastIndexOf(FILE_SEPARATOR) + 1);
    }

    /**
     * Generate unique file based on the given path and name. If the file
     * exists, then it will add "[i]" to the file name as long as the file
     * exists. The value of i can be between 0 and 2147483647 (the value of
     * Integer.MAX_VALUE).
     *
     * @param filePath The path of the unique file.
     * @param fileName The name of the unique file.
     * @return The unique file.
     * @throws IOException If unique file cannot be generated, this can be
     * caused if all file names are already in use. You may consider another
     * filename instead.
     */
    public static File uniqueFile(File filePath, String fileName) throws IOException {
        File file = new File(filePath, fileName);
        if (file.exists()) {
            // Split filename and add braces, e.g. "name.ext" --> "name[", "].ext".
            String prefix;
            String suffix;
            int dotIndex = fileName.lastIndexOf(".");
            if (dotIndex > -1) {
                prefix = fileName.substring(0, dotIndex) + "[";
                suffix = "]" + fileName.substring(dotIndex);
            } else {
                prefix = fileName + "[";
                suffix = "]";
            }
            int count = 0;
            // Add counter to filename as long as file exists.
            while (file.exists()) {
                if (count < 0) { // int++ restarts at -2147483648 after 2147483647.
                    throw new IOException("No unique filename available for " + fileName
                            + " in path " + filePath.getPath() + ".");
                }
                // append counter between prefix and suffix, e.g. "name[" + count + "].ext".
                file = new File(filePath, prefix + (count++) + suffix);
            }
        }

        return file;
    }

    /**
     * Check and create missing parent directories for the given file.
     *
     * @param file The file to check and create the missing parent directories
     * for.
     * @throws IOException If the given file is actually not a file or if
     * creating parent directories fails.
     */
    public static void mkdirs(File file) throws IOException {
        if (file.exists() && !file.isFile()) {
            throw new IOException("File " + file.getPath() + " is actually not a file.");
        }
        File parentFile = file.getParentFile();
        if (!parentFile.exists() && !parentFile.mkdirs()) {
            throw new IOException("Creating directories " + parentFile.getPath() + " failed.");
        }
    }

    /**
     * Close the given I/O resource of the given file.
     *
     * @param resource The I/O resource to be closed.
     * @param file The I/O resource's subject.
     */
    public static void close(Closeable resource, File file) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                throw new RuntimeException("Closing file " + file.getPath() + " failed.");
            }
        }
    }

    /**
     * Substitutes any suspect character of not using a file separator
     *
     * @param path
     * @return
     */
    public static String subsPathSeparators(String path) {
        return path.replace("/", File.separator).replace("\\", File.separator);
    }

    /**
     * Converts a file into a properties
     *
     * @param file
     * @return
     * @throws java.io.IOException
     */
    public static Properties loadParams(String file) throws IOException {
        Properties prop = new java.util.Properties();
        ResourceBundle bundle = java.util.ResourceBundle.getBundle(file);
        Enumeration<String> enumera = bundle.getKeys();
        String key;
        while (enumera.hasMoreElements()) {
            key = (String) enumera.nextElement();
            prop.put(key, bundle.getObject(key));
        }
        return prop;
    }

    /**
     * To use with POI. substitutes a template file with the tmpfile provided
     *
     * @param zipfile the template file
     * @param tmpfile the XML file with the sheet data
     * @param entry the name of the sheet entry to substitute, e.g.
     * xl/worksheets/sheet1.xml
     * @param out the stream to write the result to
     * @throws java.io.IOException
     */
    public static void substitute(File zipfile, File tmpfile, String entry, OutputStream out) throws IOException {

        ZipFile zip = new ZipFile(zipfile);

        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            @SuppressWarnings("unchecked")
            Enumeration<ZipEntry> en = (Enumeration<ZipEntry>) zip.entries();
            while (en.hasMoreElements()) {
                ZipEntry ze = en.nextElement();
                if (!ze.getName().equals(entry)) {
                    zos.putNextEntry(new ZipEntry(ze.getName()));
                    try (InputStream is = zip.getInputStream(ze)) {
                        copyStream(is, zos);
                    }
                }
            }
            zos.putNextEntry(new ZipEntry(entry));
            try (InputStream is = new FileInputStream(tmpfile)) {
                copyStream(is, zos);
            }
        }
    }

    /**
     * Copy the stream supplied
     *
     * @param in
     * @param out
     * @throws IOException
     */
    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] chunk = new byte[1024];
        int count;
        while ((count = in.read(chunk)) >= 0) {
            out.write(chunk, 0, count);
        }
    }

    /**
     * Gets the files in a folder
     *
     * @param folder
     * @return
     */
    public static File[] getFilesInFolder(String folder) {
        File dir = new File(folder);
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isDirectory();
            }
        };
        return dir.listFiles(fileFilter);
    }

    /**
     * Gets txt files in a folder
     *
     * @param folder
     * @return
     */
    public static File[] getTXTFilesInFolder(String folder) {
        File dir = new File(folder);
        FileFilter fileFilter = new TXTFilter();
        File[] files = dir.listFiles(fileFilter);
        Arrays.sort(files, NameFileComparator.NAME_INSENSITIVE_COMPARATOR);
        return files;
    }

    /**
     * Gets the file names in a folder order by name ascendent
     *
     * @param folder
     * @return
     */
    public static List<String> getFileNamesInFolder(String folder) {
        File[] files = getFilesInFolder(folder);
        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }

    /**
     * Gets the SQL files in a folder ordered by name ascendent
     *
     * @param folder
     * @return
     */
    public static File[] getSQLFilesInFolder(String folder) {
        File dir = new File(folder);
        FileFilter fileFilter = new SQLFilter();
        File[] files = dir.listFiles(fileFilter);
        Arrays.sort(files, NameFileComparator.NAME_INSENSITIVE_COMPARATOR);
        return files;
    }

    /**
     * Gets the SQL file names in a folder
     *
     * @param folder
     * @return
     */
    public static List<String> getSQLFileNamesInFolder(String folder) {
        File[] files = getSQLFilesInFolder(folder);
        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }

    /**
     * Gets the SQL files absolute path in a folder
     *
     * @param folder
     * @return
     */
    public static List<String> getSQLFileNamesInFolderPath(String folder) {
        File[] files = getSQLFilesInFolder(folder);
        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getAbsolutePath());
        }
        return fileNames;
    }

    /**
     * Gets the XLS files in a folder order by name ascendent
     *
     * @param folder
     * @return lista de ficheros ordenados por fecha de modificacion
     */
    public static File[] getXLSFilesInFolder(String folder) {
        File dir = new File(folder);
        FileFilter fileFilter = new XLSFilter();
        File[] files = dir.listFiles(fileFilter);
        Arrays.sort(files, NameFileComparator.NAME_INSENSITIVE_COMPARATOR);
        return files;
    }

    /**
     * Gets the XLS file names in a folder order by name ascendent
     *
     * @param folder
     * @return
     */
    public static List<String> getXLSFileNamesInFolder(String folder) {
        File[] files = getXLSFilesInFolder(folder);
        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }

    /**
     * Gets the XLS file absolute path names in a folder
     *
     * @param folder
     * @return
     */
    public static List<String> getXLSFileNamesInFolderPath(String folder) {
        File[] files = getXLSFilesInFolder(folder);
        List<String> fileNames = new ArrayList<>();
        for (File file : files) {
            fileNames.add(file.getAbsolutePath());
        }
        return fileNames;
    }

    /**
     * counts how many lines there are in a file
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static int countLines(File file) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        }
    }

    /**
     * counts how many lines there are in a file
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static int countLines(String filename) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filename))) {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        }
    }

    /**
     * Gets all folders in a path
     *
     * @param path
     * @return
     */
    public static List<File> getFolders(String path) {
        File fname = new File(path);
        List<File> folders = new ArrayList<>();
        FileFilter directoryFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };

        File[] files = fname.listFiles(directoryFilter);
        for (File file : files) {
            if (file.isDirectory()) {
                folders.addAll(Arrays.asList(file));
            }

        }
        return folders;

    }
}
