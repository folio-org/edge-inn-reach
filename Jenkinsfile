buildMvn {
  publishModDescriptor = true
  mvnDeploy = true
  doKubeDeploy = true
  doUploadApidocs = true
  buildNode = 'jenkins-agent-java11'

  doApiLint = true
  apiTypes = 'OAS'
  apiDirectories = 'src/main/resources/swagger.api'

  doDocker = {
    buildDocker {
      publishMaster = true
      healthChk =true
      healthChkCmd = 'curl -sS --fail -o /dev/null  http://localhost:8081/apidocs/ || exit 1'
    }
  }
}
