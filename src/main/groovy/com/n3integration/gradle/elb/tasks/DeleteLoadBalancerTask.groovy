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
import com.google.common.base.Strings
import com.n3integration.gradle.elb.ELBAware
import com.n3integration.gradle.elb.ELBExtention
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task to delete an existing elastic load balancer
 *
 * @author n3integration
 */
class DeleteLoadBalancerTask extends DefaultTask implements ELBAware {

    String loadBalancerName

    DeleteLoadBalancerTask() {
        this.description = "Deletes an elastic load balancer"
    }

    @TaskAction
    def deleteLoadBalancerAction() {
        def client = createClient()

        if(Strings.isNullOrEmpty(loadBalancerName)) {
            ELBExtention elbExtention = this.project.elb;

            // check against global configuration
            if(elbExtention != null) {
                elbExtention.resources.each { resource ->
                    delete(client, resource.name)
                }
            }
        }
        else {
            delete(client, loadBalancerName)
        }
    }

    def delete(AmazonElasticLoadBalancingClient client, String name) {
        logger.quiet("Deleting ${name} elastic load balancer...")
        deleteLoadBalancer(client, name)
        logger.quiet("\tdeleted: ${name}")
    }
}
