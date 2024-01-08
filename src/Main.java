import org.ice1000.jimgui.*;
import org.ice1000.jimgui.flag.JImWindowFlags;
import org.ice1000.jimgui.util.JniLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;


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
        line_max_size.modifyValue(45);



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
        boolean TRMGR = false;
        boolean TRCOF = false;
        NativeBool nb = new NativeBool();
        NativeBool nb1 = new NativeBool();
        nb.modifyValue(true);
        nb1.modifyValue(true);
        while (!imGui.windowShouldClose()){
            imGui.initNewFrame();
            JImGui.beginMainMenuBar();
            if(imGui.beginMenu("打开窗口",true)){
                if(imGui.button("T-R-Mgr")) {
                    TRMGR = true;
                    nb.modifyValue(true);
                }
                if(imGui.button("T-R-Config")){
                    TRCOF=true;
                    nb1.modifyValue(true);
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
                            if (imGui.button("恢复默认 ")) {
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



            imGui.render();
        }
    }
    public static NativeString NS_add_string(NativeString ns,String text){
        //逆天!居然得一个一个字节压进去(
        byte[] a = text.getBytes();
        for (byte b : a) {
            ns.append(b);
        }
        return ns;
    }
    public static boolean Window_y_n(JImGui imGui,String Title,String say){
        while (!imGui.windowShouldClose()) {
            imGui.initNewFrame();
            imGui.begin(Title,new NativeBool(), JImWindowFlags.NoTitleBar);
            imGui.setWindowSize(Title,400,300);
            imGui.text(Title);
            imGui.text("");
            imGui.text(AutoBR(say, 40));
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
}