package com.netflix.spinnaker.clouddriver.openstack.client

import com.netflix.spinnaker.clouddriver.openstack.security.OpenstackNamedAccountCredentials
import org.openstack4j.api.OSClient
import org.openstack4j.model.common.Identifier
import org.openstack4j.openstack.OSFactory

/**
 *
 */
class OpenstackProviderFactory {

  static OpenstackClientProvider createProvider(OpenstackNamedAccountCredentials credentials) {
    OSClient osClient
    OpenstackClientProvider provider
    if (AccountType.V2.value() == credentials.accountType) {
      osClient = OSFactory.builderV2()
        .endpoint(credentials.endpoint)
        .credentials(credentials.username, credentials.password)
        .tenantId(credentials.tenantName)
        .authenticate()
      provider = new OpenstackClientV2Provider(osClient)
    } else {
      osClient = OSFactory.builderV3()
        .endpoint(credentials.endpoint)
        .credentials(credentials.username, credentials.password, Identifier.byName(credentials.domainName))
        .scopeToProject(Identifier.byName(credentials.tenantName))
        .authenticate()
      provider = new OpenstackClientV3Provider(osClient)
    }
    provider
  }

  static enum AccountType {

    V2, V3

    String value() {
      return toString().toLowerCase()
    }
  }

}
