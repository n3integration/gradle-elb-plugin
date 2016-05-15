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

import com.n3integration.gradle.elb.models.AccessLogs
import spock.lang.Specification

class BucketPolicyGeneratorSpecification extends Specification {

    def "can generate a bucket policy"() {
        when:
        def accountId = "abcdef123456"
        SecurityGroupAware sgAware = Mock()
        sgAware.getAccountId() >> accountId

        def accesslogs = new AccessLogs()
        accesslogs.bucket = "bucketList"

        def regionAcctId = BucketPolicyGenerator.REGION2ACCTID["us-east-1"]

        then:
        def policy = BucketPolicyGenerator.generatePolicy(accesslogs, sgAware)
        !policy.contains("null")
        policy.contains(accountId)
        policy.contains(regionAcctId)
        policy.contains(accesslogs.bucket)
        policy.contains(accesslogs.bucketPrefix)
    }
}
