package main

import (
	"context"
	"flag"
	"fmt"
	"path/filepath"
	"time"

	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/tools/clientcmd"
	"k8s.io/client-go/util/homedir"
)

func main() {
	// Create a Kubernetes clientset
	var kubeconfig *string
	if home := homedir.HomeDir(); home != "" {
		kubeconfig = flag.String("kubeconfig", filepath.Join(home, ".kube", "config"), "(optional) absolute path to the kubeconfig file")
	} else {
		kubeconfig = flag.String("kubeconfig", "", "absolute path to the kubeconfig file")
	}
	flag.Parse()

	// use the current context in kubeconfig
	config, err := clientcmd.BuildConfigFromFlags("", *kubeconfig)
	if err != nil {
		panic(err.Error())
	}
	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		fmt.Printf("Error creating clientset: %v\n", err)
		return
	}

	// List all Persistent Volumes (PVs) across all namespaces
	pvList, err := clientset.CoreV1().PersistentVolumes().List(context.TODO(), metav1.ListOptions{})
	if err != nil {
		fmt.Printf("Error listing Persistent Volumes: %v\n", err)
		return
	}

	if len(pvList.Items) == 0 {
		fmt.Println("No Persistent Volumes found in any namespace.")
	} else {
		fmt.Println("==> Persistent Volumes:")
		for _, pv := range pvList.Items {
			fmt.Printf("  * Namespace: %s\n", pv.Namespace) // Include namespace for PVs
			fmt.Printf("    - Name: %s\n", pv.Name)
			fmt.Printf("      Capacity: %v\n", pv.Spec.Capacity)
			fmt.Printf("      Access Modes: %v\n", pv.Spec.AccessModes)
		}
	}

	// Loop through each namespace to list PVCs
	namespaces, err := clientset.CoreV1().Namespaces().List(context.TODO(), metav1.ListOptions{})
	if err != nil {
		fmt.Printf("Error getting namespaces: %v\n", err)
		return
	}

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
