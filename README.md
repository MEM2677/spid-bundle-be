# SPID bundle

This bundle, in its first release, configures Keycloak to use Italian SPID identity provider.
SPID (Sistema Pubblico di Identità Digitale) is a system used by Public Administrations and provate subject
to authenticate a given user.


This PBC let **Entando 7** be certified as SPID Service Provider

**NOTE:** installing the PBC alone is not sufficient to start the accreditation process: be sure to read the [technical
documentation](https://docs.italia.it/italia/idp/idp-regole-tecniche/it/stabile/index.html) first and then the whole [certification procedure](https://www.idp.gov.it/cos-e-idp/diventa-fornitore-di-servizi/).

Below the list of the Identity providers currently supported:

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

## Local development

Running the bundle for local development is pretty straightforward:

first we start a (local) Keycloak server container

```shell
ent prj xk start
```

let's have a look to the logs and wait for Keycloak to complete the booting process. 

```shell
ent prj xk logs -f
```

**NOTE:** Keycloak admin interface can be accessed at [this address](http://localhost:9080/auth/).
Username: **admin**  
Password: **admin**

At this point we can start the microservice locally

```shell
ent prj be-test-run
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
ent prj xk stop
```

In the project root of this bundle locate the folder `./src/main/docker/keycloak-db` and delete all the files inside; then restart the Keycloak container
as shown in the previous step.

In a clustered installation the revert of the configuration must be done manually from the Keycloak admin interface. Obviously this
method works also for local development.

The procedure is as follows:

- access keycloak admin interface 
- under the menu `Identity Provider` delete all the providers created
- under `Authentication` from the dropdown in Flows select `SPID first broker login`: when the page refreshes click the `Delete` button 

## Installation in a cluster
 
When installing in production (or staging) we have two options:

The first step is to prepare Keycloak for the bundle execution; we have two options:  

 - [replace the entire Keycloak image](#replace-keycloak-image)
 - [modify the existing Keycloak installation](#modify-existing-keycloak)

**IMPORTANT!** Please be aware that in the latter case the Keycloak theme is not updated so the dynamic list of providers won't be available.  
The default behaviour of Keycloak in this case is to show a separate button for each SPID identity provider.


The next steps are:

 - [create the secret containing the configuration of the organization](#create-the-secret-with-organization-data)
 - [create the bundle directory](#create-the-bundle-directory)
 - [install the bundle through the CLI](#installation)


### Replace Keycloak image

This is ideal for new Entando instances (or when Keycloak configuration can be imported or exported easily).
**NOTE:** make sure to have the CLI configured properly.

Run the following command to replace Keycloak image

- For Keycloak community 15.1.x:
```shell
ent k scale deploy default-sso-in-namespace-deployment --replicas=0
ent k set image deployment/default-sso-in-namespace-deployment server-container=entandopsdh/spid-keycloak-theme:0.0.2
```

- For Red HAT SSO 7:
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

Copy the spid-provider.jar into the Keycloak using the command appropriate for your Keycloak installation:

- Keycloak 15.1.x community edition:

```shell
ent k cp bundle_src/spid-provider.jar default-sso-in-namespace-deployment-aaabbbccc-dddee:/opt/jboss/keycloak/standalone/deployments
```

- Red HAT SSO 7:

```shell
ent k cp bundle_src/spid-provider.jar default-sso-in-namespace-deployment-aaabbbccc-dddee:/opt/eap/standalone/deployments
```

where `default-sso-in-namespace-deployment-aaabbbccc-dddee` is the name of the Keycloak pod.

You have to wait a few instants to let Keycloak detect the new provider and install it.  

The result of this operation is to add a new identity
provider, **SPID**, to the list of those already available. This provider will be configured automatically when the bundle is installed.
For this reason installing the bundle without these preliminary step will result in an error.

### Create the secret with organization data

To make the process of the creation of the secret easier developers can change the properties inside the script `configure.sh` then execute it with the command

```shell
sh ./bundle_src/configure.sh <NAMESPACE>
```

where NAMESPACE is the namespace of the Entando installation of interest.

**NOTE:** make sure to have the correct _bundle ID_ configured and to execute this script at loeast once, otherwise the installation will fail.


### Create the bundle directory

From the root of the project run the command:

```shell
mkdir bundle && cp -R bundle_src/* bundle
```

This creates the output directory where the bundle will be placed

### Installation

As expected we use the CLI to install in a cluster. The procedure is the same presented [in the official documentation](https://developer.entando.com/next/tutorials/create/pb/publish-project-bundle.html#cli-steps) so:

```shell
ent prj build
```

To build the project

```shell
ent prj pbs-init
```

To declare the Git repository where the developers want the bundle to be stored

```shell
ent prj pbs-publish
```

To push the bundle in the repository

```shell
ent prj deploy
```

To finally deploy the bundle in Entando.

At this point it is possible to access the `App Builder` to install the bundle.

Alternatively, using the CLI, execute the command:

```shell
ent prj install
```

or

```shell
ent prj install --conflict-strategy=OVERRIDE
```

The latter is used when the bundle is already installed.

## REST API support

By default, the bundle installs the defined configuration on startup; however, it is possible to [disable the automatic installation on startup](#other-environment-variables)
and demand an external authenticated client to start the configuration process.  

Below the list of the currently supported API:

| Name      | Endpoint                                                        | Type | Arguments | Description                                                                                                                                                                                                |
|-----------|-----------------------------------------------------------------|------|-----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Configure | [/api/spid/configure](http://localhost:8081/api/spid/configure) | POST | none      | Start the configuration process                                                                                                                                                                            |
| Revert    | [/api/spid/revert](http://localhost:8081/api/spid/revert)       | POST | none      | Revert the configuration                                                                                                                                                                                   |
| Status    | [/api/spid/status](http://localhost:8081/api/spid/status)       | GET  | none      | Get the status: if "installed" is true then <br/> the configuration is effective. Remaining fields are the list of providers configured and <br/> whether the custom authentication flow is present or not |


For more detailed information access the swagger browser in the [local development environment](http://localhost:8081/swagger-ui/); when requested use the following credentials on the standard login (not the SPID one!):  

Username: **admin**  
Password: **admin**

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


