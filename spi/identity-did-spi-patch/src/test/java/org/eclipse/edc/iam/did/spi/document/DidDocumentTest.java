/*
 *  Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Contributors to the Eclipse Foundation - assertion relationship tests
 *
 */

package org.eclipse.edc.iam.did.spi.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DidDocumentTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializationPublishesAssertionMethodForExistingDocuments() throws Exception {
        var document = mapper.readValue("""
                {"id":"did:web:test","verificationMethod":[
                  {"id":"did:web:test#key-1","type":"JsonWebKey2020","controller":"did:web:test","publicKeyMultibase":"zTestKey"}
                ],"capabilityInvocation":["did:web:test#key-1"]}
                """, DidDocument.class);
        var json = mapper.readTree(mapper.writeValueAsString(document));
        assertThat(json.get("assertionMethod").get(0).asText()).isEqualTo("did:web:test#key-1");
        assertThat(json.get("capabilityInvocation").get(0).asText()).isEqualTo("did:web:test#key-1");
        assertThat(mapper.readValue(json.toString(), DidDocument.class).getAssertionMethod())
                .containsExactly("did:web:test#key-1");
    }

    @Test
    void keyRotationRemovesStaleAssertionReferences() {
        var oldKey = VerificationMethod.Builder.newInstance().id("did:web:test#old").publicKeyMultibase("zOldKey").build();
        var newKey = VerificationMethod.Builder.newInstance().id("did:web:test#new").publicKeyMultibase("zNewKey").build();
        var document = DidDocument.Builder.newInstance().id("did:web:test")
                .verificationMethod(List.of(oldKey, oldKey)).build();
        assertThat(document.getAssertionMethod()).containsExactly("did:web:test#old");
        document.getVerificationMethod().clear();
        document.getVerificationMethod().add(newKey);
        assertThat(document.getAssertionMethod()).containsExactly("did:web:test#new");
        document.getVerificationMethod().clear();
        assertThat(document.getAssertionMethod()).isEmpty();
    }
}
