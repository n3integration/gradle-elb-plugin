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
package com.n3integration.gradle.elb

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient
import com.amazonaws.services.elasticloadbalancing.model.*
import com.n3integration.gradle.elb.models.ElasticLoadBalancer

trait ELBAware {

    def AmazonElasticLoadBalancingClient createClient() {
        new AmazonElasticLoadBalancingClient(defaultCredentials())
    }

    def AWSCredentials defaultCredentials() {
        def credentialsProvider = new DefaultAWSCredentialsProviderChain()
        credentialsProvider.getCredentials()
    }

    def CreateLoadBalancerResult createLoadBalancer(AmazonElasticLoadBalancingClient client, ElasticLoadBalancer elb) {
        client.createLoadBalancer(new CreateLoadBalancerRequest()
            .withLoadBalancerName(elb.name)
            .withAvailabilityZones(elb.availabilityZones)
            .withListeners(elb.listeners.collect { it.toListener() })
            .withTags(elb.tags)
            .withSecurityGroups(elb.securityGroups)
            .withSubnets(elb.subnets))
    }

    def ConfigureHealthCheckResult configureHealthCheck(AmazonElasticLoadBalancingClient client, ElasticLoadBalancer elb) {
        client.configureHealthCheck(new ConfigureHealthCheckRequest()
            .withLoadBalancerName(elb.name)
            .withHealthCheck(elb.healthCheck))
    }

    def ModifyLoadBalancerAttributesResult modifyAttributes(AmazonElasticLoadBalancingClient client, ElasticLoadBalancer elb) {
        client.modifyLoadBalancerAttributes(new ModifyLoadBalancerAttributesRequest()
            .withLoadBalancerName(elb.name)
            .withLoadBalancerAttributes(new LoadBalancerAttributes()
                .withAccessLog(elb.accessLogs?.toAccessLog())
                .withConnectionSettings(new ConnectionSettings()
                    .withIdleTimeout(elb.idleTimeout))
                .withConnectionDraining(new ConnectionDraining()
                    .withEnabled(elb.connectionDraining)
                    .withTimeout(elb.connectionDrainingTimeout))
                .withCrossZoneLoadBalancing(new CrossZoneLoadBalancing()
                    .withEnabled(elb.crossZoneLoadBalancing))))
    }

    def modifyTags(AmazonElasticLoadBalancingClient client, ElasticLoadBalancer elb) {
        client.removeTags(new RemoveTagsRequest().withLoadBalancerNames(elb.name))
        if(elb.tags) {
            client.addTags(new AddTagsRequest()
                .withLoadBalancerNames(elb.name)
                .withTags(elb.tags))
        }
    }

    def DeleteLoadBalancerResult deleteLoadBalancer(AmazonElasticLoadBalancingClient client, ElasticLoadBalancer elb) {
        client.deleteLoadBalancer(new DeleteLoadBalancerRequest()
            .withLoadBalancerName(elb.name))
    }
}
