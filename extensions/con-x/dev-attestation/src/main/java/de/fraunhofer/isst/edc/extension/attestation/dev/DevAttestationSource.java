/*
 * Copyright (c) 2026. Fraunhofer-Gesellschaft zur Foerderung der angewandten Forschung e.V. (represented by Fraunhofer ISST)
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package de.fraunhofer.isst.edc.extension.attestation.dev;

import org.eclipse.edc.issuerservice.spi.issuance.attestation.AttestationContext;
import org.eclipse.edc.issuerservice.spi.issuance.attestation.AttestationSource;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DevAttestationSource implements AttestationSource {
    public final static String BLACKLIST = "blackList";
    public final static String DEFAULT = "default";

    private final Map<String, Object> config;
    private final Monitor monitor;

    public DevAttestationSource(Map<String, Object> configuration, Monitor monitor) {
        this.config = configuration;
        this.monitor = monitor.withPrefix(this.getClass().getSimpleName());
    }

    @Override
    public Result<Map<String, Object>> execute(AttestationContext context) {
        if (config.get(BLACKLIST) instanceof List<?> blackList) {
            if (blackList.contains(context.holder().getDid())) {
                String message = "Rejecting blacklisted holder " + context.holder().getDid();
                monitor.warning(message);
                return Result.failure(message);
            }
        }
        var content = config.getOrDefault(context.holder().getDid(),
                config.getOrDefault(DEFAULT, new HashMap<>()));
        if (content instanceof Map<?, ?> contentMap && !contentMap.containsKey("id")) {
            // copy, so that the shared configuration (e.g. the "default" entry) is never mutated
            var subject = new HashMap<String, Object>((Map<String, Object>) contentMap);
            subject.put("id", context.holder().getDid());
            content = subject;
        }
        return Result.success(Map.of("content", content));
    }
}

