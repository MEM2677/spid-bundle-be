package com.entando.spid.web.rest;

import com.entando.spid.ConfigUtils;
import com.entando.spid.config.ApplicationProperties;
import com.entando.spid.domain.Idp;
import com.entando.spid.domain.Organization;
import com.entando.spid.domain.ServiceStatus;
import com.entando.spid.service.ConfigurationService;
import com.entando.spid.service.IdpService;
import com.entando.spid.service.KeycloakService;
import com.entando.spid.service.dto.ConnectionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * REST controller for managing {@link Idp}.
 */
@RestController
@RequestMapping("/api/spid")
public class SpidResource {

    private final Logger log = LoggerFactory.getLogger(SpidResource.class);

    private static final String ENTITY_NAME = "spid";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IdpService idpService;
    private final KeycloakService keycloakService;
    private final ConfigurationService configService;

    public SpidResource(IdpService idpService, KeycloakService keycloakService, ConfigurationService configService) {
        this.idpService = idpService;
        this.keycloakService = keycloakService;
        this.configService = configService;
    }

    @PostMapping("/configure")
    @PreAuthorize("hasAnyAuthority('spid-admin')")
    public ResponseEntity<Boolean> configure() throws URISyntaxException {
        log.debug("REST request to configure Keycloak");

        try {
            ConnectionClient connection = configService.getConnection();
            boolean result = keycloakService.configure(connection);
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
        } catch (Throwable t) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(false);
        }
    }

    @PostMapping("/revert")
    @PreAuthorize("hasAnyAuthority('spid-admin')")
    public ResponseEntity<Boolean> revert() throws URISyntaxException {
        log.debug("REST request to revert Keycloak configuration");

        try {
            ConnectionClient connection = configService.getConnection();
            boolean result = keycloakService.revertConfiguration(connection);
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
        } catch (Throwable t) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(false);
        }
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyAuthority('spid-admin')")
    public ResponseEntity<ServiceStatus> status() {
        log.debug("REST request to get SPID status");

        try {
            ConnectionClient connection = configService.getConnection();
            ServiceStatus result = keycloakService.getStatus(connection);
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
        } catch (Throwable t) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ServiceStatus(null, null));
        }
    }

    @PutMapping("/organization")
    public ResponseEntity<ApplicationProperties> updateOrganizationProperties(@RequestBody ApplicationProperties properties) {
        log.debug("Request to update organization properties");

        configService.updateConfiguration(properties);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            //.headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(properties);
    }

    @GetMapping("/organization")
    public ResponseEntity<ApplicationProperties> getOrganizationProprties() {
        log.debug("Request to get organization properties");

        ApplicationProperties properties = configService.getConfiguration();
        System.out.println(">>>\n " + configService.getConfiguration());
        Map<String, String> envVars = System.getenv();
        return ResponseEntity
            .status(HttpStatus.CREATED)
            //.headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(properties);
    }

    /**
     * {@code POST  /spids} : Create a new idp.
     *
     * @param idp the idp to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new idp, or with status {@code 400 (Bad Request)} if the idp has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     *//*
    @PostMapping("/spids")
    public ResponseEntity<Idp> createSpid(@RequestBody Idp idp) throws URISyntaxException {
        log.debug("REST request to save Idp : {}", idp);
        if (idp.getId() != null) {
            throw new BadRequestAlertException("A new idp cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Idp result = null;
        return ResponseEntity
            .created(new URI("/api/spids/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }*/

    /**
     * {@code PUT  /spids/:id} : Updates an existing idp.
     *
     * @param id the id of the idp to save.
     * @param idp the idp to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated idp,
     * or with status {@code 400 (Bad Request)} if the idp is not valid,
     * or with status {@code 500 (Internal Server Error)} if the idp couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     *//*
    @PutMapping("/spids/{id}")
    public ResponseEntity<Idp> updateSpid(@PathVariable(value = "id", required = false) final Long id, @RequestBody Idp idp)
        throws Throwable {
        log.debug("REST request to update Idp : {}, {}", id, idp);
        if (idp.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, idp.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }


        Idp result = null;
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, idp.getId().toString()))
            .body(result);
    }*/

    /**
     * {@code PATCH  /spids/:id} : Partial updates given fields of an existing idp, field will ignore if it is null
     *
     * @param id the id of the idp to save.
     * @param idp the idp to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated idp,
     * or with status {@code 400 (Bad Request)} if the idp is not valid,
     * or with status {@code 404 (Not Found)} if the idp is not found,
     * or with status {@code 500 (Internal Server Error)} if the idp couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     *//*
    @PatchMapping(value = "/spids/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Idp> partialUpdateSpid(@PathVariable(value = "id", required = false) final Long id, @RequestBody Idp idp)
        throws URISyntaxException {
        log.debug("REST request to partial update Idp partially : {}, {}", id, idp);
        if (idp.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, idp.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }


        Optional<Idp> result = null;

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, idp.getId().toString())
        );
    }
    */

    /**
     * {@code GET  /spids} : get all the spids.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of spids in body.
     *//*
    @GetMapping("/spids")
    public ResponseEntity<List<Idp>> getAllSpids(SpidCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Spids by criteria: {}", criteria);
        Page<Idp> page = null;
        page.getContent().stream().forEach(c -> System.out.println(c.getConfig()));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }*/



    /**
     * {@code GET  /spids/:id} : get the "id" spid.
     *
     * @param id the id of the spid to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the spid, or with status {@code 404 (Not Found)}.
     */ /*
    @GetMapping("/spids/{id}")
    public ResponseEntity<Idp> getSpid(@PathVariable Long id) {
        log.debug("REST request to get Idp : {}", id);
        Optional<Idp> spid = null;
        return ResponseUtil.wrapOrNotFound(spid);
    } */

    /**
     * {@code DELETE  /spids/:id} : delete the "id" spid.
     *
     * @param id the id of the spid to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */ /*
    @DeleteMapping("/spids/{id}")
    public ResponseEntity<Void> deleteSpid(@PathVariable Long id) {
        log.debug("REST request to delete Idp : {}", id);

        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    } */

}
