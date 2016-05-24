package com.netflix.spinnaker.clouddriver.openstack.client

import org.openstack4j.api.OSClient

/**
 * TODO we will want v2 and v3 providers to extend from this with identity specific operations.
 */
class OpenstackClientProvider {

  OSClient client

  OpenstackClientProvider(OSClient client) {
    this.client = client
  }

//  def deleteInstance(String instanceId) {
//    client.compute().servers().delete(instanceId)
//  }


}
