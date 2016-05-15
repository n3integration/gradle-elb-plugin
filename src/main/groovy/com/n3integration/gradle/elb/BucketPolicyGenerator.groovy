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
import groovy.text.SimpleTemplateEngine

/**
 * Automates the process of setting up access logging for an elastic load balancer
 *
 * @author n3integration
 * @see http://docs.aws.amazon.com/ElasticLoadBalancing/latest/DeveloperGuide/enable-access-logs.html
 */
class BucketPolicyGenerator {
    static final Map<String, String> REGION2ACCTID =
        ["us-east-1"        : "127311923021",
         "us-west-2"        : "797873946194",
         "us-west-1"        : "027434742980",
         "eu-west-1"        : "156460612806",
         "eu-central-1"     : "054676820928",
         "ap-southeast-1"   : "114774131450",
         "ap-northeast-1"   : "582318560864",
         "ap-southeast-2"   : "783225319266",
         "ap-northeast-2"   : "600734575887",
         "sa-east-1"        : "507241528517",
         "us-gov-west-1"    : "048591011584",
         "cn-north-1"       : "638102146993"]

    /**
     * Generates a valid S3 bucket policy for an elastic load balancer
     *
     * @param accessLogs
     *          the {@link AccessLog} specification
     * @param sgAware
     *          the {@link SecurityGroupAware} instance
     * @return the generated s3 bucket policy
     */
    static String generatePolicy(AccessLogs accessLogs, SecurityGroupAware sgAware) {
        def now = new Date()
        def binding = [
            policyId:       "Policy${now.getTime()}",
            statementId:    "Stmt${now.getTime()}",
            acctId:         sgAware.getAccountId(),
            bucket:         accessLogs.bucket,
            prefix:         accessLogs.bucketPrefix,
            regionAcctId:   REGION2ACCTID[accessLogs.region]]
        def engine = new SimpleTemplateEngine()
        def template = engine.createTemplate(loadTemplateData()).make(binding)
        template.toString()
    }

    private static String loadTemplateData() {
        BucketPolicyGenerator.class.getResource("/policy.tmpl").text
    }
}
