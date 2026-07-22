# Notices for Construct-X Wallet


This product includes software developed by the Project Construct-X and its contributors.
The software is licensed under the Apache License, Version 2.0.
You may obtain a copy of the License at:

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

## Third-party Components

This product includes third-party components:

- Eclipse Dataspace Connector (EDC)
  Copyright (c) 2020-2026 Contributors to the Eclipse Foundation
  Licensed under the Apache License, Version 2.0
  See: https://github.com/eclipse-edc/Connector

- Eclipse IdentityHub
  Copyright (c) 2022-2026 Contributors to the Eclipse Foundation
  Licensed under the Apache License, Version 2.0
  See: https://github.com/eclipse-edc/IdentityHub

- super-user-seed-extension
  Copyright (c) 2026 Fraunhofer-Gesellschaft zur Foerderung der angewandten Forschung e.V.
  Licensed under the Apache License, Version 2.0
  See: https://github.com/FraunhoferISST/super-user-seed-extension

- Eclipse Temurin (base container image)
  Provided by the Eclipse Adoptium project
  Licensed under the GNU General Public License, version 2 with the Classpath Exception
  See: https://adoptium.net

Additional third-party dependencies and their licenses are listed in the build configuration and in the dependency metadata, as shown in the `DEPENDENCIES` file.

**Used base image**

- [eclipse-temurin:25-jre](https://github.com/adoptium/containers)
- Official Eclipse Temurin DockerHub page: https://hub.docker.com/_/eclipse-temurin
- Eclipse Temurin Project: https://projects.eclipse.org/projects/adoptium.temurin
- Additional information about the Eclipse Temurin images: https://github.com/docker-library/repo-info/tree/master/repos/eclipse-temurin

As with all Docker images, these likely also contain other software which may be under other licenses
(such as Bash, etc. from the base distribution, along with any direct or indirect dependencies of the primary software being contained).

As for any pre-built image usage, it is the image user's responsibility to ensure that any use of this image complies with any relevant licenses for all software contained within.