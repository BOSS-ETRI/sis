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
package org.etri.sis.cli;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.onosproject.cli.AbstractShellCommand;
import org.etri.sis.BaseInformationService;
import org.etri.sis.SadisService;
import org.etri.sis.SubscriberAndDeviceInformation;

/**
 * Subscriber And Device Information Service CLI.
 */
@Service
@Command(scope = "boss", name = "sis", description = "Subscriber And Device Information Service CLI command")
public class SubscriberGetCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "ID", description = "subscriber ID", required = true, multiValued = false)
    String id;

    private SadisService sisService = get(SadisService.class);
    private BaseInformationService<SubscriberAndDeviceInformation> service = sisService.getSubscriberInfoService();

    @Override
    protected void doExecute() {
        SubscriberAndDeviceInformation info = service.get(id);
        if (info != null) {
           print(info.toString());
        } else {
           print("Subscriber not found");
        }
    }
}
