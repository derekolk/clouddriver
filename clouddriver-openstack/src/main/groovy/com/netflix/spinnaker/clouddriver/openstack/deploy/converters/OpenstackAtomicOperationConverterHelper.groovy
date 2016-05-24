package com.netflix.spinnaker.clouddriver.openstack.deploy.converters

import com.fasterxml.jackson.databind.DeserializationFeature
import com.netflix.spinnaker.clouddriver.openstack.deploy.description.instance.OpenstackInstancesDescription
import com.netflix.spinnaker.clouddriver.openstack.security.OpenstackCredentials
import com.netflix.spinnaker.clouddriver.security.AbstractAtomicOperationsCredentialsSupport

class OpenstackAtomicOperationConverterHelper {

  static OpenstackInstancesDescription convertDescription(Map input,
                                                          AbstractAtomicOperationsCredentialsSupport credentialsSupport,
                                                          Class targetDescriptionType) {

    String account = input.account

    OpenstackInstancesDescription converted = (OpenstackInstancesDescription) credentialsSupport.objectMapper
      .copy()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      .convertValue(input, targetDescriptionType)

    converted.credentials = (OpenstackCredentials) credentialsSupport.getCredentialsObject(account)?.getCredentials()

    converted
  }
}
