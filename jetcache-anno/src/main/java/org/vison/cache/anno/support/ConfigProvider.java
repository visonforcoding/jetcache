package org.vison.cache.anno.support;

import org.vison.cache.CacheBuilder;
import org.vison.cache.CacheManager;
import org.vison.cache.embedded.EmbeddedCacheBuilder;
import org.vison.cache.external.ExternalCacheBuilder;
import org.vison.cache.support.AbstractLifecycle;
import org.vison.cache.support.StatInfo;
import org.vison.cache.support.StatInfoLogger;
import org.vison.cache.template.CacheBuilderTemplate;
import org.vison.cache.template.CacheMonitorInstaller;
import org.vison.cache.template.MetricsMonitorInstaller;
import org.vison.cache.template.NotifyMonitorInstaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created on 2016/11/29.
 *
 * @author huangli
 */
public class ConfigProvider extends AbstractLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(ConfigProvider.class);

    protected GlobalCacheConfig globalCacheConfig;

    protected EncoderParser encoderParser;
    protected KeyConvertorParser keyConvertorParser;
    private Consumer<StatInfo> metricsCallback;

    private CacheBuilderTemplate cacheBuilderTemplate;

    public ConfigProvider() {
        encoderParser = new DefaultEncoderParser();
        keyConvertorParser = new DefaultKeyConvertorParser();
        metricsCallback = new StatInfoLogger(false);
    }

    @Override
    protected void doInit() {
        cacheBuilderTemplate = new CacheBuilderTemplate(globalCacheConfig.isPenetrationProtect(),
                globalCacheConfig.getLocalCacheBuilders(), globalCacheConfig.getRemoteCacheBuilders());
        for (CacheBuilder builder : globalCacheConfig.getLocalCacheBuilders().values()) {
            EmbeddedCacheBuilder eb = (EmbeddedCacheBuilder) builder;
            if (eb.getConfig().getKeyConvertor() instanceof ParserFunction) {
                ParserFunction f = (ParserFunction) eb.getConfig().getKeyConvertor();
                eb.setKeyConvertor(parseKeyConvertor(f.getValue()));
            }
        }
        for (CacheBuilder builder : globalCacheConfig.getRemoteCacheBuilders().values()) {
            ExternalCacheBuilder eb = (ExternalCacheBuilder) builder;
            if (eb.getConfig().getKeyConvertor() instanceof ParserFunction) {
                ParserFunction f = (ParserFunction) eb.getConfig().getKeyConvertor();
                eb.setKeyConvertor(parseKeyConvertor(f.getValue()));
            }
            if (eb.getConfig().getValueEncoder() instanceof ParserFunction) {
                ParserFunction f = (ParserFunction) eb.getConfig().getValueEncoder();
                eb.setValueEncoder(parseValueEncoder(f.getValue()));
            }
            if (eb.getConfig().getValueDecoder() instanceof ParserFunction) {
                ParserFunction f = (ParserFunction) eb.getConfig().getValueDecoder();
                eb.setValueDecoder(parseValueDecoder(f.getValue()));
            }
        }
        initCacheMonitorInstallers();
    }

    protected void initCacheMonitorInstallers() {
        cacheBuilderTemplate.getCacheMonitorInstallers().add(metricsMonitorInstaller());
        cacheBuilderTemplate.getCacheMonitorInstallers().add(notifyMonitorInstaller());
        for (CacheMonitorInstaller i : cacheBuilderTemplate.getCacheMonitorInstallers()) {
            if (i instanceof AbstractLifecycle) {
                ((AbstractLifecycle) i).init();
            }
        }
    }

    protected CacheMonitorInstaller metricsMonitorInstaller() {
        Duration interval = null;
        if (globalCacheConfig.getStatIntervalMinutes() > 0) {
            interval = Duration.ofMinutes(globalCacheConfig.getStatIntervalMinutes());
        }

        MetricsMonitorInstaller i = new MetricsMonitorInstaller(metricsCallback, interval);
        i.init();
        return i;
    }

    protected CacheMonitorInstaller notifyMonitorInstaller() {
        return new NotifyMonitorInstaller(area -> globalCacheConfig.getRemoteCacheBuilders().get(area));
    }

    public CacheBuilderTemplate getCacheBuilderTemplate() {
        return cacheBuilderTemplate;
    }

    @Override
    public void doShutdown() {
        try {
            for (CacheMonitorInstaller i : cacheBuilderTemplate.getCacheMonitorInstallers()) {
                if (i instanceof AbstractLifecycle) {
                    ((AbstractLifecycle) i).shutdown();
                }
            }
        } catch (Exception e) {
            logger.error("close fail", e);
        }
    }

    /**
     * Keep this method for backward compatibility.
     * NOTICE: there is no getter for encoderParser.
     */
    public Function<Object, byte[]> parseValueEncoder(String valueEncoder) {
        return encoderParser.parseEncoder(valueEncoder);
    }

    /**
     * Keep this method for backward compatibility.
     * NOTICE: there is no getter for encoderParser.
     */
    public Function<byte[], Object> parseValueDecoder(String valueDecoder) {
        return encoderParser.parseDecoder(valueDecoder);
    }

    /**
     * Keep this method for backward compatibility.
     * NOTICE: there is no getter for keyConvertorParser.
     */
    public Function<Object, Object> parseKeyConvertor(String convertor) {
        return keyConvertorParser.parseKeyConvertor(convertor);
    }

    public CacheNameGenerator createCacheNameGenerator(String[] hiddenPackages) {
        return new DefaultCacheNameGenerator(hiddenPackages);
    }

    public CacheContext newContext(CacheManager cacheManager) {
        return new CacheContext(cacheManager, this, globalCacheConfig);
    }

    public void setEncoderParser(EncoderParser encoderParser) {
        this.encoderParser = encoderParser;
    }

    public void setKeyConvertorParser(KeyConvertorParser keyConvertorParser) {
        this.keyConvertorParser = keyConvertorParser;
    }

    public GlobalCacheConfig getGlobalCacheConfig() {
        return globalCacheConfig;
    }

    public void setGlobalCacheConfig(GlobalCacheConfig globalCacheConfig) {
        this.globalCacheConfig = globalCacheConfig;
    }

    public void setMetricsCallback(Consumer<StatInfo> metricsCallback) {
        this.metricsCallback = metricsCallback;
    }

}
