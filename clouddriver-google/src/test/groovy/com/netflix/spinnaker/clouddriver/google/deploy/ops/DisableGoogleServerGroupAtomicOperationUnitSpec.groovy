/*
 * Copyright 2014 Google, Inc.
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

package com.netflix.spinnaker.clouddriver.google.deploy.ops

import com.google.api.services.compute.Compute
import com.google.api.services.compute.model.InstanceGroupManager
import com.google.api.services.compute.model.InstanceGroupManagersSetTargetPoolsRequest
import com.google.api.services.compute.model.TargetPool
import com.google.api.services.compute.model.Zone
import com.netflix.spinnaker.clouddriver.data.task.Task
import com.netflix.spinnaker.clouddriver.data.task.TaskRepository
import com.netflix.spinnaker.clouddriver.google.deploy.description.EnableDisableGoogleServerGroupDescription
import com.netflix.spinnaker.clouddriver.google.model.GoogleServerGroup
import com.netflix.spinnaker.clouddriver.google.provider.view.GoogleClusterProvider
import com.netflix.spinnaker.clouddriver.google.security.GoogleCredentials
import spock.lang.Specification
import spock.lang.Subject

class DisableGoogleServerGroupAtomicOperationUnitSpec extends Specification {
  private static final ACCOUNT_NAME = "auto"
  private static final PROJECT_NAME = "my_project"
  private static final SERVER_GROUP_NAME = "mjdapp-dev-v009"
  private static final TARGET_POOL_NAME_1 = "testlb-target-pool-1417967954401";
  private static final TARGET_POOL_NAME_2 = "testlb2-target-pool-1417963107058";
  private static final TARGET_POOL_URL_1 =
          "https://www.googleapis.com/compute/v1/projects/shared-spinnaker/regions/us-central1/targetPools/$TARGET_POOL_NAME_1"
  private static final TARGET_POOL_URL_2 =
          "https://www.googleapis.com/compute/v1/projects/shared-spinnaker/regions/us-central1/targetPools/$TARGET_POOL_NAME_2"
  private static final TARGET_POOL_URLS = [TARGET_POOL_URL_1, TARGET_POOL_URL_2]
  private static final INSTANCE_URL_1 =
          "https://www.googleapis.com/compute/v1/projects/shared-spinnaker/zones/us-central1-a/instances/mjdapp-dev-v009-hnyp"
  private static final INSTANCE_URL_2 =
          "https://www.googleapis.com/compute/v1/projects/shared-spinnaker/zones/us-central1-a/instances/mjdapp-dev-v009-qtow"
  private static final ZONE = "us-central1-b"
  private static final REGION = "us-central1"

  def googleClusterProviderMock
  def serverGroup
  def computeMock
  def instanceGroupManagersMock
  def instanceGroupManagersGetMock
  def targetPoolsMock
  def targetPoolsGetMock
  def targetPoolsRemoveInstance
  def targetPool
  def instanceGroupManagersSetTargetPoolsMock

  def instanceGroupManager
  def items
  def credentials
  def description

  def setupSpec() {
    TaskRepository.threadLocalTask.set(Mock(Task))
  }

  def setup() {
    googleClusterProviderMock = Mock(GoogleClusterProvider)
    serverGroup = new GoogleServerGroup(zone: ZONE).view
    computeMock = Mock(Compute)
    credentials = new GoogleCredentials(PROJECT_NAME, computeMock)

    instanceGroupManagersMock = Mock(Compute.InstanceGroupManagers)
    instanceGroupManagersGetMock = Mock(Compute.InstanceGroupManagers.Get)
    instanceGroupManager = new InstanceGroupManager(targetPools: TARGET_POOL_URLS)

    targetPoolsMock = Mock(Compute.TargetPools)
    targetPoolsGetMock = Mock(Compute.TargetPools.Get)
    targetPoolsRemoveInstance = Mock(Compute.TargetPools.RemoveInstance)
    targetPool = new TargetPool(instances: [INSTANCE_URL_1, INSTANCE_URL_2])

    instanceGroupManagersSetTargetPoolsMock = Mock(Compute.InstanceGroupManagers.SetTargetPools)

    description = new EnableDisableGoogleServerGroupDescription(serverGroupName: SERVER_GROUP_NAME,
                                                                region: REGION,
                                                                accountName: ACCOUNT_NAME,
                                                                credentials: credentials)
  }

  void "should remove instances and detach load balancers"() {
    setup:
      @Subject def operation = new DisableGoogleServerGroupAtomicOperation(description)
      operation.googleClusterProvider = googleClusterProviderMock

    when:
      operation.operate([])

    then:
      1 * googleClusterProviderMock.getServerGroup(ACCOUNT_NAME, REGION, SERVER_GROUP_NAME) >> serverGroup

      1 * computeMock.instanceGroupManagers() >> instanceGroupManagersMock
      1 * instanceGroupManagersMock.get(PROJECT_NAME, ZONE, SERVER_GROUP_NAME) >> instanceGroupManagersGetMock
      1 * instanceGroupManagersGetMock.execute() >> instanceGroupManager

      [TARGET_POOL_NAME_1, TARGET_POOL_NAME_2].each { targetPoolLocalName ->
        1 * computeMock.targetPools() >> targetPoolsMock
        1 * targetPoolsMock.get(PROJECT_NAME, REGION, targetPoolLocalName) >> targetPoolsGetMock
        1 * targetPoolsGetMock.execute() >> targetPool
      }

      [TARGET_POOL_NAME_1, TARGET_POOL_NAME_2].each { targetPoolLocalName ->
        1 * computeMock.targetPools() >> targetPoolsMock
        1 * targetPoolsMock.removeInstance(PROJECT_NAME, REGION, targetPoolLocalName, _) >> targetPoolsRemoveInstance
        1 * targetPoolsRemoveInstance.execute()
      }

      1 * computeMock.instanceGroupManagers() >> instanceGroupManagersMock
      1 * instanceGroupManagersMock.setTargetPools(
          PROJECT_NAME,
          ZONE,
          SERVER_GROUP_NAME,
          new InstanceGroupManagersSetTargetPoolsRequest(targetPools: [])) >> instanceGroupManagersSetTargetPoolsMock
      1 * instanceGroupManagersSetTargetPoolsMock.execute()
  }
}
