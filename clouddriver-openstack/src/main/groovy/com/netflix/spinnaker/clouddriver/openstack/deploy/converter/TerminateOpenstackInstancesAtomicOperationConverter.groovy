package com.netflix.spinnaker.clouddriver.openstack.deploy.converter

import com.netflix.spinnaker.clouddriver.openstack.OpenstackOperation
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperation
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperations
import com.netflix.spinnaker.clouddriver.security.AbstractAtomicOperationsCredentialsSupport
import org.springframework.stereotype.Component

@OpenstackOperation(AtomicOperations.TERMINATE_INSTANCES)
@Component("terminateInstancesDescription")
class TerminateOpenstackInstancesAtomicOperationConverter extends AbstractAtomicOperationsCredentialsSupport {
  @Override
  AtomicOperation convertOperation(Map input) {
    new TerminateInstancesAtomicOperation(convertDescription(input))
  }

  @Override
  TerminateInstancesDescription convertDescription(Map input) {
    def converted = objectMapper.convertValue(input, TerminateInstancesDescription)
    converted.credentials = getCredentialsObject(input.credentials as String)
    converted
  }
}
