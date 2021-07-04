package zero;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * @program: OriTest
 * @description:
 * @author: zero
 * @create: 2021-07-04 21:51
 */
public class Logger {

    //懒汉式单例模式
    private static Logger instance = new Logger();
    private File logFile = null;

    public static Logger getInstance() {
        return instance;
    }

    private Logger() {
        logFile = new File("FileMonitor.logger");
    }

    private void appendLogFile(String str) {
        BufferedWriter bw = null;
        OutputStreamWriter out = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(logFile, true);
            out = new OutputStreamWriter(fos);
            bw = new BufferedWriter(out);
            bw.write(str);
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (out != null) {
                    out.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public void info(String msg) {
        appendLogFile("[file monitor info logger ==>>]:[ " + msg + " ]");
    }

    public void error(String msg, Exception e) {
        appendLogFile("[file monitor error logger ==>>]:[ " + msg + " ]");
        appendLogFile(e.getMessage());
    }

    public void error(Exception e) {
        appendLogFile("[an exception occurs : ]");
        appendLogFile(e.getMessage());
    }

    public void error(String msg) {
        appendLogFile("[file monitor error logger ==>>]:[ " + msg + " ]");
    }

}
