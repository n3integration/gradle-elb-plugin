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

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CreateBucketRequest
import com.amazonaws.services.s3.model.SetBucketPolicyRequest
import com.n3integration.gradle.elb.models.AccessLogs

/**
 *
 * @author n3integration
 */
trait S3Aware extends SecurityGroupAware {

    /**
     * Creates a new {@link AmazonS3Client} client
     *
     * @return an initialized {@link AmazonS3Client} instance
     */
    def AmazonS3Client createS3Client() {
         new AmazonS3Client(defaultCredentials())
    }

    /**
     * Creates an S3 bucket if it does not already exist and attaches
     * an elastic load balancing log policy
     *
     * @param client
     *          the {@link AmazonS3Client} instance
     * @param accessLogs
     *          the {@link AccessLogs} instance
     */
    def void createBucketIfNotExists(AmazonS3Client client, AccessLogs accessLogs) {
        if(!client.doesBucketExist(accessLogs.bucket)) {
            client.createBucket(new CreateBucketRequest(accessLogs.bucket))
            client.setBucketPolicy(new SetBucketPolicyRequest(
                accessLogs.bucket,
                BucketPolicyGenerator.generatePolicy(accessLogs, this)
            ))
        }
    }
}