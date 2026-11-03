#
# Copyright (c) 2026. Fraunhofer-Gesellschaft zur Foerderung der angewandten Forschung e.V. (represented by Fraunhofer ISST)
#
# This program and the accompanying materials are made available under the
# terms of the Apache License, Version 2.0 which is available at
# https://www.apache.org/licenses/LICENSE-2.0.
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.
#
# SPDX-License-Identifier: Apache-2.0
#

FROM eclipse-temurin:25-jre-alpine

RUN apk upgrade --no-cache \
    && rm -rf /var/cache/apk/*

ARG IMAGE_CREATED
ARG VERSION_TAG

LABEL org.opencontainers.image.title="Construct-X Wallet" \
      org.opencontainers.image.description="Wallet-Service for Project Construct-X. It includes functionalities of both an identity-hub and an issuer-service." \
      org.opencontainers.image.url="https://github.com/project-construct-x/wallet" \
      org.opencontainers.image.source="https://github.com/project-construct-x/wallet" \
      org.opencontainers.image.version="${VERSION_TAG}" \
      org.opencontainers.image.licenses="Apache-2.0" \
      org.opencontainers.image.created="${IMAGE_CREATED}"

ARG JAR_FILE

# non-root runtime user, uid/gid aligned with the Helm chart defaults (runAsUser/fsGroup: 10100)
ARG APP_USER=wallet
ARG APP_UID=10100
ARG APP_GID=10100

RUN addgroup --system --gid "$APP_GID" "$APP_USER" \
    && adduser \
         --system \
         --shell /sbin/nologin \
         --disabled-password \
         --ingroup "$APP_USER" \
         --no-create-home \
         --uid "$APP_UID" \
         "$APP_USER"

WORKDIR /app

COPY --chown=$APP_UID:$APP_GID build/libs/${JAR_FILE} /app/app.jar

COPY --chown=$APP_UID:$APP_GID build/docker/runtimeClasspath-dependencies.txt /app/DEPENDENCIES
COPY --chown=$APP_UID:$APP_GID build/docker/LICENSE /app/LICENSE
COPY --chown=$APP_UID:$APP_GID NOTICE_DOCKER.md /app/NOTICE.md

USER $APP_UID:$APP_GID

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--log-level=debug"]
