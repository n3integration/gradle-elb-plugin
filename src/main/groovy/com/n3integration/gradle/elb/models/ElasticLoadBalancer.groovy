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
package com.n3integration.gradle.elb.models

import com.amazonaws.services.elasticloadbalancing.model.HealthCheck
import com.amazonaws.services.elasticloadbalancing.model.Tag
import com.google.common.collect.Lists

/**
 * Models an AWS elastic load balancer
 *
 * @author n3integration
 */
class ElasticLoadBalancer {

    final String name
    Boolean crossZoneLoadBalancing  = Boolean.TRUE
    Integer idleTimeout
    Boolean connectionDraining      = Boolean.TRUE
    Integer connectionDrainingTimeout

    AccessLogs accessLogs
    HealthCheck healthCheck

    List<Tag> tags
    List<String> subnets
    List<String> instances
    List<Listeners> listeners
    List<String> securityGroups
    List<String> availabilityZones

    ElasticLoadBalancer(name) {
        this.name = name
        this.tags = Lists.newArrayList()
        this.instances = Lists.newArrayList()
        this.listeners = Lists.newArrayList()
        this.availabilityZones = Lists.newArrayList("us-east-1a", "us-east-1c")
    }

    void healthCheck(@DelegatesTo(HealthCheck) Closure closure) {
        healthCheck = new HealthCheck()
        def clone = closure.rehydrate(healthCheck, this, this)
        clone.resolveStrategy = Closure.DELEGATE_ONLY
        clone()
    }

    void accessLogs(@DelegatesTo(AccessLogs) Closure closure) {
        accessLogs = new AccessLogs()
        def clone = closure.rehydrate(accessLogs, this, this)
        clone.resolveStrategy = Closure.DELEGATE_ONLY
        clone()
    }

    void tag(@DelegatesTo(Tag) Closure closure) {
        def tag = new Tag()
        def clone = closure.rehydrate(tag, this, this)
        clone.resolveStrategy = Closure.DELEGATE_ONLY
        clone()
        tags.add(tag)
    }

    void listener(@DelegatesTo(Listeners) Closure closure) {
        def listener = new Listeners()
        def clone = closure.rehydrate(listener, this, this)
        clone.resolveStrategy = Closure.DELEGATE_ONLY
        clone()
        listeners.add(listener)
    }
}
