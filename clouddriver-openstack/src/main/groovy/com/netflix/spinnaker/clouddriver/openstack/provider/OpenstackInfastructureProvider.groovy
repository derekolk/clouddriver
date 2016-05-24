package com.netflix.spinnaker.clouddriver.openstack.provider

import com.netflix.spinnaker.cats.agent.Agent
import com.netflix.spinnaker.cats.agent.AgentSchedulerAware
import com.netflix.spinnaker.clouddriver.cache.SearchableProvider
import com.netflix.spinnaker.clouddriver.openstack.OpenstackCloudProvider
import com.netflix.spinnaker.clouddriver.openstack.cache.Keys
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

@ConditionalOnProperty('openstack.enabled')
class OpenstackInfastructureProvider extends AgentSchedulerAware implements SearchableProvider  {
  public static final String PROVIDER_NAME = OpenstackInfastructureProvider.name

  private final Collection<Agent> agents

  OpenstackInfastructureProvider( Collection<Agent> agents) {
    this.agents = agents
  }

  //TODO - Need to define default caches
  final Set<String> defaultCaches = Collections.emptySet()
  //TODO - Need to define urlMappingTemplates
  final Map<String, String> urlMappingTemplates = Collections.emptyMap()
  //TODO - Need to define (if applicable)
  final Map<String, SearchableProvider.SearchResultHydrator> searchResultHydrators = Collections.emptyMap()

  @Override
  Map<String, String> parseKey(String key) {
    Keys.parse(key)
  }

  @Override
  String getProviderName() {
    PROVIDER_NAME
  }

  @Override
  Collection<Agent> getAgents() {
    agents
  }
}
