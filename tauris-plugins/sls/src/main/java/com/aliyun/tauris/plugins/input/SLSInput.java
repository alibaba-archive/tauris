package com.aliyun.tauris.plugins.input;

import com.aliyun.openservices.log.common.LogGroupData;
import com.aliyun.openservices.log.common.Logs;
import com.aliyun.openservices.loghub.client.ClientWorker;
import com.aliyun.openservices.loghub.client.ILogHubCheckPointTracker;
import com.aliyun.openservices.loghub.client.config.LogHubConfig;
import com.aliyun.openservices.loghub.client.exceptions.LogHubCheckPointException;
import com.aliyun.openservices.loghub.client.exceptions.LogHubClientWorkerException;
import com.aliyun.openservices.loghub.client.interfaces.ILogHubProcessor;
import com.aliyun.openservices.loghub.client.interfaces.ILogHubProcessorFactory;
import com.aliyun.tauris.DefaultEvent;
import com.aliyun.tauris.TEvent;
import com.aliyun.tauris.annotations.Name;
import com.aliyun.tauris.annotations.Required;
import com.aliyun.tauris.TPluginInitException;
import com.aliyun.tauris.TLogger;
import org.joda.time.DateTime;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ZhangLei on 16/12/7.
 */
@Name("sls")
public class SLSInput extends BaseTInput {

    private TLogger logger;

    @Required
    String endPoint;

    @Required
    String accessKeyId;

    @Required
    String accessKeySecret;

    @Required
    String project;

    @Required
    String logstore;

    /**
     * consumer group 的名字，不能为空，支持 [a-z][0-9] 和'_','-',长度在 [3-63]字符，只能以小写字母和数字开头结尾
     */
    @Required
    String consumerGroup;

    /**
     * consumer 的名字，必须确保同一个 consumer group 下面的各个 consumer 不重名.
     * 默认为主机名
     */
    String workerInstance;

    /**
     * 用于指出在服务端没有记录 shard 的 checkpoint 的情况下应该从什么位置消费 shard，
     * 如果服务端保存了有效的 checkpoint 信息，那么这些取值不起任何作用，
     * cursorPosition 取值可以是 [BEGIN_CURSOR, END_CURSOR]中的一个，
     * BEGIN_CURSOR 表示从 shard 中的第一条数据开始消费，END_CURSOR 表示从 shard 中的当前时刻的最后一条数据开始消费，
     * 如果不设置，需要指定startTime，确定消费日志的开始时间
     */
    LogHubConfig.ConsumePosition cursorPosition;


    /**
     * 消费日志的开始时间，单位秒。仅在cursorPosition为SPECIAL_TIMER_CURSOR时有效
     */
    Integer startTime;

    Boolean consumeInOrder;

    int checkTimeInterval = 60;

    /**
     * 单位毫秒
     */
    long dataFetchInterval = LogHubConfig.DEFAULT_DATA_FETCH_INTERVAL_MS;

    /**
     * 0 ~ 1000
     */
    int maxFetchLogGroupSize = 1000;

    private ClientWorker worker;

    private Thread workerThread;

    public SLSInput() {
        this.logger = TLogger.getLogger(this);
    }

    @Override
    public void doInit() throws TPluginInitException {
        if (workerInstance == null) {
            try {
                workerInstance = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                throw new TPluginInitException("cannot read hostname");
            }
        }
        LogHubConfig config;
        if (cursorPosition == null) {
            if (startTime == null) {
                throw new TPluginInitException("start_time is required");
            }
            config = new LogHubConfig(consumerGroup, workerInstance,
                    endPoint, project, logstore, accessKeyId, accessKeySecret, startTime);

        } else {
            config = new LogHubConfig(consumerGroup, workerInstance,
                    endPoint, project, logstore, accessKeyId, accessKeySecret,
                    cursorPosition);
        }
        config.setDataFetchIntervalMillis(dataFetchInterval);
        config.setMaxFetchLogGroupSize(maxFetchLogGroupSize);

        if (consumeInOrder != null) {
            config.setConsumeInOrder(consumeInOrder);
        }
        try {
            worker = new ClientWorker(new SLSLogHubProcessorFactory(), config);
            workerThread = new Thread(worker);
        } catch (LogHubClientWorkerException e) {
            throw new TPluginInitException("cannot create client worker", e);
        }
    }

    public void run() throws Exception {
        workerThread.setDaemon(true);
        workerThread.start();
        logInfo("sls input plugin started");
    }

    @Override
    public void close() {
        super.close();
        try {
            worker.shutdown();
            //ClientWorker 运行过程中会生成多个异步的 Task，shutdown 之后最好等待还在执行的 Task 安全退出.
            Thread.sleep(5 * 1000);
        } catch (InterruptedException e) {
        }
        logInfo("sls input plugin closed");
    }

    class SLSLogHubProcessorFactory implements ILogHubProcessorFactory {

        public ILogHubProcessor generatorProcessor() {
            // 生成一个消费实例
            return new SLSLogHubProcessor();
        }
    }


    class SLSLogHubProcessor implements ILogHubProcessor {

        private static final String META_TOPIC = "topic";
        private static final String META_CATEGORY = "category";

        private int shardId;
        // 记录上次持久化 check point 的时间
        private long lastCheckTime = 0;

        public void initialize(int shardId) {
            this.shardId = shardId;
            logInfo("start consume shard %s", shardId + "");
        }

        // 消费数据的主逻辑
        public String process(List<LogGroupData> logGroups,
                              ILogHubCheckPointTracker checkPointTracker) {
            try {
                for (LogGroupData logGroup : logGroups) {
                    Logs.LogGroup lg = logGroup.GetLogGroup();

                    List<Logs.Log> logs = lg.getLogsList();
                    if (logs.isEmpty()) {
                        continue;
                    }
                    List<TEvent> events = new LinkedList<>();
                    for (Logs.Log log : lg.getLogsList()) {
                        TEvent event = new DefaultEvent(lg.getSource());
                        event.addMeta(META_TOPIC, lg.getTopic());
                        event.addMeta(META_CATEGORY, lg.getCategory());
                        for (int i = 0; i < lg.getLogTagsCount(); i++) {
                            Logs.LogTag tag = lg.getLogTags(i);
                            event.addMeta(tag.getKey(), tag.getValue());
                        }
                        event.setTimestamp(log.getTime() * 1000L);
                        for (Logs.Log.Content cont : log.getContentsList()) {
                            event.setField(cont.getKey().trim(), cont.getValue());
                        }
                        events.add(event);
                    }
                    putEvents(events);
                }
                if (checkTimeInterval > 0) {
                    long curTime = System.currentTimeMillis();
                    // 每隔 60 秒，写一次 check point 到服务端，如果 60 秒内，worker crash，
                    // 新启动的 worker 会从上一个 checkpoint 其消费数据，有可能有重复数据
                    if (curTime - lastCheckTime > checkTimeInterval * 1000) {
                        try {
                            //参数 true 表示立即将 checkpoint 更新到服务端，为 false 会将 checkpoint 缓存在本地，默认隔 60s
                            //后台会将 checkpoint 刷新到服务端。
                            checkPointTracker.saveCheckPoint(true);
                        } catch (LogHubCheckPointException e) {
                            logError(e, "shard:%d, save checkpoint error", shardId);
                        }
                        lastCheckTime = curTime;
                    } else {
                        try {
                            checkPointTracker.saveCheckPoint(false);
                        } catch (LogHubCheckPointException e) {
                            logError(e, "shard:%d, save checkpoint error", shardId);
                        }
                    }
                }
                // 返回空表示正常处理数据， 如果需要回滚到上个 check point 的点进行重试的话，可以 return checkPointTracker.getCheckpoint()
            } catch (Exception e) {
                logError(e, "unexpected exception");
            }
            return null;
        }

        // 当 worker 退出的时候，会调用该函数，用户可以在此处做些清理工作。
        public void shutdown(ILogHubCheckPointTracker checkPointTracker) {
            //将消费断点保存到服务端。
            try {
                checkPointTracker.saveCheckPoint(true);
                logInfo("shard:%d, sls input shutdown, save check point", shardId);
            } catch (LogHubCheckPointException e) {
                logError(e, "shard:%d, save checkpoint error", shardId);
            }
        }
    }

    private void logInfo(String msg, Object... params) {
        String txt = params.length > 0 ? String.format(msg, params) : msg;
        txt = String.format("project:%s,logstore:%s,group:%s - ", project, logstore, consumerGroup) + txt;
        logger.INFO(txt);
    }

    private void logError(Throwable e, String msg, Object... params) {
        String txt = params.length > 0 ? String.format(msg, params) : msg;
        txt = String.format("project:%s,logstore:%s,group:%s - ", project, logstore, consumerGroup) + txt;
        if (e != null) {
            logger.ERROR(txt, e);
        } else {
            logger.ERROR(txt);
        }
    }

}
