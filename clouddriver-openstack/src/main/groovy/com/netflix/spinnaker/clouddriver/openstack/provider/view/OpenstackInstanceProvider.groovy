package com.netflix.spinnaker.clouddriver.openstack.provider.view

import com.netflix.spinnaker.cats.cache.Cache
import com.netflix.spinnaker.cats.cache.CacheData
import com.netflix.spinnaker.clouddriver.model.InstanceProvider
import com.netflix.spinnaker.clouddriver.openstack.cache.Keys
import com.netflix.spinnaker.clouddriver.openstack.model.OpenstackInstance
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static com.netflix.spinnaker.clouddriver.openstack.cache.Keys.Namespace.INSTANCES

@Component
@Slf4j
class OpenstackInstanceProvider implements InstanceProvider<OpenstackInstance> {

  private final Cache cacheView

  @Autowired
  OpenstackInstanceProvider(Cache cacheView) {
    this.cacheView = cacheView
  }

  @Override
  String getPlatform() {
    return null
  }

  @Override
  OpenstackInstance getInstance(String account, String region, String id) {
    CacheData instanceEntry = cacheView.get(INSTANCES.ns, Keys.getInstanceKey(id, account, region))
    if (!instanceEntry) {
      return null
    }

    return new OpenstackInstance(name: instanceEntry.attributes.name.toString(), zone: instanceEntry.attributes.zone)
  }

  @Override
  String getConsoleOutput(String account, String region, String id) {
    return null
  }
}
