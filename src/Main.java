import org.ice1000.jimgui.*;
import org.ice1000.jimgui.flag.JImWindowFlags;
import org.ice1000.jimgui.util.JniLoader;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Main {
    public static void start_window() throws IOException {

        JniLoader.load();
        JImGui imGui = new JImGui("T-R-Mgr");

        JImGuiIO imGio = imGui.getIO();
        //导出字库

        if (Main.class.getClassLoader().getResource("msyh.ttc") != null)
            imGio.getFonts().addFontFromFile("./msyh.ttc",20.0f,new JImFontConfig(),imGio.getFonts().getGlyphRangesForChineseFull());
        else//测试环境用的
            imGio.getFonts().addFontFromFile("./font/msyh.ttc",20.0f,new JImFontConfig(),imGio.getFonts().getGlyphRangesForChineseFull());

        List<List<String>> s =  PathScanLib.PathScan(true,"");
        NativeInt line_max_size = new NativeInt();
        line_max_size.modifyValue(42);





        NativeString out = new NativeString();
        NativeString V_m_p = new NativeString();
        NativeString V_m_m = new NativeString();
        NativeInt V_m_l = new NativeInt();
        NativeBool V_m_s = new NativeBool();
        NativeString V_s_p = new NativeString();
        NativeString V_s_m = new NativeString();
        NativeInt V_s_l = new NativeInt();
        //初始化配置文件信息
        Config_dirs.init_configs();
        NS_add_string(V_m_p, String.valueOf(Config_dirs.NormPort));
        NS_add_string(V_m_m, String.valueOf(Config_dirs.MainPath));
        V_m_s.modifyValue(Config_dirs.Use_Share);
        V_m_l.modifyValue(1);
        Config_dirs_b.init_configs();
        NS_add_string(V_s_p, String.valueOf(Config_dirs_b.NormPort));
        V_s_l.modifyValue(1);
        //配置窗口可视情况
        boolean TRMGR = true;
        boolean TRCOF = true;
        boolean BGCR = false;
        NativeBool nb = new NativeBool();
        NativeBool nb1 = new NativeBool();
        nb.modifyValue(true);
        nb1.modifyValue(true);

        File directory = new File(Config_dirs.MainPath);
        long directorySize = calculateDirectorySize(directory);

        while (!imGui.windowShouldClose()){
            imGui.initNewFrame();
            JImGui.beginMainMenuBar();
            if(imGui.beginMenu("文件",true)){
                if(imGui.button("导入teip/epub")) {
                    String fn = Window_Select_Things(imGui,"选择文件 - 导入teip或epub文件");
                    if(Objects.equals(fn, "epub")){
                        TeipMake.EpubMake(fn);
                    } else if (Objects.equals(fn, "teip")) {
                        TeipMake.Unzip(fn,Config_dirs.MainPath);
                    }else {
                        if (Window_y_n(imGui, "选择文件类型", "我们不能准确分别这个文件的类型,请选择\r\n   是钮为epub文件   否钮 为teip文件\r\n 如果您导入的epub文件并不可读,会导致程序闪退,请删除它再启动程序."))
                            TeipMake.EpubMake(fn);
                        else
                            TeipMake.Unzip(fn,Config_dirs.MainPath);
                    }

                }
                if(imGui.button("导入txt")){
                    String fn = Window_Select_Things(imGui,"选择文件 - 导入txt文件");
                    String fn1 = Window_Select_Things(imGui,"选择文件 - 导入图标(jpg)文件");
                    TeipMake.autoMake(fn,
                            "tmp.zip",
                            Window_Input(imGui,"输入信息","请输入小说标题","测试小说Vb"),
                            fn1,
                            Window_Input(imGui,"输入信息","请输入小说切章规则(不用动)",".*第.*章.*"),
                            Window_Input(imGui,"输入信息","请输入小说作者","测试作者"),
                            Window_Input(imGui,"输入信息","请输入小说简介","测试简介"));
                    TeipMake.Unzip("tmp.zip",Config_dirs.MainPath);
                    Window_y_n(imGui, "完成.", "小说已经成功导入.");

                }
                JImGuiGen.endMenu();
            }
            if(imGui.beginMenu("打开窗口",true)){
                if(imGui.button("小说管理")) {
                    TRMGR = true;
                    nb.modifyValue(true);
                }
                if(imGui.button("程序配置")){
                    TRCOF=true;
                    nb1.modifyValue(true);
                }
                JImGuiGen.endMenu();
            }
            if(imGui.beginMenu("杂项",true)){
                if(imGui.button("关于")) {
                    if(!Window_y_n(imGui, "关于 - TextReader Config tool", "Vre. Beta 1.3.0-2091b-240108\r\n  这是一款由IDlike自主研发的小说阅读器,本程序为书籍管\r\n理工具,本程序仅供学习参考,不可商用!\r\n                 IDSOFT @ IDlike 2024/1/8"))
                        while(!Window_y_n(imGui, "[悲]", "作者会掉小珍珠的 嘤嘤嘤..."));
                }
                if(!BGCR){
                    if(imGui.button("背景透明(可能有bug)")){
                        Runtime.getRuntime().exec("./bgcr.exe");
                        BGCR=true;
                    }
                }else {
                    if(imGui.button("重启程序")){
                        JImGui.closeCurrentPopup();
                        start_window();
                        BGCR=false;
                    }
                }

                if(imGui.button("退出程序")){
                    return;
                }

                JImGuiGen.endMenu();
            }
            JImGui.endMenuBar();
            if(TRMGR) {
                if (imGui.begin("TextReader File Manager",nb)) {
                    imGui.setWindowSize("TextReader File Manager",700,600);
                    if(!nb.accessValue()) TRMGR=false;
                    imGui.text("");
                    imGui.text("搜索小说(标题/作者/简介)");
                    imGui.text("搜索:");
                    imGui.sameLine();
                    imGui.inputText("", out);
                    imGui.sameLine();
                    if (imGui.button("搜索")) {
                        System.out.println(out);
                        s = PathScanLib.PathScan(true, String.valueOf(out));
                    }

                    imGui.text("一行显示文本数:");
                    imGui.sameLine();
                    imGui.sliderInt("5", line_max_size, 20, 150);
                    //imGui.text("#ID    操作      小说名(文件夹名)");
                    imGui.text("");
                    for (int i = 0; i < s.size(); i++) {
                        imGui.beginTabBar(String.valueOf(i));
                        imGui.text("#" + (i + 1));
                        imGui.sameLine(45f);
                        if (imGui.button("删除     " + i, 38f, 22f)) {
                            System.out.println("删除#" + i);
                            if (Window_y_n(imGui, "删除确认", "你是否真的要删除这个小说<" + s.get(i).get(1) + ">\r\n这个操作不能恢复,请谨慎操作!"))
                                IniLib.deleteFileByIO(Config_dirs.MainPath + "/" + s.get(i).get(0));

                            //刷新内容
                            s = PathScanLib.PathScan(true, "");
                            break;
                        }
                        imGui.sameLine(88f);
                        if (!PathScanLib.IsHidden(s.get(i).get(0))) {
                            if (imGui.button("隐藏     " + i, 38f, 22f)) {
                                System.out.println("隐藏#" + i);
                                IniLib.WriteFileToThis("./hidden.info", "HIDDEN!");
                                IniLib.CopyFileToThis(new File("./hidden.info"), new File(Config_dirs.MainPath + "/" + s.get(i).get(0) + "/hidden.info"));
                            }
                        } else {
                            if (imGui.button("显示     " + i, 38f, 22f)) {
                                System.out.println("显示#" + i);
                                new File(Config_dirs.MainPath + "/" + s.get(i).get(0) + "/hidden.info").delete();
                            }
                        }
                        imGui.sameLine(132f);
                        imGui.text("标题: " + s.get(i).get(1) + "  (MD5=" + s.get(i).get(0) + ")");
                        imGui.text("作者: " + s.get(i).get(3));
                        imGui.text("简介: " + AutoBR(s.get(i).get(4), line_max_size.accessValue()));
                    }//格式化目录
                }
            }

            if(TRCOF) {
                if (imGui.begin("TextReader Config Manager",nb1)) {
                    imGui.setWindowSize("TextReader Config Manager",500,300);
                    if(!nb1.accessValue()) TRCOF=false;
                    if (imGui.beginTabBar("MainBar")) {
                        if (imGui.beginTabItem("主程序")) {
                            imGui.text("服务端口:");
                            imGui.sameLine();
                            imGui.inputText("1", V_m_p);
                            imGui.sameLine();
                            if (imGui.button("恢复默认 ")) {
                                V_m_p = new NativeString();
                                NS_add_string(V_m_p, "8080");
                            }
                            imGui.text("主路径:  ");
                            imGui.sameLine();
                            imGui.inputText("2", V_m_m);
                            imGui.sameLine();
                            if (imGui.button("恢复默认  ")) {
                                V_m_m = new NativeString();
                                NS_add_string(V_m_m, "./rom");
                            }
                            imGui.text("日志等级:");
                            imGui.sameLine();
                            imGui.sliderInt("3", V_m_l, 0, 2);
                            imGui.sameLine();
                            if (imGui.button("恢复默认   ")) {
                                V_m_l.modifyValue(1);
                            }
                            if (imGui.checkbox("启用分享器?", V_m_s)) {
                                if (!V_m_s.accessValue()) {
                                    Window_y_n(imGui, "作者建议", "这个功能不建议关闭,但是您的网络环境如果比较复杂还请必须关闭!");
                                }
                            }
                            if (imGui.button("保存配置文件")) {
                                IniLib.SetThing("./config.ini", "settings", "MainPath", V_m_m.toString());
                                IniLib.SetThing("./config.ini", "settings", "Port", V_m_p.toString());
                                IniLib.SetThing("./config.ini", "settings", "LogRank", V_m_l.toString());
                                if (V_m_s.accessValue()) {
                                    IniLib.SetThing("./config.ini", "settings", "UseShare", "enable");
                                } else {
                                    IniLib.SetThing("./config.ini", "settings", "UseShare", "disable");
                                }
                            }
                            JImGuiGen.endTabItem();
                        }
                        if (imGui.beginTabItem("分享器")) {
                            imGui.text("服务端口:");
                            imGui.sameLine();
                            imGui.inputText("4", V_s_p);
                            imGui.sameLine();
                            if (imGui.button("恢复默认 ")) {
                                V_s_p = new NativeString();
                                NS_add_string(V_s_p, "8090");
                            }
                            imGui.text("日志等级:");
                            imGui.sameLine();
                            imGui.sliderInt("5", V_s_l, 0, 2);
                            imGui.sameLine();
                            if (imGui.button("恢复默认    ")) {
                                V_s_l.modifyValue(1);
                            }
                            if (imGui.button("保存配置文件")) {
                                IniLib.SetThing("./config_share.ini", "settings", "Port", V_s_p.toString());
                                IniLib.SetThing("./config_share.ini", "settings", "LogRank", V_s_l.toString());
                            }
                            JImGuiGen.endTabItem();
                        }
                    }
                    JImGuiGen.endTabBar();
                }
            }
            if (imGui.begin("TextReader Storage Manager")) {
                imGui.text("小说占用空间: "+new DecimalFormat("#.00").format((float)((int) (directorySize))/1024/1024)+"MB");
                imGui.sameLine();
                if(imGui.button("刷新")) directorySize = calculateDirectorySize(directory);
                imGui.setWindowSize("TextReader Storage Manager", 300, 70);
                imGui.setWindowPos("TextReader Storage Manager",0,32);
            }

            imGui.render();
        }
    }
    public static void NS_add_string(NativeString ns,String text){
        //逆天!居然得一个一个字节压进去(
        byte[] a = text.getBytes();
        for (byte b : a) {
            ns.append(b);
        }
    }
    public static List<List<String>> ScanPathWithThis(String path){
        File file = new File(path);
        List<List<String>> Bi = new ArrayList<>();
        //目录,文件
        Bi.add(new ArrayList<>());
        Bi.add(new ArrayList<>());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File value : files) {
                    if (value.isDirectory()) {
                        Bi.get(0).add(value.getName());
                    }else{
                        Bi.get(1).add(value.getName());
                    }
                }
            }
        }
        return Bi;
    }
    public static String Window_Select_Things(JImGui imGui,String Title) {
        String pa = "./";
        List<List<String>> p = ScanPathWithThis(pa);
        while (!imGui.windowShouldClose()) {
            imGui.initNewFrame();
            imGui.begin(Title,new NativeBool(), JImWindowFlags.NoNavFocus);
            if(imGui.button("[Path] ..")) {
                String[] a = pa.split("/");
                System.out.println(Arrays.toString(a));
                pa="";
                for (int i = 0; i < a.length - 1; i++) {
                    pa = pa + a[i]+"/";
                }
                if(pa.equals(".")) pa="./";
                if(pa.equals("")) pa="./";
                p = ScanPathWithThis(pa);
            }
            for (int i = 0; i < p.get(0).size(); i++) {
                if(imGui.button("[Path] "+p.get(0).get(i))){
                    pa = pa+"/"+p.get(0).get(i);
                    p = ScanPathWithThis(pa);
                    System.out.println(p);
                }
            }
            imGui.beginTabBar("114514");
            for (int i = 0; i < p.get(1).size(); i++) {
                if(imGui.button("[File] "+p.get(1).get(i))){
                    return pa+"/"+p.get(1).get(i);
                }
            }
            imGui.render();
        }
        return "";
    }

    public static boolean Window_y_n(JImGui imGui,String Title,String say){
        while (!imGui.windowShouldClose()) {
            imGui.initNewFrame();
            imGui.begin(Title,new NativeBool(), JImWindowFlags.NoTitleBar);
            imGui.setWindowSize(Title,400,300);
            imGui.text(Title);
            imGui.text("");
            imGui.text(say);
            if (imGui.button("是(Y)")) {
                return true;
            }
            imGui.sameLine();
            if (imGui.button("否(N)")) {
                return false;
            }
            imGui.render();
        }
        return false;
    }
    public static String Window_Input(JImGui imGui,String Title,String say,String auto_thing){
        NativeString out = new NativeString();
        NS_add_string(out,auto_thing);
        while (!imGui.windowShouldClose()) {
            imGui.initNewFrame();
            imGui.begin(Title,new NativeBool(), JImWindowFlags.NoTitleBar);
            imGui.setWindowSize(Title,400,300);
            imGui.text(Title);
            imGui.text("");
            imGui.text(AutoBR(say, 40));
            imGui.inputText("", out);
            if (imGui.button("提 交")) {
                return out.toString();
            }
            imGui.sameLine();

            imGui.render();
        }
        return out.toString();
    }
    public static String AutoBR(String text,int line_size){
        String[] t = text.split("");
        int s = 0;
        StringBuilder rt= new StringBuilder();
        for (String string : t) {
            s++;
            if (s > line_size) {
                s = 0;
                rt.append("\r\n");
            }
            rt.append(string);
        }
        return rt.toString();
    }
    public static long calculateDirectorySize(File directory) {
        long size = 0;
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    } else {
                        size += calculateDirectorySize(file); // 递归处理子目录
                    }
                }
            }
        }
        return size;
    }
}