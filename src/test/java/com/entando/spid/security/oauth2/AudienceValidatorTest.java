package com.entando.spid.security.oauth2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.entando.spid.SpidApp;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Test class for the {@link AudienceValidator} utility class.
 */
@ContextConfiguration(classes = SpidApp.class, loader = AnnotationConfigContextLoader.class)
@WebAppConfiguration
@AutoConfigureMockMvc
class AudienceValidatorTest {

    private final AudienceValidator validator = new AudienceValidator(Arrays.asList("api://default"));

    @Test
    @SuppressWarnings("unchecked")
    void testInvalidAudience() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("aud", "bar");
        Jwt badJwt = mock(Jwt.class);
        when(badJwt.getAudience()).thenReturn(new ArrayList(claims.values()));
        assertThat(validator.validate(badJwt).hasErrors()).isTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testValidAudience() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("aud", "api://default");
        Jwt jwt = mock(Jwt.class);
        when(jwt.getAudience()).thenReturn(new ArrayList(claims.values()));
        assertThat(validator.validate(jwt).hasErrors()).isFalse();
    }
}
