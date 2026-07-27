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

package de.fraunhofer.isst.edc.extension.sqlvault.dev;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.sql.QueryExecutor;
import org.eclipse.edc.sql.store.AbstractSqlStore;
import org.eclipse.edc.transaction.datasource.spi.DataSourceRegistry;
import org.eclipse.edc.transaction.spi.TransactionContext;
import org.jetbrains.annotations.Nullable;

public class SqlVault extends AbstractSqlStore implements Vault {

    private final Monitor monitor;

    public SqlVault(DataSourceRegistry dataSourceRegistry, String dataSourceName,
                    TransactionContext transactionContext, ObjectMapper objectMapper,
                    QueryExecutor queryExecutor, Monitor monitor) {
        super(dataSourceRegistry, dataSourceName, transactionContext, objectMapper, queryExecutor);
        this.monitor = monitor.withPrefix(this.getClass().getSimpleName());
    }

    @Override
    public @Nullable String resolveSecret(String key) {
        monitor.debug("Resolving secret for key: " + key);
        String command = "SELECT secret FROM sql_vault WHERE id = ?";
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                return queryExecutor.single(connection, false,
                        resultSet -> resultSet.getString("secret"), command, key);
            } catch (Exception e) {
                return null;
            }
        });

    }

    @Override
    public Result<Void> storeSecret(String key, String value) {
        String command = "INSERT INTO sql_vault (id, secret) " +
                "VALUES (?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET secret = EXCLUDED.secret";
        monitor.debug("Storing secret for key: " + key);
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                int changedEntries = queryExecutor.execute(connection, command, key, value);
                if (changedEntries != 1) {
                    monitor.debug("Unexpected number of entries for key: " + key);
                    return Result.failure("Unexpected number of entries for key: " + key);
                }
                return Result.success();
            } catch (Exception e) {
                return Result.failure(e.getMessage());
            }
        });
    }

    @Override
    public Result<Void> deleteSecret(String key) {
        String command = "DELETE FROM sql_vault " +
                "WHERE id = ?";
        monitor.debug("Deleting secret for key: " + key);
        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                int changedEntries = queryExecutor.execute(connection, command, key);
                if (changedEntries != 1) {
                    monitor.debug("Unexpected number of entries for key: " + key);
                    return Result.failure("Unexpected number of entries for key: " + key);
                }
                return Result.success();
            } catch (Exception e) {
                return Result.failure(e.getMessage());
            }
        });
    }
}
