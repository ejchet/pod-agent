package groovy

import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Test
import static junit.framework.Assert.assertEquals

class PodAgentTest extends BasePipelineTest {
    def podAgent

    def podAgentYaml = """
apiVersion: v1
kind: Pod
metadata:
  labels:
    app: jenkins-pod-agent
    env: infra
spec:
  volumes:
    - name: docker-socket
      emptyDir: { }
  containers:
        
    - name: docker
      image: docker:19.03.1
      command:
        - sleep
      args:
        - 99d
      volumeMounts:
        - name: docker-socket
          mountPath: /var/run
    - name: docker-daemon
      image: docker:19.03.1-dind
      securityContext:
        privileged: true
      volumeMounts:
        - name: docker-socket
          mountPath: /var/run
        
    - name: k8s
      image: ejchet/k8s:1.22.15
      command:
        - sleep
      args:
        - 99d
      tty: true
      volumeMounts:
        - name: docker-socket
          mountPath: /var/run
        
    - name: gradle
      image: gradle:alpine
      command:
        - sleep
      args:
        - 99d
      tty: true
      volumeMounts:
        - name: docker-socket
          mountPath: /var/run
          
      resources:
        limits:
          #cpu: 2000m
          #memory: 2G
        requests:
          #cpu: 2000m
          #memory: 2G
    """

    @Before
    void setUp() {
        super.setUp()
        podAgent = loadScript("vars/podAgent.groovy")
    }

    @Test
    void testPodAgent() {

        def generatedYaml = podAgent('docker', 'k8s', 'gradle')
        //println generatedYaml
        assertEquals "results", generatedYaml.trim().equals(podAgentYaml.trim()), true
    }

}