package com.aliyun.tauris;

import com.aliyun.tauris.config.*;
import com.aliyun.tauris.config.parser.Helper;
import org.apache.commons.cli.*;
import org.apache.log4j.*;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by ZhangLei on 16/10/20.
 */
public class Bootstrap {

    private static void fault(String message) {
        System.err.println(message);
        System.exit(1);
    }

    private static Options createCommandOptions() {
        Options options = new Options();

        options.addOption(Option.builder("f").longOpt("file").desc("Load the rule from a specific file <rule>").build());
        options.addOption(Option.builder("e").longOpt("execute").desc("Use the given <rule> as the rule. Same syntax as the rule file").build());
        options.addOption(Option.builder("t").longOpt("test").desc("Check configuration for valid syntax and then exit").build());
        options.addOption(Option.builder("P").longOpt("profiler").desc("Collection plugin's performance info").build());
        options.addOption(Option.builder("p").hasArg(true).argName("app.pid").longOpt("pid").desc("Pid file path").build());
        options.addOption(Option.builder("m").hasArg(true).argName("port[:path]").longOpt("metric").desc("Enable metric server, default path is '/metrics'").build());
        options.addOption(Option.builder("M").hasArg(true).argName("file[:interval]").longOpt("metricF").desc("Dump metrics into a <file> every <interval> seconds, interval default 15").build());
        options.addOption(Option.builder("w").hasArg(true).argName("directory").longOpt("workdir").desc("Set the working directory").build());
        options.addOption(Option.builder("o").hasArg(true).argName("app.out").longOpt("output").desc("Write stdout to <app.out> instead of stdout").build());
        options.addOption(Option.builder("l").hasArg(true).argName("file.log").longOpt("logfile").desc("Logging to <file.log>").build());
        options.addOption(Option.builder("L").hasArg(true).argName("log4j.xml").longOpt("log4j").desc("Log4j configuration file").build());

        options.addOption(Option.builder("v").longOpt("version").desc("Show version and ehen exists").build());
        options.addOption(Option.builder("h").longOpt("help").desc("Print this message").build());

        return options;

    }

    private static void logging(CommandLine cmd) throws Exception {
        String cfg = System.getProperty("log4j.configuration");
        if (cfg == null) {
            cfg = cmd.getOptionValue("log4j");
        }
        if (cfg != null) {
            if (cfg.endsWith(".xml")) {
                DOMConfigurator.configure(cfg);
            }
            if (cfg.endsWith(".properties")) {
                PropertyConfigurator.configure(cfg);
            }
            return;
        }

        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.ERROR);

        String logfile = System.getProperty("log4j.file");
        if (logfile == null) {
            logfile = cmd.getOptionValue("logfile");
        }
        if (logfile != null) {
            logfile = logfile.replaceAll("\\$pid", getPid());
            BiFunction<Level, String, Appender> makeAppender = (level, file) -> {
                DailyRollingFileAppender appender = new DailyRollingFileAppender();
                appender.setFile(file);
                appender.setAppend(true);
                appender.setEncoding("UTF-8");
                appender.setThreshold(level);
                appender.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L -- %m%n"));
                appender.activateOptions();
                return appender;
            };
            logfile = logfile.trim().toLowerCase();
            String infoFile, errFile;
            if (logfile.endsWith(".log")) {
                infoFile = logfile;
                errFile = logfile.replaceAll("\\.log$", "\\-error.log");
            } else {
                infoFile = logfile + ".log";
                errFile = logfile + "-error.log";
            }

            Consumer<Logger> initLogger = (logger) -> {
                logger.setLevel(Level.INFO);
                logger.setAdditivity(false);
                logger.addAppender(makeAppender.apply(Level.INFO, infoFile));
                logger.addAppender(makeAppender.apply(Level.ERROR, errFile));
            };
            initLogger.accept(Logger.getLogger("tauris"));
            initLogger.accept(Logger.getLogger("com.aliyun.tauris"));
            rootLogger.addAppender(makeAppender.apply(Level.ERROR, infoFile));
        } else {
            Logger.getLogger("tauris").addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n")));
            Logger.getLogger("tauris").setAdditivity(false);
            Logger.getLogger("tauris").setLevel(Level.INFO);
            Logger.getLogger("com.aliyun.tauris").addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n")));
            Logger.getLogger("com.aliyun.tauris").setAdditivity(false);
            Logger.getLogger("com.aliyun.tauris").setLevel(Level.INFO);
            rootLogger.addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n")));
        }
    }

    private static void version(CommandLine cmd) {
        String version = "unknown";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Bootstrap.class.getClassLoader().getResourceAsStream("VERSION")))) {
            String fv = br.readLine();
            if (fv != null && !fv.trim().isEmpty()) {
                version = fv;
            }
        } catch (Exception e) {
        }
        System.setProperty("tauris.version", version);
        if (cmd.hasOption("version")) {
            System.out.println("tauris " + version);
            System.exit(0);
        }
    }

    private static void workdir(CommandLine cmd) {
        String opt = cmd.getOptionValue("workdir");
        if (opt != null) {
            File dir = new File(opt);
            if (!dir.exists()) {
                fault("working directory not exists");
            }
            System.setProperty("user.dir", dir.getAbsolutePath());
        }
    }

    private static void profiler(CommandLine cmd) {
        if (cmd.hasOption("profiler")) {
            System.setProperty("tauris.filter.profiler", "true");
        }
    }

    private static void metrics(CommandLine cmd) {
        String  p  = cmd.getOptionValue("metric");
        Pattern p3 = Pattern.compile("^(?<host>[^:]+):(?<port>\\d+):(?<path>.+)$");
        Pattern p2 = Pattern.compile("^(?<port>\\d+):(?<path>.+)$");
        Pattern p1 = Pattern.compile("^(?<port>\\d+)$");
        if (p != null) {
            String port = "";
            String host = "127.0.0.1";
            String path = "/metrics";
            Matcher m = p3.matcher(p);
            if (m.matches()) {
                port = m.group("port");
                host = m.group("host");
                path = m.group("path");
            } else {
                m = p2.matcher(p);
                if (m.matches()) {
                    port = m.group("port");
                    path = m.group("path");
                } else {
                    m = p1.matcher(p);
                    if (m.matches()) {
                        port = m.group("port");
                    } else {
                        fault("invalid metric server argument");
                    }
                }
            }
            try {
                System.setProperty("tauris.metric.port", String.valueOf(Integer.parseInt(port)));
                System.setProperty("tauris.metric.host", host);
                System.setProperty("tauris.metric.path", path);
            } catch (NumberFormatException e) {
                fault("invalid metric server argument, port is not a number");
            }
        }
    }

    private static void metricsF(CommandLine cmd) {
        String  v = cmd.getOptionValue('M');
        Pattern p = Pattern.compile("(?<file>[^:]+)(:(?<interval>\\d+))?");
        if (v != null) {
            Matcher m = p.matcher(v);
            if (m.matches()) {
                String file = m.group("file");
                String interval = m.group("interval");
                System.setProperty("tauris.metric.file", file);
                if (interval != null) {
                    System.setProperty("tauris.metric.interval", interval);
                }
            } else {
                fault("invalid metric file argument");
            }
        }
    }

    private static void writePid(CommandLine cmd) {
        String opt = cmd.getOptionValue("pid");
        if (opt != null) {
            File pidFile = new File(opt);
            if ((pidFile.exists() && !pidFile.canWrite()) || !pidFile.getParentFile().canWrite()) {
                fault(String.format("pid file %s cannot be write", pidFile.getAbsoluteFile()));
            }
            try (FileWriter fw = new FileWriter(pidFile)) {
                fw.write(getPid());
            } catch (Exception e) {
                fault(String.format("pid file %s write failed", pidFile.getAbsoluteFile()));
            }
        }
    }

    public static String getPid() {
        String n = ManagementFactory.getRuntimeMXBean().getName();
        int    i = n.indexOf('@');
        if (i > 0) {
            return n.substring(0, i);
        }
        return "0";
    }

    public static void main(String[] argv) throws Exception {
        Options           options = createCommandOptions();
        CommandLineParser parser  = new DefaultParser();
        try {
            parser.parse(options, argv);
        } catch (ParseException e) {
            fault(e.getMessage());
            System.exit(1);
        }
        CommandLine cmd = parser.parse(options, argv);

        String rule = null;
        if (cmd.getArgs().length > 0) {
            rule = cmd.getArgs()[0];
        }

        if (cmd.hasOption("help") || argv.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("tauris [OPTIONS] <rule>", "OPTIONS", options, "");
            System.exit(0);
        }

        if (cmd.hasOption("output")) {
            String filename = cmd.getOptionValue("output").replaceAll("\\$pid", getPid());
            PrintStream stdout = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)), true);
            System.setOut(stdout);
        }

        logging(cmd);
        version(cmd);
        workdir(cmd);
        metrics(cmd);
        metricsF(cmd);
        writePid(cmd);

        boolean test = cmd.hasOption("test");
        if (rule == null) {
            fault("No rule source was specified");
            return;
        }

        TConfig config;
        if (cmd.hasOption("execute")) {
            config = new TConfigText(rule);
        } else {
            File ruleFile = new File(rule);
            if (!ruleFile.exists()) {
                fault(String.format("rule file \"%s\" not exists", ruleFile.getAbsoluteFile()));
            }
            config = new TConfigFile(new File(rule));
        }
        System.out.println("...................................................");
        Tauris tauris = new Tauris(config);
        try {
            tauris.load();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(Helper.m.toString());
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (test) {
            System.exit(0);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("tauris is stopping");
            tauris.stop();
            System.out.println("tauris has been stopped");
        }));
        try {
            tauris.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.print(e.getMessage());
            System.exit(1);
        }
    }
}
