/*
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.etri.sis.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.onosproject.codec.JsonCodec;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.config.ConfigFactory;
import org.onosproject.net.config.NetworkConfigRegistry;
import org.etri.sis.BaseInformation;
import org.etri.sis.BaseConfig;
import org.etri.sis.BaseInformationService;
import java.util.Set;


public abstract class InformationAdapter<T extends BaseInformation, K extends BaseConfig<T>>
        implements BaseInformationService<T> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    protected static final int DEFAULT_MAXIMUM_CACHE_SIZE = 0;
    protected static final long DEFAULT_TTL = 0;
    protected String url;
    protected ObjectMapper mapper;
    protected Cache<String, T> cache;
    protected int maxiumCacheSize = DEFAULT_MAXIMUM_CACHE_SIZE;
    protected long cacheEntryTtl = DEFAULT_TTL;

    protected Map<String, T> localCfgData = null;

    public final void updateConfig(NetworkConfigRegistry cfgService) {
        K cfg = getConfig(cfgService);
        if (cfg == null) {
            this.log.warn("Configuration not available");
            return;
        }
        this.log.info("Cache Max Size: {}", cfg.getCacheMaxSize());
        this.log.info("Cache TTL:      {}", cfg.getCacheTtl().getSeconds());
        this.log.info("Entries:        {}", cfg.getEntries());

        configure(cfg);
    }

    public K getConfig(NetworkConfigRegistry cfgService) {
        return cfgService.getConfig(getAppId(), getConfigClass());
    }

    /**
     * Configures the Adapter for data source and cache parameters.
     *
     * @param cfg Configuration data.
     */
    public void configure(K cfg) {

        String url = null;
        try {
            // if the url is not present then assume data is in netcfg
            if (cfg.getUrl() != null) {
                url = cfg.getUrl().toString();
            } else {
                localCfgData = Maps.newConcurrentMap();

                cfg.getEntries().forEach(entry ->
                        localCfgData.put(entry.id(), entry));
                log.info("url is null, data source is local netcfg data");
            }
        } catch (MalformedURLException mUrlEx) {
            log.error("Invalid URL specified: {}", mUrlEx);
        }

        int maximumCacheSeize = cfg.getCacheMaxSize();
        long cacheEntryTtl = cfg.getCacheTtl().getSeconds();

        // Rebuild cache if needed
        if (isUrlChanged(url) || maximumCacheSeize != this.maxiumCacheSize ||
                cacheEntryTtl != this.cacheEntryTtl) {
            this.maxiumCacheSize = maximumCacheSeize;
            this.cacheEntryTtl = cacheEntryTtl;
            this.url = url;

            Cache<String, T> newCache = CacheBuilder.newBuilder()
                    .maximumSize(maxiumCacheSize).expireAfterAccess(cacheEntryTtl, TimeUnit.SECONDS).build();
            Cache<String, T> oldCache = cache;

            synchronized (this) {
                cache = newCache;
            }

            oldCache.invalidateAll();
            oldCache.cleanUp();
        }
    }

    protected boolean isUrlChanged(String url) {
        if (url == null && this.url == null) {
            return false;
        }
        return !((url == this.url) || (url != null && url.equals(this.url)));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.etri.sis.SadisService#clearCache()
     */
    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.etri.sis.BaseInformationService#invalidateId()
     */
    @Override
    public void invalidateId(String id) {
        cache.invalidate(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.etri.sis.BaseInformationService#getfromCache(java.lang.
     * String)
     */
    @Override
    public T getfromCache(String id) {
        Cache<String, T> local;
        synchronized (this) {
            local = cache;
        }
        T info = local.getIfPresent(id);
        if (info != null) {
            return info;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.etri.sis.BaseInformationService#get(java.lang.
     * String)
     */
    @Override
    public T get(String id) {
        Cache<String, T> local;
        synchronized (this) {
            local = cache;
        }

        T info = local.getIfPresent(id);
        if (info != null) {
            return info;
        }

        /*
         * Not in cache, if we have a URL configured we can attempt to get it
         * from there, else check for it in the locally configured data
         */
        if (this.url == null) {
            info = (localCfgData == null) ? null : localCfgData.get(id);

            if (info != null) {
                local.put(id, info);
                return info;
            }
        } else {
            // Augment URL with query parameters
            String urlWithSubId = this.url.replaceAll("%s", id);
            log.debug("Getting data from the remote URL {}", urlWithSubId);

            try (InputStream io = new URL(urlWithSubId).openStream()) {
                info = mapper.readValue(io, getInformationClass());
                local.put(id, info);
                return info;
            } catch (IOException e) {
                // TODO use a better http library that allows us to read status code
                log.debug("Exception while reading remote data {} ", e.getMessage());
            }
        }
        log.warn("Data not found for id {}", id);
        return null;
    }

    public abstract void registerModule();

    public abstract Set<ConfigFactory> getConfigFactories();

    public abstract JsonCodec<T> getCodec();

    public abstract Class<T> getInformationClass();

    public abstract Class<K> getConfigClass();

    public abstract ApplicationId getAppId();
}
