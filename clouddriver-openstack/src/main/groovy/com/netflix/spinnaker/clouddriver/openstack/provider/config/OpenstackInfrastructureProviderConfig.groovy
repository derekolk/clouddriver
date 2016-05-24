package com.netflix.spinnaker.clouddriver.openstack.provider.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.collect.Sets
import com.netflix.spectator.api.Registry
import com.netflix.spinnaker.cats.agent.Agent
import com.netflix.spinnaker.cats.agent.CachingAgent
import com.netflix.spinnaker.cats.provider.ProviderSynchronizerTypeWrapper
import com.netflix.spinnaker.clouddriver.openstack.provider.OpenstackInfastructureProvider
import com.netflix.spinnaker.clouddriver.openstack.provider.agent.OpenstackInstanceCachingAgent
import com.netflix.spinnaker.clouddriver.openstack.security.OpenstackNamedAccountCredentials
import com.netflix.spinnaker.clouddriver.openstack.utils.OpenstackClientFactory
import com.netflix.spinnaker.clouddriver.security.AccountCredentialsRepository
import com.netflix.spinnaker.clouddriver.security.ProviderUtils
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Scope

import java.util.concurrent.ConcurrentHashMap

@Configuration
class OpenstackInfrastructureProviderConfig {
  @Bean
  @DependsOn('openstackNamedAccountCredentials')
  OpenstackInfastructureProvider openstackInfastructureProvider(AccountCredentialsRepository accountCredentialsRepository,
                                                      OpenstackClientFactory openstackClientFactory,
                                                      ObjectMapper objectMapper,
                                                      ApplicationContext ctx,
                                                      Registry registry) {
    def openstackInfastructureProvider =
      new OpenstackInfastructureProvider(Sets.newConcurrentHashSet())
      //Collections.newSetFromMap(new ConcurrentHashMap<Agent, Boolean>()))

    synchronizeOpenstackProvider(
      openstackInfastructureProvider,
      accountCredentialsRepository,
      openstackClientFactory,
      objectMapper,
      ctx,
      registry)

    openstackInfastructureProvider
  }

  @Bean
  OpenstackProviderSynchronizerTypeWrapper openstackProviderSynchronizerTypeWrapper() {
    new OpenstackProviderSynchronizerTypeWrapper()
  }

  class OpenstackProviderSynchronizerTypeWrapper implements ProviderSynchronizerTypeWrapper {
    @Override
    Class getSynchronizerType() {
      return OpenstackProviderSynchronizer
    }
  }

  class OpenstackProviderSynchronizer {}

  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  @Bean
  OpenstackProviderSynchronizer synchronizeOpenstackProvider(OpenstackInfastructureProvider openstackInfastructureProvider,
                                                                AccountCredentialsRepository accountCredentialsRepository,
                                                                OpenstackClientFactory openstackClientFactory,
                                                                ObjectMapper objectMapper,
                                                                ApplicationContext ctx,
                                                                Registry registry) {
    def scheduledAccounts = ProviderUtils.getScheduledAccounts(openstackInfastructureProvider)
    def allAccounts = ProviderUtils.buildThreadSafeSetOfAccounts(accountCredentialsRepository, OpenstackNamedAccountCredentials)

    List<CachingAgent> newlyAddedAgents = []

    allAccounts.each { OpenstackNamedAccountCredentials credentials ->
      if (!scheduledAccounts.contains(credentials.name)) {
        newlyAddedAgents << new OpenstackInstanceCachingAgent(credentials)
      }
    }

    if (!newlyAddedAgents.isEmpty()) {
      openstackInfastructureProvider.agents.addAll(newlyAddedAgents)
    }

    new OpenstackProviderSynchronizer()
  }

}
