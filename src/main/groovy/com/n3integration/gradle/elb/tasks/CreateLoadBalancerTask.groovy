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
import com.n3integration.gradle.elb.ELBAware
import com.n3integration.gradle.elb.ELBExtention
import com.n3integration.gradle.elb.S3Aware
import com.n3integration.gradle.elb.models.ElasticLoadBalancer
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task to create a new elastic load balancer
 *
 * @author n3integration
 */
class CreateLoadBalancerTask extends DefaultTask implements ELBAware, S3Aware {

    ElasticLoadBalancer loadBalancer

    CreateLoadBalancerTask() {
        this.description = "Creates an elastic load balancer"
    }

    @TaskAction
    def createLoadBalancerAction() {
        def client = createClient()

        if(loadBalancer == null) {
            ELBExtention elbExtention = this.project.elb;

            // check against global configuration
            if(elbExtention != null) {
                elbExtention.resources.each { resource ->
                    create(client, resource)
                }
            }
        }
        else {
            create(client, loadBalancer)
        }
    }

    def loadBalancer(@DelegatesTo(ElasticLoadBalancer) Closure closure) {
        loadBalancer = new ElasticLoadBalancer()
        def clone = closure.rehydrate(loadBalancer, this, this)
        clone.resolveStrategy = Closure.DELEGATE_ONLY
        clone()
    }

    def create(AmazonElasticLoadBalancingClient client, ElasticLoadBalancer elb) {
        logger.quiet("Creating '${elb.name}' elastic load balancer...")

        def result = createLoadBalancer(client, elb)
        logger.quiet("\t            created: ${result.DNSName}")

        if(elb.healthCheck) {
            result = configureHealthCheck(client, elb)
            logger.quiet("\t         configured: ${result.healthCheck}")
        }
        else {
            logger.warn("no health check configured for ${elb.name}")
        }

        if(elb.accessLogs) {
            def s3Client = createS3Client()
            createBucketIfNotExists(s3Client, elb.accessLogs)
        }

        result = modifyAttributes(client, elb)
        logger.quiet("\tmodified attributes: ${result.loadBalancerAttributes}")
    }
}
