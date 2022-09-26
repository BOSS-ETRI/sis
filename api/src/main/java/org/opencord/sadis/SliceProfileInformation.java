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
package org.etri.sis;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class SliceProfileInformation extends BaseInformation {

    @JsonProperty(value = "deviceId")
    String deviceId;

    @JsonProperty(value = "portId")
    String portId;

    @JsonProperty(value = "tConts")
    String trafficContainers;

    @JsonProperty(value = "cir")
    long committedInformationRate;

    @JsonProperty(value = "dba")
    String dbaType;

    public final String deviceId() {
        return this.deviceId;
    }

    public final void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public final String portId() {
        return this.portId;
    }

    public final void setPortId(String portId) {
        this.portId = portId;
    }

    public final String trafficContainers() {
        return this.trafficContainers;
    }

    public final void setTrafficContainers(String trafficContainers) {
        this.trafficContainers = trafficContainers;
    }

    public final String dbaType() {
        return this.dbaType;
    }

    public final void setDbaType(String dbaType) {
        this.dbaType = dbaType;
    }

    public final long committedInformationRate() {
        return this.committedInformationRate;
    }

    public final void setCommittedInformationRate(final long committedInformationRate) {
        this.committedInformationRate = committedInformationRate;
    }

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            log.info("???");
            return false;
        }
        SliceProfileInformation that = (SliceProfileInformation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                committedInformationRate,
                deviceId,
                portId,
                trafficContainers,
                dbaType
        );
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SliceProfileInformation{");
        sb.append("id=").append(id);
        sb.append(", committedInformationRate=").append(committedInformationRate);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", portId=").append(portId);
        sb.append(", trafficContainers=").append(trafficContainers);
        sb.append(", dbaType=").append(dbaType);
        sb.append('}');
        return sb.toString();
    }
}
