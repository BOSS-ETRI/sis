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
import java.util.Objects;

public class SliceProfileInformation extends BaseInformation {

    @JsonProperty(value = "serviceType")
    String serviceType;

    @JsonProperty(value = "dbaType")
    String dbaType;

    @JsonProperty(value = "technologyProfileId")
    int technologyProfileId = -1;

    public final String serviceType() {
        return this.serviceType;
    }

    public final void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public final String dbaType() {
        return this.dbaType;
    }

    public final void setDbaType(String dbaType) {
        this.dbaType = dbaType;
    }

    public final int technologyProfileId() {
        return technologyProfileId;
    }

    public final void setTechnologyProfileId(int technologyProfileId) {
        this.technologyProfileId = technologyProfileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SliceProfileInformation that = (SliceProfileInformation) o;
        return Objects.equals(serviceType, that.serviceType) &&
                Objects.equals(dbaType, that.dbaType) &&
                technologyProfileId == that.technologyProfileId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(serviceType, dbaType, technologyProfileId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SliceProfileInformation{");
        sb.append("id=").append(id);
        sb.append(", serviceType=").append(serviceType);
        sb.append(", dbaType=").append(dbaType);
        sb.append(", technologyProfileId=").append(technologyProfileId);
        sb.append('}');
        return sb.toString();
    }
}
