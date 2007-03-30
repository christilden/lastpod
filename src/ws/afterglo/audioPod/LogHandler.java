package ws.afterglo.audioPod;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.JTextArea;

/**
 * @author muti
 *
 */
public class LogHandler extends Handler {
    public void close() {
        return;
    }

    public void flush() {
        return;
    }

    public void publish(LogRecord record) {
        if ((getFilter() != null) && !getFilter().isLoggable(record)) {
            return;
        }

        JTextArea logtextarea = AudioPod.UI.getLogtextarea();
        logtextarea.append(record.getMessage() + "\n");
    }
}
