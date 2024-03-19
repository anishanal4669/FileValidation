
package main

import (
	"encoding/json"
	"fmt"

	"k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/kubernetes"
)

// Rule represents a single alert rule within a PrometheusRule
type Rule struct {
	Expr       string             `json:"expr"`
	For        v1.Duration        `json:"for,omitempty"`
	Labels     map[string]string  `json:"labels,omitempty"`
	Annotations map[string]string `json:"annotations,omitempty"`
}

func main() {
	// Replace with your in-cluster or out-of-cluster config
	config, err := restclient.InClusterConfig()
	if err != nil {
		panic(err)
	}

	// Create a Kubernetes clientset
	clientset, err := kubernetes.NewForConfig(config)
	if err != nil {
		panic(err)
	}

	// Define the namespace where you want to get PrometheusRule (replace with your namespace)
	namespace := "default"

	// Get the PrometheusRule object
	prometheusRule, err := clientset.MonitoringV1().PrometheusRules(namespace).Get("kube-pod-crashlooping", v1.GetOptions{})
	if err != nil {
		panic(err)
	}

	// Extract and print the rules
	fmt.Println("Rules:")
	for _, group := range prometheusRule.Spec.Groups {
		for _, rule := range group.Rules {
			var extractedRule Rule
			err := json.Unmarshal([]byte(rule.Expr), &extractedRule.Expr)
			if err != nil {
				fmt.Println("Error parsing rule expression:", err)
				continue
			}
			extractedRule.For = rule.For
			extractedRule.Labels = rule.Labels
			extractedRule.Annotations = rule.Annotations
			fmt.Printf("  - expr: %s\n", extractedRule.Expr)
			fmt.Printf("    for: %s\n", extractedRule.For)
			fmt.Println("    labels:")
			for k, v := range extractedRule.Labels {
				fmt.Printf("      %s: %s\n", k, v)
			}
			fmt.Println("    annotations:")
			for k, v := range extractedRule.Annotations {
				fmt.Printf("      %s: %s\n", k, v)
			}
		}
	}
}

# FileValidation
Generic Framework to compare different types of files

Creating a Lerna experimental repo involves using Lerna, a tool for managing JavaScript projects with multiple packages. Below are the general steps to set up an experimental Lerna repository using npm:

1. **Install Node.js and npm:**
   Make sure you have Node.js and npm installed on your machine. You can download them from the official website: https://nodejs.org/

2. **Initialize a New npm Project:**
   Open a terminal and navigate to the directory where you want to create your experimental Lerna repository. Run the following commands:

   ```bash
   mkdir my-lerna-repo
   cd my-lerna-repo
   npm init -y
   ```

3. **Install Lerna:**
   Install Lerna globally using npm:

   ```bash
   npm install -g lerna
   ```

4. **Initialize Lerna:**
   Run the following command to initialize Lerna in your project:

   ```bash
   lerna init --independent
   ```

   The `--independent` flag sets up independent versioning for each package, meaning each package can have its own version.

5. **Create Packages:**
   Inside the `packages` directory (created by Lerna), you can create individual packages. For example:

   ```bash
   cd packages
   mkdir package1
   mkdir package2
   ```

   Each package should have its own `package.json` file.

6. **Linking Packages:**
   To link the packages together during development, you can use Lerna's `lerna add` command. For example:

   ```bash
   lerna add package2 --scope=package1
   ```

   This command links `package2` as a dependency in `package1`.

7. **Running Commands Across Packages:**
   You can use Lerna to run commands across all packages or specific packages. For example:

   ```bash
   lerna exec -- npm install
   ```

   This installs dependencies for all packages.

8. **Run Scripts Across Packages:**
   Define scripts in the root `package.json` or in each package's `package.json`. You can then run scripts across packages using Lerna. For example:

   ```bash
   lerna run test
   ```

   This runs the `test` script in each package.

9. **Publish Packages:**
   When you are ready to publish new versions of your packages, you can use the following commands:

   ```bash
   lerna version
   lerna publish from-package
   ```

   The first command prompts you to choose new versions for changed packages, and the second command publishes the new versions to npm.

10. **Explore Lerna Commands:**
    Lerna provides various commands for managing the monorepo. You can explore them in the [official documentation](https://github.com/lerna/lerna).

Remember that Lerna is a powerful tool, and its usage might vary depending on your project's needs. Customize the configuration, scripts, and folder structure based on your specific requirements.
