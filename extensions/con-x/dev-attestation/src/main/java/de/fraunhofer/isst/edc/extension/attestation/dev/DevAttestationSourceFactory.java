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

import org.eclipse.edc.issuerservice.spi.issuance.attestation.AttestationSource;
import org.eclipse.edc.issuerservice.spi.issuance.attestation.AttestationSourceFactory;
import org.eclipse.edc.issuerservice.spi.issuance.model.AttestationDefinition;
import org.eclipse.edc.spi.monitor.Monitor;

public class DevAttestationSourceFactory implements AttestationSourceFactory {

    private final Monitor monitor;

    public DevAttestationSourceFactory(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public AttestationSource createSource(AttestationDefinition definition) {
        return new DevAttestationSource(definition.getConfiguration(), monitor);
    }

}
