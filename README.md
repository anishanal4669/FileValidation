To align the `ObservedTimestamp` (collector's reception time) and `Timestamp` (log event time) in ResourceLogs, you need to ensure proper timestamp extraction from your logs. Here's a step-by-step solution:

---

### **1. Core Issue Analysis**
- **ObservedTimestamp**: When the collector received the log (system-generated)
- **Timestamp**: Original event time (should come from your log content)
- **Mismatch Cause**: Collector isn't parsing the timestamp from your logs correctly

---

### **2. Solution Configuration**

#### **A. Update `filelog` Receiver**
Add explicit timestamp parsing in your ConfigMap:

```yaml
receivers:
  filelog:
    include: [/var/log/app/*.log]
    start_at: beginning
    operators:
      # Extract timestamp from log body
      - type: regex_parser
        regex: '^(?P<timestamp>\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.?\d*)'
        timestamp:
          parse_from: attributes.timestamp
          layout: '%Y-%m-%dT%H:%M:%S.%L'  # Handles milliseconds
          location: UTC  # Match your log's timezone
```

#### **B. Add Time Conversion Processor**
Force timestamp alignment:

```yaml
processors:
  # Convert parsed timestamp to log record's Timestamp
  attributes/converter:
    actions:
      - key: timestamp
        action: convert
        converted_type: time
        format: '%Y-%m-%dT%H:%M:%S.%L'
```

---

### **3. Full Working Configuration**

```yaml
# ConfigMap
apiVersion: v1
kind: ConfigMap
metadata:
  name: splunk-otel-collector-config
data:
  otel-collector-config.yaml: |
    receivers:
      filelog:
        include: [/var/log/app/*.log]
        operators:
          - type: regex_parser
            regex: '^(?P<timestamp>\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.?\d*)'
            timestamp:
              parse_from: attributes.timestamp
              layout: '%Y-%m-%dT%H:%M:%S.%L'
              location: UTC
          - type: move
            from: attributes.log_timestamp
            to: attributes.timestamp

    processors:
      attributes/converter:
        actions:
          - key: timestamp
            action: convert
            converted_type: time
            format: '%Y-%m-%dT%H:%M:%S.%L'
      batch: {}

    exporters:
      splunk_hec: # ... existing config ...

    service:
      pipelines:
        logs:
          receivers: [filelog]
          processors: [attributes/converter, batch]
          exporters: [splunk_hec]
```

---

### **4. Verification**

#### **A. Check Parsed Timestamps**
```bash
kubectl logs <pod-name> -c splunk-otel-collector | grep 'LogRecord' -A 5
```

**Expected Output:**
```
LogRecord #0
ObservedTimestamp: 2025-05-10 20:18:42.614038964 +0000 UTC
Timestamp: 2025-05-10 20:18:42 +0000 UTC  # Should now match!
Body: Str(2025-05-10T20:18:42 stderr info...)
```

#### **B. Validate Regex Matching**
Test with your actual log format:
```bash
# Sample log: "2025-05-10T20:18:42.614Z This is a log message"
echo "2025-05-10T20:18:42.614Z test" | \
  grep -P '^(?P<timestamp>\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.?\d*)'
```

---

### **5. Advanced Scenarios**

#### **A. For Non-Standard Timestamps**
```yaml
# For format: "May 10 20:18:42"
timestamp:
  parse_from: attributes.timestamp
  layout: '%b %d %H:%M:%S'
  location: UTC
```

#### **B. Timezone Conversion**
```yaml
timestamp:
  parse_from: attributes.timestamp
  layout: '%Y-%m-%dT%H:%M:%S'
  location: America/New_York  # Original log's timezone
```

---

### **6. Common Fixes**

| Issue | Solution |
|-------|----------|
| Millisecond mismatch | Add `.%L` to layout |
| Timezone offset missing | Include `%z` in layout |
| Month names instead of numbers | Use `%b` (Jan) or `%B` (January) |
| 12-hour clock format | Use `%I` instead of `%H` |

---

### **7. Troubleshooting Flow**
```
ObservedTimestamp ≠ Timestamp
  ↓
Check log format matches regex
  ↓
Verify layout string matches exactly
  ↓
Confirm timezone configuration
  ↓
Test regex with actual log samples
```

By following this configuration, you'll ensure the original log timestamp is properly parsed and used as the `Timestamp` value, aligning it with the `ObservedTimestamp` when processing is instantaneous.