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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.identityhub.protocols.dcp.issuer.DcpAttestationContext;
import org.eclipse.edc.identityhub.protocols.dcp.spi.model.DcpRequestContext;
import org.eclipse.edc.issuerservice.spi.holder.model.Holder;
import org.eclipse.edc.issuerservice.spi.issuance.attestation.AttestationContext;
import org.eclipse.edc.issuerservice.spi.issuance.model.AttestationDefinition;
import org.eclipse.edc.spi.monitor.Monitor;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.eclipse.edc.junit.assertions.AbstractResultAssert.assertThat;
import static org.mockito.Mockito.mock;

public class DevAttestationTest {
    static ObjectMapper mapper = new ObjectMapper();
    static DevAttestationSourceValidator validator = new DevAttestationSourceValidator();
    static Monitor monitor = mock();


    static String testTemplateOne = """
            {
              "attestationType": "dev",
              "configuration": {
                "default": {
                  "isConsumer": false,
                  "isProvider": false,
                  "foo": {
                    "bar": 0
                  }
                },
                "blackList": [
                  "did:web:provider-idhub:user:provider"
                ]
              },
              "id": "dev-def-1"
            }
            """;

    @Test
    public void testValidDefinition() {
        assertThat(validator.validate(Objects.requireNonNull(parseAttestationDefinition(testTemplateOne)))).isSucceeded();
    }

    @Test
    public void testBlackListedHolderIsRejected() {
        Holder holder = Holder.Builder.newInstance().holderName("holder-name")
                .did("did:web:provider-idhub:user:provider").holderId("holder-id").build();
        DcpRequestContext requestContext = new DcpRequestContext(holder, new HashMap<>());
        AttestationContext attestationContext = new DcpAttestationContext(requestContext);
        DevAttestationSource devAttestationSource = new DevAttestationSource(
                Objects.requireNonNull(parseAttestationDefinition(testTemplateOne)).getConfiguration(), monitor);

        assertThat(devAttestationSource.execute(attestationContext)).isFailed();
    }

    @Test
    public void testLegitimateHolderIsSuccessful() {
        Holder holder = Holder.Builder.newInstance().holderName("holder-name")
                .did("did:web:consumer-idhub:user:consumer").holderId("holder-id").build();
        DcpRequestContext requestContext = new DcpRequestContext(holder, new HashMap<>());
        AttestationContext attestationContext = new DcpAttestationContext(requestContext);
        DevAttestationSource devAttestationSource = new DevAttestationSource(
                Objects.requireNonNull(parseAttestationDefinition(testTemplateOne)).getConfiguration(), monitor);

        assertThat(devAttestationSource.execute(attestationContext)).isSucceeded();
    }

    static String testTemplateTwo = """
            {
              "attestationType": "dev",
              "configuration": {
                "did:web:consumer-idhub:user:consumer": {
                   "isConsumer": true,
                   "isProvider": false,
                   "foo": {
                     "bar": 123
                  }
                },
                "default": {
                  "isConsumer": false,
                  "isProvider": false,
                  "foo": {
                    "bar": 0
                  }
                },
                "blackList": [
                  "did:web:provider-idhub:user:provider"
                ]
              },
              "id": "dev-def-1"
            }
            """;

    @Test
    public void testValidDefinitionWithNamedCredSubject() {
        assertThat(validator.validate(Objects.requireNonNull(parseAttestationDefinition(testTemplateTwo)))).isSucceeded();
    }

    static String testTemplateThree = """
            {
              "attestationType": "dev",
              "configuration": {
                "not-a-did-web-id": {
                   "isConsumer": true,
                   "isProvider": false,
                   "foo": {
                     "bar": 123
                  }
                },
                "default": {
                  "isConsumer": false,
                  "isProvider": false,
                  "foo": {
                    "bar": 0
                  }
                },
                "blackList": [
                  "did:web:provider-idhub:user:provider"
                ]
              },
              "id": "dev-def-1"
            }
            """;

    @Test
    public void testFaultyDefinitionWithNamedCredSubject() {
        assertThat(validator.validate(Objects.requireNonNull(parseAttestationDefinition(testTemplateThree)))).isFailed();
    }

    static String testTemplateFour = """
            {
              "attestationType": "dev",
              "configuration": {
                "default": {
                  "isConsumer": false,
                  "isProvider": false
                },
                "blackList": [
                  "not-a-did-web-id"
                ]
              },
              "id": "dev-def-1"
            }
            """;

    @Test
    public void testFaultyDefinitionWithInvalidBlacklist1() {
        assertThat(validator.validate(Objects.requireNonNull(parseAttestationDefinition(testTemplateFour)))).isFailed();
    }

    static String testTemplateFive = """
            {
              "attestationType": "dev",
              "configuration": {
                "default": {
                  "isConsumer": false,
                  "isProvider": false
                },
                "blackList": [
                  true
                ]
              },
              "id": "dev-def-1"
            }
            """;

    @Test
    public void testFaultyDefinitionWithInvalidBlacklist2() {
        assertThat(validator.validate(Objects.requireNonNull(parseAttestationDefinition(testTemplateFive)))).isFailed();
    }

    static String testTemplateSix = """
            {
              "attestationType": "dev",
              "configuration": {
              },
              "id": "dev-def-1"
            }
            """;

    @Test
    public void testFaultyDefinitionWithEmptyConfig() {
        assertThat(validator.validate(Objects.requireNonNull(parseAttestationDefinition(testTemplateSix)))).isFailed();
    }

    static AttestationDefinition parseAttestationDefinition(String template) {
        try {
            Map<?, ?> json = mapper.readValue(template, Map.class);
            String type = json.get("attestationType").toString();
            String id = json.get("id").toString();
            Map<String, Object> config = (Map<String, Object>) json.get("configuration");
            return AttestationDefinition.Builder.newInstance()
                    .attestationType(type)
                    .id(id)
                    .configuration(config)
                    .participantContextId("testContextId")
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}
