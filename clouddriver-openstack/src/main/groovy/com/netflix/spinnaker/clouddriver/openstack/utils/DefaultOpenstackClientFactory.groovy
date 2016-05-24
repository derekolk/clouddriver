package com.netflix.spinnaker.clouddriver.openstack.utils

import com.netflix.spinnaker.clouddriver.openstack.security.OpenstackNamedAccountCredentials
import org.openstack4j.api.OSClient
import org.openstack4j.model.common.Identifier
import org.openstack4j.openstack.OSFactory


class DefaultOpenstackClientFactory implements OpenstackClientFactory {

  @Override
  OSClient createOpenstackClient(OpenstackNamedAccountCredentials credentials) {
    return OSFactory.builderV3()
      .endpoint(credentials.endpoint)
      .credentials(credentials.username, credentials.password)
      .scopeToProject(Identifier.byName(credentials.tenantId))
      .authenticate();
  }
}
