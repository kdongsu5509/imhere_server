package com.kdongsu5509.imhereapigateway.filter.logger;

import com.kdongsu5509.imhereapigateway.common.alert.port.out.MessageSendPort;
import com.kdongsu5509.imhereapigateway.domain.AccessLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AccessLogPrinter {
    private static final Logger log = LoggerFactory.getLogger(AccessLogPrinter.class);
    private final MessageSendPort messageSendPort;
    private final AccessLogFormatter formatter;

    public AccessLogPrinter(MessageSendPort messageSendPort, AccessLogFormatter formatter) {
        this.messageSendPort = messageSendPort;
        this.formatter = formatter;
    }

    public void print(AccessLog accessLog, boolean sendAlert) {
        String formatted = formatter.format(accessLog);
        log.info(formatted);
        sendAlertIfNeeded(accessLog, formatted, sendAlert);
    }

    private void sendAlertIfNeeded(AccessLog accessLog, String formatted, boolean sendAlert) {
        if (sendAlert && accessLog.getStatus() >= 400) {
            messageSendPort.sendMessage("## 🚨 HTTP Error\n\n```json\n" + formatted + "\n```");
        }
    }
}
