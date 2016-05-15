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

import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription
import com.google.common.base.Strings
import com.n3integration.gradle.elb.ELBAware
import com.n3integration.gradle.elb.ELBExtention
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static com.google.common.collect.Lists.newArrayList

/**
 * Gradle task to fetch the status of an existing load balancer
 *
 * @author n3integration
 */
class DescribeLoadBalancerTask extends DefaultTask implements ELBAware {

    String loadBalancerName

    DescribeLoadBalancerTask() {
        this.description = "Describe an existing Elastic Load Balancer"
    }

    @TaskAction
    def describeLoadBalancerAction() {
        def client = createClient()
        if(Strings.isNullOrEmpty(loadBalancerName)) {
            ELBExtention elbExtention = this.project.elb;

            // check against global configuration
            if(elbExtention != null) {
                def names =  elbExtention.resources.collect { it.name }
                describe(client, names)
            }
        }
        else {
            describe(client, newArrayList(loadBalancerName))
        }
    }

    void describe(AmazonElasticLoadBalancingClient client, List<String> names) {
        logger.quiet("Fetching information about elastic load balancer(s)...")

        def result = describeLoadBalancers(client, names)
        result.loadBalancerDescriptions.each { LoadBalancerDescription description ->
            logger.quiet("\n\tload balancer: ${description.loadBalancerName}")
            logger.quiet("\t    instances: ${description.instances.size()}")
            logger.quiet("\t      created: ${description.createdTime}")
            logger.quiet("\t     dns name: ${description.DNSName}")
            logger.quiet("\t       scheme: ${description.scheme}")

            def listeners = description.listenerDescriptions.collect { it.listener }
            listeners.each { listener ->
                logger.quiet("\t     listener: ${listener.loadBalancerPort} (${listener.protocol}) -> ${listener.instancePort} (${listener.instanceProtocol})")
            }
        }
    }
}
