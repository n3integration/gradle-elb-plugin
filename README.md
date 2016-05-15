# gradle-elb-plugin
[ ![Codeship Status for n3integration/gradle-elb-plugin](https://codeship.com/projects/3e7d9780-f556-0133-8394-4261b5a2c26f/status?branch=master)](https://codeship.com/projects/150348)  [ ![Download](https://api.bintray.com/packages/n3integration/maven/gradle-elb-plugin/images/download.svg) ](https://bintray.com/n3integration/maven/gradle-elb-plugin/_latestVersion)

This plugin for [Gradle](http://www.gradle.org) gives developers the ability to [setup](#create), [describe](#describe), and [teardown](#delete) [elastic load balancers](https://aws.amazon.com/elasticloadbalancing/).

- [Project Configuration](#project-configuration)
- [Create an Elastic Load Balancer](#create-an-elastic-load-balancer)
  - [Configuration](#configuration)
- [Describe an Elastic Load Balancer](#describe-an-elastic-load-balancer)
	- [Sample Output](#sample-output)
- [Delete an Elastic Load Balancer](#delete-an-elastic-load-balancer)

#### Project Configuration
The following should be placed at the head of our `build.gradle` file to include the `gradle-elb-plugin` dependency into our Gradle project.
```gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath "com.n3integration:gradle-elb-plugin:1.1.0"
    }
}

apply plugin: "aws-elb"
```

#### Create an Elastic Load Balancer
An elastic load balancer must be defined within a `loadBalancer` block within a `CreateLoadBalancer` task.

##### Configuration
<dl>
<dt>listener</dt>
<dd>A listener describes a port forwarding rule on the load balancer. For SSL connections, a certificate registered with Amazon's <a href="https://aws.amazon.com/certificate-manager/">Certificate Manager</a> can be included in the listener's sslCertificateId.</dd>
<dt>healthCheck</dt>
<dd>A healthCheck sets up a rule to determine the health of an instance and how to determine the health of service(s) running on the ec2 instances</dd>
<dt>accessLogs</dt>
<dd>The load balancer's access logs can be persisted to an S3 bucket for analysis. If the S3 bucket does not already exist, it will be created and the appropriate policy will be assigned to the bucket. Refer to the following <a href="http://docs.aws.amazon.com/ElasticLoadBalancing/latest/DeveloperGuide/enable-access-logs.html">documentation</a> for more information on manually configuring the S3 bucket.</dd>
<dt>tag</dt>
<dd>A tag helps to identify and describe load balancers. Multiple tags can be associated with a load balancer.</dd>
</dl>

```gradle
task createLoadBalancer(type: CreateLoadBalancer) {
    loadBalancer {
        name = "default-elb"
        availabilityZones = ["us-east-1a", "us-east-1c"]

        crossZoneLoadBalancing = "true"
        idleTimeout = 400
        connectionDraining = "true "
        connectionDrainingTimeout = 500

        listener {
            instancePort = 9000
            instanceProtocol = "http"
            lbPort = 80
            lbProtocol = "http"
        }

        listener {
            instancePort = 9000
            instanceProtocol = "http"
            lbPort = 443
            lbProtocol = "https"
            sslCertificateId = System.getenv("CERTIFICATE_ID")
        }

        healthCheck {
            healthyThreshold = 2
            unhealthyThreshold = 2
            timeout = 3
            target = "HTTP:9000/"
            interval = 30
        }

        accessLogs {
            bucket = System.getenv("BUCKET_NAME") ?: project.group
            bucketPrefix = "play-logs"
            interval = 60
            region = "us-east-1"
        }

        tag {
            key = "function"
            value = "play server farm"
        }
    }
}
```

#### Describe an Elastic Load Balancer
To confirm that our load balancer was created, we can create a task to describe our load balancer. A `loadBalancerName` is required for `DescribeLoadBalancer` tasks.

```gradle
task describeLoadBalancer(type: DescribeLoadBalancer) {
  loadBalancerName = "default-elb"
}
```

##### Sample Output

```bash
:describeLoadBalancer
Fetching information about elastic load balancer(s)...

  load balancer: default-elb
      instances: 2
        created: Sun May 15 17:52:00 EDT 2016
       dns name: default-elb-288261717.us-east-1.elb.amazonaws.com
         scheme: internet-facing
       listener: 80 (HTTP) -> 9000 (HTTP)
       listener: 443 (HTTPS) -> 9000 (HTTP)

BUILD SUCCESSFUL
```

#### Delete an Elastic Load Balancer
When we need to teardown our environment, we can run the following command to remove all load balancers defined in our Gradle build file.

```gradle
task deleteLoadBalancer(type: DeleteLoadBalancer) {
   loadBalancerName = "default-elb"
}
```
