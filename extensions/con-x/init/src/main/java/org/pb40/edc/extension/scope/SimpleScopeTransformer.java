/*
 *  Copyright (c) 2026 planen-bauen 4.0 GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       planen-bauen 4.0 GmbH - initial API and implementation
 *
 */

package org.pb40.edc.extension.scope;

import org.eclipse.edc.identityhub.spi.transformation.ScopeToCriterionTransformer;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.result.Result;

import java.util.List;

import static org.eclipse.edc.spi.result.Result.failure;
import static org.eclipse.edc.spi.result.Result.success;

/**
 * A ScopeToCriterionTransformer that accepts simple "<namespace>:<credentialType>:<scope>" scopes.
 */
public class SimpleScopeTransformer implements ScopeToCriterionTransformer {

    public static final String TYPE_OPERAND = "verifiableCredential.credential.type";
    public static final String CONTAINS_OPERATOR = "contains";

    @Override
    public Result<List<Criterion>> transformScope(String scope) {
        var fullyQualifiedCredentialType = tokenize(scope);
        if (fullyQualifiedCredentialType.failed()) {
            return failure("Scope string cannot be converted: %s".formatted(fullyQualifiedCredentialType.getFailureDetail()));
        }
        var credentialType = fullyQualifiedCredentialType.getContent();
        return success(List.of(new Criterion(TYPE_OPERAND, CONTAINS_OPERATOR, credentialType)));
    }

    private Result<String> tokenize(String scope) {
        if (scope == null) return failure("Scope was null");
        String credentialType = scope.substring(0, scope.lastIndexOf(":"));
        return success(credentialType.substring(credentialType.lastIndexOf(":") + 1));
    }
}
