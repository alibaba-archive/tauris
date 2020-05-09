package com.aliyun.tauris.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Counter a group of metric, to track counts of events or running totals.
 * <p>
 * Example of GroupCounters include:
 * <ul>
 * <li>Number of requests processed</li>
 * <li>Number of items that were inserted into a queue</li>
 * <li>Total amount of data a system has processed</li>
 * </ul>
 * <p>
 * GroupCounters can only go up (and be reset), if your use case can go down you should use a {@link Gauge} instead.
 * Use the <code>rate()</code> function in Prometheus to calculate the rate of increase of a GroupCounter.
 * By convention, the names of Counters are suffixed by <code>_total</code>.
 * <p>
 * <p>
 * An example GroupCounter:
 * <pre>
 * {@code
 *   class YourClass {
 *     static final GroupCounter requests = GroupCounter.build()
 *         .name("requests_total").help("Total requests.").register();
 *     static final GroupCounter failedRequests = GroupCounter.build()
 *         .name("requests_failed_total").help("Total failed requests.").register();
 *
 *     void processRequest() {
 *        requests.values('bytes', 'packets').inc(123, 456);
 *        try {
 *          // Your code here.
 *        } catch (Exception e) {
 *          failedRequests.inc();
 *          throw e;
 *        }
 *     }
 *   }
 * }
 * </pre>
 * <p>
 * <p>
 * You can also use labels to track different types of metric:
 * <pre>
 * {@code
 *   class YourClass {
 *     static final Counter requests = Counter.build()
 *         .name("requests_total").help("Total requests.")
 *         .labelNames("method").register();
 *
 *     void processGetRequest() {
 *        requests.labels("get").inc();
 *        // Your code here.
 *     }
 *     void processPostRequest() {
 *        requests.labels("post").inc();
 *        // Your code here.
 *     }
 *   }
 * }
 * </pre>
 * These can be aggregated and processed together much more easily in the Promtheus
 * server than individual metrics for each labelset.
 */
public class GroupCounter extends SimpleCollector<GroupCounter.Child> {

    final String valueLabel;;
    final String[] valueNames;;

    GroupCounter(Builder b) {
        super(b);
        this.valueLabel = b.valueLabel;
        this.valueNames = b.valueNames;
    }

    public static class Builder extends SimpleCollector.Builder<Builder, GroupCounter> {
        String[] valueNames = new String[]{};
        String valueLabel;

        public Builder() {
            this.dontInitializeNoLabelsChild = true;
        }

        @Override
        public GroupCounter create() {
            return new GroupCounter(this);
        }

        public Builder valueNames(String ...valueNames) {
            this.valueNames = valueNames;
            return this;
        }

        public Builder valueLabel(String valueLabel) {
            this.valueLabel = valueLabel;
            return this;
        }

    }

    /**
     * Return a Builder to allow configuration of a new Counter.
     */
    public static Builder build() {
        return new Builder();
    }

    @Override
    protected Child newChild() {
        return new Child(valueNames.length);
    }

    /**
     * The value of a single Counter.
     * <p>
     * <em>Warning:</em> References to a Child become invalid after using
     * {@link SimpleCollector#remove} or {@link SimpleCollector#clear},
     */
    public static class Child implements CollectorChild {

        private final DoubleAdder[] values;

        private volatile long lastModified = System.currentTimeMillis();

        public Child(int valueCount) {
            this.values = new DoubleAdder[valueCount];
            int i = 0;
            for (DoubleAdder value : this.values) {
                value = new DoubleAdder();
                this.values[i++] = value;
            }
        }

        /**
         * Increment the counter by 1.
         */
        public void inc(double ...amt) {
            if (amt.length != values.length) {
                throw new IllegalArgumentException("Incorrect number of values.");
            }
            for (int i = 0; i < values.length; i++) {
                values[i].add(amt[i]);
            }
            lastModified = System.currentTimeMillis();
        }

        public long getLastModified() {
            return lastModified;
        }
    }

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples.Sample> samples = new ArrayList<MetricFamilySamples.Sample>();

        List<String> labelNames = new ArrayList<>(this.labelNames);
        labelNames.add(valueLabel); //eg. code

        for (Map.Entry<LabelsKey, Child> entry : children.entrySet()) {
            Child child = entry.getValue();

            for (int i = 0; i < valueNames.length; i++) {
                List<String> labelValues = new ArrayList<>(entry.getKey().getKeys());
                labelValues.add(valueNames[i]);// eg. request_bytes
                samples.add(new MetricFamilySamples.Sample(fullname, labelNames, labelValues, child.values[i].sum()));
            }
        }
        MetricFamilySamples mfs = new MetricFamilySamples(fullname, Type.COUNTER, help, samples);

        List<MetricFamilySamples> mfsList = new ArrayList<MetricFamilySamples>();
        mfsList.add(mfs);
        return mfsList;
    }
}
