/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.spinnaker.clouddriver.aws.deploy.ops
import com.amazonaws.services.autoscaling.model.AutoScalingGroup
import com.amazonaws.services.autoscaling.model.Instance
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest
import com.netflix.spinnaker.clouddriver.aws.TestCredential
import com.netflix.spinnaker.clouddriver.aws.deploy.description.EnableDisableAsgDescription
import com.netflix.spinnaker.clouddriver.aws.model.AutoScalingProcessType
import com.netflix.spinnaker.clouddriver.data.task.DefaultTaskStatus
import com.netflix.spinnaker.clouddriver.data.task.TaskState

class EnableAsgAtomicOperationUnitSpec extends EnableDisableAtomicOperationUnitSpecSupport {

  def setupSpec() {
    def cred = TestCredential.named('test', [discovery: 'http://{{region}}.discovery.netflix.net'])
    description.credentials = cred
    op = new EnableAsgAtomicOperation(description)

  }

  void 'should register instances from load balancers and resume scaling processes'() {
    setup:
    def asg = Mock(AutoScalingGroup)
    asg.getAutoScalingGroupName() >> "asg1"
    asg.getLoadBalancerNames() >> ["lb1"]
    asg.getInstances() >> [new Instance().withInstanceId("i1")]

    when:
    op.operate([])

    then:
    1 * asgService.getAutoScalingGroup(_) >> asg
    1 * asgService.resumeProcesses(_, AutoScalingProcessType.getDisableProcesses())
    1 * loadBalancing.registerInstancesWithLoadBalancer(_) >> { RegisterInstancesWithLoadBalancerRequest req ->
      assert req.instances[0].instanceId == "i1"
      assert req.loadBalancerName == "lb1"
    }
  }

  void 'should enable instances for asg in discovery'() {
    setup:
    def asg = Mock(AutoScalingGroup)
    asg.getAutoScalingGroupName() >> "asg1"
    asg.getInstances() >> [new Instance().withInstanceId("i1")]

    when:
    op.operate([])

    then:
    2 * task.getStatus() >> new DefaultTaskStatus(state: TaskState.STARTED)
    1 * asgService.getAutoScalingGroup(_) >> asg
    1 * eureka.getInstanceInfo('i1') >>
        [
            instance: [
                app: "asg1"
            ]
        ]
    1 * eureka.updateInstanceStatus('asg1', 'i1', 'UP')
  }

  void 'should skip discovery if not enabled for account'() {
    setup:
    def noDiscovery = new EnableDisableAsgDescription([
      asgs: [[
        serverGroupName: "kato-main-v000",
        region         : "us-west-1"
      ]],
      credentials: TestCredential.named('foo')
    ])

    def noDiscoveryOp = new EnableAsgAtomicOperation(noDiscovery)
    wireOpMocks(noDiscoveryOp)

    def asg = Mock(AutoScalingGroup)
    asg.getAutoScalingGroupName() >> "asg1"
    asg.getInstances() >> [new Instance().withInstanceId("i1")]

    when:
    noDiscoveryOp.operate([])

    then:
    1 * asgService.getAutoScalingGroup(_) >> asg
    0 * eureka.updateInstanceStatus(*_)
  }

}
