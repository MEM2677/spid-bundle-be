# SPID bundle

This bundle, in its first release, configures Keycloak to use Italian SPID identity provider.
SPID (Sistema Pubblico di Identità Digitale) is a system used by Public Administrations and provate subject
to authenticate a given user.




This PBC let **Entando 7** to be certified as SPID Service Provider

**NOTE:** installing the PBC alone is not sufficient to start the accreditation process: be sure to read the [technical
documentation](https://docs.italia.it/italia/idp/idp-regole-tecniche/it/stabile/index.html) first and then the whole [certification procedure](https://www.idp.gov.it/cos-e-idp/diventa-fornitore-di-servizi/).

Below the list of the Identity providers currently supported

| Name of the provider         |
|------------------------------|
| Aruba PEC S.p.A.             |
| In.Te.S.A. S.p.A.            |
| InfoCert S.p.A.              |
| Lepida S.c.p.A.              |
| Namirial S.p.A.              |
| Poste Italiane S.p.A.        |
| Register S.p.A.              |
| Sielte S.p.A.                |
| TeamSystem S.p.A.            |
| TI Trust Technologies S.r.l. |

**NOTE:** a test IdP will also be created to let the organization validate the general setup of the environment against the [test server](https://demo.spid.gov.it/#/login) 
made available by the public administration.


## Prerequisites

Before installing the bundle in your Entando 7 installation, a few operations must be carried out by the devOps / sysOps of the cluster.

These operations include:
- copy of the provider JAR into Keycloak pod
- creation of the secret
- configure the bundle by specifying the organization data

The first two steps must be performed by the sysOps or devOps, the last one can be done by a developer.

### Install the SPID provider in keycloak

Locate the pod containing the Keycloak installation: in a standard installation the name of the pod always start with **default-sso-in-namespace-deployment**

Alternatively you can use the following command from Mac / Linux terminal:

```shell
kubectl get po -n <NAMESPACE> | grep default-sso-in-namespace-deployment | head -n1 | cut -d " " -f1
````
where NAMESPACE is the namespace where Entando was installed to.

Copy the idp-provider.jar into the Keycloak pod with the command

```shell
kubectl bundle/idp-provider.jar default-sso-in-namespace-deployment-aaabbbccc-dddee:/opt/jboss/keycloak/standalone/deployments
```

where `default-sso-in-namespace-deployment-aaabbbccc-dddee` is the name of the Keycloak pod

You have to wait a few instants to let Keycloak sense the new provider and install it.  
The result of this operation is to add a new identity
provider, **SPID**, to the list of those already available. This provider will be configured automatically when the bundle is installed.
For this reason installing the bundle without these preliminary step will result in an error.

### Prepare the client secret

Secrets are a means of transport of sensible information to the bundle, so to let it perform various setup operations.
The service connects to Keycloak using a SAT account, so the secret contains the secret of the Keycloak client used for connection
(the client ID is obtained from the environment variables but that can be overridden).

**NOTE:** the creation of the secret must be done only once and repeated only when the Git repository of the bundle changes.

As specified in the [documentation](https://developer.entando.com/next/tutorials/devops/plugin-environment-variables.html) the secrets bound to a bundle
must have specific names starting with the bundle ID.  
To obtain the bundle ID execute the following command:

```shell
ent ecr get-bundle-id --auto "<MY_REPOSITORY>"
```
where <MY_REPOSITORY> is the address of the Git repository containing the bundle (inclusive of the trailing `.git`).

Create the secret for username and password with the following commands

```shell
kubectl create secret generic <BUNDLE_ID>-client-secret --from-literal=password=<CLIENT_SECRET> -n <NAMESPACE>

```

with NAMESPACE being the namespace where Entando is installed, CLIENT_SECRET the value of the Keycloak client-secret and
BUNDLE_ID the bundle ID found in the previous step.

### Configure the bundle

In the last step is necessary to specify the organization data: they are replicated for every identity provider known by the
installer.

Below the list of the field shared between private organizations and public administrations:

| Variable                   | Example                                  | Description                                                                                                                                                |
|----------------------------|------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| ORGANIZATION_NAMES         | en&#124;Entando, it&#124;Entando         | Localized name of the organization. <br>The pipe "&#124;" separates the language <br>code from the property, the comma<br/> separates different properties |
| ORGANIZATION_DISPLAY_NAMES | en&#124;Entando, it&#124;Entando         | Localized name shown publicly                                                                                                                              |
| ORGANIZATION_URLS          | en&#124;entando.com, it&#124;entando.com | The organization URL                                                                                                                                       |
| OTHER_CONTACT_COMPANY      | Entando                                  | Contact company name                                                                                                                                       |
| OTHER_CONTACT_PHONE        | +395556935632                            | Contact phone                                                                                                                                              |
| OTHER_CONTACT_EMAIL        | anymail@company.com                      | Contact email                                                                                                                                              |


The following fields determines whether the organization is private or public:

| Variable                    | Example | Description                                          |
|-----------------------------|---------|------------------------------------------------------|
| OTHER_CONTACT_IS_SP_PRIVATE | true    | true if the organization is provate, false otherwise |


The following field is for **Italian public administraion only**:

| Variable               | Example | Description                 |
|------------------------|---------|-----------------------------|
| OTHER_CONTACT_IPA_CODE | PIA123  | IPA code assigned to the PA |


The following fields are for **private organizations only**:

| Variable                      | Example                           | Description                 |
|-------------------------------|-----------------------------------|-----------------------------|
| OTHER_CONTACT_VAT_NUMBER      | IT03264290929                     | The VAT of the organization |
| BILLING_CONTACT_COMPANY       | Entando                           | The billing company name    |
| BILLING_CONTACT_EMAIL         | billing@company.com               | The billing mail            |
| BILLING_CONTACT_REGISTRY_NAME | Entando                           | The registry name           |
| BILLING_CONTACT_SITE_ADDRESS  | Via&#124;P.zza&#124;V.le Sardegna | Address of the company      |
| BILLING_CONTACT_SITE_NUMBER   | 9                                 | Address number              |
| BILLING_CONTACT_SITE_CITY     | Cagliari                          | Company HQ City             |
| BILLING_CONTACT_SITE_ZIP_CODE | 09127                             | Italian HQ ZIP code (CAP)   |
| BILLING_CONTACT_SITE_PROVINCE | CA                                | Company HQ province (Prov)  |
| BILLING_CONTACT_SITE_COUNTRY  | IT                                | Company HQ country          |

### Other environment variables

The following environment variable are also available:

| Variable                | Example | Description                                                                                                               |
|-------------------------|---------|---------------------------------------------------------------------------------------------------------------------------|
| SPID_CONFIG_ACTIVE      | true    | Toggle the configuration setup: when false nothing is done                                                                |
| KEYCLOAK_PROTO          | https   | Let developers override the protocol to connect to Keycloak with                                                          |
| KEYCLOAK_SPID_CLIENT_ID |         | Secret to override the **client_id** to use for the connection to Keycloak; <br> update the **client_secret** accordingly |


## Development

To start your application in the dev profile, run:

```
./mvnw
```

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

### JHipster Control Center

JHipster Control Center can help you manage and control your application(s). You can start a local control center server (accessible on http://localhost:7419) with:

```
docker-compose -f src/main/docker/jhipster-control-center.yml up
```

### OAuth 2.0 / OpenID Connect

Congratulations! You've selected an excellent way to secure your JHipster application. If you're not sure what OAuth and OpenID Connect (OIDC) are, please see [What the Heck is OAuth?](https://developer.okta.com/blog/2017/06/21/what-the-heck-is-oauth)

To log in to your app, you'll need to have [Keycloak](https://keycloak.org) up and running. The JHipster Team has created a Docker container for you that has the default users and roles. Start Keycloak using the following command.

```
docker-compose -f src/main/docker/keycloak.yml up
```

The security settings in `src/main/resources/config/application.yml` are configured for this image.

```yaml
spring:
  ...
  security:
    oauth2:
      client:
        provider:
          oidc:
            issuer-uri: http://localhost:9080/auth/realms/jhipster
        registration:
          oidc:
            client-id: web_app
            client-secret: web_app
            scope: openid,profile,email
```

### Okta

If you'd like to use Okta instead of Keycloak, it's pretty quick using the [Okta CLI](https://cli.okta.com/). After you've installed it, run:

```shell
okta register
```

Then, in your JHipster app's directory, run `okta apps create` and select **JHipster**. This will set up an Okta app for you, create `ROLE_ADMIN` and `ROLE_USER` groups, create a `.okta.env` file with your Okta settings, and configure a `groups` claim in your ID token.

Run `source .okta.env` and start your app with Maven or Gradle. You should be able to sign in with the credentials you registered with.

If you're on Windows, you should install [WSL](https://docs.microsoft.com/en-us/windows/wsl/install-win10) so the `source` command will work.

If you'd like to configure things manually through the Okta developer console, see the instructions below.

First, you'll need to create a free developer account at <https://developer.okta.com/signup/>. After doing so, you'll get your own Okta domain, that has a name like `https://dev-123456.okta.com`.

Modify `src/main/resources/config/application.yml` to use your Okta settings.

```yaml
spring:
  ...
  security:
    oauth2:
      client:
        provider:
          oidc:
            issuer-uri: https://{yourOktaDomain}/oauth2/default
        registration:
          oidc:
            client-id: {clientId}
            client-secret: {clientSecret}
security:
```

Create an OIDC App in Okta to get a `{clientId}` and `{clientSecret}`. To do this, log in to your Okta Developer account and navigate to **Applications** > **Add Application**. Click **Web** and click the **Next** button. Give the app a name you’ll remember, specify `http://localhost:8080` as a Base URI, and `http://localhost:8080/login/oauth2/code/oidc` as a Login Redirect URI. Click **Done**, then Edit and add `http://localhost:8080` as a Logout redirect URI. Copy and paste the client ID and secret into your `application.yml` file.

Create a `ROLE_ADMIN` and `ROLE_USER` group and add users into them. Modify e2e tests to use this account when running integration tests. You'll need to change credentials in `src/test/javascript/e2e/account/account.spec.ts` and `src/test/javascript/e2e/admin/administration.spec.ts`.

Navigate to **API** > **Authorization Servers**, click the **Authorization Servers** tab and edit the default one. Click the **Claims** tab and **Add Claim**. Name it "groups", and include it in the ID Token. Set the value type to "Groups" and set the filter to be a Regex of `.*`.

After making these changes, you should be good to go! If you have any issues, please post them to [Stack Overflow](https://stackoverflow.com/questions/tagged/jhipster). Make sure to tag your question with "jhipster" and "okta".

### Auth0

If you'd like to use [Auth0](https://auth0.com/) instead of Keycloak, follow the configuration steps below:

- Create a free developer account at <https://auth0.com/signup>. After successful sign-up, your account will be associated with a unique domain like `dev-xxx.us.auth0.com`
- Create a new application of type `Regular Web Applications`. Switch to the `Settings` tab, and configure your application settings like:
  - Allowed Callback URLs: `http://localhost:8080/login/oauth2/code/oidc`
  - Allowed Logout URLs: `http://localhost:8080/`
- Navigate to **User Management** > **Roles** and create new roles named `ROLE_ADMIN`, and `ROLE_USER`.
- Navigate to **User Management** > **Users** and create a new user account. Click on the **Role** tab to assign roles to the newly created user account.
- Navigate to **Auth Pipeline** > **Rules** and create a new Rule. Choose `Empty rule` template. Provide a meaningful name like `JHipster claims` and replace `Script` content with the following and Save.

```javascript
function (user, context, callback) {
  user.preferred_username = user.email;
  const roles = (context.authorization || {}).roles;

  function prepareCustomClaimKey(claim) {
    return `https://www.jhipster.tech/${claim}`;
  }

  const rolesClaim = prepareCustomClaimKey('roles');

  if (context.idToken) {
    context.idToken[rolesClaim] = roles;
  }

  if (context.accessToken) {
    context.accessToken[rolesClaim] = roles;
  }

  callback(null, user, context);
}
```

- In your `JHipster` application, modify `src/main/resources/config/application.yml` to use your Auth0 application settings:

```yaml
spring:
  ...
  security:
    oauth2:
      client:
        provider:
          oidc:
            # make sure to include the ending slash!
            issuer-uri: https://{your-auth0-domain}/
        registration:
          oidc:
            client-id: {clientId}
            client-secret: {clientSecret}
            scope: openid,profile,email
jhipster:
  ...
  security:
    oauth2:
      audience:
        - https://{your-auth0-domain}/api/v2/
```

## Building for production

### Packaging as jar

To build the final jar and optimize the idp application for production, run:

```
./mvnw -Pprod clean verify
```

To ensure everything worked, run:

```
java -jar target/*.jar
```

Refer to [Using JHipster in production][] for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

```
./mvnw -Pprod,war clean verify
```

## Testing

To launch your application's tests, run:

```
./mvnw verify
```

For more information, refer to the [Running tests page][].

### Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker-compose -f src/main/docker/sonar.yml up -d
```

Note: we have turned off authentication in [src/main/docker/sonar.yml](src/main/docker/sonar.yml) for out of the box experience while trying out SonarQube, for real use cases turn it back on.

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the maven plugin.

Then, run a Sonar analysis:

```
./mvnw -Pprod clean verify sonar:sonar
```

If you need to re-run the Sonar phase, please be sure to specify at least the `initialize` phase since Sonar properties are loaded from the sonar-project.properties file.

```
./mvnw initialize sonar:sonar
```

For more information, refer to the [Code quality page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a postgresql database in a docker container, run:

```
docker-compose -f src/main/docker/postgresql.yml up -d
```

To stop it and remove the container, run:

```
docker-compose -f src/main/docker/postgresql.yml down
```

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

```
./mvnw -Pprod verify jib:dockerBuild
```

Then run:

```
docker-compose -f src/main/docker/app.yml up -d
```

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[jhipster homepage and latest documentation]: https://www.jhipster.tech
[jhipster 7.2.0 archive]: https://www.jhipster.tech/documentation-archive/v7.2.0
[doing microservices with jhipster]: https://www.jhipster.tech/documentation-archive/v7.2.0/microservices-architecture/
[using jhipster in development]: https://www.jhipster.tech/documentation-archive/v7.2.0/development/
[using docker and docker-compose]: https://www.jhipster.tech/documentation-archive/v7.2.0/docker-compose
[using jhipster in production]: https://www.jhipster.tech/documentation-archive/v7.2.0/production/
[running tests page]: https://www.jhipster.tech/documentation-archive/v7.2.0/running-tests/
[code quality page]: https://www.jhipster.tech/documentation-archive/v7.2.0/code-quality/
[setting up continuous integration]: https://www.jhipster.tech/documentation-archive/v7.2.0/setting-up-ci/
[node.js]: https://nodejs.org/
[npm]: https://www.npmjs.com/
