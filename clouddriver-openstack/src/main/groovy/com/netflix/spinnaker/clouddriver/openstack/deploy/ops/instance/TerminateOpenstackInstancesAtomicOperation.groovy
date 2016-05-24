package com.netflix.spinnaker.clouddriver.openstack.deploy.ops.instance

import com.netflix.spinnaker.clouddriver.data.task.Task
import com.netflix.spinnaker.clouddriver.data.task.TaskRepository
import com.netflix.spinnaker.clouddriver.openstack.deploy.description.instance.OpenstackInstancesDescription
import com.netflix.spinnaker.clouddriver.orchestration.AtomicOperation
import groovy.util.logging.Slf4j

/**
 *
 */
@Slf4j
class TerminateOpenstackInstancesAtomicOperation implements AtomicOperation<Void> {

  private final String BASE_PHASE = "TERMINATE_INSTANCES"
  OpenstackInstancesDescription description

  TerminateOpenstackInstancesAtomicOperation(OpenstackInstancesDescription description) {
    this.description = description
  }

  protected static Task getTask() {
    TaskRepository.threadLocalTask.get()
  }

  /*
   * curl -X POST -H "Content-Type: application/json" -d '[ { "terminateInstances": { "instanceIds": ["os-test-v000-beef"], "account": "test" }} ]' localhost:7002/openstack/ops
   * curl -X GET -H "Accept: application/json" localhost:7002/task/1
   */
  @Override
  Void operate(List priorOutputs) {
    task.updateStatus BASE_PHASE, "Initializing terminate instances operation..."

    def credentials = description.credentials
    log.info(credentials.toString())

    description.instanceIds.each {
      log.info("Deleting $it")
      //TODO delete instance
      //TODO if it fails throw exception
      //throw new OpenstackOperationException("Failed to delete instance $it")
    }

    task.updateStatus BASE_PHASE, "Successfully terminated provided instances."
  }
}
