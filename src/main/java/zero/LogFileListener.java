package zero;

import com.github.kevinsawicki.http.HttpRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @description:
 * @author: kyang
 * @create: 2021-07-04 02:12
 */
final public class LogFileListener extends FileAlterationListenerAdaptor {

    private long currentLine = 0L;
    private final String url;
    private final String encoding;
    private final Logger logger = Logger.getInstance();

    public LogFileListener(String path, String url, String encoding) {
        File file = new File(path);
        this.url = url;
        this.encoding = encoding;
        if (file.exists()) {
            try {
                currentLine = Files.lines(Paths.get(path)).count();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private void request(String line) {
        try {
            HttpRequest httpRequest = new HttpRequest(url, "POST");
            httpRequest.contentType("application/json", "UTF-8");
            httpRequest.send(line).body();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void onFileChange(File file) {
        if (!file.exists()) {
            logger.error(file.getName() + "监控文件在轮询中无法找到，程序退出");
        }
        LineIterator lineIterator = null;
        try {
            lineIterator = FileUtils.lineIterator(file, encoding);
            int cursor = 0;
            while (lineIterator.hasNext()) {
                String line = lineIterator.next();
                if (cursor >= currentLine) {
                    logger.info(line);
                    request(line);
                }
                cursor++;
            }
            currentLine = cursor;
        } catch (Exception e) {
            logger.error(e);
        } finally {
            try {
                if (lineIterator != null) {
                    lineIterator.close();
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

}
