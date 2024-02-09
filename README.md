# SPID Integration bundle

This bundle, in its first release, configures Keycloak to use Italian SPID identity provider.
SPID (Sistema Pubblico di Identità Digitale) is a system used by Public Administrations and provate subject
to authenticate a given user.


This PBC let **Entando 7.x** be certified as SPID Service Provider

**NOTE:** installing the PBC alone is not sufficient to start the accreditation process: be sure to read the [technical
documentation](https://docs.italia.it/italia/idp/idp-regole-tecniche/it/stabile/index.html) first and then the whole [certification procedure](https://www.idp.gov.it/cos-e-idp/diventa-fornitore-di-servizi/).

Currently, the bundle configures the following identity providers:

| Name of the provider   |
|------------------------|
| Aruba ID               |
| EtnaHitech S.C.p.A.    |
| InfoCamere S.C.p.A.    |
| Infocert ID            |
| Intesi Group S.p.A.    |
| Lepida ID              |
| Namirial ID            |
| Sielte ID              |
| SPIDItalia Register.it |
| TeamSystem ID.         |
| Tim ID                 |

**NOTE:** a test IdP will also be created to let the organization validate the general setup of the environment against the [test server](https://demo.spid.gov.it/#/login) 
made available by the public administration.

## Local development

Running the bundle for local development is pretty straightforward:

first we start a (local) Keycloak server container

```shell
ent bundle svc start keycloak
```

let's have a look to the logs and wait for Keycloak to complete the booting process. 

```shell
ent bundle svc logs keycloak
```

**NOTE:** Keycloak admin interface can be accessed at [this address](http://localhost:9080/auth/).  

Username: **admin**  
Password: **admin**

At this point we can start the microservice locally

```shell
ent bundle run spid-config 
```

As a result the Keycloak will be configured with pre-defined Italian SPID identity providers.

**NOTE:** Please keep in mind that the microservice configures Keycloak only once: so running the microservice against
an already configured Keycloak bears no result! To configure SPID again the configuration must be 
reverted as shown [in this paragraph](#reverting-the-configuration).

### Configuration for the local execution

The default Spring configuration should be ok for the most cases!
However, the configuration file `src/main/resources/config/application-dev.yml` contains both the parameters of the organization, which are not relevant for local execution, and a little group
of properties that might be of interest for the developer (eg. default realm, keycloak client id and password etc.).

The organization parameters are discussed [here](#organization-properties). 


## Reverting the configuration

The fastest way to revert the configuration is through the invocation of the appropriate [REST API](#rest-api-support).  

Alternatively, when developing locally please follow these steps:

First we stop the Keycloak instance:

```shell
ent bundle svc stop keycloak
```

In the project root of this bundle locate the folder `./svc/keycloak/keycloak-db` and delete all the files inside; then restart the Keycloak container
as shown in the previous step.

In a clustered installation the revert of the configuration must be done manually from the Keycloak admin interface. Obviously this
method works also for local development.

The procedure is as follows:

- access keycloak admin interface 
- under the menu `Identity Provider` delete all the providers created
- under `Authentication` from the dropdown in Flows select `SPID first broker login`: when the page refreshes click the `Delete` button 

## Installation in a cluster
 
When installing in production (or staging) we have two options:

The first step is to prepare Keycloak for the bundle execution; we have two options;  
so we either [replace the entire Keycloak image](#replace-keycloak-image) OR  [modify the existing Keycloak installation](#modify-existing-keycloak).

**IMPORTANT!** Please be aware that in the latter case the Keycloak theme is not updated so the dynamic list of providers won't be available.  
The default behaviour of Keycloak in this case is to show a separate button for each SPID identity provider.


### Replace Keycloak image

This is ideal for new Entando instances (or when Keycloak configuration can be imported or exported easily).
**NOTE:** make sure to have the CLI configured properly.

Run the following command to replace Keycloak image

- For Keycloak community 18.0.x:
```shell
ent k scale deploy default-sso-in-namespace-deployment --replicas=0
ent k set image deployment/default-sso-in-namespace-deployment server-container=entandopsdh/spid-keycloak-theme:0.0.2
```

- For Red HAT SSO:
```shell
ent k scale deploy default-sso-in-namespace-deployment --replicas=0
ent k set image deployment/default-sso-in-namespace-deployment server-container=entandopsdh/spid-rhsso-theme:0.0.1
```

finally restart the deployment 

```shell
ent k scale deploy default-sso-in-namespace-deployment --replicas=1
```

### Modify existing Keycloak

This method is necessary when Keycloak image can't be replaced.

#### Installing the SPID provider in Keycloak manually

Locate the pod containing the Keycloak installation: 

Alternatively you can use the following command from Mac / Linux terminal:

```shell
ent k get po -n <NAMESPACE> | grep default-sso-in-namespace-deployment | head -n1 | cut -d " " -f1
````
where NAMESPACE is the namespace where Entando was installed to.

Copy the [spid-provider.jar](https://github.com/entando-ps/entando-spid-integration/blob/master/bundle_src/spid-provider.jar) into the Keycloak using the command appropriate for your Keycloak installation:

- Keycloak 18.0.x community edition:

```shell
ent k cp ./provider/spid-provider.jar default-sso-in-namespace-deployment-aaabbbccc-dddee:/opt/jboss/keycloak/standalone/deployments
```

- Red HAT SSO:

```shell
ent k cp ./provider/spid-provider.jar default-sso-in-namespace-deployment-aaabbbccc-dddee:/opt/eap/standalone/deployments
```

where `default-sso-in-namespace-deployment-aaabbbccc-dddee` is the name of the Keycloak pod.

You have to wait a few instants to let Keycloak detect the new provider and install it.  

The result of this operation is to add a new identity
provider, **SPID**, to the list of those already available. This provider will be configured automatically when the bundle is installed.
For this reason installing the bundle without these preliminary step will result in an error.

Once that Keycloak has been prepared:

- [create the secret containing the configuration of the organization](#create-the-secret-with-organization-data)
- [install the bundle through the CLI](#installation)


### Create the secret with organization data

To make the process of the creation of the secret easier developers can change the properties inside the script `configure.sh` then execute it with the command

```shell
sh ./script/configure.sh <NAMESPACE>
```

where NAMESPACE is the namespace of the Entando installation of interest.

**NOTE:** make sure to have the correct _bundle ID_ configured and to execute this script at least once, otherwise the installation will fail.   
To get the bundle id of your custom repository and organization run the following command

```shell
ent ecr get-bundle-id docker://registry.hub.docker.com/<ORGANIZATION>/spid-bundle
```

Replace the value in the `configure.sh` script and inside the [environment variables defined in the entando.json](https://developer.entando.com/next/docs/curate/bundle-details.html#microservices-specifications) file

### Installation

Final users at this point can install the bundle from the [official Entando Hub](https://entando.com/en/hub.page), while the developers can install the bundle using the CLI;  
the procedure is the same presented [in the official documentation](https://developer.entando.com/v7.1/tutorials/create/pb/publish-project-bundle.html#create-and-deploy-a-bundle-project), so:

```shell
ent bundle pack
```

To build the project

```shell
ent bundle publish
```

**NOTE:** append the switches `--registry=registry.hub.docker.com --org=my-docker-organization` to customize the registry and the organization.  
This is mandatory when pushing the bundle image to a new repository.

```shell
ent bundle deploy
```

To finally deploy the bundle in Entando.

At this point it is possible to access the `App Builder` to install the bundle.


## REST API support

By default, the bundle installs the defined configuration on startup; however, it is possible to [disable the automatic installation on startup](#other-environment-variables)
and demand an external authenticated client to start the configuration process.  

Below the list of the currently supported API:

| Name      | Endpoint                                                              | Type | Description                                                                                                                                                                                                |
|-----------|-----------------------------------------------------------------------|------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Configure | [/api/spid/configure](http://localhost:8081/api/spid/configure)       | POST | Start the configuration process                                                                                                                                                                            | 
| Revert    | [/api/spid/revert](http://localhost:8081/api/spid/revert)             | POST | Revert the configuration                                                                                                                                                                                   |
| Status    | [/api/spid/status](http://localhost:8081/api/spid/status)             | GET  | Get the status: if "installed" is true then <br/> the configuration is effective. Remaining fields are the list of providers configured and <br/> whether the custom authentication flow is present or not |
| organization    | [/api/spid/organization](http://localhost:8081/api/spid/organization) | GET  | Get the actual Organization properties used for the configuration (or that will be used for the configuration)                                                                                             |
| organization    | [/api/spid/organization](http://localhost:8081/api/spid/organization) | POST | Set the organization properties that will be used for the configuration process                                                                                                                            |


For more detailed information access the swagger browser in the [local development environment](http://localhost:8081/swagger-ui/): keep the default client ID and secret and, when requested, use the following credentials on the standard login (not the SPID one!):  

Username: **admin**  
Password: **admin**

**NOTE:** Users need the permission **spid-admin** to consume the endpoints.

## Organization properties

These properties are replicated for every identity provider known by the installer.

Below the list of the fields shared between private organizations and public administrations:

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

The following environment variable is also available:

| Variable                | Example | Description                                                                                                  |
|-------------------------|---------|--------------------------------------------------------------------------------------------------------------|
| SPID_CONFIG_ACTIVE      | true    | true = configure Keycloak on service startup; false = wait for configure REST API to start the configuration |
