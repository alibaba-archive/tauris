package com.aliyun.tauris.plugins.input.tailer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Class FileManager
 *
 * @author yundun_waf_dev
 *
 */
public class FileManager {

    private static Logger logger = LoggerFactory.getLogger(FileManager.class);

    private File dir;

    private Pattern pattern;

    /**
     * 多少毫秒以上文件长度无变化则关闭文件
     */
    private long idleClosedMillis = 60000;

    /**
     * 保存文件读取位置的文件
     */
    private File statFile;

    private Map<File, FileStat> positions = new ConcurrentHashMap<>();

    private Consumer<File> onFileClose;

    public FileManager(File dir, Pattern pattern, long idleClosedMillis, File statFile) {
        this.dir = dir;
        this.pattern = pattern;
        this.idleClosedMillis = idleClosedMillis;
        this.statFile = statFile;
    }

    public void init() throws IOException {
        if (statFile != null && statFile.exists()) {
            try (Scanner scanner = new Scanner(new FileReader(statFile))) {
                scanner.useDelimiter("\n");
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    String[] kv = line.split(",");
                    if (kv.length != 2) {
                        continue;
                    }
                    File fn = new File(kv[0]);
                    if (!fn.exists()) {
                        continue;
                    }
                    Long pos = Long.parseLong(kv[1]);
                    positions.put(fn, new FileStat(fn, pos));
                }
            } catch (IOException e) {
                logger.error("read record file '" + statFile + "' failed", e);
            } catch (Exception e) {
                logger.error("parse record file '" + statFile + "' failed", e);
            } finally {
                statFile.deleteOnExit();
            }
        }
        for (File file: listFiles()) {
            if (!positions.containsKey(file)) {
                positions.put(file, new FileStat(file));
            } else {
                FileStat stat = positions.get(file);
                if (stat.position < file.length()) {
                    positions.put(file, new FileStat(file, stat.position));
                } else {
                    positions.put(file, new FileStat(file));
                }
            }
        }
    }

    public List<FileStat> listNewFiles() throws IOException {
        List<FileStat> newFiles = new ArrayList<>();
        Set<File> existsFiles = new HashSet<>(listFiles());

        for (File file: existsFiles) {
            FileStat stat = positions.get(file);
            if (stat == null) { // new file
                stat = new FileStat(file, 0);
                positions.put(file, stat);
                newFiles.add(stat);
                continue;
            }
            if (stat.position > file.length()) {
                newFiles.add(new FileStat(file));
            } else if (stat.position < file.length()) {
                newFiles.add(new FileStat(file, stat.position));
            }
        }
        Set<File> statFiles = new HashSet<>(positions.keySet());
        for (File file: statFiles) {
            if (!existsFiles.contains(file)) {
                positions.remove(file);
                if (onFileClose != null) {
                    onFileClose.accept(file);
                }
            } else {
                //将长时间未读取的文件关闭
                FileStat stat = positions.get(file);
                if (stat.reading && stat.position == file.length() && System.currentTimeMillis() - file.lastModified() > idleClosedMillis ) {
                    if (onFileClose != null) {
                        onFileClose.accept(file);
                    }
                    stat.setReading(false);
                }
            }
        }
        return newFiles;
    }

    private List<File> listFiles() throws IOException {
        FileVisitor v = new FileVisitor();
        Files.walkFileTree(dir.toPath(), v);
        return v.getFiles();
    }

    public void snapPosition(File file, long position) {
        FileStat stat = positions.get(file);
        if (stat == null) {
            stat = new FileStat(file, position);
        }
        stat.setPosition(position);
        stat.setReading(true);
        positions.put(file, stat);
    }

    public void savePositions() {
        if (statFile != null) {
            try (FileWriter writer = new FileWriter(statFile)) {
                boolean first = true;
                for (Map.Entry<File, FileManager.FileStat> e: positions.entrySet()) {
                    if (!first) {
                        writer.write('\n');
                    }
                    writer.write(e.getKey().getAbsolutePath());
                    writer.write(',');
                    writer.write(String.valueOf(e.getValue().getPosition()));
                    first = false;
                }
            } catch (IOException e) {
                logger.error("save file read position to " + statFile.getAbsolutePath() + " failed", e);
            }
        }
    }

    public void onFileClose(Consumer<File> callback) {
        onFileClose = callback;
    }

    public static class FileStat {

        private File file;

        private long position;

        private boolean reading;

        public FileStat(File file) {
            this(file, file.length());
        }

        public FileStat(File file, long position) {
            this.file = file;
            this.position = position;
        }

        public void setPosition(long position) {
            this.position = position;
        }

        public void setReading(boolean reading) {
            this.reading = reading;
        }

        public long getPosition() {
            return position;
        }

        public boolean isReading() {
            return reading;
        }

        public File getFile() {
            return file;
        }
    }

    private class FileVisitor extends SimpleFileVisitor<Path> {

        private List<File> files = new ArrayList<>();

        public FileVisitor() {
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
            File f = path.toFile();
            if (attrs.isRegularFile() && pattern.matcher(f.getName()).matches()) {
                files.add(new File(f.getPath()));
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        public List<File> getFiles() {
            return files;
        }
    }
}
