package zero;


import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: kyang
 * @create: 2021-07-04 02:12
 */
public class FileMonitor {

    private static Logger logger = Logger.getInstance();

    /***
     * 启动命令范例
     * 通过命令行启动 java -jar FileMonitor.jar C:\test\test.txt http://127.0.0.1:8080/spring/ UTF-8
     * 第一个参数是需要监控的文件路径[目前只为监控日志，只监控追加数据]
     * 第二个参数是监控的数据变化钩子，会把监控到的追加数据以post方式，封装到request body发送到该url
     * 第三个参数是监控文件的读取编码，默认是utf-8
     * @param args
     */
    public static void main(String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                System.out.println("参数无法匹配\n参数:[file_path*, hook_url*, file_encoding(可选)]");
                System.exit(-1);
            }
            File file = new File(args[0]);
            if (!file.exists()) {
                System.out.println(args[0] + "文件找不到");
                System.exit(-1);
            }
            String encoding = "UTF-8";
            if (args.length == 3) {
                encoding = args[2];
            }
            long interval = TimeUnit.SECONDS.toMillis(1);
            String fileName = file.getName();
            File parentFile = file.getParentFile();
            if (parentFile == null) {
                System.out.println(args[0] + "文件父目录有误");
                logger.error("程序启动失败, 文件父目录有误");
                System.exit(-1);
            }
            // 创建一个文件观察器用于处理文件的格式
            FileAlterationObserver observer = new FileAlterationObserver(parentFile, FileFilterUtils.and(
                    FileFilterUtils.fileFileFilter(), FileFilterUtils.nameFileFilter(fileName)));
            //设置文件变化监听器
            observer.addListener(new LogFileListener(args[0], args[1], encoding));
            FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
            monitor.start();
        } catch (Exception e) {
            logger.error(e);
        }
    }
}

