package com.netflix.spinnaker.clouddriver.openstack.utils

import com.netflix.spinnaker.clouddriver.openstack.security.OpenstackNamedAccountCredentials
import org.openstack4j.api.OSClient

interface OpenstackClientFactory {
  OSClient createOpenstackClient(OpenstackNamedAccountCredentials credentials)
}
