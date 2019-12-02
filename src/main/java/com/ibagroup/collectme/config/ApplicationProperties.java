package com.ibagroup.collectme.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Collectme.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private Ldap ldap;

    public Ldap getLdap() {
        return ldap;
    }

    public void setLdap(Ldap ldap) {
        this.ldap = ldap;
    }

    public static class Ldap {

        private String url;
        private String baseSource;
        private String baseDn;
        private String basePassword;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getBaseSource() {
            return baseSource;
        }

        public void setBaseSource(String baseSource) {
            this.baseSource = baseSource;
        }

        public String getBaseDn() {
            return baseDn;
        }

        public void setBaseDn(String baseDn) {
            this.baseDn = baseDn;
        }

        public String getBasePassword() {
            return basePassword;
        }

        public void setBasePassword(String basePassword) {
            this.basePassword = basePassword;
        }

    }
}
