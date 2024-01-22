import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class Boot {
    public static void main(String[] args) throws IOException {
        System.setProperty("file.encoding", "UTF-8");
        //导出库文件  ./annotations-20.1.0.jar ./core-v0.22.0.jar
        if (Main.class.getClassLoader().getResource("epublib-core-4.0-complete.jar") != null) {
            InputStream in = Objects.requireNonNull(Main.class.getClassLoader().getResource("epublib-core-4.0-complete.jar")).openStream();
            try (OutputStream ot = new FileOutputStream("./epublib-core-4.0-complete.jar")) {
                byte[] bytes = new byte[1024];
                int byteread;
                while ((byteread = in.read(bytes)) != -1) ot.write(bytes, 0, byteread);
            }
        }
        if (Main.class.getClassLoader().getResource("annotations-20.1.0.jar") != null) {
            InputStream in = Objects.requireNonNull(Main.class.getClassLoader().getResource("annotations-20.1.0.jar")).openStream();
            try (OutputStream ot = new FileOutputStream("./annotations-20.1.0.jar")) {
                byte[] bytes = new byte[1024];
                int byteread;
                while ((byteread = in.read(bytes)) != -1) ot.write(bytes, 0, byteread);
            }
        }
        if (Main.class.getClassLoader().getResource("core-v0.22.0.jar") != null) {
            InputStream in = Objects.requireNonNull(Main.class.getClassLoader().getResource("core-v0.22.0.jar")).openStream();
            try (OutputStream ot = new FileOutputStream("./core-v0.22.0.jar")) {
                byte[] bytes = new byte[1024];
                int byteread;
                while ((byteread = in.read(bytes)) != -1) ot.write(bytes, 0, byteread);
            }
        }
        if (Main.class.getClassLoader().getResource("msyh.ttc") != null) {
            InputStream in = Objects.requireNonNull(Main.class.getClassLoader().getResource("msyh.ttc")).openStream();
            try (OutputStream ot = new FileOutputStream("./msyh.ttc")) {
                byte[] bytes = new byte[1024];
                int byteread;
                while ((byteread = in.read(bytes)) != -1) ot.write(bytes, 0, byteread);
            }
        }




        Main.start_window();
    }
}
