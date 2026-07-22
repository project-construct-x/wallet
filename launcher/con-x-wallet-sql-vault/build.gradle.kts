/*
 * Copyright (c) 2026. Fraunhofer-Gesellschaft zur Foerderung der angewandten Forschung e.V. (represented by Fraunhofer ISST)
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import org.gradle.api.tasks.Exec
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.time.Instant

plugins {
    `java-library`
    id("application")
    alias(libs.plugins.shadow)
}

val edcVersion = project.property("con-x-edcVersion") as String

dependencies {
    runtimeOnly("org.eclipse.edc:console-monitor:${edcVersion}")
    runtimeOnly("org.eclipse.edc:identityhub-bom:${edcVersion}")
    runtimeOnly("org.eclipse.edc:issuerservice-bom:${edcVersion}")
    runtimeOnly("org.eclipse.edc:identityhub-feature-sql-bom:${edcVersion}")
    runtimeOnly("org.eclipse.edc:issuerservice-feature-sql-bom:${edcVersion}")
    runtimeOnly(project(":extensions:con-x:sql-vault"))

    runtimeOnly(project(":extensions:super-user-seed-extension"))
    runtimeOnly(project(":extensions:con-x:dev-attestation"))
}

repositories { mavenCentral() }

application {
    mainClass.set("org.eclipse.edc.boot.system.runtime.BaseRuntime")
}

val releaseVersion = "$edcVersion-1"

tasks.shadowJar {
    mergeServiceFiles()
    archiveFileName.set("$releaseVersion.jar")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest { attributes["Main-Class"] = application.mainClass.get() }
}

val depsReportFile = layout.buildDirectory.file("docker/runtimeClasspath-dependencies.txt")

val generateRuntimeClasspathDeps = tasks.register("generateRuntimeClasspathDeps") {
    outputs.file(depsReportFile)

    doLast {
        val conf = configurations.runtimeClasspath.get()

        val outFile = depsReportFile.get().asFile
        outFile.parentFile.mkdirs()

        outFile.printWriter().use { out ->
            out.println("runtimeClasspath dependencies:")
            conf.incoming.resolutionResult.allComponents.forEach { component ->
                val id = component.moduleVersion
                if (id != null) {
                    out.println("${id.group}:${id.name}:${id.version}")
                } else {
                    out.println(component.id.toString())
                }
            }
        }
    }
}


val imageName = "wallet-sql-vault:${releaseVersion}"
val shadowJar = tasks.named<ShadowJar>("shadowJar")
val jarFileName = "$releaseVersion.jar"

tasks.register<Exec>("dockerize") {
    dependsOn(shadowJar, generateRuntimeClasspathDeps)

    workingDir = project.projectDir

    doFirst {
        val dockerfile = project.projectDir.resolve("Dockerfile")
        require(dockerfile.exists()) { "Dockerfile missing in ${project.projectDir}" }

        val jarFile = project.layout.buildDirectory.file("libs/$jarFileName").get().asFile
        require(jarFile.exists()) { "Shadow-Jar build/libs/$jarFileName missing " +
                "– please make sure \"shadowJar\" gradle task is successful" }

        val depsFile = depsReportFile.get().asFile
        require(depsFile.exists()) {
            "Dependencies report ${depsFile.absolutePath} missing – check generateRuntimeClasspathDeps task"
        }

        val dockerDir = project.layout.buildDirectory.dir("docker").get().asFile
        project.copy {
            from(rootProject.files("LICENSE"))
            into(dockerDir)
        }


        val imageCreated = Instant.now().toString()

        commandLine(
            "docker", "build",
            "--build-arg", "JAR_FILE=$jarFileName",
            "--build-arg", "IMAGE_CREATED=$imageCreated",
            "--build-arg", "VERSION_TAG=$releaseVersion",
            "-t", imageName,
            "."
        )
    }
}