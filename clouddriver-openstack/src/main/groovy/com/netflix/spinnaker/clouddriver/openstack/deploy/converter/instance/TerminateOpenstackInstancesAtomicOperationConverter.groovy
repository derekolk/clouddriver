package com.netflix.spinnaker.clouddriver.openstack.deploy.converter.instance

import com.netflix.spinnaker.clouddriver.openstack.OpenstackOperation
import com.netflix.spinnaker.clouddriver.openstack.deploy.description.instance.OpenstackInstancesDescription
import com.netflix.spinnaker.clouddriver.openstack.deploy.ops.instance.TerminateOpenstackInstancesAtomicOperation
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperation
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperations
import com.netflix.spinnaker.clouddriver.security.AbstractAtomicOperationsCredentialsSupport
import org.springframework.stereotype.Component

@OpenstackOperation(AtomicOperations.TERMINATE_INSTANCES)
@Component("terminateInstancesDescription")
class TerminateOpenstackInstancesAtomicOperationConverter extends AbstractAtomicOperationsCredentialsSupport {
  @Override
  AtomicOperation convertOperation(Map input) {
    new TerminateOpenstackInstancesAtomicOperation(convertDescription(input))
  }

  @Override
  OpenstackInstancesDescription convertDescription(Map input) {
    def converted = objectMapper.convertValue(input, OpenstackInstancesDescription)
    converted.credentials = getCredentialsObject(input.credentials as String)
    converted
  }
}
