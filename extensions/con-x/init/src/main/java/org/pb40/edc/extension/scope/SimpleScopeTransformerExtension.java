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
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.spi.system.ServiceExtension;

@Extension("Simple Dev Scope Transformer")
public class SimpleScopeTransformerExtension implements ServiceExtension {

    @Provider
    public ScopeToCriterionTransformer createScopeTransformer() {
        return new SimpleScopeTransformer();
    }
}
