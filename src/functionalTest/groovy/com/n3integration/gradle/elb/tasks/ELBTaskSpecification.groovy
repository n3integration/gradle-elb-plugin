/*
 *  Copyright 2016 n3integration
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.n3integration.gradle.elb.tasks

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class ELBTaskSpecification extends Specification {

    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    @Unroll
    def "can create a load balancer with Gradle version #gradleVersion"() {
        given:
        buildFile << """
        buildscript {
            repositories {
                mavenLocal()
                mavenCentral()
            }
            dependencies {
                classpath "com.n3integration:gradle-elb-plugin:0.1.0"
            }
        }

        apply plugin: 'aws-elb'

        elb {
            resources {
                "some-function-elb" {
                    availabilityZones = ["us-east-1a", "us-east-1c"]

                    crossZoneLoadBalancing = "true"
                    idleTimeout = 400
                    connectionDraining = "true "
                    connectionDrainingTimeout = 500

                    listener {
                        instancePort = 9000
                        instanceProtocol = "http"
                        lbPort = 80
                        lbProtocol = "http"
                    }

                    listener {
                        instancePort = 9000
                        instanceProtocol = "http"
                        lbPort = 443
                        lbProtocol = "https"
                        sslCertificateId = "arn:aws:iam::123456789012:server-certificate/certName"
                    }

                    healthCheck {
                        healthyThreshold = 2
                        unhealthyThreshold = 2
                        timeout = 3
                        target = "HTTP:3000/"
                        interval = 30
                    }

                    tag {
                        key = "function"
                        value = "node-cluster"
                    }
                }
            }
        }""".stripMargin()

        when:
        def result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.root)
            .withArguments('createLoadBalancer')
            .build()

        then:
        result.task(":createLoadBalancer").outcome == TaskOutcome.SUCCESS

        where:
        gradleVersion << ['2.13']
    }
}
