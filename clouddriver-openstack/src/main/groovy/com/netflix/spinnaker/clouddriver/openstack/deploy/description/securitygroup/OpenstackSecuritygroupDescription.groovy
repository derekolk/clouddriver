/*
 * Copyright 2016 Target, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.clouddriver.openstack.deploy.description.securitygroup

import com.netflix.spinnaker.clouddriver.openstack.deploy.description.OpenstackAtomicOperationDescription

/**
 * Description for creating security groups with TCP rules.
 *
 * UDP and ICMP are not supported.
 *
 * Only CIDR remote types are supported.
 */
class OpenstackSecurityGroupDescription extends OpenstackAtomicOperationDescription {

  String name
  String description
  List<Rule> rules

  static class Rule {
    String ruleType = "TCP" //only support TCP for now, in openstack could also be UDP or ICMP
    int fromPort
    int toPort
    String cidr
  }


}