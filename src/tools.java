import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class tools {
    public static boolean TextClearNullLine(String file) throws IOException {

        if(!new File(file).isFile()){
            System.out.println("E: 打不开文件");
            return false;
        }
        List<String> a = TeipMake.ReadCFGFile(file);
        StringBuilder buffer_text = new StringBuilder();
        for (int i = 0; i < a.size(); i++) {
            if(!Objects.equals(a.get(i), "")){
                buffer_text.append(a.get(i)).append("\r\n");
            }
        }
        TeipMake.WriteFileToThis(file, String.valueOf(buffer_text));
        return true;
    }
}
