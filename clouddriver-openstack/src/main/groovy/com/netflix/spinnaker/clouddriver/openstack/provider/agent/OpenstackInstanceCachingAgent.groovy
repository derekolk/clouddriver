/*
 * Copyright 2015 Netflix, Inc.
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

package com.netflix.spinnaker.clouddriver.openstack.provider.agent

import com.netflix.spinnaker.cats.agent.*
import com.netflix.spinnaker.cats.cache.CacheData
import com.netflix.spinnaker.cats.cache.DefaultCacheData
import com.netflix.spinnaker.cats.provider.ProviderCache
import com.netflix.spinnaker.clouddriver.openstack.cache.Keys
import com.netflix.spinnaker.clouddriver.openstack.provider.OpenstackInfastructureProvider
import com.netflix.spinnaker.clouddriver.openstack.provider.view.MutableCacheData
import com.netflix.spinnaker.clouddriver.openstack.security.OpenstackNamedAccountCredentials
import groovy.util.logging.Slf4j
import org.openstack4j.api.OSClient
import org.openstack4j.model.compute.Server
import org.openstack4j.model.identity.v3.Region
import org.openstack4j.openstack.OSFactory

import static com.netflix.spinnaker.cats.agent.AgentDataType.Authority.AUTHORITATIVE

@Slf4j
class OpenstackInstanceCachingAgent implements CachingAgent, AccountAware {

  final OpenstackNamedAccountCredentials account

  static final Set<AgentDataType> types = Collections.unmodifiableSet([
    AUTHORITATIVE.forType(Keys.Namespace.INSTANCES.ns)
  ] as Set)


  OpenstackInstanceCachingAgent(OpenstackNamedAccountCredentials account) {
    this.account = account
  }

  @Override
  String getProviderName() {
    OpenstackInfastructureProvider.PROVIDER_NAME
  }

  @Override
  String getAgentType() {
    "${account.name}/${OpenstackInstanceCachingAgent.simpleName}"
  }

  @Override
  String getAccountName() {
    account.name
  }

  @Override
  Collection<AgentDataType> getProvidedDataTypes() {
    return types
  }

  @Override
  CacheResult loadData(ProviderCache providerCache) {
    log.info("Describing items in ${agentType}")

    Map<String, MutableCacheData> cachedInstances = MutableCacheData.mutableCacheMap()

    ['test'].each { String region ->
      log.info("Region: ${region}")
      List<Server> servers = this.account.credentials.provider.getServers()

      servers?.each { Server server ->
        String name = server.name
        log.info("Server: " + name)
        String key = Keys.getInstanceKey(name, accountName, region)
        cachedInstances[key].with {
          attributes.name = name
          attributes.zone = server.availabilityZone
          attributes.imageName = server.flavor.name
          attributes.launchedDate = server.launchedAt
        }
      }
    }

    log.info("Caching ${cachedInstances.size()} items in ${agentType}")
    new DefaultCacheResult([(Keys.Namespace.INSTANCES.ns): cachedInstances.values()])
  }
}
