package main

import (
	"encoding/csv"
	"fmt"
	"log"
	"os"
	"strconv"

	"github.com/xanzy/go-gitlab"
)

func main() {
	// Create GitLab client
	git, err := gitlab.NewClient("your_access_token")
	if err != nil {
		log.Fatalf("Failed to create client: %v", err)
	}

	groupID := 12345 // Replace with your top-level group ID
	fileName := "repositories.csv"

	file, err := os.Create(fileName)
	if err != nil {
		log.Fatalf("Failed to create file: %v", err)
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	// Write CSV headers
	err = writer.Write([]string{"GroupID", "GroupName", "ProjectID", "ProjectName", "ProjectURL"})
	if err != nil {
		log.Fatalf("Failed to write CSV headers: %v", err)
	}

	// Fetch and write repositories
	err = fetchAndWriteRepositories(git, groupID, writer)
	if err != nil {
		log.Fatalf("Error fetching repositories: %v", err)
	}

	fmt.Printf("Repositories list has been saved to %s\n", fileName)
}

func fetchAndWriteRepositories(client *gitlab.Client, groupID int, writer *csv.Writer) error {
	groups := []*gitlab.Group{}
	listOptions := &gitlab.ListGroupsOptions{
		AllAvailable: gitlab.Bool(true),
	}

	// Fetch all sub-groups
	for {
		groupsPage, resp, err := client.Groups.ListSubgroups(groupID, listOptions)
		if err != nil {
			return fmt.Errorf("failed to list subgroups: %v", err)
		}
		groups = append(groups, groupsPage...)

		if resp.NextPage == 0 {
			break
		}
		listOptions.Page = resp.NextPage
	}

	// Include the current group
	mainGroup, _, err := client.Groups.GetGroup(groupID, &gitlab.GetGroupOptions{})
	if err != nil {
		return fmt.Errorf("failed to get group: %v", err)
	}
	groups = append(groups, mainGroup)

	// Fetch projects for each group
	for _, group := range groups {
		listProjectsOptions := &gitlab.ListGroupProjectsOptions{
			ListOptions: gitlab.ListOptions{PerPage: 100},
		}
		for {
			projects, resp, err := client.Groups.ListGroupProjects(group.ID, listProjectsOptions)
			if err != nil {
				return fmt.Errorf("failed to list projects: %v", err)
			}

			for _, project := range projects {
				record := []string{
					strconv.Itoa(group.ID),
					group.Name,
					strconv.Itoa(project.ID),
					project.Name,
					project.WebURL,
				}
				if err := writer.Write(record); err != nil {
					return fmt.Errorf("failed to write record: %v", err)
				}
			}

			if resp.NextPage == 0 {
				break
			}
			listProjectsOptions.Page = resp.NextPage
		}
	}

	return nil
}
