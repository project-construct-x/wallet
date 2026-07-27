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

import org.eclipse.edc.issuerservice.spi.issuance.attestation.AttestationDefinitionValidatorRegistry;
import org.eclipse.edc.issuerservice.spi.issuance.attestation.AttestationSourceFactoryRegistry;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

@Extension("Dev Attestation Extension")
public class DevAttestationExtension implements ServiceExtension {

    @Inject
    private AttestationSourceFactoryRegistry registry;

    @Inject
    private AttestationDefinitionValidatorRegistry validatorRegistry;

    @Override
    public String name() {
        return "Dev Attestation Extension";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        registry.registerFactory("dev", new DevAttestationSourceFactory(context.getMonitor()));
        validatorRegistry.registerValidator("dev", new DevAttestationSourceValidator());
    }
}
