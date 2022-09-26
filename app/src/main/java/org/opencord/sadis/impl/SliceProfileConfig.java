package org.etri.sis.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.etri.sis.SliceProfileInformation;
import org.etri.sis.BaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SliceProfileConfig extends BaseConfig<SliceProfileInformation> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<SliceProfileInformation> getEntries() {
        List<SliceProfileInformation> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule());
        final JsonNode entries = this.object.path(ENTRIES);
        log.info("hello world");
        entries.forEach(entry -> {
            try {
                result.add(mapper.readValue(entry.toString(), SliceProfileInformation.class));
            } catch (IOException e) {
                log.warn("Unable to parse configuration entry, '{}', error: {}", entry, e.getMessage());
            }
        });

        return result;
    }
}
