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

import com.n3integration.gradle.elb.models.ElasticLoadBalancer
import com.n3integration.gradle.elb.tasks.CreateLoadBalancerTask
import com.n3integration.gradle.elb.tasks.DeleteLoadBalancerTask
import com.n3integration.gradle.elb.tasks.DescribeLoadBalancerTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

/**
 * Elastic load balancer Gradle {@link Plugin}
 *
 * @author n3integration
 */
class ELBPlugin implements Plugin<Project> {

    private static Logger logger = Logging.getLogger(ELBPlugin)

    public static final String ELB_EXTENSION = "elb"
    public static final String AWS_SDK_VERSION = "1.10.69"
    public static final String AWS_JAVA_CONFIGURATION_NAME = "awsJava"

    @Override
    void apply(Project project) {
        def resources = project.container(ElasticLoadBalancer)

        project.tasks.create("createLoadBalancers", CreateLoadBalancerTask)
        project.tasks.create("describeLoadBalancers", DescribeLoadBalancerTask)
        project.tasks.create("deleteLoadBalancers", DeleteLoadBalancerTask)

        addCreateTaskType(project)
        addDescribeTaskType(project)
        addDeleteTaskType(project)

        project.extensions.create(ELB_EXTENSION, ELBExtention, resources)
        project.configurations.create(AWS_JAVA_CONFIGURATION_NAME)
            .setVisible(false)
            .setTransitive(true)
            .setDescription('The AWS SDK to be used for this project.')

        Configuration config = project.configurations[AWS_JAVA_CONFIGURATION_NAME]
        config.defaultDependencies { dependencies ->
            dependencies.add(project.dependencies.create("com.amazonaws:aws-java-sdk-config:${AWS_SDK_VERSION}"))
            dependencies.add(project.dependencies.create("com.amazonaws:aws-java-sdk-core:${AWS_SDK_VERSION}"))
            dependencies.add(project.dependencies.create("com.amazonaws:aws-java-sdk-ec2:${AWS_SDK_VERSION}"))
            dependencies.add(project.dependencies.create("com.amazonaws:aws-java-sdk-elasticloadbalancing:${AWS_SDK_VERSION}"))
            dependencies.add(project.dependencies.create("com.amazonaws:aws-java-sdk-iam:${AWS_SDK_VERSION}"))
            dependencies.add(project.dependencies.create("com.amazonaws:aws-java-sdk-s3:${AWS_SDK_VERSION}"))
            dependencies.add(project.dependencies.create("com.amazonaws:aws-java-sdk-sts:${AWS_SDK_VERSION}"))
            dependencies.add(project.dependencies.create('org.slf4j:slf4j-simple:1.7.5'))
        }
    }

    private void addCreateTaskType(Project project) {
        logger.info("Adding create task type")
        project.ext.CreateLoadBalancer = CreateLoadBalancerTask.class
    }

    private void addDescribeTaskType(Project project) {
        logger.info("Adding describe task type")
        project.ext.DescribeLoadBalancer = DescribeLoadBalancerTask.class
    }

    private void addDeleteTaskType(Project project) {
        logger.info("Adding delete task type")
        project.ext.DeleteLoadBalancer = DeleteLoadBalancerTask.class
    }
}
