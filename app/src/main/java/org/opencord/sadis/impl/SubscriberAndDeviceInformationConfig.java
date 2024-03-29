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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.onlab.packet.Ip4Address;
import org.onlab.packet.MacAddress;
import org.onlab.packet.VlanId;
import org.etri.sis.BaseConfig;
import org.etri.sis.SubscriberAndDeviceInformation;

import org.etri.sis.UniTagInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Configuration options for the Subscriber And Device Information Service.
 *
 * <pre>
 * "sis" : {
 *     "integration" : {
 *         "url" : "http://{hostname}{:port}",
 *         "cache" : {
 *             "maxsize"  : number of entries,
 *             "entryttl" : duration, i.e. 10s or 1m
 *         }
 *     },
 *     "entries" : [
 *         {
 *             "id"                         : "uniqueid",
 *             "nasportid"                  : string,
 *             "port"                       : int,
 *             "slot"                       : int,
 *             "hardwareidentifier"         : string,
 *             "ipAddress"                  : string,
 *             "nasId"                      : string,
 *             "circuitId"                  : string,
 *             "remoteId"                   : string,
 *             "uniTagList": [
 *                  {
 *                  "uniTagMatch"               : int,
 *                  "ponCTag"                   : string,
 *                  "ponSTag"                   : string,
 *                  "usPonCTagPriority"         : int,
 *                  "dsPonCTagPriority"         : int,
 *                  "usPonSTagPriority"         : int,
 *                  "dsPonSTagPriority"         : int,
 *                  "technologyProfileId"       : int,
 *                  "upstreamBandwidthProfile"  : string,
 *                  "downstreamBandwidthProfile": string,
 *                  "enableMacLearning"         : string,
 *                  "configuredDacAddress"      : string,
 *                  "isDhcpRequired"            : string,
 *                  "isIgmpRequired"            : string,
 *                  "serviceName"               : string
 *                 }
 *                 ]
 *         }, ...
 *     ]
 * }
 * </pre>
 */
public class SubscriberAndDeviceInformationConfig extends BaseConfig<SubscriberAndDeviceInformation> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final int NO_PCP = -1;
    private static final String NO_SN = "";
    private static final String UNI_TAG_MATCH = "uniTagMatch";
    private static final String PON_C_TAG = "ponCTag";
    private static final String PON_S_TAG = "ponSTag";
    private static final String US_C_TAG_PCP = "usPonCTagPriority";
    private static final String US_S_TAG_PCP = "usPonSTagPriority";
    private static final String DS_C_TAG_PCP = "dsPonCTagPriority";
    private static final String DS_S_TAG_PCP = "dsPonSTagPriority";
    private static final String MAC_LEARNING = "enableMacLearning";
    private static final String TP_ID = "technologyProfileId";
    private static final String US_BW = "upstreamBandwidthProfile";
    private static final String DS_BW = "downstreamBandwidthProfile";
    private static final String SERVICE_NAME = "serviceName";
    private static final String IS_DHCP_REQ = "isDhcpRequired";
    private static final String IS_IGMP_REQ = "isIgmpRequired";
    private static final String MAC_ADDRESS = "configuredMacAddress";
    private static final String US_SP = "upstreamSliceProfile";

    public List<SubscriberAndDeviceInformation> getEntries() {
        List<SubscriberAndDeviceInformation> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(VlanId.class, new VlanIdDeserializer());
        module.addDeserializer(Ip4Address.class, new Ip4AddressDeserializer());
        module.addDeserializer(UniTagInformation.class, new UniTagDeserializer());
        mapper.registerModule(module);
        final JsonNode entries = this.object.path(ENTRIES);
        entries.forEach(entry -> {
            try {
                result.add(mapper.readValue(entry.toString(), SubscriberAndDeviceInformation.class));
            } catch (IOException e) {
                log.warn("Unable to parse configuration entry, '{}', error: {}", entry, e.getMessage());
            }
        });

        return result;
    }

    public class VlanIdDeserializer extends JsonDeserializer<VlanId> {
        @Override
        public VlanId deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            ObjectCodec oc = jp.getCodec();
            JsonNode node = oc.readTree(jp);
            return VlanId.vlanId((short) node.asInt());
        }
    }

    public class Ip4AddressDeserializer extends JsonDeserializer<Ip4Address> {
        @Override
        public Ip4Address deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            ObjectCodec oc = jp.getCodec();
            JsonNode node = oc.readTree(jp);
            return Ip4Address.valueOf(node.asText());
        }
    }

    public class UniTagDeserializer extends JsonDeserializer<UniTagInformation> {
        @Override
        public UniTagInformation deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            ObjectCodec oc = jp.getCodec();
            JsonNode node = oc.readTree(jp);
            log.info(node.toString());
            return getUniTagInformation(node);
        }
    }

    public UniTagInformation getUniTagInformation(JsonNode node) {
        return new UniTagInformation.Builder()
                .setUniTagMatch(VlanId.vlanId(node.get(UNI_TAG_MATCH) == null ? VlanId.NO_VID
                        : (short) node.get(UNI_TAG_MATCH).asInt()))
                .setPonCTag(VlanId.vlanId((short) node.get(PON_C_TAG).asInt()))
                .setPonSTag(VlanId.vlanId((short) node.get(PON_S_TAG).asInt()))
                .setUsPonCTagPriority(node.get(US_C_TAG_PCP) == null ? NO_PCP :
                        node.get(US_C_TAG_PCP).asInt())
                .setUsPonSTagPriority(node.get(US_S_TAG_PCP) == null ? NO_PCP :
                        node.get(US_S_TAG_PCP).asInt())
                .setDsPonCTagPriority(node.get(DS_C_TAG_PCP) == null ? NO_PCP :
                        node.get(DS_C_TAG_PCP).asInt())
                .setDsPonSTagPriority(node.get(DS_S_TAG_PCP) == null ? NO_PCP :
                        node.get(DS_S_TAG_PCP).asInt())
                .setEnableMacLearning(node.get(MAC_LEARNING) == null ? false :
                        node.get(MAC_LEARNING).asBoolean())
                .setTechnologyProfileId(node.get(TP_ID).asInt())
                .setUpstreamBandwidthProfile(node.get(US_BW) == null ? null
                        : node.get(US_BW).asText())
                .setDownstreamBandwidthProfile(node.get(DS_BW) == null ? null
                        : node.get(DS_BW).asText())
                .setServiceName(node.get(SERVICE_NAME) == null ? NO_SN :
                        node.get(SERVICE_NAME).asText())
                .setIsDhcpRequired(node.get(IS_DHCP_REQ) == null ? false : node.get(IS_DHCP_REQ).asBoolean())
                .setIsIgmpRequired(node.get(IS_IGMP_REQ) == null ? false : node.get(IS_IGMP_REQ).asBoolean())
                .setConfiguredMacAddress(node.get(MAC_ADDRESS) == null ? MacAddress.NONE.toString() :
                        node.get(MAC_ADDRESS).asText())
                .setUpstreamSliceProfile(node.get(US_SP) == null ? null
                        : node.get(US_SP).asText())
                .build();
    }
}
