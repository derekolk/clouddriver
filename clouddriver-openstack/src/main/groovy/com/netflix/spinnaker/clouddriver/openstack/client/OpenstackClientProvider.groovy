package com.netflix.spinnaker.clouddriver.openstack.client

import org.openstack4j.api.OSClient

/**
 *
 */
abstract class OpenstackClientProvider {

  OSClient client

  OpenstackClientProvider(OSClient client) {
    this.client = client
  }

  //TODO test
  def deleteInstance(String instanceId) {
    client.compute().servers().delete(instanceId)
  }

  //TODO stuff common to v2 and v3

}
