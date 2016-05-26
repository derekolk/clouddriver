/*
 * Copyright 2016 Target, Inc.
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

package com.netflix.spinnaker.clouddriver.openstack.client

import org.openstack4j.api.OSClient

/**
 * Provides access to the Openstack API.
 */
abstract class OpenstackClientProvider {

  abstract OSClient getOSClient()

  //TODO test
  /**
   * Delete an instance.
   * @param instanceId
   * @return
   */
  def deleteInstance(String instanceId) {
    getOSClient().compute().servers().delete(instanceId)
  }

  def getServers() {
    getOSClient().compute().servers().list()
  }

  /**
   * Get a new token id.
   * @return
   */
  abstract String getTokenId()

  //TODO stuff common to v2 and v3

}
