

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class IniLib {
    private static final char[] hexCode = "0123456789abcdef".toCharArray();
    public static void allClose(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            try {
                if (closeable != null) closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> ReadCFGFile(String strFilePath) {
        File file = new File(strFilePath);
        List<String> rstr = new ArrayList<>();
        if (!file.exists() || file.isDirectory()) {
            System.out.println((char) 27 + "[31m[E]: 找不到文件." + (char) 27 + "[39;49m");
        } else {
            FileInputStream fileInputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                fileInputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(fileInputStream, EncodingDetect.getJavaEncode(strFilePath));
                bufferedReader = new BufferedReader(inputStreamReader);
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    rstr.add(str);
                }
                return rstr;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                allClose(bufferedReader, inputStreamReader, fileInputStream);
            }
        }
        return rstr;
    }

    public static boolean lastLineisCRLF(String filename) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(filename, "r");
            long pos = raf.length() - 2;
            if (pos < 0) return false; // too short
            raf.seek(pos);
            return raf.read() == '\r' && raf.read() == '\n';
        } catch (IOException e) {
            return false;
        } finally {
            if (raf != null) try {
                raf.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static String GetThing(String FileName, String Node, String key) {//will return key
        //如果文件尾部没有换行符,就要添加,否则会报错!!!!
        try {
            if (!lastLineisCRLF(FileName))
                Files.write(Paths.get(FileName), "\r\n".getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> lines = ReadCFGFile(FileName);
        boolean getLN = false;
        for (String line : lines) {
            if (line.contains("[" + Node + "]")) {
                getLN = true;
                continue;
            }
            if (getLN & (line.contains(key + "=") || line.contains(key + " ="))) {
                String[] a = line.split("=");
                return a[1];
            }
            if (line.contains("[") & line.contains("]")) {
                getLN = false;
            }
        }
        return "UnknownThing";
    }


    public static void SetThing(String FileName, String Node, String key, String Value) {//will return key
        if (!new File(FileName).isFile()) WriteFileToThis(FileName, "[" + Node + "]");
        List<String> lines = ReadCFGFile(FileName);
        boolean getLN = false;
        boolean changed = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.contains("[" + Node + "]")) {
                getLN = true;
                continue;
            }
            if (getLN & (line.contains(key + "=") || line.contains(key + " ="))) {
                lines.set(i, key + "=" + Value + "\r\n");//存在就覆写上去
                changed = true;
                break;
            }
            if (line.contains("[") & line.contains("]")) {
                if (getLN) {
                    lines.add(i - 1, key + "=" + Value + "\r\n");//如果找到node却没有key则在下一个node前插入k+v
                    changed = true;
                    break;
                }
                getLN = false;
            }
        }
        if (!changed) {//这种情况就是没有node或只有一个node
            if (getLN) {//直接写
                lines.add(key + "=" + Value + "\r\n");
            } else {//没node就创建
                lines.add("[" + Node + "]" + "\r\n");
                lines.add(key + "=" + Value + "\r\n");
            }
        }
        //写回文件
        StringBuilder ln = new StringBuilder();
        for (String line : lines) {
            if (line.contains("=") || line.contains("[") || line.contains("#")) ln.append(line).append("\r\n");
        }
        if (new File(FileName).isFile()) new File(FileName).delete();
        WriteFileToThis(FileName, String.valueOf(ln));
    }
    public static void WriteFileToThis(String file_name, String data) {
        try {
            File file = new File(file_name);
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getName(), true);
            BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
            bufferWriter.write(data);
            bufferWriter.close();
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void CopyFileToThis(File source, File dest) throws IOException {
        try (FileChannel inputChannel = new FileInputStream(source).getChannel(); FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
    }

    public static void compressFolder(String sourceFolder, String folderName, ZipOutputStream zipOutputStream) throws IOException {
        File folder = new File(sourceFolder);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 压缩子文件夹
                    compressFolder(file.getAbsolutePath(), folderName + "/" + file.getName(), zipOutputStream);
                } else {
                    // 压缩文件
                    addToZipFile(folderName + "/" + file.getName(), file.getAbsolutePath(), zipOutputStream);
                }
            }
        }
    }

    public static void addToZipFile(String fileName, String fileAbsolutePath, ZipOutputStream zipOutputStream) throws IOException {
        // 创建ZipEntry对象并设置文件名
        ZipEntry entry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(entry);

        // 读取文件内容并写入Zip文件
        try (FileInputStream fileInputStream = new FileInputStream(fileAbsolutePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                zipOutputStream.write(buffer, 0, bytesRead);
            }
        }

        // 完成当前文件的压缩
        zipOutputStream.closeEntry();
    }

    public static void unzipFiles(ZipInputStream zipInputStream, String outputFolder) throws IOException {
        byte[] buffer = new byte[1024];
        ZipEntry entry;

        while ((entry = zipInputStream.getNextEntry()) != null) {
            String fileName = entry.getName();
            File outputFile = new File(outputFolder + "/" + fileName);

            // 创建文件夹
            if (entry.isDirectory()) {
                outputFile.mkdirs();
            } else {
                // 创建文件并写入内容
                new File(outputFile.getParent()).mkdirs();
                try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                    int bytesRead;
                    while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                }
            }

            zipInputStream.closeEntry();
        }
    }

    public static String getFileMD5(String fileName) {
        File file = new File(fileName);
        try (InputStream stream = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int len;
            while ((len = stream.read(buf)) > 0) {
                digest.update(buf, 0, len);
            }
            return toHexString(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String getTextMD5(String Text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = Text.getBytes();
            digest.update(buf, 0, buf.length);
            return toHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    public static String toHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }


    public static String getFileHash256(String fileName) {
        File file = new File(fileName);
        FileInputStream fis = null;
        String sha256 = "";
        try {
            fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }
            byte[] digest = md.digest();
            sha256 = byte2hexLower(digest);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        return sha256;
    }

    private static String byte2hexLower(byte[] b) {
        String hs = "";
        String stmp = "";
        for (byte value : b) {
            stmp = Integer.toHexString(value & 0XFF);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }
    public static void deleteFileByIO(String filePath) {
        File file = new File(filePath);
        File[] list = file.listFiles();
        if (list != null) {
            for (File temp : list) {
                deleteFileByIO(temp.getAbsolutePath());
            }
        }
        file.delete();
    }

}
