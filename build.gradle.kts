/*
 * Copyright 2021 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import org.gradle.api.tasks.testing.logging.TestExceptionFormat.*
import org.gradle.api.tasks.testing.logging.TestLogEvent.*


plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    id("com.netflix.dgs.codegen") version "6.1.3"
    id("org.springframework.boot") version "3.2.1"
    id("nebula.dependency-recommender") version "11.0.0"
    id("nebula.netflixoss") version "11.3.2"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    // ----
    // Before we release the DGS Framework our CI Pipeline tests this project against the current snapshot.
    // To support that we need to have `mavenLocal` support.
    mavenLocal()
    // ----
}

dependencies {
    implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:9.1.3"))
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.+")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.+")
    implementation("net.datafaker:datafaker:2.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.9.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.+")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
}

tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
    generateClientv2 = true
    packageName = "com.example.demo.generated"
}


tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(FAILED, STANDARD_ERROR, SKIPPED)
        exceptionFormat = FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}
