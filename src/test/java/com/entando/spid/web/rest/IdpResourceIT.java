package com.entando.spid.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.entando.spid.IntegrationTest;
import com.entando.spid.domain.Idp;
import com.entando.spid.repository.IdpRepository;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SpidResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class IdpResourceIT {

    private static final String DEFAULT_CONFIG = "AAAAAAAAAA";
    private static final String UPDATED_CONFIG = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/spids";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private IdpRepository idpRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSpidMockMvc;

    private Idp idp;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Idp createEntity(EntityManager em) {
        Idp idp = new Idp().config(DEFAULT_CONFIG);
        return idp;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Idp createUpdatedEntity(EntityManager em) {
        Idp idp = new Idp().config(UPDATED_CONFIG);
        return idp;
    }

    @BeforeEach
    public void initTest() {
        idp = createEntity(em);
    }

    @Test
    @Transactional
    void createSpid() throws Exception {
        int databaseSizeBeforeCreate = idpRepository.findAll().size();
        // Create the Idp
        restSpidMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(idp))
            )
            .andExpect(status().isCreated());

        // Validate the Idp in the database
        List<Idp> idpList = idpRepository.findAll();
        assertThat(idpList).hasSize(databaseSizeBeforeCreate + 1);
        Idp testIdp = idpList.get(idpList.size() - 1);
        assertThat(testIdp.getConfig()).isEqualTo(DEFAULT_CONFIG);
    }

    @Test
    @Transactional
    void createSpidWithExistingId() throws Exception {
        // Create the Idp with an existing ID
        idp.setId(1L);

        int databaseSizeBeforeCreate = idpRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpidMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(idp))
            )
            .andExpect(status().isBadRequest());

        // Validate the Idp in the database
        List<Idp> idpList = idpRepository.findAll();
        assertThat(idpList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSpids() throws Exception {
        // Initialize the database
        idpRepository.saveAndFlush(idp);

        // Get all the spidList
        restSpidMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(idp.getId().intValue())))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG)));
    }

    @Test
    @Transactional
    void getSpid() throws Exception {
        // Initialize the database
        idpRepository.saveAndFlush(idp);

        // Get the idp
        restSpidMockMvc
            .perform(get(ENTITY_API_URL_ID, idp.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(idp.getId().intValue()))
            .andExpect(jsonPath("$.config").value(DEFAULT_CONFIG));
    }

    @Test
    @Transactional
    void getSpidsByIdFiltering() throws Exception {
        // Initialize the database
        idpRepository.saveAndFlush(idp);

        Long id = idp.getId();

        defaultSpidShouldBeFound("id.equals=" + id);
        defaultSpidShouldNotBeFound("id.notEquals=" + id);

        defaultSpidShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSpidShouldNotBeFound("id.greaterThan=" + id);

        defaultSpidShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSpidShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSpidsByConfigIsEqualToSomething() throws Exception {
        // Initialize the database
        idpRepository.saveAndFlush(idp);

        // Get all the spidList where config equals to DEFAULT_CONFIG
        defaultSpidShouldBeFound("config.equals=" + DEFAULT_CONFIG);

        // Get all the spidList where config equals to UPDATED_CONFIG
        defaultSpidShouldNotBeFound("config.equals=" + UPDATED_CONFIG);
    }

    @Test
    @Transactional
    void getAllSpidsByConfigIsNotEqualToSomething() throws Exception {
        // Initialize the database
        idpRepository.saveAndFlush(idp);

        // Get all the spidList where config not equals to DEFAULT_CONFIG
        defaultSpidShouldNotBeFound("config.notEquals=" + DEFAULT_CONFIG);

        // Get all the spidList where config not equals to UPDATED_CONFIG
        defaultSpidShouldBeFound("config.notEquals=" + UPDATED_CONFIG);
    }

    @Test
    @Transactional
    void getAllSpidsByConfigIsInShouldWork() throws Exception {
        // Initialize the database
        idpRepository.saveAndFlush(idp);

        // Get all the spidList where config in DEFAULT_CONFIG or UPDATED_CONFIG
        defaultSpidShouldBeFound("config.in=" + DEFAULT_CONFIG + "," + UPDATED_CONFIG);

        // Get all the spidList where config equals to UPDATED_CONFIG
        defaultSpidShouldNotBeFound("config.in=" + UPDATED_CONFIG);
    }

    @Test
    @Transactional
    void getAllSpidsByConfigIsNullOrNotNull() throws Exception {
        // Initialize the database
        idpRepository.saveAndFlush(idp);

        // Get all the spidList where config is not null
        defaultSpidShouldBeFound("config.specified=true");

        // Get all the spidList where config is null
        defaultSpidShouldNotBeFound("config.specified=false");
    }

    @Test
    @Transactional
    void getAllSpidsByConfigContainsSomething() throws Exception {
        // Initialize the database
        idpRepository.saveAndFlush(idp);

        // Get all the spidList where config contains DEFAULT_CONFIG
        defaultSpidShouldBeFound("config.contains=" + DEFAULT_CONFIG);

        // Get all the spidList where config contains UPDATED_CONFIG
        defaultSpidShouldNotBeFound("config.contains=" + UPDATED_CONFIG);
    }

    @Test
    @Transactional
    void getAllSpidsByConfigNotContainsSomething() throws Exception {
        // Initialize the database
        idpRepository.saveAndFlush(idp);

        // Get all the spidList where config does not contain DEFAULT_CONFIG
        defaultSpidShouldNotBeFound("config.doesNotContain=" + DEFAULT_CONFIG);

        // Get all the spidList where config does not contain UPDATED_CONFIG
        defaultSpidShouldBeFound("config.doesNotContain=" + UPDATED_CONFIG);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSpidShouldBeFound(String filter) throws Exception {
        restSpidMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(idp.getId().intValue())))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG)));

        // Check, that the count call also returns 1
        restSpidMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSpidShouldNotBeFound(String filter) throws Exception {
        restSpidMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSpidMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSpid() throws Exception {
        // Get the idp
        restSpidMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSpid() throws Exception {
        // Initialize the database
        idpRepository.saveAndFlush(idp);

        int databaseSizeBeforeUpdate = idpRepository.findAll().size();

        // Update the idp
        Idp updatedIdp = idpRepository.findById(idp.getId()).get();
        // Disconnect from session so that the updates on updatedIdp are not directly saved in db
        em.detach(updatedIdp);
        updatedIdp.config(UPDATED_CONFIG);

        restSpidMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedIdp.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedIdp))
            )
            .andExpect(status().isOk());

        // Validate the Idp in the database
        List<Idp> idpList = idpRepository.findAll();
        assertThat(idpList).hasSize(databaseSizeBeforeUpdate);
        Idp testIdp = idpList.get(idpList.size() - 1);
        assertThat(testIdp.getConfig()).isEqualTo(UPDATED_CONFIG);
    }

    @Test
    @Transactional
    void putNonExistingSpid() throws Exception {
        int databaseSizeBeforeUpdate = idpRepository.findAll().size();
        idp.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpidMockMvc
            .perform(
                put(ENTITY_API_URL_ID, idp.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(idp))
            )
            .andExpect(status().isBadRequest());

        // Validate the Idp in the database
        List<Idp> idpList = idpRepository.findAll();
        assertThat(idpList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSpid() throws Exception {
        int databaseSizeBeforeUpdate = idpRepository.findAll().size();
        idp.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpidMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(idp))
            )
            .andExpect(status().isBadRequest());

        // Validate the Idp in the database
        List<Idp> idpList = idpRepository.findAll();
        assertThat(idpList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSpid() throws Exception {
        int databaseSizeBeforeUpdate = idpRepository.findAll().size();
        idp.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpidMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(idp))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Idp in the database
        List<Idp> idpList = idpRepository.findAll();
        assertThat(idpList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSpidWithPatch() throws Exception {
        // Initialize the database
        idpRepository.saveAndFlush(idp);

        int databaseSizeBeforeUpdate = idpRepository.findAll().size();

        // Update the idp using partial update
        Idp partialUpdatedIdp = new Idp();
        partialUpdatedIdp.setId(idp.getId());

        partialUpdatedIdp.config(UPDATED_CONFIG);

        restSpidMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIdp.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIdp))
            )
            .andExpect(status().isOk());

        // Validate the Idp in the database
        List<Idp> idpList = idpRepository.findAll();
        assertThat(idpList).hasSize(databaseSizeBeforeUpdate);
        Idp testIdp = idpList.get(idpList.size() - 1);
        assertThat(testIdp.getConfig()).isEqualTo(UPDATED_CONFIG);
    }

    @Test
    @Transactional
    void fullUpdateSpidWithPatch() throws Exception {
        // Initialize the database
        idpRepository.saveAndFlush(idp);

        int databaseSizeBeforeUpdate = idpRepository.findAll().size();

        // Update the idp using partial update
        Idp partialUpdatedIdp = new Idp();
        partialUpdatedIdp.setId(idp.getId());

        partialUpdatedIdp.config(UPDATED_CONFIG);

        restSpidMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedIdp.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedIdp))
            )
            .andExpect(status().isOk());

        // Validate the Idp in the database
        List<Idp> idpList = idpRepository.findAll();
        assertThat(idpList).hasSize(databaseSizeBeforeUpdate);
        Idp testIdp = idpList.get(idpList.size() - 1);
        assertThat(testIdp.getConfig()).isEqualTo(UPDATED_CONFIG);
    }

    @Test
    @Transactional
    void patchNonExistingSpid() throws Exception {
        int databaseSizeBeforeUpdate = idpRepository.findAll().size();
        idp.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpidMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, idp.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(idp))
            )
            .andExpect(status().isBadRequest());

        // Validate the Idp in the database
        List<Idp> idpList = idpRepository.findAll();
        assertThat(idpList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSpid() throws Exception {
        int databaseSizeBeforeUpdate = idpRepository.findAll().size();
        idp.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpidMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(idp))
            )
            .andExpect(status().isBadRequest());

        // Validate the Idp in the database
        List<Idp> idpList = idpRepository.findAll();
        assertThat(idpList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSpid() throws Exception {
        int databaseSizeBeforeUpdate = idpRepository.findAll().size();
        idp.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpidMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(idp))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Idp in the database
        List<Idp> idpList = idpRepository.findAll();
        assertThat(idpList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSpid() throws Exception {
        // Initialize the database
        idpRepository.saveAndFlush(idp);

        int databaseSizeBeforeDelete = idpRepository.findAll().size();

        // Delete the idp
        restSpidMockMvc
            .perform(delete(ENTITY_API_URL_ID, idp.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Idp> idpList = idpRepository.findAll();
        assertThat(idpList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
