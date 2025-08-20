package com.example.MetricMicrometer;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;

@Component
public class FilmMetric implements MeterBinder {

    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    @Override
    public void bindTo(MeterRegistry meterRegistry) {
        meterRegistry.gauge("film.memory.used.heap", memoryMXBean, m-> m.getHeapMemoryUsage().getUsed());
        meterRegistry.gauge("film.memory.heap.nonheap", memoryMXBean,m-> m.getNonHeapMemoryUsage().getUsed());

        meterRegistry.gauge("film.thread.live", threadMXBean, ThreadMXBean::getThreadCount);
        meterRegistry.gauge("film.thread.daemon", threadMXBean, ThreadMXBean::getDaemonThreadCount);

        ManagementFactory.getGarbageCollectorMXBeans().forEach(gc ->
                meterRegistry.gauge("film.gc.pause."+ gc.getName(),gc, g-> g.getCollectionTime()));
    }
}
