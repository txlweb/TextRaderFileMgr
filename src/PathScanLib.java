import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class PathScanLib {
    public static String MainPath = "./rom";
    public static List<List<String>> PathScan(boolean is_vh, String key) {
        File file = new File(MainPath);
        String Blist = "";
        List<List<String>> Bi = new ArrayList<>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File value : files) {
                    if (value.isDirectory()) {
                        if (!IsHidden(value.getName()) | is_vh) {
                            List<String> a = new ArrayList<>();
                            if (!IsFile(MainPath + "/" + value.getName() + "/resource.ini")) {
                                if (value.getName().contains(key)) {
                                    if (!IsFile(MainPath + "/" + value.getName() + "/main.epub")) {
                                        //文件夹名,标题,图片,作者,简介
                                        a.add(value.getName());
                                        a.add(value.getName());
                                        a.add("icon.jpg");
                                        a.add("UNKNOW");
                                        a.add("UNKNOW");
                                        Bi.add(a);
                                    }else {
                                        a.add(value.getName());
                                        a.add(TextReaderLibVc.GetName(MainPath + "/" + value.getName() + "/main.epub"));
                                        a.add("icon.jpg");
                                        a.add(TextReaderLibVc.GetInfo(MainPath + "/" + value.getName() + "/main.epub").split("\"")[7]);
                                        if(TextReaderLibVc.GetInfo(MainPath + "/" + value.getName() + "/main.epub").split("\"").length>=12)
                                            a.add(TextReaderLibVc.GetInfo(MainPath + "/" + value.getName() + "/main.epub").split("\"")[11]);
                                        else
                                            a.add("UNKNOW");
                                        Bi.add(a);
                                    }
                                }
                            } else {
                                if (Objects.equals(key, "")) {
                                    a.add(value.getName());
                                    a.add(IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "title"));
                                    a.add("icon.jpg");
                                    a.add(IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "by"));
                                    a.add(IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "ot"));
                                    Bi.add(a);
                                } else {
                                    String things = IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "title") + IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "by") + IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "ot");
                                    //标题+作者+简介 或 md5真值完全一致
                                    if (things.contains(key) || value.getPath().equals(key)) {
                                        a.add(value.getName());
                                        a.add(IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "title"));
                                        a.add("icon.jpg");
                                        a.add(IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "by"));
                                        a.add(IniLib.GetThing(MainPath + "/" + value.getName() + "/resource.ini", "conf", "ot"));
                                        Bi.add(a);
                                    }
                                }
                                if (Config_dirs.Use_Server_LOG_DEBUG) System.out.println(value.getName());
                            }
                        }
                    }
                }
            }
            return Bi;
        }
        return Bi;
    }
    public static Boolean IsHidden(String name) {
        File t = new File(MainPath + "/" + name + "/hidden.info");
        return t.isFile();
    }
    public static boolean IsFile(String File_name) {
        return new File(File_name).exists();
    }
}
