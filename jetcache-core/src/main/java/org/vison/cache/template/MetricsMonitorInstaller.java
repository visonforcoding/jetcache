/**
 * Created on 2022/08/01.
 */
package org.vison.cache.template;

import org.vison.cache.Cache;
import org.vison.cache.CacheManager;
import org.vison.cache.CacheUtil;
import org.vison.cache.MultiLevelCache;
import org.vison.cache.support.AbstractLifecycle;
import org.vison.cache.support.DefaultCacheMonitor;
import org.vison.cache.support.DefaultMetricsManager;
import org.vison.cache.support.StatInfo;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author huangli
 */
public class MetricsMonitorInstaller extends AbstractLifecycle implements CacheMonitorInstaller {

    private final Consumer<StatInfo> metricsCallback;
    private final Duration interval;

    private DefaultMetricsManager metricsManager;

    public MetricsMonitorInstaller(Consumer<StatInfo> metricsCallback, Duration interval) {
        this.metricsCallback = metricsCallback;
        this.interval = interval;
    }

    @Override
    protected void doInit() {
        if (metricsCallback != null && interval != null) {
            metricsManager = new DefaultMetricsManager((int) interval.toMinutes(),
                    TimeUnit.MINUTES, metricsCallback);
            metricsManager.start();
        }
    }

    @Override
    protected void doShutdown() {
        if (metricsManager != null) {
            metricsManager.stop();
            metricsManager.clear();
            metricsManager = null;
        }
    }

    @Override
    public void addMonitors(CacheManager cacheManager, Cache cache, QuickConfig quickConfig) {
        if (metricsManager == null) {
            return;
        }
        cache = CacheUtil.getAbstractCache(cache);
        if (cache instanceof MultiLevelCache) {
            MultiLevelCache mc = (MultiLevelCache) cache;
            if (mc.caches().length == 2) {
                Cache local = mc.caches()[0];
                Cache remote = mc.caches()[1];
                DefaultCacheMonitor localMonitor = new DefaultCacheMonitor(quickConfig.getName() + "_local");
                local.config().getMonitors().add(localMonitor);
                DefaultCacheMonitor remoteMonitor = new DefaultCacheMonitor(quickConfig.getName() + "_remote");
                remote.config().getMonitors().add(remoteMonitor);
                metricsManager.add(localMonitor, remoteMonitor);
            }
        }

        DefaultCacheMonitor monitor = new DefaultCacheMonitor(quickConfig.getName());
        cache.config().getMonitors().add(monitor);
        metricsManager.add(monitor);
    }
}
