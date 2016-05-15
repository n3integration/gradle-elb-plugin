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

import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest

/**
 * @author n3integration
 */
trait SecurityGroupAware extends AwsAware {

    static final String DEFAULT_SECURITY_GROUP = "Default"

    /**
     * Initializes a new ec2 client instance
     *
     * @return an {@link AmazonEC2Client} instance
     */
    def AmazonEC2Client createEc2Client() {
        new AmazonEC2Client(defaultCredentials())
    }

    /**
     * Determines the AWS account identifier by investigating the 'Default'
     * security group, which should be present on all AWS accounts.
     *
     * @return the account identifier
     */
    def String getAccountId() {
        def client = createEc2Client()
        def result = client.describeSecurityGroups(new DescribeSecurityGroupsRequest()
            .withGroupNames(DEFAULT_SECURITY_GROUP))
        result.securityGroups.collect { sg ->
            sg.ownerId
        }.first()
    }
}