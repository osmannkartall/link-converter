# Deploying Link Converter Application in a Kubernetes Environment

**Prerequisites**

- Install [Docker](https://docs.docker.com/desktop/)
- Install [minikube](https://minikube.sigs.k8s.io/docs/start/)
- Install [Helm](https://helm.sh/docs/intro/install/)

## 1. Starting the Cluster

**1.1. Start Docker**

**1.2. Start the Kubernetes cluster**

```bash
minikube start --cpus 2 --memory 6144
```

See [recommended settings to run a Couchbase cluster](https://docs.couchbase.com/operator/current/howto-couchbase-create.html#preparing-the-couchbase-cluster-configuration).

**1.3. Enable the metrics server**

```bash
minikube addons enable metrics-server
```

**1.4. Export the environment variables**

```bash
export SPRING_COUCHBASE_CONNECTION_STRING="couchbase://couchbase-db"
export SPRING_COUCHBASE_USERNAME="Administrator"
export SPRING_COUCHBASE_PASSWORD="password"
export SPRING_COUCHBASE_BUCKET_NAME="link-conversion"
export COUCHBASE_ADMINISTRATOR_USERNAME="Administrator"
export COUCHBASE_ADMINISTRATOR_PASSWORD="password"
export SPRING_PROFILES_ACTIVE="couchbase"
```

## 2. Creating Couchbase Deployment

**2.1. Add the Couchbase Helm Chart repository to Helm:**

```bash
helm repo add couchbase https://couchbase-partners.github.io/helm-charts/
```

**2.2. Update the repository index**

```bash
helm repo update
```

**2.3. Install the Couchbase Operator**

```bash
helm upgrade --install default couchbase/couchbase-operator
```

**Verifying the deployment**

Ensure that the `admission controller` and `autonomous operator` are deployed, up and running.

```bash
kubectl get deployments
```

The output should look like:

```text
NAME                                     READY   UP-TO-DATE   AVAILABLE   AGE
default-couchbase-admission-controller   1/1     1            1           3m28s
default-couchbase-operator               1/1     1            1           3m28s
```

**2.4. Apply the Couchbase deployment**

```bash
kubectl apply -f k8s/couchbase-cluster.yaml
```

**Verifying the deployment**

**Note:** It may take a few minutes.

```bash
kubectl get pods
```

The output should look like:

```text
NAME                                                      READY   STATUS    RESTARTS   AGE
couchbase-db-0000                                         1/1     Running   0          2m20s
couchbase-db-0001                                         1/1     Running   0          26s
couchbase-db-0002                                         1/1     Running   0          26s
default-couchbase-admission-controller-59cdf4d9ff-tdk2f   1/1     Running   0          4m39s
default-couchbase-operator-6cb589d5b4-lcmnq               1/1     Running   0          4m39s
```

## 3. Creating Spring Boot Deployment with a Load Balancer and Horizontal Pod Autoscaler

**3.1. Remove the existing build**

```bash
rm -rf build
```

**3.2. Build the jar**

```bash
# Exclude integration tests for quicker build
./gradlew clean build -x integrationTest
```

**3.3. Build the image inside minikube**

```bash
eval $(minikube docker-env)
```

```bash
docker build -t link-converter-app .
```

**3.4. Create a secret for the password**

```bash
kubectl create secret generic couchbase-secret --from-literal=SPRING_COUCHBASE_PASSWORD=$SPRING_COUCHBASE_PASSWORD
```

**3.5. Apply the Spring Boot deployment with a load balancer and horizontal pod autoscaler**

```bash
kubectl apply -f k8s/web-app.yaml
```

## Using the App

**Open NodePort access**

```bash
minikube service link-converter-app-service --url
```

The output should contain a URL like:

```text
http://127.0.0.1:58052
```

**Use the given URL to send a request**

```bash
curl -X POST http://127.0.0.1:58052/link_conversions \
    -H "Content-Type: application/json" \
    -d '{
        "deeplink": "app://item&id=12345",
        "url": "https://any.domain.com/item/12345"
    }'
```

See [Accessing apps](https://minikube.sigs.k8s.io/docs/handbook/accessing/) in the minikube docs.

**Note:** If the result is `curl: (56) Recv failure: Connection reset by peer`, wait for the application servers to start.

## Running the k6 Test Script

**Deploy the k6 Operator with Helm**

```bash
helm repo add grafana https://grafana.github.io/helm-charts
```

```bash
helm repo update
```

```bash
helm install k6-operator grafana/k6-operator
```

**Add the test script to the cluster using a ConfigMap**

```bash
kubectl create configmap hpa-load-test-script --from-file=k8s/hpa-load-test.js
```

**Run the test**

```bash
kubectl apply -f k8s/hpa-load-test.yaml
```

## Accessing the Couchbase Web Console

Refer to [Access the Couchbase Server User Interface - Port Forwarding](https://docs.couchbase.com/operator/current/howto-ui.html#port-forwarding) to learn how to access the Couchbase Web Console.

## Cleanup

To completely destroy the cluster:

```bash
minikube delete
```

## Notes

- Use `imagePullPolicy:IfNotPresent` or `imagePullPolicy:Never` in your deployments. [See the minikube docs.](https://minikube.sigs.k8s.io/docs/handbook/pushing/#1-pushing-directly-to-the-in-cluster-docker-daemon-docker-env)
- Refer to [CouchbaseCluster Reference Architecture, an advanced production configuration](https://docs.couchbase.com/operator/current/reference-reference-architecture.html) for production usage.