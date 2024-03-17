
// use the current context in kubeconfig
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
