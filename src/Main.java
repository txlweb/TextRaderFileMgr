import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ice1000.jimgui.*;
import org.ice1000.jimgui.flag.JImWindowFlags;
import org.ice1000.jimgui.util.JniLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class Main {
    public static void start_window() throws IOException {
        //System.out.println(TextReaderLibVc.IsEpubFile("bgcr.exe"));
        JniLoader.load();
        JImGui imGui = new JImGui("T-R-Mgr");

        JImGuiIO imGio = imGui.getIO();
        //导出字库

        if (Main.class.getClassLoader().getResource("msyh.ttc") != null || new File("./msyh.ttc").isFile())
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
        NativeBool V_m_f = new NativeBool();
        NativeString V_s_p = new NativeString();
        NativeString V_s_m = new NativeString();
        NativeInt V_s_l = new NativeInt();
        //初始化配置文件信息
        Config_dirs.init_configs();
        NS_add_string(V_m_p, String.valueOf(Config_dirs.NormPort));
        NS_add_string(V_m_m, String.valueOf(Config_dirs.MainPath));
        V_m_s.modifyValue(Config_dirs.Use_Share);
        V_m_f.modifyValue(Config_dirs.Fire_Wall);
        V_m_l.modifyValue(1);
        Config_dirs_b.init_configs();
        NS_add_string(V_s_p, String.valueOf(Config_dirs_b.NormPort));
        V_s_l.modifyValue(1);
        //配置窗口可视情况
        boolean TRMGR = true;
        boolean TRCOF = true;
        boolean BGCR = false;
        boolean CFGS = true;
        boolean Re_load = false;
        boolean fix_json = false;
        NativeBool nb = new NativeBool();
        NativeBool nb1 = new NativeBool();
        NativeBool nb2 = new NativeBool();
        nb.modifyValue(true);
        nb1.modifyValue(true);
        nb2.modifyValue(true);
        JsonParser jp_apis = new JsonParser();
        JsonObject json_apis = null;
        JsonParser jp_confs = new JsonParser();
        JsonObject json_confs = null;
        try {//检查json是否能读,不能提示修复

            json_apis = (JsonObject) jp_apis.parse(new FileReader("./style/API_list.json"));

            json_confs = (JsonObject) jp_confs.parse(new FileReader("./style/config.json"));
            System.out.println(json_apis.toString());
            System.out.println(json_confs.toString());
        } catch (Exception e) {
            fix_json = true;
        }
        File directory = new File(Config_dirs.MainPath);
        long directorySize = calculateDirectorySize(directory);
        NativeInt V_c_c = new NativeInt();

        while (!imGui.windowShouldClose()){
            imGui.initNewFrame();
            JImGui.beginMainMenuBar();
            if(imGui.beginMenu("文件",true)){
                if(imGui.button("导入teip/epub")) {
                    String fn = Window_Select_Things(imGui,"选择文件 - 导入teip或epub文件");
                    if(!fn.isEmpty()) {
                        if (Objects.equals(fn, "epub")) {
                            TeipMake.EpubMake(fn);
                        } else if (Objects.equals(fn, "teip")) {
                            TeipMake.Unzip(fn, Config_dirs.MainPath);
                        } else {
                            if (Window_y_n(imGui, "选择文件类型", "我们不能准确分别这个文件的类型,请选择\r\n   是钮为epub文件   否钮 为teip文件\r\n 如果您导入的epub文件并不可读,会导致程序闪退,请删除它再启动程序."))
                                TeipMake.EpubMake(fn);
                            else
                                TeipMake.Unzip(fn, Config_dirs.MainPath);
                        }
                    }
                    s = PathScanLib.PathScan(true, String.valueOf(out));
                }
                if(imGui.button("导入txt")){
                    String fn = Window_Select_Things(imGui,"选择文件 - 导入txt文件");
                    if(!fn.isEmpty()) {
                        String fn1 = Window_Select_Things(imGui, "选择文件 - 导入图标(jpg)文件");
                        if (!fn1.isEmpty()) {
                            TeipMake.autoMake(fn,
                                    "tmp.zip",
                                    Window_Input(imGui, "输入信息", "请输入小说标题", "测试小说Vb"),
                                    fn1,
                                    Window_Input(imGui, "输入信息", "请输入小说切章规则(不用动)", ".*第.*章.*"),
                                    Window_Input(imGui, "输入信息", "请输入小说作者", "测试作者"),
                                    Window_Input(imGui, "输入信息", "请输入小说简介", "测试简介"));
                            TeipMake.Unzip("tmp.zip", Config_dirs.MainPath);
                            Window_y_n(imGui, "完成.", "小说已经成功导入.");
                        }
                    }
                    s = PathScanLib.PathScan(true, String.valueOf(out));
                }
                if(imGui.button("txt去空行")) {
                    if(tools.TextClearNullLine(Window_Select_Things(imGui,"选择文件 - 处理txt文件空行"))){
                        Window_y_n(imGui, "完成.", "小说已去空行");
                    }else {
                        Window_y_n(imGui, "失败.", "!! 这个小说不能被打开 !!");
                    }
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
                if(imGui.button("json配置")){
                    CFGS=true;
                    nb2.modifyValue(true);
                }
                JImGuiGen.endMenu();
            }
            if(imGui.beginMenu("杂项",true)){
                if(imGui.button("关于")) {
                    Window_y_n(imGui, "关于 - TextReader Config tool", "Vre. Beta 1.3.2-2190b-240115\r\n  这是一款由IDlike自主研发的小说阅读器,本程序为书籍管\r\n理工具,本程序仅供学习参考,不可商用!\r\n\r\n作者B站UID:694692509                 \r\nIDSOFT @ IDlike 2024/1/8");
                        //Window_y_n(imGui, "[悲]", "作者会掉小珍珠的 嘤嘤嘤...");
                }
                if(!BGCR){
                    if(imGui.button("背景透明(可能有bug)")){
                        Runtime.getRuntime().exec("./bgcr.exe");
                        BGCR=true;
                    }
                }else {
//                    if(imGui.button("重启程序")){
//
//                        //Boot.main(null);
//                        //BGCR=false;
//                        Re_load = true;
//                        break;
//                        //return;
//                    }
                }

                if(imGui.button("退出程序")){
                    return;
                }

                JImGuiGen.endMenu();
            }
            JImGui.endMenuBar();
            if(fix_json){
                if (imGui.begin("!!JSON文件需要修复!!")) {
                    imGui.text("检测到至少有一个json文件不可读,是否修复? 这个操作将会清除资源文件夹.");
                    if(imGui.button("修复并重启 (Y)")){TeipMake.deleteFileByIO("./style/");return;}
                    imGui.sameLine();
                    if(imGui.button("修复并重启 (Y) ")){TeipMake.deleteFileByIO("./style/");return;}
                    imGui.sameLine();
                    if(imGui.button("修复并重启 (Y)  ")){TeipMake.deleteFileByIO("./style/");return;}
                }
                imGui.render();
                continue;
            }
            if(CFGS){
                if (imGui.begin("TextReader Json Config Manager",nb2)) {
                    if(!nb2.accessValue()) CFGS=false;
                    if (imGui.beginTabBar("MainBar1")) {
                        if (imGui.beginTabItem("设置")) {
                            if(imGui.button("+ [增加快捷关键词]")){
                                JsonParser tjp = new JsonParser();
                                JsonObject tjsp = (JsonObject) jp_apis.parse("{}");
                                tjsp.addProperty("name", Window_input(imGui,"请输入标题","请输入标题"));
                                tjsp.addProperty("key", Window_input(imGui,"请输入搜索值","请输入搜索值"));
                                json_confs.get("conf_tags").getAsJsonArray().add(tjsp);
                                TeipMake.WriteFileToThis_("./style/config.json",json_confs.toString());
                            }
                            for (int i = 0; i < json_confs.get("conf_tags").getAsJsonArray().size(); i++) {
                                imGui.text("显示标题: "+json_confs.get("conf_tags").getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString());
                                imGui.text("搜索内容: "+json_confs.get("conf_tags").getAsJsonArray().get(i).getAsJsonObject().get("key").getAsString());
                                if(imGui.button("- [删除#"+i+"]")){
                                    json_confs.get("conf_tags").getAsJsonArray().remove(i);
                                    TeipMake.WriteFileToThis_("./style/config.json",json_confs.toString());
                                }
                                imGui.sameLine(45f);
                                imGui.text("");
                            }
                            imGui.text("");
                            imGui.text("AI朗读设置");
                            imGui.sameLine();
                            if(imGui.button("更改")){
                                json_confs.get("conf_AI").getAsJsonObject().addProperty("API_URL_L",Window_input_(imGui,"API前段(文本前):","API前段(文本前):","http://127.0.0.1:7880/api/tts?speaker=[讲述人]&text="));
                                json_confs.get("conf_AI").getAsJsonObject().addProperty("API_URL_R",Window_input_(imGui,"API后段(文本后):","API后段(文本后):","&format=wav&language=auto&length=1&sdp=0.2&noise=0.6&noisew=0.8&emotion=7&seed=107000"));
                                TeipMake.WriteFileToThis_("./style/config.json",json_confs.toString());
                            }
                            imGui.text("L: "+json_confs.get("conf_AI").getAsJsonObject().get("API_URL_L").getAsString());
                            imGui.text("R: "+json_confs.get("conf_AI").getAsJsonObject().get("API_URL_R").getAsString());
                            imGui.text("[这些条目都是实时保存的!]");
                            JImGuiGen.endTabItem();
                        }
                        if (imGui.beginTabItem("在线获取小说API")) {
                            if(imGui.button("+ [增加API]")){
                                JsonParser tjp = new JsonParser();
                                JsonObject tjsp = (JsonObject) jp_apis.parse("{}");
                                tjsp.addProperty("tell", Window_input(imGui,"请输入API名字","请输入API名字"));
                                tjsp.addProperty("host", Window_input(imGui,"请输入API网络URL","请输入API网络URL"));
                                tjsp.addProperty("icon", "host.ico");
                                json_apis.get("data").getAsJsonArray().add(tjsp);
                                TeipMake.WriteFileToThis_("./style/API_list.json",json_apis.toString());
                            }
                            for (int i = 0; i < json_apis.get("data").getAsJsonArray().size(); i++) {
                                imGui.text("Name: "+json_apis.get("data").getAsJsonArray().get(i).getAsJsonObject().get("tell").getAsString());
                                imGui.text("Host: "+json_apis.get("data").getAsJsonArray().get(i).getAsJsonObject().get("host").getAsString());
                                imGui.text("Icon: "+json_apis.get("data").getAsJsonArray().get(i).getAsJsonObject().get("icon").getAsString());
                                if(imGui.button("- [删除#"+i+"]")){
                                    json_apis.get("data").getAsJsonArray().remove(i);
                                    TeipMake.WriteFileToThis_("./style/API_list.json",json_apis.toString());
                                }
                                imGui.sameLine(45f);
                                imGui.text("");
                            }
                            imGui.text("[这些条目都是实时保存的!]");
                            JImGuiGen.endTabItem();
                        }
                    }
                    JImGuiGen.endTabBar();

                }
            }
            if(TRMGR) {
                if (imGui.begin("TextReader File Manager",nb)) {
                    imGui.setWindowSize("TextReader File Manager",700,600);
                    if(!nb.accessValue()) TRMGR=false;
                    imGui.text("");
                    if (imGui.button("重载小说索引")) {
                        s = PathScanLib.PathScan(true, "");
                    }
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
                            if(s.get(i).get(1)!="无效的EPUB文件,建议清理!")
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
                            if (imGui.checkbox("IFireWall防火墙", V_m_f)) {
                                if (!V_m_s.accessValue()) {
                                    Window_y_n(imGui, "作者建议", "这个功能不建议关闭,如果需要外部访问的话则必须打开,否则他们不能访问!");
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
                                if (V_m_f.accessValue()) {
                                    IniLib.SetThing("./config.ini", "settings", "FireWall", "enable");
                                } else {
                                    IniLib.SetThing("./config.ini", "settings", "FireWall", "disable");
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
            if (imGui.begin("Program controller")) {
                imGui.text("小说占用空间: "+new DecimalFormat("#.00").format((float)((int) (directorySize))/1024/1024)+"MB");
                imGui.sameLine();
                if(imGui.button("刷新")) directorySize = calculateDirectorySize(directory);
                //显示切换主题
                imGui.text("0=Dark; 1=Light; 2=Classic");
                imGui.sliderInt("主题", V_c_c, 0, 2);
                imGui.setWindowSize("Program controller", 300, 120);
                imGui.setWindowPos("Program controller",0,32);
            }
            if(V_c_c.intValue()==0) imGui.styleColorsDark();
            if(V_c_c.intValue()==1) imGui.styleColorsLight();
            if(V_c_c.intValue()==2) imGui.styleColorsClassic();
            imGui.render();
        }
        System.out.println("Window is closed.");
        if(Re_load){
            imGui.render();
            imGui.close();

            Boot.main(null);
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
            imGui.begin("",new NativeBool(), JImWindowFlags.NoResize);
            if(imGui.button("取消选择文件")) {
                return "";
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

public static String Window_input(JImGui imGui,String Title,String say){
    NativeString out = new NativeString();
        while (!imGui.windowShouldClose()) {
            imGui.initNewFrame();
            imGui.begin(Title,new NativeBool(), JImWindowFlags.NoTitleBar);
            imGui.setWindowSize(Title,400,300);
            imGui.text(Title);
            imGui.text("");
            imGui.text(say);
            imGui.inputText("in",out);
            imGui.sameLine();
            if (imGui.button("提交")) {
                return out.toString();
            }

            imGui.render();
        }
        return "";
    }
    public static String Window_input_(JImGui imGui,String Title,String say,String auto){
        NativeString out = new NativeString();
        NS_add_string(out,auto);
        while (!imGui.windowShouldClose()) {
            imGui.initNewFrame();
            imGui.begin(Title,new NativeBool(), JImWindowFlags.NoTitleBar);
            imGui.setWindowSize(Title,400,300);
            imGui.text(Title);
            imGui.text("");
            imGui.text(say);
            imGui.inputText("in",out);
            imGui.sameLine();
            if (imGui.button("提交")) {
                return out.toString();
            }

            imGui.render();
        }
        return "";
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