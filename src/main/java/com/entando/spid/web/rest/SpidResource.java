package com.entando.spid.web.rest;

import com.entando.spid.domain.Idp;
import com.entando.spid.service.IdpService;
import com.entando.spid.service.KeycloakService;
import com.entando.spid.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * REST controller for managing {@link Idp}.
 */
@RestController
@RequestMapping("/api/spid")
public class SpidResource {

    private final Logger log = LoggerFactory.getLogger(SpidResource.class);

    private static final String ENTITY_NAME = "spidSpid";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final IdpService idpService;
    private final KeycloakService keycloakService;


    public SpidResource(IdpService idpService, KeycloakService keycloakService) {
        this.idpService = idpService;
        this.keycloakService = keycloakService;
    }


//    @PostMapping("/configure")
//    public ResponseEntity<Boolean> configure() throws URISyntaxException {
//        log.debug("REST request to configure Keycloak");
//
//        success = keycloakService.configure();
//        return ResponseEntity
//            .created(new URI("/api/spids/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(true);
//    }


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
