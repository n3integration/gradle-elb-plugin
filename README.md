# gradle-elb-plugin
[ ![Codeship Status for n3integration/gradle-elb-plugin](https://codeship.com/projects/3e7d9780-f556-0133-8394-4261b5a2c26f/status?branch=master)](https://codeship.com/projects/150348)  [ ![Download](https://api.bintray.com/packages/n3integration/maven/gradle-elb-plugin/images/download.svg) ](https://bintray.com/n3integration/maven/gradle-elb-plugin/_latestVersion)

This plugin for [Gradle](http://www.gradle.org) gives developers the ability to [setup](#create), [describe](#describe), and [teardown](#delete) [elastic load balancers](https://aws.amazon.com/elasticloadbalancing/).

#### Project Configuration
The following should be placed at the head of our `build.gradle` file to include the `gradle-elb-plugin` dependency into our Gradle project.
```gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath "com.n3integration:gradle-elb-plugin:1.0.0"
    }
}

apply plugin: "aws-elb"
```

#### Elastic Load Balancer Definition
Below, we demonstrate how to declare an elastic load balancer, named **my-elb**. The following configuration includes all available properties for the plugin for demonstration purposes and doesn't indicate that the values are necessarily required.


```gradle

elb {
    resources {
        "my-elb" {
            availabilityZones = ["us-east-1b", "us-east-1c"]

            crossZoneLoadBalancing = "true"
            idleTimeout = 400
            connectionDraining = "true "
            connectionDrainingTimeout = 500

            listener {
                instancePort = 3000
                instanceProtocol = "http"
                lbPort = 80
                lbProtocol = "http"
            }

            listener {
                instancePort = 3000
                instanceProtocol = "http"
                lbPort = 443
                lbProtocol = "https"
                sslCertificateId = "arn:aws:iam::123456789012:server-certificate/name"
            }

            healthCheck {
                healthyThreshold = 2
                unhealthyThreshold = 2
                timeout = 3
                target = "HTTP:3000/"
                interval = 30
            }

            accessLogs {
                bucket = "mybucket"
                bucketPrefix = "logs"
                interval = 60
            }

            tag {
                key = "function"
                value = "nodejs-cluster"
            }
        }
    }
}
```

#### Configuration
<dl>
<dt>listener</dt>
<dd>A listener describes a port forwarding rule on the load balancer. For SSL connections, a certificate registered with Amazon's <a href="https://aws.amazon.com/certificate-manager/">Certificate Manager</a> can be included in the listener's sslCertificateId.</dd>
<dt>healthCheck</dt>
<dd>A healthCheck sets up a rule to determine the health of an instance and how to manage unhealthy instances</dd>
<dt>accessLogs</dt>
<dd>The load balancer's access logs can be persisted to an S3 bucket for analysis. Refer to the following <a href="http://docs.aws.amazon.com/ElasticLoadBalancing/latest/DeveloperGuide/enable-access-logs.html">documentation</a> for more information on configuring the S3 bucket.</dd>
<dt>tag</dt>
<dd>A tag helps to identify and describe load balancers. Multiple tags can be declared per load balancer.</dd>
</dl>

####<a name="create"></a>Creating an Elastic Load Balancer
Using the above definition, we can create the elastic load balancer by executing the following command
```bash
gradle createLoadBalancers
```

####<a name="describe"></a>Describe an Elastic Load Balancer
To confirm that our load balancer was created, we can run the following command to view all of the load balancers defined in our Gradle build file.
```bash
gradle describeLoadBalancers
```

####<a name="delete"></a>Deleting an Elastic Load Balancer
When we need to teardown our environment, we can run the following command to remove all load balancers defined in our Gradle build file.
```bash
gradle deleteLoadBalancers
```
