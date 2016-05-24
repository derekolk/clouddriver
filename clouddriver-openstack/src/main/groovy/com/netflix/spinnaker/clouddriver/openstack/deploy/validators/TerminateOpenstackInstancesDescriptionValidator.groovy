package com.netflix.spinnaker.clouddriver.openstack.deploy.validators

import com.netflix.spinnaker.clouddriver.deploy.DescriptionValidator
import com.netflix.spinnaker.clouddriver.openstack.deploy.description.instance.OpenstackInstancesDescription
import com.netflix.spinnaker.clouddriver.security.AccountCredentialsProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.Errors

/**
 *
 */
class TerminateOpenstackInstancesDescriptionValidator extends DescriptionValidator<OpenstackInstancesDescription> {

  @Autowired
  AccountCredentialsProvider accountCredentialsProvider

  @Override
  void validate(List priorDescriptions, OpenstackInstancesDescription description, Errors errors) {
    //TODO validate credentials, account, instance ids, etc
  }

}
