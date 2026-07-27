/*
 *  Copyright (c) 2026 Fraunhofer-Gesellschaft zur Foerderung der angewandten Forschung e.V. (represented by Fraunhofer ISST)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Fraunhofer ISST - initial API and implementation
 *
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
