package io.camunda.actorfilter.solvecandidategroup;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.camunda.zeebe.client.api.worker.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class SolveCandidateGroup implements JobHandler {
  Logger logger = LoggerFactory.getLogger(SolveCandidateGroup.class.getName());

  Random random = new Random();

  public static JobWorker registerWorker(ZeebeClient zeebeClient) {
    return zeebeClient.newWorker().jobType("actorfilter-solvecandidategroup").handler(new SolveCandidateGroup()).open();

  }

  public void handle(JobClient jobClient, ActivatedJob job) throws Exception {
    Object candidateGroups = job.getVariablesAsMap().get("candidateGroups");

    String firstCandidateGroup="";
    if (candidateGroups instanceof List candidateList) {
      firstCandidateGroup= candidateList.size()>0? candidateList.get(0).toString():"";
    }
    else if (candidateGroups instanceof String candidateGroup) {
      firstCandidateGroup=candidateGroup;
    }
    Map<String, Object> variables = new HashMap<String, Object>();
    if ("quality-check".equals(firstCandidateGroup))
      variables.put("candidateUsers", List.of("demo", "walter.bates"));
    else if ("production-check".equals(firstCandidateGroup))
      variables.put("candidateUsers", List.of("helen.kelly", "walter.bates"));

    logger.info("ActorFilter - candidateGroup[{}] listOfusers[{}]", firstCandidateGroup, variables.get("candidateUsers"));
    jobClient.newCompleteCommand(job.getKey()).variables(variables).send();

  }

}
