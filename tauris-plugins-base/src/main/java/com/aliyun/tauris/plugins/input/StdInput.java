package com.aliyun.tauris.plugins.input;

import com.aliyun.tauris.DecodeException;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.TQueue;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.utils.TLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
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
                TEvent event = codec.decode(scanner.next());
                putEvent(event);
            } catch (DecodeException e) {
                logger.WARN2("decode failed", e, e.getSource());
            }
        }
    }
}
