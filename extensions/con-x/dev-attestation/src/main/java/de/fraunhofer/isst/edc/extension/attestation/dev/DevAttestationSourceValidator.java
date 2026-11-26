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

import org.eclipse.edc.issuerservice.spi.issuance.model.AttestationDefinition;
import org.eclipse.edc.validator.spi.ValidationResult;
import org.eclipse.edc.validator.spi.Validator;
import org.eclipse.edc.validator.spi.Violation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static de.fraunhofer.isst.edc.extension.attestation.dev.DevAttestationSource.BLACKLIST;
import static de.fraunhofer.isst.edc.extension.attestation.dev.DevAttestationSource.DEFAULT;

public class DevAttestationSourceValidator implements Validator<AttestationDefinition> {

    private static final Pattern DID_WEB_REGEX = Pattern.compile(
            "^did:web:" +
                    "(?:(?!-)[A-Za-z0-9-]{1,63}(?<!-)(?:\\.(?!-)[A-Za-z0-9-]{1,63}(?<!-))*)" +
                    "(?:%3[Aa][0-9]{1,5})?" +
                    "(?::(?:[A-Za-z0-9._-]|%[0-9A-Fa-f]{2})+)*$");

    @Override
    public ValidationResult validate(AttestationDefinition input) {
        Map<String, Object> config = input.getConfiguration();
        List<Violation> violations = new ArrayList<>();
        if (config.isEmpty()) {
            violations.add(new Violation("-configuration must not be empty-", "", ""));
        }
        if (config.get(BLACKLIST) != null) {
            if (!(config.get(BLACKLIST) instanceof List<?> blackList)) {
                violations.add(new Violation("-configuration.blackList must be of type list-",
                        "configuration.blackList", config.get(BLACKLIST)));
            } else {
                if (!blackList.stream().allMatch(it -> it instanceof String)) {
                    violations.add(new Violation("-configuration.blackList must contain only string-typed objects-",
                            "configuration.blackList", blackList));
                }
                for (var id : blackList) {
                    if (id instanceof String did && !DID_WEB_REGEX.matcher(did).matches()) {
                        violations.add(new Violation("-blacklisted id " + did + " is not a valid did:web id-",
                                "configuration.blackList", did));
                    }
                    if (config.containsKey(id.toString())) {
                        violations.add(new Violation("-" + id + " is blacklisted, but at the same time given an " +
                                "individual credential subject content-", "", ""));
                    }
                }
            }
        }
        for (String key : config.keySet()) {
            if (!BLACKLIST.equals(key)) {
                if (!DEFAULT.equals(key) && !DID_WEB_REGEX.matcher(key).matches()) {
                    violations.add(new Violation("-" + key + " is not a valid did:web id", "", ""));
                }
                if (!(config.get(key) instanceof Map<?, ?>)) {
                    violations.add(new Violation("-" + key + " must be of type object", "configuration." + key,
                            config.get(key)));
                }
            }
        }
        if (!violations.isEmpty()) {
            return ValidationResult.failure(violations);
        }
        return ValidationResult.success();
    }
}
