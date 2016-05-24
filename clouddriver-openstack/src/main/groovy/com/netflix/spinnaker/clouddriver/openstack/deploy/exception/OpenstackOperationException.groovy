/*
 * Copyright 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.clouddriver.openstack.deploy.exception

import groovy.transform.InheritConstructors

@InheritConstructors
class OpenstackOperationException extends RuntimeException {
  OpenstackOperationException(String operation, Exception e) {
    super("$operation failed: ${e.status?.message ?: e.message}".toString())
  }

  OpenstackOperationException(String account, String operation, Exception e) {
    super("$operation for account $account failed: ${e.status?.message ?: e.message}".toString())
  }
}
