def call(String... containers = [bash]) {
    def podAgentYaml = getHeader()

    containers.each { containerName ->
        switch (containerName){
            case 'docker':
                podAgentYaml += getDocker()
                break
            case 'k8s':
                podAgentYaml += getK8s()
                break
            case 'gradle':
                podAgentYaml += getGradle()
                break
            case 'bash':
            default:
                podAgentYaml += getBash()
        }
    }
    podAgentYaml += getResourceLimits()

    return podAgentYaml
}

def getHeader() {
    '''
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
    '''
}

def getDocker() {
    '''    
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
    '''
}

def getK8s() {
    '''    
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
    '''
}

def getGradle() {
    '''    
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
    '''
}

def getBash() {
    '''    
    - name: busybox
      image: busybox
      command:
        - cat
      tty: true
    '''
}

def getResourceLimits() {
    '''      
      resources:
        limits:
          #cpu: 2000m
          #memory: 2G
        requests:
          #cpu: 2000m
          #memory: 2G
    '''
}