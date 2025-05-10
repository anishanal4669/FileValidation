receivers:
  filelog:
    include:
      - /path/to/your/logs/*.log
    start_at: beginning
    operators:
      # Extract the full log line first
      - type: regex_parser
        regex: '^(?P<time>\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2})\s+(?P<level>\w+)\s+(?P<tag>\w+)\s+(?P<message>.+)$'
      # Then handle the timestamp separately
      - type: time_parser
        parse_from: attributes.time
        layout_type: gotime
        layout: '2006-01-02T15:04:05'

processors:
  batch:
  memory_limiter:
    check_interval: 1s
    limit_mib: 500
    spike_limit_mib: 100

exporters:
  splunk_hec:
    token: "${SPLUNK_HEC_TOKEN}"
    endpoint: "${SPLUNK_HEC_URL}"
    source: "otel-collector"
    sourcetype: "custom:logs"
    index: "main"
    tls:
      insecure_skip_verify: false

service:
  pipelines:
    logs:
      receivers: [filelog]
      processors: [batch, memory_limiter]
      exporters: [splunk_hec]
