package com.netflix.spinnaker.clouddriver.openstack.client

import org.openstack4j.api.OSClient

/**
 *
 */
class OpenstackClientV2Provider extends OpenstackClientProvider {

  OpenstackClientV2Provider(OSClient client) {
    super(client)
  }

  //TODO v2 specific operations

}
