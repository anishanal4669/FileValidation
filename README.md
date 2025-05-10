rnetes.NewForConfig(config)
	
	for _, namespace := range namespaces.Items {
		fmt.Printf("\n==> Namespace: %s\n", namespace.Name)

		// List Persistent Volume Claims (PVCs)
		pvcList, err := clientset.CoreV1().PersistentVolumeClaims(namespace.Name).List(context.TODO(), metav1.ListOptions{})
		if err != nil {
			fmt.Printf("Error listing PVCs in namespace %s: %v\n", namespace.Name, err)
			continue
		}

		if len(pvcList.Items) == 0 {
			fmt.Println("  * No Persistent Volume Claims found.")
		} else {
			fmt.Println("  * Persistent Volume Claims:")
			for _, pvc := range pvcList.Items {
				fmt.Printf("    - Name: %s\n", pvc.Name)
				fmt.Printf("      Storage Class: %s\n", pvc.Spec.StorageClassName)
				fmt.Printf("      Access Modes: %v\n", pvc.Spec.AccessModes)

				age := calculateAge(pvc.CreationTimestamp)
				fmt.Printf("PVC Name: %s, Namespace: %s, Age: %s\n", pvc.Name, namespace.Name, age)

				if pvc.Status.Phase == "Pending" {
					fmt.Printf("Pending PVC found in namespace %s: %s\n", namespace.Name, pvc.Name)
				} else {

				}
				if pvc.Status.Phase == v1.ClaimBound {
					fmt.Printf("      Bound to PV: %s\n", pvc.Status.AccessModes)
				} else {
					fmt.Printf("      Status: %s\n", pvc.Status.Phase)
				}
			}
		}
		// Print PVCs with pending status
	}
}

// calculateAge calculates the age of a resource based on its creation timestamp
func calculateAge(creationTime metav1.Time) string {
	now := time.Now()
	duration := now.Sub(creationTime.Time)
	return fmt.Sprint(duration)
}





'''
receivers:
  filelog:
    include:
      - /path/to/your/logs/*.log
    start_at: beginning
    operators:
      - type: regex_parser
        regex: '^(?P<timestamp>\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2})\s+(?P<level>\w+)\s+(?P<tag>\w+)\s+(?P<message>.+)$'
        timestamp:
          parse_from: timestamp
          layout: '%Y-%m-%dT%H:%M:%S'
        severity:
          parse_from: level

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
'''
