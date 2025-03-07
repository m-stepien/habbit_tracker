FROM quay.io/keycloak/keycloak:latest
COPY keycloak-listener/target/ /opt/keycloak/providers/
RUN /opt/keycloak/bin/kc.sh build
ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
