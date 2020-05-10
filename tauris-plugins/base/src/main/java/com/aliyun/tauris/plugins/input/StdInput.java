package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.TLogger;

import java.util.Scanner;

/**
 * Created by ZhangLei on 16/12/9.
 */
@Name("stdin")
public class StdInput extends BaseTInput {

    private TLogger logger;

    String delimiter = "\n";

    public StdInput() {
        logger = TLogger.getLogger(this);
    }

    @Override
    public void run() throws Exception {
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter(delimiter);
        while(scanner.hasNext()) {
            try {
                TEvent event = codec.decode(scanner.next(), getEventFactory());
                putEvent(event);
            } catch (DecodeException e) {
                logger.WARN2("decode failed", e, e.getSource());
            }
        }
    }
}
