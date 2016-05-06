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

import com.n3integration.gradle.elb.ELBAware
import com.n3integration.gradle.elb.ELBExtention
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task to create a new elastic load balancer
 *
 * @author n3integration
 */
class CreateLoadBalancer extends DefaultTask implements ELBAware {

    CreateLoadBalancer() {
        this.description = "Creates an elastic load balancer"
    }

    @TaskAction
    def createLoadBalancerAction() {
        ELBExtention elbExtention = this.project.elb;

        if(elbExtention != null) {
            def client = createClient()

            elbExtention.resources.each { resource ->
                logger.quiet("Creating '${resource.name}' elastic load balancer...")
                def result = createLoadBalancer(client, resource)
                logger.quiet("\t            created: ${result.DNSName}")

                if(resource.healthCheck) {
                    result = configureHealthCheck(client, resource)
                    logger.quiet("\t         configured: ${result.healthCheck}")
                }
                else {
                    logger.warn("no health check configured for ${resource.name}")
                }

                result = modifyAttributes(client, resource)
                logger.quiet("\tmodified attributes: ${result.loadBalancerAttributes}")
            }
        }
    }
}
