FROM prom/prometheus:v2.54.1

USER root

# 설정 파일 템플릿 복사
COPY prometheus.yml /etc/prometheus/prometheus.yml.tpl

# 엔트리포인트 스크립트 복사
COPY entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]