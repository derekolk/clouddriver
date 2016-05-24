package com.netflix.spinnaker.clouddriver.openstack.client

import org.openstack4j.api.OSClient

/**
 *
 */
class OpenstackClientV3Provider extends OpenstackClientProvider {

  OpenstackClientV3Provider(OSClient client) {
    super(client)
  }

  //TODO v3 specific operations

}
